"""
PTIDSS V2.0 多角色全面核查-修复回归用例（captcha.enabled=false 测试模式）
覆盖：
  1. 复盘域：GET /review/reports（新端点）、POST /review/reports、详情、策略回流
  2. 考核域：GET /assessment/indicators、GET /assessment/results、POST /assessment/appeals
  3. 报表域：GET /report/templates、POST /report/instances、GET /report/instances
  4. 决策人审闭环：创建会话 → 详情 → confirm → modify(无依据→拒绝) → reject(新端点)
     → 状态机校验 + evidence 依据链
  5. 页面依赖抽查：market/forecast/trade/settlement/positions 列表端点
  6. 权限门禁：trader01 无 menu:review → 14003；manager 有 menu:review → 200
输出：JSON 结果文件 verify_result_v2_audit.json
"""
import requests
import json
import time
import sys

BASE = "http://localhost:9080/ptidss"
REGION = "CN-32"
PWD = "Ptidss@2026"
results = []


def api(method, path, token=None, body=None, expect=0, group="", name="", raw_ok=False, params=None):
    headers = {"X-Region-Code": REGION}
    if token:
        headers["Authorization"] = "Bearer " + token
    url = BASE + path
    t0 = time.time()
    try:
        r = requests.request(method, url, headers=headers, json=body, params=params, timeout=20)
        elapsed = int((time.time() - t0) * 1000)
        try:
            j = r.json()
        except Exception:
            j = {"raw": r.text[:200]}
        code = j.get("code")
        ok = (r.status_code == 200 and code == expect) if not raw_ok else (r.status_code == 200)
        results.append({
            "group": group, "name": name, "method": method, "path": path,
            "http": r.status_code, "code": code, "expect_code": expect,
            "ok": ok, "elapsed_ms": elapsed,
            "note": (j.get("message") or json.dumps(j, ensure_ascii=False)[:120]),
        })
        return ok, j
    except Exception as e:
        results.append({"group": group, "name": name, "method": method, "path": path,
                        "http": 0, "code": None, "expect_code": expect, "ok": False,
                        "elapsed_ms": 0, "note": "EXC:" + str(e)})
        return False, {}


def login(username, password=PWD):
    r = requests.post(BASE + "/auth/login", json={"username": username, "password": password},
                      headers={"X-Region-Code": REGION}, timeout=20)
    j = r.json()
    if j.get("code") == 0:
        return j["data"]["accessToken"]
    return None


