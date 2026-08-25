"""
PTIDSS V2.2 平台配置化回归用例（captcha.enabled=false 测试模式）
覆盖 6 项产品化诉求 + P2/P3 遗留完善：
  G1/G2 信息源对接配置化：intel_source/data_source 连接方式(api/jwt/oauth2/basic/file/poll)/
        连接参数/频率/启停 可定义可调整（PUT 生效回读；非法枚举拒绝；非 admin 拒绝）
  G1.5 连接参数字段级加密（P2）：敏感字段(password/apiKey/secretKey...)AES 加密存储(enc: 前缀)/
        对外脱敏(******)/编辑 merge 保留（提交 ****** 不覆盖原值）/非敏感字段保留
  G3 审批流定义化：flow_definition CRUD（环节/角色/用户/时限）+ 实例按定义流转推进
        （approve 逐环节推进 / reject 终止 / 留痕 actions / 停用禁发起 / 非 admin 拒绝）
  G4 LLM 模型配置：llm_model CRUD（编码唯一/供应商枚举/温度限额/启停）
  G4.5 真实 LLM HTTP 通道（P2）：OpenAI 兼容协议真实调用(gateway=real, mock server)/
        死端口降级(gateway=degraded 不中断)/无密钥模拟(gateway=simulate)
  G5 智能体关联 LLM 推理：model-config llmCode 绑定 → mode=llm / model+llm 输出叠加
        LLM 解读（可回退），不存在的 llmCode 拒绝
  G6 算法注册替换：algorithm_registry CRUD（类目过滤/编码+版本唯一）+ 决策过程按类目
        匹配最新启用版本并标注（reasoning 含"算法应用"），停用旧版后自动切新版本
  G6.5 算法 SPI 插件化执行（P3）：/algorithm/spis 清单 8 项/非法 spiKey 拒绝/
        决策真实执行留痕（execution.spiKey/summary）/显式绑定跟随/类目兜底
输出：verify_result_platform.json
"""
import requests
import json
import time

BASE = "http://localhost:9080/ptidss"
REGION = "CN-32"
PWD = "Ptidss@2026"
results = []


def api(method, path, token=None, body=None, expect=0, group="", name="", params=None):
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
        ok = r.status_code == 200 and code == expect
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


def check(group, name, ok, note=""):
    results.append({"group": group, "name": name, "http": 200, "code": 0, "expect_code": 0,
                    "ok": ok, "elapsed_ms": 0, "note": note[:200]})


def login(username, password=PWD):
    r = requests.post(BASE + "/auth/login", json={"username": username, "password": password},
                      headers={"X-Region-Code": REGION}, timeout=20)
    j = r.json()
    if j.get("code") == 0:
        return j["data"]["accessToken"]
    return None


def find_by(data_list, key, val):
    return next((x for x in data_list if x.get(key) == val), None)


