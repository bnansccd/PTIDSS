"""
PTIDSS 模拟用户全量功能测试（captcha.enabled=false 测试模式）
按业务逻辑模拟用户（admin 全权限主流程 + trader01/settle01/manager01 角色门禁），
覆盖 19 菜单域（对齐原型左侧 5 分组）带模拟数据全量验证：
  总览：    决策驾驶舱（当前用户/权限）
  行情与预测：市场行情（现货96点/中长期/供需/热力）、预测中心（任务/结果/模型）、数据管理（源/质量/血缘/采集）
  决策与交易：智能决策（编排/详情/依据链/情报重评/确认/状态机）、交易申报（方案/申报/持仓/成交）、
            联合优化（任务/回测/策略库）、智能体管理（注册表/绑定/运行记录/指标）、模型平台（注册/训练/推理96点/评估）
  结算与复盘：结算（台账/核对/差异工单处理）、复盘（报告/策略回流）、成效考核（指标/结果/申诉处理）、报表（模板/实例/导出）
  政策与系统：情报中心（源/新闻/推送规则/执行→触发重评）、政策中心（列表/解析）、消息中心（列表/已读）、
            数据底座（OCR任务）、审批流（发起）、系统管理（用户/角色/权限/区域/审计日志）
  V2.1.1 新增回归：情报触发式重算（intel-reassess 端点 + 推送联动）、模型推理深度（96点序列/置信度/指标）、
            模型接入会话（forecast 绑 price → mode=model + 序列摘要 + 置信度覆盖）
输出：verify_result_full_user.json
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


def check(group, name, cond, note):
    results.append({"group": group, "name": name, "method": "-", "path": "-",
                    "http": 200, "code": 0, "expect_code": 0, "ok": bool(cond),
                    "elapsed_ms": 0, "note": str(note)[:200]})


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
    settle = login("settle01")
    manager = login("manager01")
    ts = int(time.time() * 1000) % 100000
    day = "2026-08-22"
    yesterday = "2026-08-21"

    # ================= 总览组 =================
    ok, j = api("GET", "/auth/current", trader, group="总览", name="决策驾驶舱-当前用户权限")
    if ok:
        perms = j.get("data", {}).get("permissions") or []
        check("总览", "决策驾驶舱-权限码非空", len(perms) >= 10, f"perms={len(perms)}")

    # ================= 行情与预测组 =================
    # 市场行情
    api("GET", "/market/price/spot?marketType=intra_province&stage=day_ahead"
        f"&startAt={day}%2000:00:00&endAt={day}%2023:59:59", trader,
        group="行情与预测", name="市场行情-现货96点")
    api("GET", "/market/price/midlong?variety=electricity&startAt=2026-08-01%2000:00:00&endAt=2026-08-31%2023:59:59",
        trader, group="行情与预测", name="市场行情-中长期价格")
    api("GET", f"/market/supply-demand?startAt={day}%2000:00:00&endAt={day}%2023:59:59", trader,
        group="行情与预测", name="市场行情-供需平衡")
    api("GET", "/market/heatmap?startDate=2026-08-21&endDate=2026-08-22", trader,
        group="行情与预测", name="市场行情-区域热力")
    # 预测中心
    ok, j = api("GET", f"/forecast/results?predictType=price&tradeDate={day}", trader,
                group="行情与预测", name="预测中心-96点结果")
    if ok:
        data = j.get("data") or []
        pts = data if isinstance(data, list) else (data.get("points") or [])
        check("行情与预测", "预测中心-96点完整性", len(pts) >= 96, f"points={len(pts)}")
    api("GET", "/forecast/models", admin, group="行情与预测", name="预测中心-模型注册")
    ok, j = api("POST", "/forecast/tasks", admin,
                body={"modelCode": "price", "predictDate": day, "marketType": "intra_province"},
                group="行情与预测", name="预测中心-创建任务")
    if ok and j.get("data", {}).get("taskId"):
        api("GET", f"/forecast/tasks/{j['data']['taskId']}", admin,
            group="行情与预测", name="预测中心-任务详情")
    # 数据管理
    api("GET", "/data/sources", admin, group="行情与预测", name="数据管理-数据源台账")
    api("GET", "/data/quality/report?date=2026-08-22", admin, group="行情与预测", name="数据管理-质量报告")
    api("GET", "/data/lineage", admin, group="行情与预测", name="数据管理-血缘追踪")
    api("POST", "/data/collect-tasks", admin,
        body={"taskType": "market", "force": False},
        group="行情与预测", name="数据管理-采集任务")

    # ================= 决策与交易组 =================
    # 智能决策：完整业务流（编排 → 详情 → 情报重评 → 依据链 → 确认 → 状态机）
    ok, j = api("POST", "/decision/sessions", trader,
                body={"sessionType": "spot_quote", "tradeDate": day, "scenario": "aggressive"},
                group="决策与交易", name="智能决策-发起会话(激进)")
    sid = j.get("data", {}).get("sessionId") if ok else None
    if sid:
        ok, j = api("GET", f"/decision/sessions/{sid}", trader, group="决策与交易", name="智能决策-会话详情")
        if ok:
            st = j.get("data", {})
            check("决策与交易", "智能决策-降级标注", st.get("evidenceSummary", {}).get("degraded") is True,
                  f"degraded={st.get('evidenceSummary', {}).get('degraded')}")
        # 情报触发式重算（新端点）：评分快照更新 + note 留痕
        ok, j = api("POST", f"/decision/sessions/{sid}/intel-reassess", trader,
                    group="决策与交易", name="智能决策-情报重评(pending)")
        if ok:
            st = j.get("data", {}).get("finalStrategy") or {}
            intel = st.get("intel") or {}
            check("决策与交易", "智能决策-重评后情报快照", "score" in intel and "window" in intel,
                  f"intel={json.dumps(intel, ensure_ascii=False)[:120]}")
            check("决策与交易", "智能决策-重评留痕note", "情报变更重评" in str(st.get("note", "")),
                  str(st.get("note", ""))[:100])
        # 依据链
        api("GET", f"/decision/sessions/{sid}/evidence", trader, group="决策与交易", name="智能决策-依据链回溯")
        # 确认后重评/重复确认 → 拒绝（状态机）
        api("POST", f"/decision/sessions/{sid}/confirm", trader, group="决策与交易", name="智能决策-人审确认")
        api("POST", f"/decision/sessions/{sid}/intel-reassess", trader,
            group="决策与交易", name="智能决策-确认后重评→拒绝", expect=500)
        api("POST", f"/decision/sessions/{sid}/confirm", trader,
            group="决策与交易", name="智能决策-重复确认→拒绝", expect=500)
    # 智能体运行记录
    api("GET", "/agent/runs?pageNo=1&pageSize=10", trader, group="决策与交易", name="智能体-运行记录")
    # 交易申报（业务流：方案列表 → 申报 → 提交 → 持仓/成交）
    api("GET", f"/trade/rolling-plans?tradeDate={day}", trader, group="决策与交易", name="交易申报-日滚动方案")
    ok, j = api("POST", "/trade/declarations", trader,
                body={"tradeDate": day, "marketType": "intra_province", "stage": "day_ahead",
                      "items": [{"segmentNo": 1, "price": 420.5, "volume": 80}]},
                group="决策与交易", name="交易申报-创建申报单")
    api("GET", "/trade/declarations", trader, group="决策与交易", name="交易申报-申报列表")
    ok, j = api("GET", "/trade/declarations?pageNo=1&pageSize=5", trader,
                group="决策与交易", name="交易申报-申报列表(分页)")
    if ok:
        rows = j.get("data") or {}
        decl = (rows if isinstance(rows, list) else (rows.get("records") or []))
        if decl:
            did = decl[0].get("id") or decl[0].get("declarationId")
            if did:
                api("POST", f"/trade/declarations/{did}/submit", trader,
                    group="决策与交易", name="交易申报-提交申报")
    api("GET", f"/trade/positions?tradeDate={day}", trader, group="决策与交易", name="交易申报-持仓曲线")
    api("GET", f"/trade/results?tradeDate={yesterday}", trader, group="决策与交易", name="交易申报-成交结果")
    # 联合优化
    ok, j = api("POST", "/optimize/joint-tasks", trader,
                body={"taskType": "daily", "horizonDays": 3, "scenarioCount": 100,
                      "objectiveWeights": {"revenue": 1}, "constraints": {}},
                group="决策与交易", name="联合优化-创建任务(MILP)")
    if ok and j.get("data", {}).get("taskId"):
        api("GET", f"/optimize/joint-tasks/{j['data']['taskId']}", trader,
            group="决策与交易", name="联合优化-任务详情")
    api("POST", "/optimize/backtests", trader,
        body={"strategyCode": "STRAT-DA-PRICE", "startDate": "2026-06-01", "endDate": "2026-07-31"},
        group="决策与交易", name="联合优化-策略回测")
    api("GET", "/optimize/strategies?pageNo=1&pageSize=10", trader,
        group="决策与交易", name="联合优化-策略库")
    # 智能体管理
    ok, j = api("GET", "/agent/registry", trader, group="决策与交易", name="智能体-注册表7个")
    if ok:
        codes = [a["agentCode"] for a in j.get("data", [])]
        check("决策与交易", "智能体-7大智能体齐全",
              sorted(codes) == sorted(["compliance", "forecast", "market", "quote", "review", "risk", "settlement"]),
              codes)
    api("GET", "/agent/metrics", trader, group="决策与交易", name="智能体-效果评估")
    # 模型平台：推理深度（96点序列/置信度/指标）
    ok, j = api("POST", "/model/inference", admin,
                body={"modelCode": "price", "input": {"date": day, "features": ["load", "weather"]}, "temperature": 0.3},
                group="决策与交易", name="模型平台-在线推理")
    if ok:
        d = j.get("data", {})
        out = d.get("output") or {}
        series = out.get("predictionSeries") or []
        stats = out.get("seriesStats") or {}
        check("决策与交易", "模型平台-96点预测序列", len(series) == 96, f"points={len(series)}")
        check("决策与交易", "模型平台-序列摘要+置信度",
              stats.get("avg") and d.get("confidence") and d.get("metrics"),
              f"avg={stats.get('avg')} conf={d.get('confidence')}")
    api("POST", "/model/evaluate", admin,
        body={"modelVersion": "v1.0.0", "testSetVersion": "v2026-07"},
        group="决策与交易", name="模型平台-离线评估")
    api("POST", "/forecast/models/train", admin,
        body={"modelCode": "price", "mode": "daily_increment"},
        group="决策与交易", name="模型平台-触发训练")
    api("GET", "/model/registry", admin, group="决策与交易", name="模型平台-注册表")
    # 模型接入会话：forecast 绑定 price 在线模型 → mode=model + 序列摘要 + 置信度覆盖
    ok, j = api("GET", "/agent/registry", trader, group="决策与交易", name="智能体-绑定前置查询")
    if ok:
        forecast = next((a for a in j.get("data", []) if a["agentCode"] == "forecast"), None)
        if forecast:
            api("POST", f"/agent/registry/{forecast['id']}/model-config", trader,
                body={"modelCode": "price"}, group="决策与交易", name="智能体-forecast绑定price模型")
            ok2, j2 = api("POST", "/decision/sessions", trader,
                          body={"sessionType": "rolling", "tradeDate": yesterday, "scenario": "baseline"},
                          group="决策与交易", name="智能决策-模型接入会话(rolling)")
            if ok2:
                sid2 = j2["data"]["sessionId"]
                ok3, j3 = api("GET", f"/decision/sessions/{sid2}/evidence", trader,
                              group="决策与交易", name="智能决策-模型接入依据链")
                if ok3:
                    fr = next((a for a in j3.get("data", {}).get("agents", [])
                               if a.get("agentCode") == "forecast"), None)
                    check("决策与交易", "模型接入-forecast mode=model",
                          fr and fr.get("mode") == "model" and fr.get("modelVersion"),
                          json.dumps(fr, ensure_ascii=False)[:150] if fr else "no forecast run")
                    check("决策与交易", "模型接入-输出含96点序列摘要",
                          fr and "预测序列 96 点" in str(fr.get("output", "")),
                          str(fr.get("output", ""))[:100] if fr else "")
                    check("决策与交易", "模型接入-置信度覆盖为模型值",
                          fr and 0.80 <= float(fr.get("confidence", 0)) <= 0.95,
                          f"conf={fr.get('confidence') if fr else None}")
                # 清理：解绑恢复幂等基线
                api("POST", f"/agent/registry/{forecast['id']}/model-config", trader,
                    body={"modelCode": ""}, group="决策与交易", name="智能体-解绑恢复")
        # 绑定不存在模型 → 拒绝（防静默降级）
        quote = next((a for a in j.get("data", []) if a["agentCode"] == "quote"), None)
        if quote:
            api("POST", f"/agent/registry/{quote['id']}/model-config", trader,
                body={"modelCode": "nonexist-model"}, group="决策与交易",
                name="智能体-绑定不存在模型→拒绝", expect=500)

    # ================= 结算与复盘组 =================
    # 结算
    ok, j = api("GET", "/settlement/tickets?pageNo=1&pageSize=10", settle,
                group="结算与复盘", name="结算-差异工单列表")
    if ok:
        rows = j.get("data") or {}
        ticket = (rows if isinstance(rows, list) else (rows.get("records") or []))
        if ticket:
            tid = ticket[0].get("id") or ticket[0].get("ticketId")
            if tid:
                api("POST", f"/settlement/tickets/{tid}/process", settle,
                    body={"action": "confirm", "remark": f"回归处理-{ts}"},
                    group="结算与复盘", name="结算-工单处理")
    api("GET", "/settlement/records?period=2026-08&pageNo=1&pageSize=10", settle,
        group="结算与复盘", name="结算-台账")
    ok, j = api("GET", "/settlement/records?period=2026-08", settle,
                group="结算与复盘", name="结算-核对记录")
    if ok:
        rows = j.get("data") or {}
        rec = (rows if isinstance(rows, list) else (rows.get("records") or []))
        if rec:
            rid = rec[0].get("id") or rec[0].get("recordId")
            if rid:
                api("POST", f"/settlement/records/{rid}/reconcile", settle,
                    group="结算与复盘", name="结算-人工核对")
    # 复盘
    api("GET", "/review/reports", manager, group="结算与复盘", name="复盘-报告列表")
    ok, j = api("POST", "/review/reports", manager,
                body={"reportType": "weekly", "startDate": "2026-08-10", "endDate": "2026-08-16",
                      "focusTopics": ["现货价格波动"]},
                group="结算与复盘", name="复盘-生成周报")
    api("POST", "/review/strategy-feedback", manager,
        body={"strategyCode": "STRAT-DA-PRICE", "feedback": "effective"},
        group="结算与复盘", name="复盘-策略回流")
    # 成效考核
    api("GET", "/assessment/indicators", manager, group="结算与复盘", name="考核-指标体系")
    ok, j = api("GET", "/assessment/results?period=2026-08", manager,
                group="结算与复盘", name="考核-结果")
    if ok:
        rows = j.get("data") or []
        if rows:
            ar_id = rows[0].get("id")
            if ar_id:
                ok, j = api("POST", "/assessment/appeals", manager,
                            body={"resultId": ar_id, "appealReason": f"全量回归申诉-{ts}"},
                            group="结算与复盘", name="考核-申诉")
                if ok:
                    ap = j.get("data") or {}
                    ap_id = ap[0].get("id") if isinstance(ap, list) and ap else ap.get("id")
                    if ap_id:
                        api("POST", f"/assessment/appeals/{ap_id}/process", manager,
                            body={"decision": "approve", "comment": f"回归处理-{ts}"},
                            group="结算与复盘", name="考核-申诉处理")
    # 报表
    ok, j = api("GET", "/report/templates", manager, group="结算与复盘", name="报表-模板")
    tpl = j["data"][0].get("code") if ok and j.get("data") else None
    if tpl:
        api("POST", "/report/instances", manager,
            body={"templateCode": tpl, "period": "2026-08", "format": "csv"},
            group="结算与复盘", name="报表-生成实例")
    ok, j = api("GET", "/report/instances", manager, group="结算与复盘", name="报表-实例列表")
    if ok:
        rows = j.get("data") or {}
        inst = (rows if isinstance(rows, list) else (rows.get("records") or []))
        if inst:
            iid = inst[0].get("id") or inst[0].get("instanceId")
            if iid:
                api("GET", f"/report/instances/{iid}/export", manager,
                    group="结算与复盘", name="报表-导出", raw_ok=True)

    # ================= 政策与系统组 =================
    # 情报中心（含推送规则 → 执行 → 触发式重算联动）
    api("GET", "/intel/sources", admin, group="政策与系统", name="情报-数据源台账")
    api("POST", "/intel/sources", admin,
        body={"sourceCode": f"INTL-TEST-{ts}", "sourceName": f"回归测试源-{ts}", "intelType": "opinion",
              "fetchMode": "api", "frequency": "1 小时"},
        group="政策与系统", name="情报-注册数据源")
    api("GET", "/intel/news?pageNo=1&pageSize=10", admin, group="政策与系统", name="情报-新闻流分页")
    api("POST", "/intel/push-rules", admin,
        body={"ruleName": f"回归推送规则-{ts}", "matchTags": ["价格", "供需"], "importance": "high",
              "targets": ["trader", "analyst"]},
        group="政策与系统", name="情报-创建推送规则")
    api("GET", "/intel/push-rules", admin, group="政策与系统", name="情报-推送规则列表")
    ok, j = api("POST", "/intel/push-rules/execute", admin,
                group="政策与系统", name="情报-执行推送(high触发决策重评)")
    if ok:
        d = j.get("data") or {}
        check("政策与系统", "情报-推送结果含重评计数", "intelReassessed" in d,
              f"matched={d.get('matchedNews')} pushed={d.get('pushedMessages')} reassessed={d.get('intelReassessed')}")
    # 政策中心（业务流：上传登记 → 解析提取）
    api("GET", "/policy/list", admin, group="政策与系统", name="政策-规则列表")
    ok, j = api("POST", "/policy/upload", admin,
                body={"title": f"回归政策文件-{ts}", "category": "provincial",
                      "tags": ["申报", "考核"], "publishDate": "2026-08-01",
                      "effectiveDate": "2026-09-01"},
                group="政策与系统", name="政策-上传登记")
    if ok and j.get("data"):
        pid = j["data"].get("id") or j["data"].get("policyId")
        if pid:
            api("POST", "/policy/parse", admin, body={"policyId": str(pid)},
                group="政策与系统", name="政策-解析提取条款")
    # 消息中心
    ok, j = api("GET", "/message/list", trader, group="政策与系统", name="消息-列表")
    if ok:
        rows = j.get("data") or {}
        msgs = (rows if isinstance(rows, list) else (rows.get("records") or []))
        if msgs:
            mid = msgs[0].get("id")
            if mid:
                api("POST", f"/message/{mid}/read", trader, group="政策与系统", name="消息-标记已读")
    # 数据底座
    api("GET", "/ocr/tasks", settle, group="政策与系统", name="数据底座-OCR任务")
    # 审批流
    api("POST", "/flow/start", trader,
        body={"processKey": "declaration_approve", "bizId": f"DEC-{ts}", "variables": {"amount": 100000}},
        group="政策与系统", name="审批流-发起申报审批")
    # 系统管理
    api("GET", "/admin/users", admin, group="政策与系统", name="系统管理-用户列表")
    api("GET", "/admin/roles", admin, group="政策与系统", name="系统管理-角色列表")
    api("GET", "/admin/permissions", admin, group="政策与系统", name="系统管理-权限清单")
    api("GET", "/admin/regions", admin, group="政策与系统", name="系统管理-区域")
    api("GET", "/admin/logs", admin, group="政策与系统", name="系统管理-审计日志")

    # ================= 角色门禁（真实用户越权拦截） =================
    if trader:
        api("GET", "/admin/users", trader, group="权限", name="trader 系统管理→14003", expect=14003)
        api("GET", "/review/reports", trader, group="权限", name="trader 复盘→14003", expect=14003)
        api("GET", "/assessment/indicators", trader, group="权限", name="trader 考核→14003", expect=14003)
        api("GET", "/report/templates", trader, group="权限", name="trader 报表→14003", expect=14003)
        api("GET", f"/decision/sessions/{sid}", trader, group="权限", name="trader 决策详情→200")
    if manager:
        api("GET", "/review/reports", manager, group="权限", name="manager01 复盘→200")
        api("GET", "/report/templates", manager, group="权限", name="manager01 报表→200")
    if settle:
        api("GET", "/settlement/tickets", settle, group="权限", name="settle01 结算→200")
        api("GET", "/admin/users", settle, group="权限", name="settle01 系统管理→14003", expect=14003)

    # ================= 汇总 =================
    total = len(results)
    passed = sum(1 for r in results if r["ok"])
    out = {"summary": {"total": total, "passed": passed, "failed": total - passed,
                       "pass_rate": f"{passed / total * 100:.1f}%" if total else "N/A"},
           "groups": {}}
    for r in results:
        out["groups"].setdefault(r["group"], {"total": 0, "passed": 0})
        out["groups"][r["group"]]["total"] += 1
        out["groups"][r["group"]]["passed"] += 1 if r["ok"] else 0
    out["results"] = results
    with open("/home/odoo/workspace/PTIDSS/tests/full/verify_result_full_user.json", "w",
              encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=2)
    print(json.dumps(out["summary"], ensure_ascii=False))
    for g, s in out["groups"].items():
        print(f"  [{g}] {s['passed']}/{s['total']}")
    for r in results:
        if not r["ok"]:
            print(f"  FAIL {r['group']}/{r['name']}: http={r['http']} code={r['code']} expect={r['expect_code']} note={r['note']}")


if __name__ == "__main__":
    main()
