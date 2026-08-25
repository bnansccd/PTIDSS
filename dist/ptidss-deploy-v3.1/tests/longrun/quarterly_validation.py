"""
PTIDSS 长期验证-季验证（V2.5 遗留建议②）：全面复核基线
- 区域配置：市场化省份区域清单与启停状态（/admin/regions）
- 数据源/情报源规模与健康（/intel/sources、/intel/fetch-status）
- 模型任务/算法规模（/model/tasks、/model/registry）
- 系统可用性：登录 + 令牌续期健康（X-New-Token 可观测）
用法：python3 quarterly_validation.py
输出：reports/quarterly_YYYYQn.json
"""
import datetime
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import requests  # noqa: E402
from common import BASE, REGION, api, login, need, save_report, summary  # noqa: E402


def main():
    token = login()
    need(token, "登录失败")

    # 1) 区域配置（市场化省份）
    http, j = api(token, "GET", "/admin/regions")
    regions = j.get("data") or []
    if isinstance(regions, dict):
        regions = regions.get("list") or regions.get("records") or []
    enabled = [r for r in regions if (r.get("status") or "") == "enabled"]
    market_codes = [r.get("regionCode") for r in regions
                    if r.get("regionCode", "").startswith("CN-")]
    spot_provinces = [r.get("regionName") for r in regions
                      if r.get("marketSupport") and "spot" in str(r.get("marketSupport"))]

    # 2) 情报源规模与健康（健康率按启用中源计算；已停用源在 details 中体现）
    http, j = api(token, "GET", "/intel/fetch-status")
    sources = j.get("data") or []
    enabled_sources = [s for s in sources if (s.get("status") or "") == "enabled"]
    healthy = [s for s in enabled_sources if s.get("healthy")]
    source_health_rate = len(healthy) / len(enabled_sources) if enabled_sources else 1.0

    # 3) 模型任务/算法规模
    http, j = api(token, "GET", "/model/tasks", params={"limit": 100})
    task_data = j.get("data") or []
    if isinstance(task_data, dict):
        task_data = task_data.get("list") or task_data.get("records") or []
    task_total = len(task_data)
    http, j = api(token, "GET", "/model/registry")
    registry = j.get("data") or []
    if isinstance(registry, dict):
        registry = registry.get("list") or registry.get("records") or []

    # 4) 令牌滑动续期健康（刚登录令牌未到续期窗口时无响应头属正常；仅记录可观测性）
    headers = {"X-Region-Code": REGION, "Authorization": "Bearer " + token}
    r = requests.get(BASE + "/intel/fetch-status", headers=headers, timeout=20)
    new_token_seen = "x-new-token" in {k.lower() for k in r.headers.keys()}

    q = (datetime.date.today().month - 1) // 3 + 1
    quarter = f"{datetime.date.today().year}Q{q}"
    payload = {
        "period": "quarterly", "quarter": quarter,
        "generatedAt": datetime.datetime.now().isoformat(),
        "metrics": {
            "enabledRegions": len(enabled), "spotProvinces": len(spot_provinces),
            "sourceHealthRate": round(source_health_rate, 4),
            "modelTaskTotal": int(task_total or 0), "algorithmCount": len(registry),
            "tokenRefreshHeaderSeen": new_token_seen,
        },
        "details": {
            "regionCodes": market_codes, "spotProvinces": spot_provinces,
            "sourceTotal": len(sources), "sourceHealthy": len(healthy),
        },
    }
    path = save_report(f"quarterly_{quarter}.json", payload)
    print(f"== PTIDSS 季验证 {quarter} ==")
    ok = summary([
        ("启用区域数", len(enabled), 6, True),
        ("现货市场省份数", len(spot_provinces), 6, True),
        ("情报源健康率", round(source_health_rate, 4), 0.95, True),
        ("模型任务总数", int(task_total or 0), 0, True),
        ("算法注册数", len(registry), 0, True),
        ("令牌续期响应头", "已观测" if new_token_seen else "未到续期窗口（正常）", "-", True),
    ])
    print("报告已保存：", path)
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
