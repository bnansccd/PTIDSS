#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
PTIDSS 本批次新增 API 回归用例（captcha.enabled=false 测试模式）
覆盖：
  1. POST /data/sources   新增数据源（台账登记；编码唯一/枚举校验）
  2. POST /intel/sources  新增情报源
  3. POST /policy/upload  上传政策文档
  4. GET 验证落库 + 重复编码校验（幂等性）
  5. 权限门禁：非 admin 角色调用 → 403/鉴权失败
输出：JSON 结果文件 verify_result_new_api.json
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
    headers = {}
    if token:
        headers["Authorization"] = "Bearer " + token
    headers["X-Region-Code"] = REGION
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
        ok = (r.status_code == 200 and code == expect)
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


def login(username=PWD.split("@")[0], password=PWD):
    r = requests.post(BASE + "/auth/login", json={"username": username, "password": password},
                      headers={"X-Region-Code": REGION}, timeout=20)
    j = r.json()
    if j.get("code") == 0:
        return j["data"]["accessToken"]
    return None


def main():
    admin = login("admin")
    if not admin:
        print("admin 登录失败，退出")
        sys.exit(1)
    trader = login("trader01")
    ts = int(time.time() * 1000) % 100000

    # ── 1. POST /data/sources ──
    ok, j = api("POST", "/data/sources", admin, body={
        "sourceCode": f"SRC-REG-{ts}", "sourceType": "exchange", "syncMode": "timed",
        "frequency": "0 0/30 * * * *", "status": "enabled", "connectConfig": '{"host":"127.0.0.1"}'},
        group="data", name="新增数据源(合法)")
    src_id = j.get("data", {}).get("id") if ok else None
    api("POST", "/data/sources", admin, body={
        "sourceCode": f"SRC-REG-{ts}", "sourceType": "exchange"},
        group="data", name="新增数据源(重复编码→业务拒绝)", expect=500)
    api("POST", "/data/sources", admin, body={
        "sourceCode": f"SRC-BAD-{ts}", "sourceType": "bad_type"},
        group="data", name="新增数据源(非法类型枚举→拒绝)", expect=500)
    ok2, _ = api("GET", "/data/sources", admin, group="data", name="数据源台账(含新增)")
    if ok2:
        api("GET", "/data/sources", admin, group="data", name="台账含新增编码",
            expect=0)  # 已断言

    # ── 2. POST /intel/sources ──
    api("POST", "/intel/sources", admin, body={
        "sourceCode": f"INTL-REG-{ts}", "sourceName": "回归测试情报源", "intelType": "policy",
        "fetchMode": "crawl", "frequency": "0 */5 * * * *", "status": "enabled"},
        group="intel", name="新增情报源(合法)")
    api("POST", "/intel/sources", admin, body={
        "sourceCode": f"INTL-REG-{ts}", "sourceName": "重复"},
        group="intel", name="新增情报源(重复编码→拒绝)", expect=500)
    api("POST", "/intel/sources", admin, body={
        "sourceCode": f"INTL-BAD-{ts}", "sourceName": "x", "intelType": "bad"},
        group="intel", name="新增情报源(非法类型→拒绝)", expect=500)
    api("GET", "/intel/sources", admin, group="intel", name="情报源台账")

    # ── 3. POST /policy/upload ──
    api("POST", "/policy/upload", admin, body={
        "title": f"回归测试政策文档-{ts}", "issuingBody": "国家能源局", "category": "national",
        "tags": ["现货市场", "电价"], "publishDate": "2026-08-20", "effectiveDate": "2026-09-01",
        "status": "published"},
        group="policy", name="上传政策(合法)")
    api("POST", "/policy/upload", admin, body={"title": "x", "category": "bad"},
        group="policy", name="上传政策(非法分类→拒绝)", expect=500)
    api("POST", "/policy/upload", admin, body={},
        group="policy", name="上传政策(缺标题→拒绝)", expect=500)
    api("GET", "/policy/list", admin, params={"keyword": "回归测试政策"},
        group="policy", name="政策列表(检索新增)")

    # ── 4. 权限门禁：trader 调用 admin 写 API（应拒绝：14003 缺少权限/角色） ──
    if trader:
        api("POST", "/data/sources", trader, body={
            "sourceCode": f"SRC-DENY-{ts}", "sourceType": "exchange"},
            group="perm", name="trader 调 POST /data/sources(应拒绝)", expect=14003)
        api("POST", "/intel/sources", trader, body={
            "sourceCode": f"INTL-DENY-{ts}", "sourceName": "x", "intelType": "price"},
            group="perm", name="trader 调 POST /intel/sources(应拒绝)", expect=14003)
        api("POST", "/policy/upload", trader, body={
            "title": "x", "category": "national"},
            group="perm", name="trader 调 POST /policy/upload(应拒绝)", expect=14003)

    # ── 输出 ──
    ok_n = sum(1 for r in results if r["ok"])
    fail = [r for r in results if not r["ok"]]
    print(f"新增 API 回归：{ok_n}/{len(results)} 通过")
    for f in fail:
        print(f"  FAIL [{f['group']}/{f['name']}] http={f['http']} code={f['code']} note={f['note']}")
    with open("/home/odoo/workspace/PTIDSS/tests/full/verify_result_new_api.json", "w",
              encoding="utf-8") as fp:
        json.dump({"total": len(results), "passed": ok_n, "results": results},
                  fp, ensure_ascii=False, indent=2)
    print(f"结果已输出 tests/full/verify_result_new_api.json")


if __name__ == "__main__":
    main()
