"""
PTIDSS 长期验证-月验证（V2.5 遗留建议②）
- 模型准确性：近 30 天评估任务 MAPE ≤ 8%、方向准确率 ≥ 80%（取任务报告 result_json）
- 模型对标稳定度：同模型同类型连续任务 compare.delta 汇总（无恶化趋势）
- 算法成功率：任务列表 train/evaluate/inference 成功率 ≥ 99%
用法：python3 monthly_validation.py
输出：reports/monthly_YYYYMM.json
"""
import datetime
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import api, login, need, save_report, summary  # noqa: E402


def main():
    token = login()
    need(token, "登录失败")

    # 1) 模型任务列表（评估/训练/推理；limit 取近 100 条）
    http, j = api(token, "GET", "/model/tasks", params={"limit": 100})
    need(http == 200 and j.get("code") == 0, "model/tasks 接口异常")
    tasks = j.get("data") or []
    if isinstance(tasks, dict):
        tasks = tasks.get("list") or tasks.get("records") or []

    total = len(tasks)
    success = [t for t in tasks if (t.get("status") or "") == "success"]
    task_success_rate = len(success) / total if total else 1.0

    # 2) 评估任务准确性指标：列表接口为轻量字段，逐条读详情（result_json/compare_json 在详情）
    mares, dirs = [], []
    compare_deltas = []
    eval_ok = [t for t in tasks if t.get("taskType") == "evaluate"
               and (t.get("status") or "") == "success"]
    for t in eval_ok[-5:]:
        http2, j2 = api(token, "GET", "/model/tasks/" + str(t.get("id")))
        if http2 != 200 or j2.get("code") != 0:
            continue
        detail = j2.get("data") or {}
        result = detail.get("result") or detail.get("resultJson") or {}
        if isinstance(result, str):
            try:
                import json
                result = json.loads(result)
            except Exception:
                result = {}
        mape = result.get("mape")
        direction = result.get("directionAccuracy")
        if isinstance(mape, (int, float)):
            mares.append(float(mape))
        if isinstance(direction, (int, float)):
            dirs.append(float(direction))
        compare = detail.get("compare") or detail.get("compareJson") or {}
        if isinstance(compare, str):
            try:
                import json
                compare = json.loads(compare)
            except Exception:
                compare = {}
        delta = compare.get("delta") or {}
        if isinstance(delta, dict) and delta.get("mape") is not None:
            compare_deltas.append(float(delta["mape"]))
    avg_mape = sum(mares) / len(mares) if mares else None
    avg_dir = sum(dirs) / len(dirs) if dirs else None
    degraded = any(d > 0 for d in compare_deltas)

    # 4) 算法注册规模
    http, j = api(token, "GET", "/model/registry")
    registry = j.get("data") or []
    if isinstance(registry, dict):
        registry = registry.get("list") or registry.get("records") or []
    alg_count = len(registry)

    month = datetime.date.today().strftime("%Y%m")
    payload = {
        "period": "monthly", "month": month, "generatedAt": datetime.datetime.now().isoformat(),
        "metrics": {
            "taskSuccessRate": round(task_success_rate, 4),
            "avgMape": round(avg_mape, 4) if avg_mape is not None else None,
            "avgDirectionAccuracy": round(avg_dir, 4) if avg_dir is not None else None,
            "compareMapeDeltas": [round(d, 4) for d in compare_deltas],
            "algorithmCount": alg_count,
        },
        "details": {"taskTotal": total, "taskSuccess": len(success),
                    "evaluateTasks": len(eval_ok), "degradedVsBaseline": degraded},
    }
    path = save_report(f"monthly_{month}.json", payload)
    print(f"== PTIDSS 月验证 {month} ==")
    ok = summary([
        ("算法任务成功率", round(task_success_rate, 4), 0.99, True),
        ("平均 MAPE", round(avg_mape, 4) if avg_mape is not None else "-", 0.08, False),
        ("平均方向准确率", round(avg_dir, 4) if avg_dir is not None else "-", 0.80, True),
        ("对标恶化（MAPE delta>0）", "是" if degraded else "否", "-", True),
    ])
    print("报告已保存：", path)
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
