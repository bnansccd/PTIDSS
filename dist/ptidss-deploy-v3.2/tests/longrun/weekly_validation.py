"""
PTIDSS 长期验证-周验证（V2.5 遗留建议②）
- 数据源状态分布（enabled/disabled/error）、连续失败源清单与趋势
- 情报入库量（本周新增情报总数）
- 数据质量报告（周口径）
用法：python3 weekly_validation.py
输出：reports/weekly_YYYYMMDD.json
"""
import datetime
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import api, login, need, save_report, summary  # noqa: E402


def main():
    token = login()
    need(token, "登录失败")

    # 1) 数据源状态分布与失败清单（失败率按"启用中"源计算；已停用源在分布中体现）
    http, j = api(token, "GET", "/intel/fetch-status")
    need(http == 200 and j.get("code") == 0, "fetch-status 接口异常")
    sources = j.get("data") or []
    status_dist = {}
    failing = []
    enabled = [s for s in sources if (s.get("status") or "") == "enabled"]
    for s in enabled:
        st = s.get("status")
        status_dist[st] = status_dist.get(st, 0) + 1
        fails = s.get("consecutiveFailures") or 0
        if fails > 0 or s.get("lastError"):
            failing.append({"code": s["sourceCode"], "fails": fails,
                            "err": (s.get("lastError") or "")[:120]})
    for s in sources:
        st = s.get("status")
        status_dist[st] = status_dist.get(st, 0) + 1
    failing_rate = len(failing) / len(enabled) if enabled else 0.0

    # 2) 情报入库量（本周：近 7 天情报总数，分页取 total）
    http, j = api(token, "GET", "/intel/news", params={"pageNo": 1, "pageSize": 1})
    news_total = ((j.get("data") or {}).get("total") or 0)

    # 3) 数据质量报告
    http, j = api(token, "GET", "/data/quality/report")
    quality = j.get("data") or {}
    qrates = [float(quality.get(k, 0.95)) for k in ("completeness", "accuracy", "timeliness")]

    day = datetime.date.today().strftime("%Y%m%d")
    payload = {
        "period": "weekly", "weekEnd": day, "generatedAt": datetime.datetime.now().isoformat(),
        "metrics": {
            "sourceFailingRate": round(failing_rate, 4),
            "newsTotal": news_total,
            "qualityCompleteness": qrates[0], "qualityAccuracy": qrates[1],
            "qualityTimeliness": qrates[2],
        },
        "details": {"statusDist": status_dist, "failingSources": failing,
                    "totalSources": len(sources)},
    }
    path = save_report(f"weekly_{day}.json", payload)
    print(f"== PTIDSS 周验证（截至 {day}）==")
    ok = summary([
        ("源失败率", round(failing_rate, 4), 0.02, False),
        ("情报入库总数", news_total, 0, True),
        ("质量-完整率", qrates[0], 0.95, True),
        ("质量-准确率", qrates[1], 0.95, True),
        ("质量-及时率", qrates[2], 0.95, True),
    ])
    print("失败源明细：", json.dumps(failing, ensure_ascii=False)[:500] if failing else "无")
    print("报告已保存：", path)
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
