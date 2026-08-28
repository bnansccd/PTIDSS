"""
PTIDSS 长期验证公共模块（V2.5 遗留建议②：90 天长期验证日/周/月/季脚本共用）
- 登录（captcha 生产开启时需验证码，本模块支持直接账号密码模式：测试态 captcha=false；
  生产建议配置专用验证账号或通过 nginx 内网直连）
- 统一 API 请求封装（超时/JSON/异常兜底）
- 报告输出：reports/{period}_{ts}.json + 控制台摘要；指标含目标值比较与 PASS/WARN/FAIL
"""
import json
import os
import sys
import time
import requests

BASE = os.environ.get("PTIDSS_BASE", "http://localhost:9080/ptidss")
REGION = os.environ.get("PTIDSS_REGION", "CN-32")
USER = os.environ.get("PTIDSS_USER", "admin")
PWD = os.environ.get("PTIDSS_PWD", "Ptidss@2026")
REPORT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "reports")


def login(username=USER, password=PWD):
    try:
        r = requests.post(BASE + "/auth/login",
                          json={"username": username, "password": password},
                          headers={"X-Region-Code": REGION}, timeout=20)
        j = r.json()
        if j.get("code") == 0:
            return j["data"]["accessToken"]
        return None
    except Exception as e:
        print("登录异常:", e)
        return None


def api(token, method, path, body=None, params=None, timeout=20):
    headers = {"X-Region-Code": REGION}
    if token:
        headers["Authorization"] = "Bearer " + token
    try:
        r = requests.request(method, BASE + path, headers=headers,
                             json=body, params=params, timeout=timeout)
        return r.status_code, r.json()
    except Exception as e:
        return 0, {"code": -1, "message": "EXC:" + str(e)}


def save_report(name, payload):
    os.makedirs(REPORT_DIR, exist_ok=True)
    path = os.path.join(REPORT_DIR, name)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(payload, f, ensure_ascii=False, indent=2)
    return path


def summary(items):
    """items: [(指标名, 值, 目标, 方向)] 方向 True=越高越好 / False=越低越好"""
    lines = []
    all_pass = True
    for name, value, target, higher_better in items:
        if isinstance(value, (int, float)) and isinstance(target, (int, float)):
            ok = value >= target if higher_better else value <= target
            status = "PASS" if ok else "FAIL"
            if not ok:
                all_pass = False
            lines.append(f"  [{status}] {name}: {value}（目标 {'≥' if higher_better else '≤'} {target}）")
        else:
            lines.append(f"  [INFO] {name}: {value}")
    print("\n".join(lines))
    return all_pass


def need(cond, msg):
    if not cond:
        print("FATAL:", msg)
        sys.exit(2)
