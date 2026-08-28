#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""PTIDSS 生产环境 (nginx:80) 登录页验证脚本
验证: 标题 / 登录表单 / 验证码渲染与刷新 / 控制台 error/warn / 网络请求状态
"""
import json
import time
from playwright.sync_api import sync_playwright

URL = "http://127.0.0.1:80/"
SHOT = "/home/odoo/workspace/prod_nginx_login.png"
SHOT2 = "/home/odoo/workspace/prod_nginx_login_after_refresh.png"

console_msgs = []
failed_reqs = []
bad_responses = []
captcha_src_before = None
captcha_src_after = None


def main():
    global captcha_src_before, captcha_src_after
    with sync_playwright() as p:
        browser = p.chromium.launch(
            headless=True,
            args=["--no-sandbox", "--disable-dev-shm-usage"],
        )
        context = browser.new_context(
            viewport={"width": 1440, "height": 900},
            ignore_https_errors=True,
        )
        page = context.new_page()

        # ---- 监听控制台 ----
        def on_console(msg):
            if msg.type in ("error", "warning"):
                console_msgs.append({"type": msg.type, "text": msg.text, "loc": msg.location})

        page.on("console", on_console)

        # ---- 监听请求失败 ----
        def on_requestfailed(req):
            failed_reqs.append({
                "url": req.url,
                "failure": req.failure,
            })

        page.on("requestfailed", on_requestfailed)

        # ---- 监听 >=400 响应 ----
        def on_response(resp):
            if resp.status >= 400:
                bad_responses.append({"url": resp.url, "status": resp.status})

        page.on("response", on_response)

        # ---- 访问页面 ----
        page.goto(URL, wait_until="networkidle", timeout=30000)
        page.wait_for_timeout(1500)  # 等待 SPA 渲染

        result = {"url": URL}

        # 1. 标题
        result["title"] = page.title()

        # 2. 登录表单元素
        inputs = page.locator("input")
        input_info = []
        for i in range(inputs.count()):
            el = inputs.nth(i)
            input_info.append({
                "type": el.get_attribute("type"),
                "name": el.get_attribute("name"),
                "placeholder": el.get_attribute("placeholder"),
                "visible": el.is_visible(),
            })
        result["inputs"] = input_info

        buttons = page.locator("button")
        btn_info = []
        for i in range(buttons.count()):
            el = buttons.nth(i)
            btn_info.append({
                "text": (el.inner_text() or "").strip(),
                "visible": el.is_visible(),
            })
        result["buttons"] = btn_info

        # 3. 验证码图片
        captcha = page.locator("img").all()
        img_info = []
        captcha_el = None
        for el in captcha:
            src = el.get_attribute("src") or ""
            info = {
                "src": src,
                "src_prefix": src[:40],
                "naturalWidth": el.evaluate("(e) => e.naturalWidth"),
                "naturalHeight": el.evaluate("(e) => e.naturalHeight"),
                "complete": el.evaluate("(e) => e.complete"),
                "visible": el.is_visible(),
                "displayed_w": el.evaluate("(e) => e.getBoundingClientRect().width"),
                "displayed_h": el.evaluate("(e) => e.getBoundingClientRect().height"),
            }
            img_info.append(info)
            if "captcha" in (el.get_attribute("class") or "").lower() or "captcha" in src.lower():
                captcha_el = el
        result["imgs"] = img_info

        if captcha_el is None and img_info:
            # 兜底: 选最后一个 img (通常是验证码)
            captcha_el = page.locator("img").nth(len(img_info) - 1)

        # 4. 截图(点击前)
        page.screenshot(path=SHOT, full_page=False)

        # 5. 点击验证码刷新
        if captcha_el is not None:
            captcha_src_before = captcha_el.get_attribute("src")
            try:
                captcha_el.click(timeout=3000)
                page.wait_for_timeout(1200)
                captcha_src_after = captcha_el.get_attribute("src")
            except Exception as e:
                result["captcha_click_error"] = str(e)
            result["captcha_src_before"] = captcha_src_before
            result["captcha_src_after"] = captcha_src_after
            result["captcha_src_changed"] = captcha_src_before != captcha_src_after
            result["captcha_el_attrs"] = {
                "class": captcha_el.get_attribute("class"),
                "id": captcha_el.get_attribute("id"),
                "width": captcha_el.get_attribute("width"),
                "height": captcha_el.get_attribute("height"),
            }
            # 验证码刷新后的 naturalWidth (重新读取)
            result["captcha_after_natural"] = {
                "naturalWidth": captcha_el.evaluate("(e) => e.naturalWidth"),
                "naturalHeight": captcha_el.evaluate("(e) => e.naturalHeight"),
            }

        # 6. 截图(点击后)
        page.wait_for_timeout(500)
        page.screenshot(path=SHOT2, full_page=False)

        # 7. 标题补充: 静态HTML标题 vs SPA渲染后标题
        result["title_initial_html"] = page.evaluate(
            "() => document.querySelector('head title') ? document.querySelector('head title').textContent : null"
        )

        # 6. 控制台/网络汇总
        result["console"] = console_msgs
        result["failed_requests"] = failed_reqs
        result["bad_responses"] = bad_responses

        browser.close()

    print(json.dumps(result, ensure_ascii=False, indent=2, default=str))


if __name__ == "__main__":
    main()
