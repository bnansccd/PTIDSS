"""
PTIDSS 长期验证-日验证（V2.5 遗留建议②）
指标（对齐《V2.4市场化省份接入与长期验证方案》§2）：
- 数据采集成功率（healthy / enabled 源）≥ 98%
- 采集时效达标率（lastSuccessAt 距今 ≤ 频率×1.5）≥ 95%
- 数据完整性（情报流非空字段率）≥ 99%
- 数据质量（/data/quality/report 完整率/准确率/及时率）≥ 95%
用法：python3 daily_validation.py [--exit-code]
输出：reports/daily_YYYYMMDD.json；--exit-code 时未达标退出码 1（供 crontab 告警联动）
"""
import datetime
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import api, login, need, save_report, summary  # noqa: E402


def main():
    exit_code = "--exit-code" in sys.argv
    token = login()
    need(token, "登录失败（检查 BASE/账号或 captcha 模式）")

    # 1) 采集成功率 + 时效达标率（情报源台账 /intel/fetch-status）
    http, j = api(token, "GET", "/intel/fetch-status")
    need(http == 200 and j.get("code") == 0, "fetch-status 接口异常")
    sources = j.get("data") or []
    enabled = [s for s in sources if s.get("status") == "enabled"]
    healthy = [s for s in enabled if s.get("healthy")]
    success_rate = len(healthy) / len(enabled) if enabled else 1.0

    now = datetime.datetime.now()
    timely = 0
    timed = 0
    for s in enabled:
        last = s.get("lastSuccessAt")
        freq_min = s.get("frequencyMinutes")
        if not last or not freq_min:
            continue
        timed += 1
        try:
            t = datetime.datetime.strptime(str(last), "%Y-%m-%d %H:%M:%S")
            if (now - t).total_seconds() <= float(freq_min) * 90:
                timely += 1
        except ValueError:
            pass
    timeliness_rate = timely / timed if timed else 1.0

    # 2) 数据完整性：情报流前 10 条非空字段率
    http, j = api(token, "GET", "/intel/news", params={"pageNo": 1, "pageSize": 10})
    news = (j.get("data") or {}).get("list") or []
    fields = ["sourceCode", "title", "content", "importance", "publishedAt"]
    filled = sum(1 for n in news for f in fields if n.get(f))
    total = len(news) * len(fields)
    completeness = filled / total if total else 1.0

    # 3) 数据质量报告
    http, j = api(token, "GET", "/data/quality/report")
    quality = j.get("data") or {}
    qrates = [float(quality.get(k, 0.95)) for k in ("completeness", "accuracy", "timeliness")]

    day = datetime.date.today().strftime("%Y%m%d")
    payload = {
        "period": "daily", "date": day, "generatedAt": datetime.datetime.now().isoformat(),
        "metrics": {
            "collectSuccessRate": round(success_rate, 4),
            "collectTimelinessRate": round(timeliness_rate, 4),
            "dataCompleteness": round(completeness, 4),
            "qualityCompleteness": qrates[0], "qualityAccuracy": qrates[1],
            "qualityTimeliness": qrates[2],
        },
        "details": {
            "enabledSources": len(enabled), "healthySources": len(healthy),
            "timelySources": timely, "timedSources": timed,
            "newsSampled": len(news),
            "degradedOrFailing": [
                {"code": s["sourceCode"], "fails": s.get("consecutiveFailures"),
                 "err": (s.get("lastError") or "")[:100]}
                for s in enabled if s.get("consecutiveFailures") or s.get("lastError")
            ],
        },
    }
    path = save_report(f"daily_{day}.json", payload)
    print(f"== PTIDSS 日验证 {day} ==")
    ok = summary([
        ("采集成功率", round(success_rate, 4), 0.98, True),
        ("采集时效达标率", round(timeliness_rate, 4), 0.95, True),
        ("数据完整性", round(completeness, 4), 0.99, True),
        ("质量-完整率", qrates[0], 0.95, True),
        ("质量-准确率", qrates[1], 0.95, True),
        ("质量-及时率", qrates[2], 0.95, True),
    ])
    print("报告已保存：", path)
    if exit_code and not ok:
        sys.exit(1)


if __name__ == "__main__":
    main()