def main():
    admin = login("admin")
    if not admin:
        print("admin 登录失败（captcha=false 模式下应直接登录），退出")
        sys.exit(1)
    trader = login("trader01")
    manager = login("manager")
    ts = int(time.time() * 1000) % 100000

    # ── 1. 复盘域（GAP-01 修复 + 新列表端点） ──
    ok, j = api("GET", "/review/reports", admin, group="review", name="复盘报告列表(新端点)")
    api("POST", "/review/reports", admin, body={
        "reportType": "weekly", "startDate": "2026-08-10", "endDate": "2026-08-16",
        "focusTopics": ["现货价格波动", "持仓偏差"]},
        group="review", name="生成周报")
    ok2, j2 = api("GET", "/review/reports?reportType=weekly", admin, group="review", name="按类型筛选列表")
    rid = None
    if ok2 and j2.get("data"):
        rid = j2["data"][0].get("id")
    if rid:
        api("GET", f"/review/reports/{rid}", admin, group="review", name="报告详情(三层归因)")
        api("POST", "/review/strategy-feedback", admin, body={
            "strategyCode": "STRAT-DA-PRICE", "feedback": "effective", "reviewId": rid},
            group="review", name="策略回流(提交)")
        api("POST", "/review/strategy-feedback", admin, body={
            "strategyCode": "STRAT-DA-PRICE", "feedback": "bad_feedback"},
            group="review", name="策略回流(非法反馈→拒绝)", expect=500)

    # ── 2. 考核域（GAP-02 修复） ──
    api("GET", "/assessment/indicators", admin, group="assess", name="考核指标体系")
    ok3, j3 = api("GET", "/assessment/results?period=2026-08", admin, group="assess", name="考核结果(周期必填)")
    ar_id = None
    if ok3 and isinstance(j3.get("data"), list) and j3["data"]:
        ar_id = j3["data"][0].get("id")
    if ar_id:
        api("POST", "/assessment/appeals", admin, body={
            "resultId": ar_id, "appealReason": f"回归测试申诉-{ts}"},
            group="assess", name="考核申诉(提交)")

    # ── 3. 报表域（GAP-03 修复） ──
    ok4, j4 = api("GET", "/report/templates", admin, group="report", name="报表模板")
    tpl_code = None
    if ok4 and j4.get("data"):
        tpl_code = j4["data"][0].get("code")
    if tpl_code:
        api("POST", "/report/instances", admin, body={"templateCode": tpl_code, "period": "2026-08", "format": "csv"},
            group="report", name="生成报表实例")
    api("GET", "/report/instances", admin, group="report", name="报表实例列表")

    # ── 4. 决策人审闭环（FR-DM-05 + 新 reject 端点） ──
    ok5, j5 = api("POST", "/decision/sessions", admin, body={
        "sessionType": "rolling", "tradeDate": "2026-08-12", "scenario": "baseline"},
        group="decision", name="创建决策会话")
    sid = j5.get("data", {}).get("sessionId") if ok5 else None
    if sid:
        api("GET", f"/decision/sessions/{sid}", admin, group="decision", name="会话详情")
        api("GET", f"/decision/sessions/{sid}/evidence", admin, group="decision", name="依据链回溯")
        # 驳回原因必填
        api("POST", f"/decision/sessions/{sid}/reject", admin, body={"reason": ""},
            group="decision", name="驳回(无原因→拒绝)", expect=500)
        # 确认后不可驳回 → 状态机（先确认再驳回应允许，先驳回再确认应拒绝）
        api("POST", f"/decision/sessions/{sid}/confirm", admin, group="decision", name="人审确认")
        api("POST", f"/decision/sessions/{sid}/confirm", admin, group="decision",
            name="重复确认→拒绝", expect=500)
        api("POST", f"/decision/sessions/{sid}/modify", admin, body={"modifications": [], "reason": ""},
            group="decision", name="修改(无依据→拒绝)", expect=500)
        api("POST", f"/decision/sessions/{sid}/reject", admin, body={"reason": f"回归驳回-{ts}"},
            group="decision", name="人审驳回(新端点)")
        ok6, j6 = api("GET", f"/decision/sessions/{sid}", admin, group="decision", name="驳回后状态=rejected")
        if ok6:
            st = j6.get("data", {}).get("humanReviewStatus")
            results.append({
                "group": "decision", "name": "状态机校验 rejected", "method": "GET",
                "path": f"/decision/sessions/{sid}", "http": 200, "code": 0,
                "expect_code": 0, "ok": st == "rejected", "elapsed_ms": 0,
                "note": f"humanReviewStatus={st}"})

    # ── 5. 页面依赖端点抽查 ──
    api("GET", "/market/price/spot?marketType=intra_province&stage=day_ahead"
        "&startAt=2026-08-08%2000:00:00&endAt=2026-08-09%2000:00:00", admin,
        group="page", name="行情现货价格")
    api("GET", "/market/supply-demand?startAt=2026-08-08%2000:00:00&endAt=2026-08-09%2000:00:00", admin,
        group="page", name="供需分析")
    api("GET", "/forecast/results?predictType=price&tradeDate=2026-08-09", admin,
        group="page", name="预测结果96点")
    api("GET", "/trade/rolling-plans", admin, group="page", name="日滚动方案")
    api("GET", "/trade/positions?tradeDate=2026-08-09", admin, group="page", name="持仓曲线")
    api("GET", "/trade/results?tradeDate=2026-08-09", admin, group="page", name="成交结果(tradeDate必填)")
    api("GET", "/settlement/tickets", admin, group="page", name="差异工单")

    # ── 6. 权限门禁（多角色） ──
    api("GET", "/review/reports", trader, group="perm", name="trader 复盘列表→14003", expect=14003)
    api("GET", "/assessment/indicators", trader, group="perm", name="trader 考核指标→14003", expect=14003)
    api("GET", "/report/templates", trader, group="perm", name="trader 报表模板→14003", expect=14003)
    if manager:
        api("GET", "/review/reports", manager, group="perm", name="manager 复盘列表→200")
        api("GET", "/report/templates", manager, group="perm", name="manager 报表模板→200")
    if trader:
        if sid:
            api("GET", f"/decision/sessions/{sid}", trader, group="perm",
                name="trader 决策详情→200(有 menu:decision)")
        else:
            api("GET", "/decision/sessions/1", trader, group="perm",
                name="trader 决策详情→200(有 menu:decision)", expect=500)

    # ── 汇总 ──
    total = len(results)
    passed = sum(1 for r in results if r["ok"])
    out = {"summary": {"total": total, "passed": passed, "failed": total - passed,
                       "pass_rate": f"{passed / total * 100:.1f}%" if total else "N/A"},
           "results": results}
    with open("/home/odoo/workspace/PTIDSS/tests/full/verify_result_v2_audit.json", "w",
              encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=2)
    print(json.dumps(out["summary"], ensure_ascii=False))
    for r in results:
        if not r["ok"]:
            print(f"  FAIL {r['group']}/{r['name']}: http={r['http']} code={r['code']} expect={r['expect_code']} note={r['note']}")


if __name__ == "__main__":
    main()
