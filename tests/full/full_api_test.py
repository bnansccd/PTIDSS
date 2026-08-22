#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
PTIDSS 全量接口测试（captcha.enabled=false 测试模式）
覆盖：认证 / 系统管理 / 12 业务域（market/trade/decision/settlement/ocr/review/
assessment/report/intel/policy/message/data/forecast/optimize/model/flow）
+ 权限门禁矩阵 + 边界校验
输出：JSON 结果文件（供独立测试验证报告使用）
"""
import requests
import json
import sys
import time
import io

BASE = "http://localhost:9080/ptidss"
REGION = "CN-32"
PWD = "Ptidss@2026"
results = []  # {group, name, method, path, status, code, expect_code, elapsed_ms, note}


def api(method, path, token=None, region=REGION, body=None, files=None, expect=0,
        group="", name="", fail_ok=False, raw_ok=False, params=None):
    """执行一次接口调用并断言。返回 (ok, resp_json)；raw_ok=CSV 等非 JSON 响应 http==200 即通过"""
    headers = {}
    if token:
        headers["Authorization"] = "Bearer " + token
    if region:
        headers["X-Region-Code"] = region
    url = BASE + path
    t0 = time.time()
    try:
        if files:
            r = requests.request(method, url, headers=headers, files=files, params=params, timeout=20)
        elif body is not None:
            r = requests.request(method, url, headers=headers, json=body, params=params, timeout=20)
        else:
            r = requests.request(method, url, headers=headers, params=params, timeout=20)
        elapsed = int((time.time() - t0) * 1000)
        try:
            j = r.json()
        except Exception:
            j = {"raw": r.text[:200]}
        code = j.get("code")
        if raw_ok:
            ok = (r.status_code == 200)
        else:
            ok = (r.status_code == 200 and code == expect)
        results.append({
            "group": group, "name": name, "method": method, "path": path,
            "http": r.status_code, "code": code, "expect": expect,
            "elapsed_ms": elapsed, "ok": ok,
            "note": "" if ok else json.dumps(j, ensure_ascii=False)[:300],
        })
        if not ok and not fail_ok:
            print(f"  [FAIL] {group} {name} {method} {path} -> http={r.status_code} code={code} {json.dumps(j, ensure_ascii=False)[:200]}")
        return ok, j
    except Exception as e:
        results.append({"group": group, "name": name, "method": method, "path": path,
                        "http": -1, "code": None, "expect": expect, "elapsed_ms": 0,
                        "ok": False, "note": str(e)[:300]})
        print(f"  [FAIL] {group} {name} {method} {path} -> EXC {e}")
        return False, {}


# 创建类接口返回的主键键名（契约各域命名不一：declarationId/sessionId/taskId/...）
ID_KEYS = ("id", "declarationId", "sessionId", "taskId", "reportId", "instanceId",
           "appealId", "processInstanceId")


def first_id(j):
    """从响应 data 提取主键：兼容 id / *Id 后缀键 / records 分页 / 数组 / 直接返回字符串"""
    data = j.get("data") if isinstance(j, dict) else None
    if isinstance(data, dict):
        for k in ("records", "list", "items", "rows"):
            if isinstance(data.get(k), list) and data[k]:
                rec = data[k][0]
                if isinstance(rec, dict):
                    for kk in ID_KEYS:
                        if kk in rec:
                            return rec.get(kk)
                return None
        for kk in ID_KEYS:
            if kk in data:
                return data.get(kk)
        if isinstance(data, str) and len(data) > 5:
            return data
    if isinstance(data, list) and data and isinstance(data[0], dict):
        for kk in ID_KEYS:
            if kk in data[0]:
                return data[0].get(kk)
    return None


def find_record(j, field, value):
    """从列表响应（records 分页或数组）中按字段值精确匹配记录"""
    data = j.get("data") if isinstance(j, dict) else None
    recs = None
    if isinstance(data, dict):
        for k in ("records", "list", "items", "rows"):
            if isinstance(data.get(k), list):
                recs = data[k]
                break
    elif isinstance(data, list):
        recs = data
    if not recs:
        return None
    for r in recs:
        if isinstance(r, dict) and str(r.get(field)) == str(value):
            return r
    return None


def first_field(j, field, key="id"):
    data = j.get("data") if isinstance(j, dict) else None
    if isinstance(data, dict):
        for k in ("records", "list", "items", "rows"):
            if isinstance(data.get(k), list) and data[k]:
                return data[k][0].get(field)
        return data.get(field)
    if isinstance(data, list) and data:
        return data[0].get(field)
    return None


def login(user):
    ok, j = api("POST", "/auth/login", body={"username": user, "password": PWD},
                group="auth", name=f"登录 {user}")
    if not ok:
        print(f"!! {user} 登录失败，终止")
        sys.exit(1)
    data = j.get("data") or {}
    tk = data.get("token") or data.get("accessToken") or data.get("access_token")
    if not tk and isinstance(data, str):
        try:
            tk = json.loads(data).get("token")
        except Exception:
            tk = None
    return tk


def main():
    print("=" * 70)
    print("PTIDSS 全量接口测试开始（captcha 测试模式）")
    print("=" * 70)

    # ---------- 认证 ----------
    print("\n[1] 认证")
    admin_tk = login("admin")
    trader_tk = login("trader01")
    settle_tk = login("settle01")
    manager_tk = login("manager01")
    api("GET", "/auth/current", token=admin_tk, group="auth", name="当前用户 admin")
    api("GET", "/auth/current", token=trader_tk, group="auth", name="当前用户 trader01")
    api("GET", "/auth/current", token=settle_tk, group="auth", name="当前用户 settle01")
    api("GET", "/auth/current", token=manager_tk, group="auth", name="当前用户 manager01")
    ok, j = api("POST", "/auth/login", body={"username": "admin", "password": "WrongPwd@999"},
        group="auth", name="错误密码被拒", expect=500, fail_ok=True)

    # ---------- 系统管理（admin） ----------
    print("\n[2] 系统管理（admin）")
    api("GET", "/admin/regions", token=admin_tk, group="admin", name="区域列表")
    api("GET", "/admin/users", token=admin_tk, group="admin", name="用户列表")
    api("GET", "/admin/roles", token=admin_tk, group="admin", name="角色列表")
    api("GET", "/admin/permissions", token=admin_tk, group="admin", name="权限列表")
    api("GET", "/admin/logs", token=admin_tk, group="admin", name="审计日志列表")
    # 新建区域→改→查→删（幂等清理；创建接口返回 data=null，需列表查询取 id）
    reg_code = "CN-TEST" + str(int(time.time()) % 100000)
    ok, j = api("POST", "/admin/regions", token=admin_tk,
                body={"regionCode": reg_code, "regionName": "测试区", "status": "enabled",
                      "marketSupport": ["spot"], "exchangeChannel": "rest",
                      "settlementPeriod": "natural_month", "launchOrder": 99},
                group="admin", name="新建区域")
    reg_id = None
    if ok:
        ok2, j2 = api("GET", "/admin/regions", token=admin_tk, group="admin", name="区域列表(取新建id)")
        rec = find_record(j2, "regionCode", reg_code)
        if rec:
            reg_id = rec.get("id")
    if reg_id:
        api("PUT", "/admin/regions", token=admin_tk,
            body={"id": reg_id, "regionCode": reg_code, "regionName": "测试区改", "status": "disabled"},
            group="admin", name="修改区域")
        api("DELETE", f"/admin/regions/{reg_id}", token=admin_tk, group="admin", name="删除区域")
    # 新建用户→改→删（创建接口返回 data=null，需 keyword 列表查询取 id）
    uname = "testu" + str(int(time.time()) % 100000)
    ok, j = api("POST", "/admin/users", token=admin_tk,
                body={"username": uname, "password": "Test@2026", "roleIds": [2],
                      "displayName": "测试用户", "regionCodes": ["CN-32"]},
                group="admin", name="新建用户")
    uid = None
    if ok:
        ok2, j2 = api("GET", "/admin/users", token=admin_tk, params={"keyword": uname},
                      group="admin", name="用户列表(取新建id)")
        rec = find_record(j2, "username", uname)
        if rec:
            uid = rec.get("id")
    if uid:
        api("PUT", "/admin/users", token=admin_tk,
            body={"id": uid, "displayName": "测试用户改", "roleIds": [2], "regionCodes": ["CN-32"], "status": "active"},
            group="admin", name="修改用户")
        api("PUT", f"/admin/users/{uid}/password", token=admin_tk,
            body={"password": "NewPass@2026"}, group="admin", name="重置密码")
        api("GET", f"/admin/users/{uid}/regions", token=admin_tk, group="admin", name="用户区域授权")
        api("DELETE", f"/admin/users/{uid}", token=admin_tk, group="admin", name="删除用户")
    # 角色权限树读→写回（全量写回幂等；注意 sys_role id=1 是 trader，切勿覆盖破坏权限）
    ok, j = api("GET", "/admin/roles", token=admin_tk, group="admin", name="角色列表(取id)")
    role_id = first_id(j)
    if role_id:
        ok2, j2 = api("GET", f"/admin/roles/{role_id}/permissions", token=admin_tk,
                      group="admin", name="角色权限树")
        pids = j2.get("data") if ok2 else None
        if isinstance(pids, list) and pids:
            api("PUT", f"/admin/roles/{role_id}/permissions", token=admin_tk,
                body={"permissionIds": pids}, group="admin", name="角色权限保存(全量写回)")
    ok, j = api("GET", "/admin/logs", token=admin_tk, params={"pageNo": 1, "pageSize": 5},
                group="admin", name="审计日志分页")
    log_id = first_id(j)
    if log_id:
        api("GET", f"/admin/logs/{log_id}", token=admin_tk, group="admin", name="审计日志详情")

    # ---------- 市场行情（trader01） ----------
    print("\n[3] 市场行情（trader01）")
    # 注：startAt/endAt 按系统实际约定 "yyyy-MM-dd HH:mm:ss" 传参（契约 format: date-time 与实现不一致，见报告）
    api("GET", "/market/price/spot", token=trader_tk,
        params={"marketType": "intra_province", "stage": "day_ahead",
                "startAt": "2026-08-20 00:00:00", "endAt": "2026-08-20 23:59:59"},
        group="market", name="现货 96 点")
    api("GET", "/market/price/midlong", token=trader_tk,
        params={"variety": "monthly", "startAt": "2026-08-01 00:00:00", "endAt": "2026-08-31 23:59:59"},
        group="market", name="中长期 24 点")
    api("GET", "/market/supply-demand", token=trader_tk,
        params={"startAt": "2026-08-20 00:00:00", "endAt": "2026-08-20 23:59:59"},
        group="market", name="供需平衡 96 点")
    api("GET", "/market/heatmap", token=trader_tk,
        params={"startDate": "2026-08-01", "endDate": "2026-08-07"},
        group="market", name="量价热力图")

    # ---------- 交易申报（trader01） ----------
    print("\n[4] 交易申报（trader01）")
    api("GET", "/trade/rolling-plans", token=trader_tk, group="trade", name="日滚动方案列表")
    ok, j = api("GET", "/trade/rolling-plans", token=trader_tk, group="trade", name="日滚动方案(取id)")
    plan_id = first_id(j)
    if plan_id:
        api("POST", f"/trade/rolling-plans/{plan_id}/confirm", token=trader_tk,
            group="trade", name="方案确认", fail_ok=True)
    api("GET", "/trade/declarations", token=trader_tk, group="trade", name="申报单列表")
    ok, j = api("POST", "/trade/declarations", token=trader_tk,
                body={"tradeDate": "2026-08-21", "marketType": "intra_province", "stage": "day_ahead",
                      "items": [{"segmentNo": 1, "price": 420.5, "volume": 120},
                                {"segmentNo": 2, "price": 435.0, "volume": 80}]},
                group="trade", name="创建申报单")
    decl_id = first_id(j)
    if decl_id:
        api("POST", f"/trade/declarations/{decl_id}/submit", token=trader_tk,
            group="trade", name="提交申报单")
    api("GET", "/trade/results", token=trader_tk,
        params={"tradeDate": "2026-08-21", "marketType": "intra_province"},
        group="trade", name="成交结果")
    api("GET", "/trade/positions", token=trader_tk,
        params={"tradeDate": "2026-08-21"},
        group="trade", name="持仓曲线")
    # 合规预检边界：段数超限（软校验：code=0 且 complianceCheck.passed=false）
    ok, j = api("POST", "/trade/declarations", token=trader_tk,
        body={"tradeDate": "2026-08-21", "marketType": "intra_province", "stage": "day_ahead",
              "items": [{"segmentNo": i, "price": 400 + i, "volume": 10} for i in range(1, 30)]},
        group="trade", name="合规预检(段数超限 passed=false)", fail_ok=True)
    cc = (j.get("data") or {}).get("complianceCheck") or {}
    passed_soft = (j.get("code") == 0 and cc.get("passed") is False
                   and any("段数" in str(v) for v in (cc.get("violations") or [])))
    results[-1]["ok"] = passed_soft
    results[-1]["note"] = "" if passed_soft else json.dumps(j, ensure_ascii=False)[:200]
    print(f"  {'[PASS]' if passed_soft else '[FAIL]'} trade 合规预检(段数超限 passed=false)")

    # ---------- 辅助决策（trader01） ----------
    print("\n[5] 辅助决策（trader01）")
    ok, j = api("POST", "/decision/sessions", token=trader_tk,
                body={"sessionType": "spot_quote", "tradeDate": "2026-08-21", "scenario": "baseline",
                      "agents": ["forecast", "quote", "risk", "compliance", "settlement", "review"]},
                group="decision", name="创建决策会话")
    sid = first_id(j)
    if sid:
        api("GET", f"/decision/sessions/{sid}", token=trader_tk, group="decision", name="会话详情")
        api("POST", f"/decision/sessions/{sid}/confirm", token=trader_tk,
            group="decision", name="人机确认")
        api("POST", f"/decision/sessions/{sid}/modify", token=trader_tk,
            body={"modifications": [{"segmentNo": 1, "price": 430.0}], "reason": "人工调整",
                  "secondReviewer": "manager01"},
            group="decision", name="人工修改")
        api("GET", f"/decision/sessions/{sid}/evidence", token=trader_tk,
            group="decision", name="依据链回溯")
    else:
        print("  [SKIP] decision 会话创建失败，后续用例跳过")

    # ---------- 结算管理（settle01） ----------
    print("\n[6] 结算管理（settle01）")
    api("GET", "/settlement/records", token=settle_tk, params={"period": "2026-08"},
        group="settlement", name="结算记录列表")
    ok, j = api("GET", "/settlement/records", token=settle_tk, params={"period": "2026-08"},
                group="settlement", name="结算记录(取id)")
    rec_id = first_id(j)
    if rec_id:
        api("POST", f"/settlement/records/{rec_id}/reconcile", token=settle_tk,
            group="settlement", name="发起核对")
    api("GET", "/settlement/tickets", token=settle_tk, group="settlement", name="差异工单列表")
    ok, j = api("GET", "/settlement/tickets", token=settle_tk,
                group="settlement", name="差异工单(核对后取id)")
    tick_id = first_id(j)
    if tick_id:
        api("POST", f"/settlement/tickets/{tick_id}/process", token=settle_tk,
            body={"action": "assign", "handler": "settle01", "comment": "分配处理"},
            group="settlement", name="工单分配(assign)", fail_ok=True)
        api("POST", f"/settlement/tickets/{tick_id}/process", token=settle_tk,
            body={"action": "close", "handler": "settle01", "comment": "非法跳转"},
            group="settlement", name="工单非法流转被拒", expect=500, fail_ok=True)

    # ---------- OCR 结算单识别（settle01） ----------
    print("\n[7] OCR（settle01）")
    api("GET", "/ocr/tasks", token=settle_tk, group="ocr", name="OCR 任务列表")
    png = io.BytesIO(b"\x89PNG\r\n\x1a\n" + b"\x00" * 64)
    ok, j = api("POST", "/ocr/tasks", token=settle_tk,
                files={"file": ("settlement.png", png, "image/png")},
                group="ocr", name="上传结算单识别")
    ocr_id = first_id(j)
    if ocr_id:
        api("GET", f"/ocr/tasks/{ocr_id}", token=settle_tk, group="ocr", name="识别结果")
        api("POST", f"/ocr/tasks/{ocr_id}/review", token=settle_tk,
            body={"approved": True, "comment": "人工复核通过"},
            group="ocr", name="低置信人工复核")

    # ---------- 复盘考核（manager01） ----------
    print("\n[8] 复盘考核（manager01）")
    ok, j = api("POST", "/review/reports", token=manager_tk,
                body={"reportType": "weekly", "startDate": "2026-08-10", "endDate": "2026-08-16",
                      "focusTopics": ["预测偏差", "申报策略"]},
                group="review", name="生成复盘报告")
    rpt_id = first_id(j)
    if rpt_id:
        api("GET", f"/review/reports/{rpt_id}", token=manager_tk, group="review", name="报告详情")
    api("POST", "/review/strategy-feedback", token=manager_tk,
        body={"strategyCode": "STRAT-DEMO-001", "feedback": "effective", "updatedParams": {"riskLevel": "medium"}},
        group="review", name="策略回流", fail_ok=True)
    api("GET", "/assessment/indicators", token=manager_tk, group="assessment", name="考核指标")
    api("GET", "/assessment/results", token=manager_tk, params={"period": "2026-08"},
        group="assessment", name="考核结果")
    ok, j = api("GET", "/assessment/results", token=manager_tk, params={"period": "2026-08"},
                group="assessment", name="考核结果(取id)")
    res_id = first_id(j)
    if res_id:
        ok2, j2 = api("POST", "/assessment/appeals", token=manager_tk,
                      body={"resultId": res_id, "appealReason": "考核口径有误"},
                      group="assessment", name="提交申诉")
        app_id = first_id(j2)
        if app_id:
            api("POST", f"/assessment/appeals/{app_id}/process", token=manager_tk,
                body={"decision": "approve", "comment": "申诉成立"}, group="assessment", name="处理申诉")

    # ---------- 报表中心（manager01） ----------
    print("\n[9] 报表中心（manager01）")
    api("GET", "/report/templates", token=manager_tk, group="report", name="报表模板")
    ok, j = api("GET", "/report/templates", token=manager_tk, group="report", name="报表模板(取code)")
    tpl_code = first_field(j, "code")
    if tpl_code:
        ok2, j2 = api("POST", "/report/instances", token=manager_tk,
                      body={"templateCode": tpl_code, "period": "2026-08", "format": "csv"},
                      group="report", name="生成报表实例")
        inst_id = first_id(j2)
        if inst_id:
            api("GET", f"/report/instances/{inst_id}/export", token=manager_tk,
                group="report", name="CSV 导出", fail_ok=True, raw_ok=True)
    api("GET", "/report/instances", token=manager_tk, params={"period": "2026-08"},
        group="report", name="报表实例列表")

    # ---------- 情报中心（trader01） ----------
    print("\n[10] 情报中心（trader01）")
    api("GET", "/intel/news", token=trader_tk, group="intel", name="情报流")
    api("GET", "/intel/sources", token=trader_tk, group="intel", name="情报源台账")
    api("GET", "/intel/push-rules", token=trader_tk, group="intel", name="推送规则列表")
    api("POST", "/intel/push-rules", token=trader_tk,
        body={"ruleName": "测试规则", "matchTags": ["现货价格"], "importance": "high",
              "targets": [{"role": "trader", "channel": "sms"}]},
        group="intel", name="新建推送规则", fail_ok=True)

    # ---------- 政策中心（trader01） ----------
    print("\n[11] 政策中心（trader01）")
    ok, j = api("GET", "/policy/list", token=trader_tk, group="policy", name="政策列表")
    pol_id = first_id(j)
    if pol_id:
        api("GET", f"/policy/{pol_id}", token=trader_tk, group="policy", name="政策详情")
        api("POST", "/policy/parse", token=trader_tk, body={"policyId": pol_id},
            group="policy", name="政策解析(幂等)")
        api("POST", "/policy/parse", token=trader_tk, body={"policyId": pol_id, "reparse": True},
            group="policy", name="reparse 强制重建")
        api("GET", f"/policy/{pol_id}/brief", token=trader_tk, group="policy", name="CSV 简报导出",
            fail_ok=True, raw_ok=True)
    else:
        print("  [SKIP] policy 列表为空")

    # ---------- 消息中心（trader01） ----------
    print("\n[12] 消息中心（trader01）")
    ok, j = api("GET", "/message/list", token=trader_tk, group="message", name="消息列表")
    msg_id = first_id(j)
    if msg_id:
        api("POST", f"/message/{msg_id}/read", token=trader_tk, group="message", name="标记已读")

    # ---------- 数据底座（manager01） ----------
    print("\n[13] 数据底座（manager01）")
    api("GET", "/data/sources", token=manager_tk, group="data", name="数据源台账")
    api("POST", "/data/collect-tasks", token=manager_tk,
        body={"taskType": "market", "force": True},
        group="data", name="手动采集(market force)")
    api("POST", "/data/collect-tasks", token=manager_tk,
        body={"taskType": "weather", "force": False},
        group="data", name="手动采集(weather)")
    api("POST", "/data/collect-tasks", token=manager_tk,
        body={"taskType": "NOT_EXIST", "force": True},
        group="data", name="非法 taskType 被拒", expect=500, fail_ok=True)
    api("GET", "/data/quality/report", token=manager_tk, group="data", name="质量报告")
    api("GET", "/data/lineage", token=manager_tk, group="data", name="数据血缘")

    # ---------- 预测中心（trader01） ----------
    print("\n[14] 预测中心（trader01）")
    api("GET", "/forecast/models", token=trader_tk, group="forecast", name="模型注册列表")
    ok, j = api("POST", "/forecast/tasks", token=trader_tk,
                body={"modelCode": "price", "predictDate": "2026-08-21", "marketType": "intra_province"},
                group="forecast", name="创建预测任务(price)")
    ftask = first_id(j)
    if ftask:
        api("GET", f"/forecast/tasks/{ftask}", token=trader_tk, group="forecast", name="任务状态")
    api("POST", "/forecast/tasks", token=trader_tk,
        body={"modelCode": "generation", "predictDate": "2026-08-21", "regionCode": "CN-32"},
        group="forecast", name="创建预测任务(generation)")
    api("GET", "/forecast/results", token=trader_tk,
        params={"predictType": "price", "tradeDate": "2026-08-21"},
        group="forecast", name="预测结果(96点区间)")
    api("POST", "/forecast/models/train", token=trader_tk,
        body={"modelCode": "load", "mode": "daily_increment"},
        group="forecast", name="触发模型训练")
    api("POST", "/forecast/tasks", token=trader_tk,
        body={"modelCode": "unknown_model", "predictDate": "2026-08-21"},
        group="forecast", name="非法 modelCode 被拒", expect=500, fail_ok=True)

    # ---------- 联合优化（trader01） ----------
    print("\n[15] 联合优化（trader01）")
    ok, j = api("POST", "/optimize/joint-tasks", token=trader_tk,
                body={"taskType": "daily", "horizonDays": 3, "scenarioCount": 100},
                group="optimize", name="创建联合优化任务")
    jt_id = first_id(j)
    if jt_id:
        api("GET", f"/optimize/joint-tasks/{jt_id}", token=trader_tk, group="optimize", name="任务详情")
    api("POST", "/optimize/backtests", token=trader_tk,
        body={"strategyCode": "STRAT-DEMO-001", "startDate": "2026-07-01", "endDate": "2026-07-31"},
        group="optimize", name="策略回测", fail_ok=True)
    api("GET", "/optimize/strategies", token=trader_tk, group="optimize", name="策略库")
    api("POST", "/optimize/joint-tasks", token=trader_tk,
        body={"taskType": "daily", "horizonDays": 8},
        group="optimize", name="horizonDays=8 被拒", expect=500, fail_ok=True)

    # ---------- 模型平台（admin，analyst 无种子账号） ----------
    print("\n[16] 模型平台（admin）")
    ok, j = api("GET", "/model/registry", token=admin_tk, group="model", name="模型注册表")
    m_code = first_field(j, "modelCode")
    m_ver = first_field(j, "modelVersion") or first_field(j, "version")
    if m_code:
        api("POST", "/model/inference", token=admin_tk,
            body={"modelCode": m_code, "input": {"date": "2026-08-21", "hour": 8}},
            group="model", name="在线推理")
    if m_ver:
        api("POST", "/model/evaluate", token=admin_tk,
            body={"modelVersion": m_ver, "testSetVersion": "TS-2026Q3"},
            group="model", name="离线评估", fail_ok=True)
    api("POST", "/model/inference", token=admin_tk,
        body={"modelCode": "NOT_EXIST", "input": {}},
        group="model", name="非法 modelCode 被拒", expect=500, fail_ok=True)

    # ---------- 审批流（trader01 含 mobile 角色） ----------
    print("\n[17] 审批流（trader01）")
    ok, j = api("POST", "/flow/start", token=trader_tk,
                body={"processKey": "settlement_ticket_review", "bizId": "TICKET-TEST-001",
                      "variables": {"amount": 10000, "urgency": "medium"}},
                group="flow", name="发起流程实例")
    f_id = first_id(j)
    if f_id:
        api("GET", f"/flow/instances/{f_id}", token=trader_tk, group="flow", name="实例详情(含待办)")
    api("POST", "/flow/start", token=trader_tk,
        body={"processKey": "settlement_ticket_review", "bizId": "TICKET-TEST-001",
              "variables": {"amount": 10000}},
        group="flow", name="同单据幂等")
    api("POST", "/flow/start", token=trader_tk,
        body={"processKey": "NOT_REGISTERED", "bizId": "X-001"},
        group="flow", name="未注册 processKey 被拒", expect=500, fail_ok=True)

    # ---------- 权限门禁矩阵（负向） ----------
    print("\n[18] 权限门禁矩阵")
    api("GET", "/data/sources", token=trader_tk, group="gate", name="trader01×data 拒",
        expect=14003, fail_ok=True)
    api("GET", "/model/registry", token=trader_tk, group="gate", name="trader01×model 拒",
        expect=14003, fail_ok=True)
    api("POST", "/forecast/tasks", token=manager_tk,
        body={"modelCode": "price", "predictDate": "2026-08-21"},
        group="gate", name="manager01×forecast 拒", expect=14003, fail_ok=True)
    api("GET", "/model/registry", token=manager_tk, group="gate", name="manager01×model 拒",
        expect=14003, fail_ok=True)
    api("GET", "/market/price/spot", token=settle_tk, group="gate", name="settle01×market 拒",
        params={"marketType": "intra_province", "stage": "day_ahead",
                "startAt": "2026-08-20 00:00:00", "endAt": "2026-08-20 23:59:59"},
        expect=14003, fail_ok=True)
    api("GET", "/data/sources", token=None, group="gate", name="未带 token 拒",
        expect=14001, fail_ok=True)
    api("GET", "/message/list", token=admin_tk, group="gate", name="admin×message 放行",
        expect=0, fail_ok=True)

    # ---------- 登出（最后执行，避免注销主 token） ----------
    api("POST", "/auth/logout", token=trader_tk, group="auth", name="登出 trader01")

    # ---------- 汇总 ----------
    total = len(results)
    passed = sum(1 for r in results if r["ok"])
    failed = total - passed
    print("\n" + "=" * 70)
    print(f"测试汇总：共 {total} 项，通过 {passed}，失败 {failed}")
    print("=" * 70)
    if failed:
        print("\n失败明细：")
        for r in results:
            if not r["ok"]:
                print(f"  [{r['group']}] {r['name']} {r['method']} {r['path']} -> {r['note']}")
    # 保存结果
    out = {
        "generated_at": time.strftime("%Y-%m-%d %H:%M:%S"),
        "base": BASE,
        "total": total, "passed": passed, "failed": failed,
        "results": results,
    }
    with open("/home/odoo/workspace/PTIDSS/tests/full/result_full_api.json", "w", encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=2)
    print("结果已保存：PTIDSS/tests/full/result_full_api.json")
    return 0 if failed == 0 else 2


if __name__ == "__main__":
    sys.exit(main())