def main():
    admin = login("admin")
    if not admin:
        print("admin 登录失败（captcha=false 模式应直接登录），退出")
        return
    trader = login("trader01")
    ts = int(time.time() * 1000) % 100000

    # ============ G1 情报源对接配置化 ============
    ok, j = api("GET", "/intel/sources", admin, group="G1情报源", name="台账含对接字段")
    if ok:
        srcs = j["data"]
        has_fields = all("connType" in s for s in srcs)
        check("G1情报源", "台账每条含 connType/connConfig", has_fields and len(srcs) > 0,
              f"n={len(srcs)}")
    # 新增：jwt 对接方式 + 连接参数
    icode = f"INTL-V22-{ts}"
    ok, j = api("POST", "/intel/sources", admin,
                body={"sourceCode": icode, "sourceName": f"V2.2对接源-{ts}", "intelType": "price",
                      "fetchMode": "api", "connType": "jwt",
                      "connConfig": '{"endpoint":"https://example.com/api","authType":"jwt","tokenRef":"API_KEY_X"}',
                      "frequency": "30 分钟", "status": "enabled"},
                group="G1情报源", name="新增 jwt 对接情报源")
    isid = None
    if ok:
        isid = j["data"]["id"]
        check("G1情报源", "新增返回 connType=jwt", j["data"].get("connType") == "jwt",
              json.dumps(j["data"], ensure_ascii=False)[:120])
    # 编辑：切 oauth2 + 新参数 + 停用 → 回读生效
    if isid:
        ok2, j2 = api("PUT", f"/intel/sources/{isid}", admin,
                      body={"connType": "oauth2",
                            "connConfig": '{"endpoint":"https://example.com/oauth","clientId":"cid","scope":"read"}',
                            "frequency": "15 分钟", "status": "disabled"},
                      group="G1情报源", name="编辑对接配置(oauth2+停用)")
        if ok2:
            ok3, j3 = api("GET", "/intel/sources", admin, group="G1情报源", name="台账验证编辑生效")
            if ok3:
                s = find_by(j3["data"], "sourceCode", icode)
                ok4 = s is not None and s.get("connType") == "oauth2" \
                    and s.get("status") == "disabled" and "clientId" in str(s.get("connConfig", ""))
                check("G1情报源", "编辑生效(connType/status/参数)", ok4,
                      f"connType={s and s.get('connType')} status={s and s.get('status')}")
    # 非法枚举拒绝 + 非 admin 拒绝（14003 权限码）
    api("POST", "/intel/sources", admin,
        body={"sourceCode": f"INTL-BAD-{ts}", "sourceName": "坏源", "intelType": "price",
              "fetchMode": "api", "connType": "grpc", "connConfig": "{}", "status": "enabled"},
        group="G1情报源", name="非法 connType 拒绝", expect=500)
    if isid:
        api("PUT", f"/intel/sources/{isid}", trader,
            body={"connType": "api"}, group="G1情报源", name="非 admin 编辑拒绝", expect=14003)

    # ============ G2 数据底座对接配置化 ============
    ok, j = api("GET", "/data/sources", admin, group="G2数据源", name="台账含对接字段")
    if ok:
        dsrcs = j["data"]
        check("G2数据源", "台账每条含 connType", len(dsrcs) > 0 and all("connType" in s for s in dsrcs),
              f"n={len(dsrcs)}")
    dcode = f"DS-V22-{ts}"
    ok, j = api("POST", "/data/sources", admin,
                body={"sourceCode": dcode, "sourceType": "exchange", "syncMode": "timed",
                      "connType": "oauth2", "connectConfig": '{"url":"https://example.com/exchange","authType":"oauth2"}',
                      "frequency": "0 */15 * * * *", "status": "enabled"},
                group="G2数据源", name="新增 oauth2 对接数据源")
    dsid = None
    if ok:
        dsid = j["data"]["id"]
        check("G2数据源", "新增返回 connType=oauth2", j["data"].get("connType") == "oauth2")
    if dsid:
        api("PUT", f"/data/sources/{dsid}", admin,
            body={"syncMode": "realtime", "connType": "jwt",
                  "connectConfig": '{"url":"https://example.com/x","tokenRef":"T"}',
                  "frequency": "0 */5 * * * *", "status": "disabled"},
            group="G2数据源", name="编辑对接配置(jwt+停用)")
        ok3, j3 = api("GET", "/data/sources", admin, group="G2数据源", name="台账验证编辑生效")
        if ok3:
            s = find_by(j3["data"], "sourceCode", dcode)
            ok4 = s is not None and s.get("connType") == "jwt" and s.get("syncMode") == "realtime" \
                and s.get("status") == "disabled" and "tokenRef" in str(s.get("connectConfig", ""))
            check("G2数据源", "编辑生效(connType/syncMode/参数/停用)", ok4,
                  f"connType={s and s.get('connType')} mode={s and s.get('syncMode')}")
    api("POST", "/data/sources", admin,
        body={"sourceCode": f"DS-BAD-{ts}", "sourceType": "exchange", "syncMode": "timed",
              "connType": "smtp", "connectConfig": "{}", "frequency": "0 */30 * * * *", "status": "enabled"},
        group="G2数据源", name="非法 connType 拒绝", expect=500)

    # ============ G1.5 连接参数字段级加密（P2） ============
    import subprocess

    def db_query(sql):
        try:
            r = subprocess.run(
                ["psql", "-h", "127.0.0.1", "-p", "5432", "-U", "ptidss", "-d", "ptidss",
                 "-t", "-A", "-c", sql],
                capture_output=True, text=True, timeout=15)
            return r.stdout.strip()
        except Exception as e:
            return "DBERR:" + str(e)

    # 情报源：敏感字段(password/apiKey/secretKey)加密存储+对外脱敏；非敏感(clientId/endpoint)保留
    ecode = f"INTL-ENC-{ts}"
    sec_cfg = ('{"endpoint":"https://sec.example.com/api","clientId":"cid_keep","password":"p@ss-123",'
               '"apiKey":"sk-live-abc","secretKey":"sk-very-secret","url":"https://sec.example.com/path"}')
    ok, j = api("POST", "/intel/sources", admin,
                body={"sourceCode": ecode, "sourceName": f"加密源-{ts}", "intelType": "price",
                      "fetchMode": "api", "connType": "jwt", "connConfig": sec_cfg,
                      "frequency": "30 分钟", "status": "enabled"},
                group="G1.5加密", name="新增含敏感字段情报源")
    eid = None
    if ok:
        eid = j["data"]["id"]
        cfg = str(j["data"].get("connConfig", ""))
        masked = cfg.count("******") == 3 and all(k not in cfg for k in ["p@ss-123", "sk-live-abc", "sk-very-secret"])
        keep = "clientId" in cfg and "cid_keep" in cfg and "endpoint" in cfg
        check("G1.5加密", "返回视图敏感字段脱敏+非敏感保留", masked and keep, cfg[:160])
    if eid:
        dbc = db_query(f"SELECT conn_config FROM intel_source WHERE source_code='{ecode}'")
        check("G1.5加密", "DB 存储 enc: 前缀密文", '"enc:' in dbc and "p@ss-123" not in dbc and "sk-live-abc" not in dbc,
              dbc[:90])
        # 编辑提交 ****** → 保留原加密值（mergeMasked）
        ok2, j2 = api("PUT", f"/intel/sources/{eid}", admin,
                      body={"connConfig": ('{"endpoint":"https://sec.example.com/api","clientId":"cid_keep",'
                                            '"password":"******","apiKey":"******","secretKey":"******",'
                                            '"url":"https://sec.example.com/path"}')},
                      group="G1.5加密", name="编辑提交脱敏占位(merge)")
        if ok2:
            cfg2 = str(j2.get("data", {}).get("connConfig", ""))
            check("G1.5加密", "编辑后敏感字段仍脱敏", cfg2.count("******") == 3, cfg2[:120])
        dbc2 = db_query(f"SELECT conn_config FROM intel_source WHERE source_code='{ecode}'")
        check("G1.5加密", "merge 后 DB 仍为密文(未覆盖)", '"enc:' in dbc2 and "p@ss-123" not in dbc2,
              dbc2[:90])
        # 真实修改敏感字段（提交新值）→ 新值加密落库
        ok3, j3 = api("PUT", f"/intel/sources/{eid}", admin,
                      body={"connConfig": ('{"endpoint":"https://sec.example.com/api","clientId":"cid_keep",'
                                            '"password":"new-pass-9"}'),
                            "frequency": "15 分钟"},
                      group="G1.5加密", name="编辑提交新敏感值")
        if ok3:
            cfg3 = str(j3.get("data", {}).get("connConfig", ""))
            check("G1.5加密", "新值保存后仍脱敏", cfg3.count("******") == 1 and "new-pass-9" not in cfg3, cfg3[:120])
        dbc3 = db_query(f"SELECT conn_config FROM intel_source WHERE source_code='{ecode}'")
        check("G1.5加密", "DB 新密文且不含明文", '"enc:' in dbc3 and "new-pass-9" not in dbc3,
              dbc3[:90])
    # 数据源同样加密
    dcode2 = f"DS-ENC-{ts}"
    ok4, j4 = api("POST", "/data/sources", admin,
                  body={"sourceCode": dcode2, "sourceType": "exchange", "syncMode": "timed",
                        "connType": "basic",
                        "connectConfig": ('{"url":"https://ds.example.com/x","username":"u1",'
                                          '"password":"ds-p@ss","accessKey":"ak-live"}'),
                        "frequency": "0 */15 * * * *", "status": "enabled"},
                  group="G1.5加密", name="数据源敏感字段加密")
    if ok4:
        cfg4 = str(j4["data"].get("connectConfig", ""))
        check("G1.5加密", "数据源返回脱敏+url保留",
              "******" in cfg4 and "ds-p@ss" not in cfg4 and "ak-live" not in cfg4 and "url" in cfg4, cfg4[:160])
        dsd = db_query(f"SELECT connect_config FROM data_source WHERE source_code='{dcode2}'")
        check("G1.5加密", "数据源 DB enc: 密文", '"enc:' in dsd and "ds-p@ss" not in dsd and "ak-live" not in dsd,
              dsd[:90])


    # ============ G3 审批流定义化 ============
    ok, j = api("GET", "/flow/definitions", admin, group="G3流程定义", name="定义列表(种子5)")
    if ok:
        defs = j["data"]
        keys = [d["processKey"] for d in defs]
        seed_ok = all(k in keys for k in
                      ["decision_confirm", "declaration_approve", "ticket_handle", "appeal_review",
                       "settlement_ticket_review"])
        check("G3流程定义", "种子 5 定义齐全", seed_ok, "keys=" + ",".join(keys))
    # 新增自定义流程（3 环节：角色+用户）
    pkey = f"custom_confirm_{ts}"
    steps = [
        {"stepNo": "apply", "stepName": "申请", "approveMode": "any", "roleCodes": ["trader"], "userIds": [], "timeoutHours": 24},
        {"stepNo": "manager_check", "stepName": "经理审核", "approveMode": "any", "roleCodes": ["manager"], "userIds": ["u_1001"], "timeoutHours": 48},
        {"stepNo": "finance_confirm", "stepName": "财务确认", "approveMode": "any", "roleCodes": ["finance"], "userIds": [], "timeoutHours": 24},
    ]
    ok, j = api("POST", "/flow/definitions", admin,
                body={"processKey": pkey, "processName": f"V2.2自定义流程-{ts}", "bizType": "custom", "steps": steps},
                group="G3流程定义", name="新增自定义流程定义")
    defid = None
    if ok:
        defid = j["data"]["id"]
        check("G3流程定义", "新增返回 3 环节", len(j["data"].get("steps", [])) == 3)
    # 非 admin 新增拒绝（14003 权限码）
    api("POST", "/flow/definitions", trader,
        body={"processKey": f"hack_{ts}", "processName": "越权", "bizType": "custom", "steps": steps},
        group="G3流程定义", name="非 admin 新增拒绝", expect=14003)
    # 编辑环节（改角色）
    if defid:
        ok3, j3 = api("PUT", f"/flow/definitions/{defid}", admin,
                      body={"processName": f"V2.2自定义流程-{ts}-改", "steps": steps},  # steps 原样
                      group="G3流程定义", name="更新定义(名称)")
        check("G3流程定义", "更新生效", ok3 and j3.get("data", {}).get("processName", "").endswith("改"))
    # 按自定义定义发起 → 流转推进
    if defid:
        ok4, j4 = api("POST", "/flow/start", trader,
                      body={"processKey": pkey, "bizId": f"BIZ-{ts}"},
                      group="G3流程定义", name="按自定义定义发起")
        if ok4:
            iid = j4["data"]["instanceId"]
            cn = j4["data"].get("currentNode")
            check("G3流程定义", "首环节=manager_check(定义驱动)", cn == "manager_check", f"currentNode={cn}")
            # approve → 下一环节
            ok5, j5 = api("POST", f"/flow/instances/{iid}/advance", trader,
                          body={"action": "approve", "comment": "经理同意"},
                          group="G3流程定义", name="环节1 通过→下一环节")
            if ok5:
                check("G3流程定义", "推进到 finance_confirm", j5["data"].get("currentNode") == "finance_confirm",
                      f"node={j5['data'].get('currentNode')}")
            # approve → 完成
            api("POST", f"/flow/instances/{iid}/advance", trader,
                body={"action": "approve", "comment": "财务确认"},
                group="G3流程定义", name="环节2 通过→完成")
            ok6, j6 = api("GET", f"/flow/instances/{iid}", trader, group="G3流程定义", name="完成态详情")
            if ok6:
                d6 = j6["data"]
                acts = (d6.get("variables") or {}).get("actions", [])
                ok7 = d6.get("status") == "completed" and len(acts) == 2 \
                    and all(a.get("comment") for a in acts)
                check("G3流程定义", "completed+2 次推进留痕", ok7,
                      f"status={d6.get('status')} actions={len(acts)}")
                check("G3流程定义", "留痕含处理人/意见/时间", len(acts) > 0
                      and all(a.get("operator") and a.get("time") for a in acts))
            # 已结束再推进 → 拒绝
            api("POST", f"/flow/instances/{iid}/advance", trader,
                body={"action": "approve"}, group="G3流程定义", name="已结束再推进拒绝", expect=500)
        # reject 终止
        ok8, j8 = api("POST", "/flow/start", trader,
                      body={"processKey": pkey, "bizId": f"BIZ-R-{ts}"},
                      group="G3流程定义", name="再发起(驳回场景)")
        if ok8:
            iid2 = j8["data"]["instanceId"]
            api("POST", f"/flow/instances/{iid2}/advance", trader,
                body={"action": "reject", "comment": "不符合条件"},
                group="G3流程定义", name="环节 驳回终止")
            ok9, j9 = api("GET", f"/flow/instances/{iid2}", trader, group="G3流程定义", name="终止态详情")
            if ok9:
                d9 = j9["data"]
                ok10 = d9.get("status") == "terminated" and d9.get("endTime") \
                    and (d9.get("variables") or {}).get("actions", [])[-1].get("action") == "reject"
                check("G3流程定义", "terminated+endTime+驳回留痕", ok10,
                      f"status={d9.get('status')} endTime={d9.get('endTime')}")
        # 停用定义 → 禁发起
        api("PUT", f"/flow/definitions/{defid}", admin,
            body={"status": "disabled"}, group="G3流程定义", name="停用定义")
        api("POST", "/flow/start", trader,
            body={"processKey": pkey, "bizId": f"BIZ-D-{ts}"},
            group="G3流程定义", name="停用后发起拒绝", expect=500)
        api("PUT", f"/flow/definitions/{defid}", admin,
            body={"status": "enabled"}, group="G3流程定义", name="恢复启用定义")

    # ============ G4 LLM 模型配置 ============
    ok, j = api("GET", "/llm/models", admin, group="G4LLM", name="LLM 列表(种子3)")
    if ok:
        llms = j["data"]
        lcodes = [m["modelCode"] for m in llms]
        check("G4LLM", "种子 3 模型齐全", all(c in lcodes for c in ["deepseek-v3", "glm-4", "qwen-plus"]),
              "codes=" + ",".join(lcodes))
    lcode = f"test-llm-{ts}"
    ok, j = api("POST", "/llm/models", admin,
                body={"modelCode": lcode, "modelName": f"测试LLM-{ts}", "provider": "openai-compatible",
                      "endpoint": "https://api.example.com/v1", "baseModel": "gpt-test",
                      "temperature": 0.5, "maxTokens": 2048, "status": "enabled"},
                group="G4LLM", name="新增 LLM 模型")
    llmid = None
    if ok:
        llmid = j["data"]["id"]
        check("G4LLM", "新增返回 temperature=0.5", str(j["data"].get("temperature")) == "0.5")
    if llmid:
        api("PUT", f"/llm/models/{llmid}", admin,
            body={"temperature": 0.9, "maxTokens": 4096, "status": "disabled"},
            group="G4LLM", name="更新配置(温度/限额/停用)")
        ok3, j3 = api("GET", "/llm/models", admin, group="G4LLM", name="回读验证更新生效")
        if ok3:
            m = find_by(j3["data"], "modelCode", lcode)
            ok4 = m is not None and str(m.get("temperature")) == "0.9" and m.get("status") == "disabled"
            check("G4LLM", "更新生效(temperature/status)", ok4,
                  f"temp={m and m.get('temperature')} status={m and m.get('status')}")
    # 编码重复 / 非法 provider / 非 admin（14003 权限码）
    api("POST", "/llm/models", admin,
        body={"modelCode": lcode, "modelName": "重复", "provider": "openai-compatible", "status": "enabled"},
        group="G4LLM", name="编码重复拒绝", expect=500)
    api("POST", "/llm/models", admin,
        body={"modelCode": f"bad-llm-{ts}", "modelName": "坏供应商", "provider": "aliens", "status": "enabled"},
        group="G4LLM", name="非法 provider 拒绝", expect=500)
    api("POST", "/llm/models", trader,
        body={"modelCode": f"hack-llm-{ts}", "modelName": "越权", "provider": "openai-compatible", "status": "enabled"},
        group="G4LLM", name="非 admin 新增拒绝", expect=14003)

    # ============ G4.5 真实 LLM HTTP 通道（P2：OpenAI 兼容 + 降级） ============
    import threading
    import http.server

    class MockLlmHandler(http.server.BaseHTTPRequestHandler):
        def do_POST(self):
            try:
                length = int(self.headers.get("Content-Length", 0))
                payload = json.loads(self.rfile.read(length) or b"{}")
                model = payload.get("model", "?")
                data = json.dumps({
                    "choices": [{"message": {"role": "assistant", "content": f"MOCK-GATEWAY-REPLY[{model}]"}}],
                    "usage": {"total_tokens": 88}, "model": model,
                }).encode()
                self.send_response(200)
                self.send_header("Content-Type", "application/json")
                self.send_header("Content-Length", str(len(data)))
                self.end_headers()
                self.wfile.write(data)
            except Exception:
                pass

        def log_message(self, *a):
            pass

    mock_llm = http.server.ThreadingHTTPServer(("127.0.0.1", 19081), MockLlmHandler)
    threading.Thread(target=mock_llm.serve_forever, daemon=True).start()
    time.sleep(0.3)

    gcode = f"gw-llm-{ts}"
    ok, j = api("POST", "/llm/models", admin,
                body={"modelCode": gcode, "modelName": f"网关测试-{ts}", "provider": "openai-compatible",
                      "endpoint": "http://127.0.0.1:19081/v1/chat/completions", "baseModel": "mock-chat",
                      "apiKeyRef": "LLM_API_KEY_MOCK", "temperature": 0.3, "maxTokens": 256, "status": "enabled"},
                group="G4.5网关", name="新增网关模型(指向本地 mock)")
    gid = None
    if ok:
        gid = j["data"]["id"]
    ok1, j1 = api("GET", "/agent/registry", admin, group="G4.5网关", name="注册表(market id)")
    market_id2 = None
    if ok1:
        market_id2 = next((a["id"] for a in j1["data"] if a["agentCode"] == "market"), None)
    if gid and market_id2:
        api("POST", f"/agent/registry/{market_id2}/model-config", admin,
            body={"llmCode": gcode}, group="G4.5网关", name="market 绑定网关模型")
        # 路径1：真实调用（mock server 返回 OpenAI 兼容响应）
        ok2, j2 = api("POST", "/decision/sessions", admin,
                      body={"sessionType": "rolling", "tradeDate": "2026-08-28", "scenario": "baseline"},
                      group="G4.5网关", name="决策会话(real 路径)")
        if ok2:
            ok3, j3 = api("GET", f"/decision/sessions/{j2['data']['sessionId']}/evidence", admin,
                          group="G4.5网关", name="依据链(real)")
            if ok3:
                mk = next((r for r in j3["data"].get("agents", []) if r.get("agentCode") == "market"), {})
                check("G4.5网关", "gateway=real 真实调用", mk.get("llmGateway") == "real"
                      and mk.get("llmSimulate") is False and "MOCK-GATEWAY-REPLY" in str(mk.get("output", "")),
                      f"gateway={mk.get('llmGateway')} simulate={mk.get('llmSimulate')} out={str(mk.get('output', ''))[-90:]}")
        # 路径2：死端口 → degraded 降级（决策不中断）
        api("PUT", f"/llm/models/{gid}", admin, body={"endpoint": "http://127.0.0.1:19999/v1/chat/completions"},
            group="G4.5网关", name="模型指向死端口")
        ok4, j4 = api("POST", "/decision/sessions", admin,
                      body={"sessionType": "rolling", "tradeDate": "2026-08-29", "scenario": "baseline"},
                      group="G4.5网关", name="决策会话(degraded 路径)")
        if ok4:
            ok5, j5 = api("GET", f"/decision/sessions/{j4['data']['sessionId']}/evidence", admin,
                          group="G4.5网关", name="依据链(degraded)")
            if ok5:
                mk2 = next((r for r in j5["data"].get("agents", []) if r.get("agentCode") == "market"), {})
                check("G4.5网关", "gateway=degraded 降级不中断", mk2.get("llmGateway") == "degraded"
                      and mk2.get("llmSimulate") is True and "降级内置模拟" in str(mk2.get("output", "")),
                      f"gateway={mk2.get('llmGateway')} simulate={mk2.get('llmSimulate')}")
        # 路径3：密钥引用不存在 → simulate（模拟推理）
        api("PUT", f"/llm/models/{gid}", admin,
            body={"endpoint": "http://127.0.0.1:19081/v1/chat/completions", "apiKeyRef": "LLM_API_KEY_NOPE"},
            group="G4.5网关", name="模型密钥引用置空")
        ok6, j6 = api("POST", "/decision/sessions", admin,
                      body={"sessionType": "rolling", "tradeDate": "2026-08-30", "scenario": "baseline"},
                      group="G4.5网关", name="决策会话(simulate 路径)")
        if ok6:
            ok7, j7 = api("GET", f"/decision/sessions/{j6['data']['sessionId']}/evidence", admin,
                          group="G4.5网关", name="依据链(simulate)")
            if ok7:
                mk3 = next((r for r in j7["data"].get("agents", []) if r.get("agentCode") == "market"), {})
                check("G4.5网关", "无密钥 gateway=simulate", mk3.get("llmGateway") == "simulate"
                      and mk3.get("llmSimulate") is True, f"gateway={mk3.get('llmGateway')}")
        # 清理：停用网关测试模型 + 解绑 market（G5 将重新绑定）
        api("PUT", f"/llm/models/{gid}", admin, body={"status": "disabled"}, group="G4.5网关", name="停用网关测试模型")
        api("POST", f"/agent/registry/{market_id2}/model-config", admin,
            body={"modelCode": ""}, group="G4.5网关", name="解绑 market 恢复")
    mock_llm.shutdown()

    # ============ G5 智能体关联 LLM 推理 ============
    ok, j = api("GET", "/agent/registry", admin, group="G5LLM绑定", name="注册表(绑定前)")
    agents = j["data"] if ok else []
    forecast_id = next((a["id"] for a in agents if a["agentCode"] == "forecast"), None)
    market_id = next((a["id"] for a in agents if a["agentCode"] == "market"), None)
    # 绑定不存在的 llmCode → 拒绝
    if forecast_id:
        api("POST", f"/agent/registry/{forecast_id}/model-config", admin,
            body={"modelCode": "price", "llmCode": "no-such-llm"},
            group="G5LLM绑定", name="绑定不存在 LLM 拒绝", expect=500)
    # forecast：数值模型+LLM 双绑定 → mode=model+llm
    if forecast_id:
        api("POST", f"/agent/registry/{forecast_id}/model-config", admin,
            body={"modelCode": "price", "llmCode": "deepseek-v3"},
            group="G5LLM绑定", name="forecast 绑定 price+deepseek-v3")
    # market：仅 LLM → mode=llm
    if market_id:
        api("POST", f"/agent/registry/{market_id}/model-config", admin,
            body={"llmCode": "glm-4"},
            group="G5LLM绑定", name="market 绑定 glm-4")
    # 决策会话验证执行模式
    ok3, j3 = api("POST", "/decision/sessions", admin,
                  body={"sessionType": "rolling", "tradeDate": "2026-08-25", "scenario": "baseline"},
                  group="G5LLM绑定", name="创建决策会话(LLM绑定)")
    if ok3:
        sid = j3["data"]["sessionId"]
        ok4, j4 = api("GET", f"/decision/sessions/{sid}/evidence", admin,
                      group="G5LLM绑定", name="依据链(LLM 执行模式)")
        if ok4:
            runs4 = j4["data"].get("agents", [])
            fc = next((r for r in runs4 if r.get("agentCode") == "forecast"), {})
            mk = next((r for r in runs4 if r.get("agentCode") == "market"), {})
            check("G5LLM绑定", "forecast mode=model+llm", fc.get("mode") == "model+llm"
                  and "LLM 解读" in str(fc.get("output", "")), f"mode={fc.get('mode')}")
            check("G5LLM绑定", "market mode=llm 叠加解读", mk.get("mode") == "llm"
                  and "LLM 解读" in str(mk.get("output", "")), f"mode={mk.get('mode')}")
            check("G5LLM绑定", "llmModel 标注+模拟标记", "llmModel" in fc and fc.get("llmSimulate") is True,
                  f"llmModel={fc.get('llmModel')} simulate={fc.get('llmSimulate')}")
    # 恢复绑定（避免影响既有回归：forecast 只留 modelCode，market 解绑）
    if forecast_id:
        api("POST", f"/agent/registry/{forecast_id}/model-config", admin,
            body={"modelCode": "price"}, group="G5LLM绑定", name="恢复 forecast 仅数值模型")
    if market_id:
        api("POST", f"/agent/registry/{market_id}/model-config", admin,
            body={"modelCode": ""}, group="G5LLM绑定", name="解绑 market 恢复")

    # ============ G6 算法注册替换 ============
    ok, j = api("GET", "/algorithm/registry", admin, group="G6算法", name="算法注册表(种子9)")
    if ok:
        algs = j["data"]
        codes = [a["algCode"] for a in algs]
        seed_codes = ["LSTM-PRICE-96", "SENTI-NEWS-1", "SEG-AGG-3PCT", "MC-CVAR-95", "MILP-OPT-1",
                      "DEV-ASSESS-1", "KB-REVIEW-1", "RULE-ENGINE-DROOLS", "HEDGE-STRATEGY-1"]
        enabled = [a for a in algs if a["status"] == "enabled"]
        check("G6算法", "种子 9 条齐全且启停混布",
              all(c in codes for c in seed_codes) and 0 < len(enabled) < len(algs),
              f"total={len(algs)} enabled={len(enabled)}")
    ok, j = api("GET", "/algorithm/registry?category=forecast&status=enabled", admin,
                group="G6算法", name="类目+状态过滤")
    if ok:
        algs_f = j["data"]
        check("G6算法", "过滤仅 forecast+enabled",
              len(algs_f) > 0 and all(a["category"] == "forecast" and a["status"] == "enabled" for a in algs_f),
              f"n={len(algs_f)}")
    # 注册新版本（替换语义：新版本启用+旧版停用）
    acode = f"FORECAST-REPLACE-{ts}"
    ok, j = api("POST", "/algorithm/registry", admin,
                body={"algCode": acode, "algName": f"替换预测算法-{ts}", "category": "forecast",
                      "description": "V2.2 测试替换", "paramsSchema": '{"method":"xgboost","lookback":24}',
                      "version": "v1.0", "status": "enabled"},
                group="G6算法", name="注册新算法(forecast)")
    algid = None
    if ok:
        algid = j["data"]["id"]
    # 编码+版本重复拒绝
    api("POST", "/algorithm/registry", admin,
        body={"algCode": acode, "algName": "重复", "category": "forecast", "version": "v1.0", "status": "enabled"},
        group="G6算法", name="编码+版本重复拒绝", expect=500)
    # 非 admin 拒绝（14003 权限码）
    api("POST", "/algorithm/registry", trader,
        body={"algCode": f"hack-alg-{ts}", "algName": "越权", "category": "forecast",
              "version": "v1.0", "status": "enabled"},
        group="G6算法", name="非 admin 注册拒绝", expect=14003)
    # 决策会话：forecast 应匹配新注册算法并标注
    ok3, j3 = api("POST", "/decision/sessions", admin,
                  body={"sessionType": "rolling", "tradeDate": "2026-08-26", "scenario": "baseline"},
                  group="G6算法", name="创建决策会话(算法匹配)")
    if ok3:
        sid2 = j3["data"]["sessionId"]
        ok4, j4 = api("GET", f"/decision/sessions/{sid2}/evidence", admin,
                      group="G6算法", name="依据链(算法标注)")
        if ok4:
            fc = next((r for r in j4["data"].get("agents", []) if r.get("agentCode") == "forecast"), {})
            alg = fc.get("algorithm") or {}
            basis = str((fc.get("reasoning") or {}).get("basis", ""))
            check("G6算法", "决策匹配最新算法并标注",
                  alg.get("algCode") == acode and "算法应用" in basis and "v1.0" in basis,
                  f"alg={json.dumps(alg, ensure_ascii=False)} basis={basis[-80:]}")
            ex = alg.get("execution") or {}
            check("G6算法", "SPI 真实执行留痕(类目默认)",
                  ex.get("spiKey") == "forecast" and bool(ex.get("summary")) and "执行：" in basis,
                  f"spi={ex.get('spiKey')} summary={str(ex.get('summary'))[-60:]}")
    # 停用新算法 → 决策回退到上一启用版本（不再标注新编码）
    if algid:
        api("PUT", f"/algorithm/registry/{algid}", admin,
            body={"status": "disabled"}, group="G6算法", name="停用新算法(替换回退)")
        ok5, j5 = api("POST", "/decision/sessions", admin,
                      body={"sessionType": "rolling", "tradeDate": "2026-08-27", "scenario": "baseline"},
                      group="G6算法", name="再建会话(替换回退)")
        if ok5:
            sid3 = j5["data"]["sessionId"]
            ok6, j6 = api("GET", f"/decision/sessions/{sid3}/evidence", admin,
                          group="G6算法", name="回退依据链")
            if ok6:
                fc2 = next((r for r in j6["data"].get("agents", []) if r.get("agentCode") == "forecast"), {})
                alg2 = fc2.get("algorithm") or {}
                ok7 = alg2.get("algCode") != acode and bool(alg2.get("algCode"))
                check("G6算法", "停用后自动回退上一版本", ok7,
                      f"alg={json.dumps(alg2, ensure_ascii=False)}")
    # 恢复新算法启用（保持注册表种子+1 的一致性说明）
    if algid:
        api("PUT", f"/algorithm/registry/{algid}", admin,
            body={"status": "enabled"}, group="G6算法", name="恢复新算法启用")

    # ============ G6.5 算法 SPI 插件化执行（P3） ============
    ok, j = api("GET", "/algorithm/spis", admin, group="G6.5SPI", name="SPI 执行器清单")
    if ok:
        spis = j["data"]
        skeys = {s["spiKey"] for s in spis}
        expect_keys = {"forecast", "market_analysis", "quote_strategy", "risk_measure",
                       "optimize", "settlement", "review", "rule_engine"}
        check("G6.5SPI", "内置执行器 8 项齐全", expect_keys.issubset(skeys) and len(skeys) >= 8,
              "keys=" + ",".join(sorted(skeys)))
        check("G6.5SPI", "执行器含类目标注", len(spis) > 0 and all(s.get("category") for s in spis),
              "n=%d" % len(spis))
    # 非法 spiKey 注册拒绝
    api("POST", "/algorithm/registry", admin,
        body={"algCode": f"BAD-SPI-{ts}", "algName": "坏执行器", "category": "forecast",
              "version": "v1.0", "spiKey": "no-such-spi", "status": "enabled"},
        group="G6.5SPI", name="非法 spiKey 注册拒绝", expect=500)
    # 决策执行：FORECAST-REPLACE(spiKey 空→类目兜底) → execution 留痕
    ok2, j2 = api("POST", "/decision/sessions", admin,
                  body={"sessionType": "rolling", "tradeDate": "2026-08-31", "scenario": "baseline"},
                  group="G6.5SPI", name="决策会话(SPI 类目兜底)")
    if ok2:
        ok3, j3 = api("GET", f"/decision/sessions/{j2['data']['sessionId']}/evidence", admin,
                      group="G6.5SPI", name="依据链(SPI 执行)")
        if ok3:
            fc = next((r for r in j3["data"].get("agents", []) if r.get("agentCode") == "forecast"), {})
            ex = (fc.get("algorithm") or {}).get("execution") or {}
            basis = str((fc.get("reasoning") or {}).get("basis", ""))
            check("G6.5SPI", "类目兜底执行 forecast 并留痕",
                  ex.get("spiKey") == "forecast" and bool(ex.get("summary")) and "执行：" in basis,
                  f"spi={ex.get('spiKey')} summary={str(ex.get('summary'))[-60:]} basis={basis[-80:]}")
    # 显式绑定跨类目 SPI（settlement）→ 执行器跟随绑定
    if algid:
        api("PUT", f"/algorithm/registry/{algid}", admin,
            body={"spiKey": "settlement"}, group="G6.5SPI", name="绑定跨类目 SPI")
        ok4, j4 = api("POST", "/decision/sessions", admin,
                      body={"sessionType": "rolling", "tradeDate": "2026-09-01", "scenario": "baseline"},
                      group="G6.5SPI", name="决策会话(显式 SPI)")
        if ok4:
            ok5, j5 = api("GET", f"/decision/sessions/{j4['data']['sessionId']}/evidence", admin,
                          group="G6.5SPI", name="依据链(显式 SPI)")
            if ok5:
                fc2 = next((r for r in j5["data"].get("agents", []) if r.get("agentCode") == "forecast"), {})
                ex2 = (fc2.get("algorithm") or {}).get("execution") or {}
                check("G6.5SPI", "执行器跟随显式绑定(spiKey=settlement)", ex2.get("spiKey") == "settlement"
                      and bool(ex2.get("summary")), f"spi={ex2.get('spiKey')} summary={str(ex2.get('summary'))[-60:]}")
        api("PUT", f"/algorithm/registry/{algid}", admin,
            body={"spiKey": ""}, group="G6.5SPI", name="恢复类目默认")

    # 汇总
    total = len(results)
    passed = sum(1 for r in results if r["ok"])
    print(f"TOTAL={total} PASS={passed} FAIL={total - passed}")
    for r in results:
        if not r["ok"]:
            print("FAIL:", r["group"], r["name"], r["note"][:200])
    with open("/home/odoo/workspace/PTIDSS/tests/full/verify_result_platform.json", "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=2, default=str)


if __name__ == "__main__":
    main()
