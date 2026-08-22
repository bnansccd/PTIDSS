"""
PTIDSS 多智能体与专业算法核查-补充回归用例（captcha.enabled=false 测试模式）
覆盖（对齐 SRS FR-DM-01/02、PRD FR-TR-05）：
  1. 智能体注册表：GET /agent/registry → 7 大智能体（含契约/版本/状态）
  2. 效果评估：GET /agent/metrics → 7 行聚合
  3. 决策会话创建 → agent_run 自动持久化 7 条运行记录（输入快照/输出/置信度/耗时）
  4. 依据链：GET /decision/sessions/{id}/evidence → agents+conflicts
  5. 低置信度标注：aggressive 场景 risk 智能体置信度 0.65 → attentionRequired=true（SRS R3）
  6. 超时降级：spot_quote+aggressive → review/settlement 标记 timeout 降级（SRS R1）
  7. 仲裁记录：报价 vs 风险价差仲裁 + 合规优先（SRS R1 优先级）
  8. 情报→决策联动：market 智能体近 24h 情报评分非零并影响报价（FR-INT-04）
  9. 模型接入：forecast 绑定 price 在线模型 → mode=model；未绑定在线模型 → 回退 mock+fallback
 10. 降级补跑：POST /decision/sessions/{id}/rerun → 降级清除、agent_run 追加 -R 记录
 11. 模型绑定：POST /agent/registry/{id}/model-config（绑定/拒绝/解绑）
 12. 智能体启停：POST /agent/registry/{id}/status → maintenance → 恢复 active
输出：verify_result_agent.json
"""
import requests
import json
import time
import re

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
    token = login("trader01")
    if not token:
        print("登录失败")
        return
    manager = login("manager01")
    # ---------- 1. 智能体注册表 ----------
    ok, j = api("GET", "/agent/registry", token, group="agent", name="注册表 7 大智能体")
    if ok:
        agents = j["data"]
        codes = [a["agentCode"] for a in agents]
        expect_codes = ["compliance", "forecast", "market", "quote", "review", "risk", "settlement"]
        ok2 = len(agents) == 7 and sorted(codes) == sorted(expect_codes)
        results.append({"group": "agent", "name": "注册表编码完整性(7)", "http": 200, "code": 0,
                        "expect_code": 0, "ok": ok2, "elapsed_ms": 0,
                        "note": "codes=" + ",".join(codes)})
        api("GET", "/agent/metrics", token, group="agent", name="效果评估聚合")
        # 状态切换
        fid = next(a["id"] for a in agents if a["agentCode"] == "forecast")
        ok, _ = api("POST", f"/agent/registry/{fid}/status", token, {"status": "maintenance"},
                    group="agent", name="启停维护 maintenance")
        ok, j2 = api("GET", "/agent/registry", token, group="agent", name="状态回读")
        if ok:
            st = next(a["status"] for a in j2["data"] if a["agentCode"] == "forecast")
            results.append({"group": "agent", "name": "状态已变更", "http": 200, "code": 0,
                            "expect_code": 0, "ok": st == "maintenance", "elapsed_ms": 0,
                            "note": "status=" + st})
        api("POST", f"/agent/registry/{fid}/status", token, {"status": "active"},
            group="agent", name="恢复 active")
        api("POST", "/agent/registry/999999/status", token, {"status": "active"},
            group="agent", name="不存在智能体→拒绝", expect=500)

    # ---------- 2. 会话创建 → agent_run 持久化 ----------
    ok, j = api("POST", "/decision/sessions", token, {
        "sessionType": "rolling", "tradeDate": "2026-08-20", "scenario": "baseline"},
        group="decision", name="创建 rolling 会话")
    sid = j.get("data", {}).get("sessionId") if ok else None
    if sid:
        ok, jr = api("GET", f"/decision/sessions/{sid}", token, group="decision", name="会话详情")
        if ok:
            agents_list = jr["data"].get("agents", [])
            results.append({"group": "decision", "name": "编排智能体=7(含market/forecast)",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": len(agents_list) == 7 and "market" in agents_list and "forecast" in agents_list,
                            "elapsed_ms": 0, "note": "agents=" + ",".join(agents_list)})
        # agent_run 回读（按会话过滤）
        session_no = jr["data"].get("sessionNo") if ok else None
        ok, jruns = api("GET", "/agent/runs", token, params={"sessionId": session_no},
                        group="agent", name="运行记录按会话过滤")
        if ok:
            runs = jruns["data"]
            results.append({"group": "agent", "name": "agent_run 7 条/会话",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": len(runs) == 7, "elapsed_ms": 0,
                            "note": "runs=" + str(len(runs))})
            first = runs[0] if runs else {}
            has_fields = all(k in first for k in ("inputSnapshot", "output", "confidence", "elapsedMs", "reasoning"))
            results.append({"group": "agent", "name": "运行记录字段完整(输入/输出/置信度/耗时)",
                            "http": 200, "code": 0, "expect_code": 0, "ok": has_fields,
                            "elapsed_ms": 0, "note": "fields=" + ",".join(first.keys())})
        # 依据链
        api("GET", f"/decision/sessions/{sid}/evidence", token, group="decision", name="依据链回溯")
        # 合规智能体引用 rule_config
        ok, jr = api("GET", f"/decision/sessions/{sid}/evidence", token, group="decision",
                     name="合规智能体规则引用")
        if ok:
            comp = next((a for a in jr["data"].get("agents", []) if a.get("agentCode") == "compliance"), {})
            out = str(comp.get("output", ""))
            results.append({"group": "decision", "name": "合规引用 rule_config",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": "RULE-DECL-SEG" in out, "elapsed_ms": 0,
                            "note": "output=" + out[:100]})

    # ---------- 3. 低置信度标注（aggressive → risk 0.65）----------
    ok, j = api("POST", "/decision/sessions", token, {
        "sessionType": "spot_quote", "tradeDate": "2026-08-21", "scenario": "aggressive"},
        group="decision", name="创建 aggressive 现货会话")
    sid2 = j.get("data", {}).get("sessionId") if ok else None
    if sid2:
        ok, jr = api("GET", f"/decision/sessions/{sid2}", token, group="decision", name="低置信度场景详情")
        if ok:
            strategy = jr["data"].get("finalStrategy", {})
            attention = strategy.get("attentionRequired") is True
            low_conf = strategy.get("lowConfidenceAgents", [])
            degraded = strategy.get("degraded") is True
            results.append({"group": "decision", "name": "置信度<0.7强制人工关注(R3)",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": attention and "risk" in low_conf,
                            "elapsed_ms": 0, "note": "lowConf=" + json.dumps(low_conf)})
            results.append({"group": "decision", "name": "超时降级标记(R1)",
                            "http": 200, "code": 0, "expect_code": 0, "ok": degraded,
                            "elapsed_ms": 0, "note": "degraded=" + str(degraded)})
        api("GET", f"/decision/sessions/{sid2}/evidence", token, group="decision", name="降级会话依据链")

    # ---------- 4. 仲裁记录（compliance 优先于 quote）----------
    ok, j = api("POST", "/decision/sessions", token, {
        "sessionType": "rolling", "tradeDate": "2026-08-22", "scenario": "baseline"},
        group="decision", name="仲裁场景会话")
    sid3 = j.get("data", {}).get("sessionId") if ok else None
    if sid3:
        ok, je = api("GET", f"/decision/sessions/{sid3}/evidence", token, group="decision", name="仲裁记录")
        if ok:
            conflicts = je["data"].get("conflicts", [])
            notes = [c.get("arbitration", "") for c in conflicts]
            has_priority = any("合规 > 风险 > 收益" in n for n in notes)
            results.append({"group": "decision", "name": "仲裁优先级说明(合规>风险>收益)",
                            "http": 200, "code": 0, "expect_code": 0, "ok": has_priority,
                            "elapsed_ms": 0, "note": "conflicts=" + str(len(conflicts))})

    # ---------- 5. 权限门禁 ----------
    ok, j = api("GET", "/agent/registry", manager, group="perm", name="manager 访问 agent 注册表")
    if not ok:
        results.append({"group": "perm", "name": "manager 有 menu:decision", "http": 200, "code": 14003,
                        "expect_code": 0, "ok": False, "elapsed_ms": 0, "note": j.get("message")})

    # ---------- 6. 情报→决策联动（FR-INT-04：market 情报评分接入报价链路）----------
    # 前置：确保 forecast 绑定 price 在线模型（幂等；供第 7 节模型接入断言，独立运行不依赖外部状态）
    ok, jr0 = api("GET", "/agent/registry", token, group="agent", name="绑定前置-注册表")
    fc_id = None
    if ok:
        fc_id = next((a.get("id") for a in jr0.get("data", []) if a.get("agentCode") == "forecast"), None)
    if fc_id:
        api("POST", f"/agent/registry/{fc_id}/model-config", token, {"modelCode": "price"},
            group="agent", name="绑定前置-forecast→price")
    ok, j = api("POST", "/decision/sessions", token, {
        "sessionType": "rolling", "tradeDate": "2026-08-23", "scenario": "baseline"},
        group="decision", name="情报联动场景会话")
    sid4 = j.get("data", {}).get("sessionId") if ok else None
    if sid4:
        ok, je = api("GET", f"/decision/sessions/{sid4}/evidence", token, group="decision", name="情报联动依据链")
        if ok:
            market = next((a for a in je["data"].get("agents", []) if a.get("agentCode") == "market"), {})
            out = str(market.get("output", ""))
            snap = str(market.get("inputSnapshot", ""))
            results.append({"group": "decision", "name": "market 情报评分接入",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": "情报评分" in out and "近24h情报流" in snap,
                            "elapsed_ms": 0, "note": "output=" + out[:120]})
            m = re.search(r"情报评分 ([+-]?\d+\.\d+)", out)
            score = float(m.group(1)) if m else 0.0
            results.append({"group": "decision", "name": "情报评分非零(种子情报关键词)",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": score != 0.0, "elapsed_ms": 0, "note": "score=" + str(score)})

    # ---------- 7. 模型接入（FR-TR-05：绑定在线模型 → 模型推理；未绑定 → 确定性算法回退）----------
    ok, je = api("GET", f"/decision/sessions/{sid4}/evidence", token, group="decision", name="模型接入依据链")
    if ok:
        runs4 = je["data"].get("agents", [])
        fc = next((a for a in runs4 if a.get("agentCode") == "forecast"), {})
        rk = next((a for a in runs4 if a.get("agentCode") == "risk"), {})
        results.append({"group": "decision", "name": "forecast 走模型推理(mode=model)",
                        "http": 200, "code": 0, "expect_code": 0,
                        "ok": fc.get("mode") == "model" and "modelVersion" in fc,
                        "elapsed_ms": 0, "note": "mode=" + str(fc.get("mode"))
                        + " version=" + str(fc.get("modelVersion"))})
        # risk 懒种子绑定 modelCode=risk（model_registry 无此编码）→ 模型推理失败回退确定性算法
        results.append({"group": "decision", "name": "未绑定在线模型回退(mock+fallback)",
                        "http": 200, "code": 0, "expect_code": 0,
                        "ok": rk.get("mode") == "mock" and rk.get("modelFallback") is True,
                        "elapsed_ms": 0, "note": "mode=" + str(rk.get("mode"))
                        + " fallback=" + str(rk.get("modelFallback"))})

    # ---------- 8. 降级补跑（SRS FR-DM-01 R1：POST /decision/sessions/{id}/rerun）----------
    ok, j = api("POST", "/decision/sessions", token, {
        "sessionType": "spot_quote", "tradeDate": "2026-08-24", "scenario": "aggressive"},
        group="decision", name="补跑场景会话(降级)")
    sid5 = j.get("data", {}).get("sessionId") if ok else None
    if sid5:
        ok, jr = api("POST", f"/decision/sessions/{sid5}/rerun", token, group="decision", name="降级补跑")
        if ok:
            data = jr.get("data", {})
            strategy = data.get("finalStrategy", {})
            note = str(strategy.get("note", ""))
            results.append({"group": "decision", "name": "补跑后降级清除+留痕",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": strategy.get("degraded") is False and "已人工补跑" in note,
                            "elapsed_ms": 0, "note": "degraded=" + str(strategy.get("degraded"))})
            ok2, jruns = api("GET", "/agent/runs", token,
                             params={"sessionId": data.get("sessionNo")},
                             group="agent", name="补跑后运行记录")
            if ok2:
                r5 = [r for r in jruns["data"] if r["runId"].endswith("-R")]
                ok3 = len(r5) == 7 and all(r["status"] == "success" for r in r5)
                results.append({"group": "agent", "name": "补跑补齐 7 智能体(-R, success)",
                                "http": 200, "code": 0, "expect_code": 0, "ok": ok3,
                                "elapsed_ms": 0, "note": "R-runs=" + str(len(r5))
                                + " statuses=" + ",".join(r["status"] for r in r5)})
        # 非降级会话拒绝补跑
        api("POST", f"/decision/sessions/{sid4}/rerun", token, group="decision",
            name="非降级会话补跑→拒绝", expect=500)

    # ---------- 9. 模型绑定（PRD FR-TR-05：POST /agent/registry/{id}/model-config）----------
    ok, j = api("GET", "/agent/registry", token, group="agent", name="注册表(绑定前)")
    if ok:
        qid = next(a["id"] for a in j["data"] if a["agentCode"] == "quote")
        api("POST", f"/agent/registry/{qid}/model-config", token, {"modelCode": "price"},
            group="agent", name="quote 绑定 price 模型")
        ok, j3 = api("GET", "/agent/registry", token, group="agent", name="绑定回读")
        if ok:
            mc = next(a["modelConfig"] for a in j3["data"] if a["agentCode"] == "quote")
            results.append({"group": "agent", "name": "绑定生效(modelConfig.modelCode)",
                            "http": 200, "code": 0, "expect_code": 0,
                            "ok": mc.get("modelCode") == "price", "elapsed_ms": 0,
                            "note": "modelConfig=" + json.dumps(mc, ensure_ascii=False)})
        api("POST", f"/agent/registry/{qid}/model-config", token, {"modelCode": "nonexist"},
            group="agent", name="绑定不存在模型→拒绝", expect=500)
        api("POST", f"/agent/registry/{qid}/model-config", token, {"modelCode": ""},
            group="agent", name="解绑恢复(确定性算法)")

    # ---------- 10. 智能体启停（FR-TR-05）----------
    ok, j = api("GET", "/agent/registry", token, group="agent", name="注册表(启停)")
    if ok:
        fid = next(a["id"] for a in j["data"] if a["agentCode"] == "forecast")
        api("POST", f"/agent/registry/{fid}/status", token, {"status": "maintenance"},
            group="agent", name="启停维护 maintenance")
        api("POST", f"/agent/registry/{fid}/status", token, {"status": "active"},
            group="agent", name="恢复 active")

    # 汇总
    total = len(results)
    passed = sum(1 for r in results if r["ok"])
    print(f"TOTAL={total} PASS={passed} FAIL={total - passed}")
    for r in results:
        if not r["ok"]:
            print("FAIL:", r["group"], r["name"], r["note"][:200])
    with open("/home/odoo/workspace/PTIDSS/tests/full/verify_result_agent.json", "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=2, default=str)


if __name__ == "__main__":
    main()
