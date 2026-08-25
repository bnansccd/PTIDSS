#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""V2.3 操作友好性新接口验证：政策文件上传/下载、算法文件解析、日期宽松格式"""
import io
import json

import requests

BASE = "http://localhost:9080/ptidss"
REGION = "CN-32"
PASS = 0
FAIL = 0


def check(name, cond, note=""):
    global PASS, FAIL
    if cond:
        PASS += 1
        print(f"  [PASS] {name} {note}")
    else:
        FAIL += 1
        print(f"  [FAIL] {name} {note}")


def api(method, path, token=None, body=None, params=None, files=None, form=None):
    headers = {"X-Region-Code": REGION}
    if token:
        headers["Authorization"] = "Bearer " + token
    r = requests.request(method, BASE + path, headers=headers, json=body,
                         data=form, params=params, files=files, timeout=30)
    try:
        return r.status_code, r.json()
    except Exception:
        return r.status_code, {"raw": r.text[:200]}


# 1. 登录
r = requests.post(BASE + "/auth/login",
                  json={"username": "admin", "password": "Ptidss@2026"},
                  headers={"X-Region-Code": REGION}, timeout=20)
j = r.json()
token = j.get("data", {}).get("accessToken", "")
check("admin 登录", r.status_code == 200 and j.get("code") == 0, f"token={token[:20]}...")

# 2. 政策文件上传（multipart）
fake_pdf = b"%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n%%EOF\nV2.3 policy test content"
files = {"file": ("华东电力现货细则-测试.pdf", io.BytesIO(fake_pdf), "application/pdf")}
data = {"title": "华东电力现货细则（V2.3 上传验证）", "issuingBody": "华东能源监管局",
        "category": "regional", "tags": "现货,测试", "status": "published"}
code, j = api("POST", "/policy/upload-file", token, files=files, form=data)
doc = j.get("data", {}) if j.get("code") == 0 else {}
check("政策文件上传", code == 200 and j.get("code") == 0 and doc.get("fileUrl", "").startswith("local://"),
      f"id={doc.get('id')} fileUrl={doc.get('fileUrl')}")
policy_id = doc.get("id")

# 3. 政策文件下载
if policy_id:
    r = requests.get(f"{BASE}/policy/{policy_id}/file",
                     headers={"X-Region-Code": REGION, "Authorization": "Bearer " + token},
                     timeout=20)
    ok = r.status_code == 200 and r.content[:5] == b"%PDF-"
    check("政策文件下载（内容一致）", ok, f"http={r.status_code} len={len(r.content)}")
    cd = r.headers.get("Content-Disposition", "")
    check("下载文件名（Content-Disposition）", "filename*=UTF-8''" in cd, cd[:80])

# 4. 政策列表含本地文件标记
code, j = api("GET", "/policy/list", token, params={"keyword": "V2.3 上传验证"})
found = any(str(d.get("id")) == str(policy_id) and d.get("fileUrl", "").startswith("local://")
            for d in j.get("data", {}).get("list", [])) if j.get("code") == 0 else False
check("政策列表回显 fileUrl", found)

# 5. 算法文件解析（.json 参数模板 + 类目猜测）
alg_json = json.dumps({"horizon": 96, "confidence_band": 90, "lookback": 720}).encode()
files = {"file": ("price_forecast_lstm.json", io.BytesIO(alg_json), "application/json")}
code, j = api("POST", "/algorithm/parse-file", token, files=files)
d = j.get("data", {}) if j.get("code") == 0 else {}
check("算法 JSON 文件解析", code == 200 and j.get("code") == 0
      and d.get("algCode") == "PRICE-FORECAST-LSTM" and d.get("category") == "forecast"
      and "horizon" in d.get("paramsSchema", ""),
      f"code={d.get('algCode')} cat={d.get('category')} schema={d.get('paramsSchema')}")

# 6. 算法文件解析（.py 风险算法，正文关键字猜类目）
py_src = "# Monte Carlo CVaR risk measurement module\nimport numpy as np\n"
files = {"file": ("mc_cvar_risk.py", io.BytesIO(py_src.encode()), "text/x-python")}
code, j = api("POST", "/algorithm/parse-file", token, files=files)
d = j.get("data", {}) if j.get("code") == 0 else {}
check("算法 PY 文件解析（关键字猜类目）", code == 200 and j.get("code") == 0
      and d.get("category") == "risk_measure" and "Monte Carlo" in d.get("description", ""),
      f"cat={d.get('category')} desc={d.get('description', '')[:50]}")

# 7. 日期宽松格式：datetime-local（T 分隔）查询接口
code, j = api("GET", "/forecast/results", token,
              params={"predictType": "price", "tradeDate": "2026-08-21T14:30"})
check("预测结果 datetime-local 日期", code == 200 and j.get("code") == 0,
      f"count={len(j.get('data', []) or [])}")

# 8. 日期宽松格式：日期+时间（空格分隔）查询接口
code, j = api("GET", "/trade/rolling-plans", token, params={"tradeDate": "2026-08-21 14:30:00"})
check("滚动方案 空格时间日期", code == 200 and j.get("code") == 0)

# 9. 日期宽松格式：纯日期仍兼容
code, j = api("GET", "/review/reports", token, params={"periodStart": "2026-08-01"})
check("复盘报告 纯日期", code == 200 and j.get("code") == 0)

# 10. 政策 JSON 登记接口仍可用（无文件）
code, j = api("POST", "/policy/upload", token,
              body={"title": "JSON 登记兼容验证", "category": "national", "publishDate": "2026-08-22T09:00"})
check("政策 JSON 登记兼容", code == 200 and j.get("code") == 0,
      f"id={j.get('data', {}).get('id')}")

print(f"\nTOTAL={PASS + FAIL} PASS={PASS} FAIL={FAIL}")
