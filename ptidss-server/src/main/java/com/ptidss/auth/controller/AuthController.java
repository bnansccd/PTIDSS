package com.ptidss.auth.controller;

import com.ptidss.auth.dto.CaptchaResult;
import com.ptidss.auth.dto.CurrentUser;
import com.ptidss.auth.dto.LoginBody;
import com.ptidss.auth.dto.LoginResult;
import com.ptidss.auth.service.CaptchaService;
import com.ptidss.auth.service.SysLoginService;
import com.ptidss.common.annotation.Log;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证接口（对齐 OpenAPI V1.0 /auth 契约 + low-code-dev TokenController）
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysLoginService sysLoginService;
    private final CaptchaService captchaService;

    @Value("${ptidss.captcha.enabled:true}")
    private boolean captchaEnabled;

    public AuthController(SysLoginService sysLoginService, CaptchaService captchaService) {
        this.sysLoginService = sysLoginService;
        this.captchaService = captchaService;
    }

    /** 生成验证码 */
    @GetMapping("/captcha")
    public Result<CaptchaResult> captcha() {
        return Result.success(captchaService.generate());
    }

    /** 登录 */
    @Log(action = "login", targetType = "auth", recordArgs = false)
    @PostMapping("/login")
    public Result<LoginResult> login(@Validated @RequestBody LoginBody body) {
        if (captchaEnabled) {
            captchaService.verify(body.getCaptchaKey(), body.getCaptchaCode());
        }
        return Result.success("登录成功", sysLoginService.login(body.getUsername(), body.getPassword()));
    }

    /** 登出 */
    @Log(action = "logout", targetType = "auth")
    @PostMapping("/logout")
    public Result<Void> logout() {
        sysLoginService.logout();
        return Result.success();
    }

    /** 刷新令牌（refreshToken 传当前有效 accessToken，续期换新令牌，对齐 OpenAPI /auth/refresh 契约） */
    @PostMapping("/refresh")
    public Result<LoginResult> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new ServiceException("refreshToken 不能为空");
        }
        return Result.success("刷新成功", sysLoginService.refresh(refreshToken));
    }

    /** 当前用户信息（未登录返回 14001 统一错误码，对齐其他受保护端点契约） */
    @GetMapping("/current")
    public Result<CurrentUser> current() {
        CurrentUser cu = sysLoginService.currentUser();
        if (cu == null) {
            throw new UnauthorizedException("未登录或令牌已失效");
        }
        return Result.success(cu);
    }
}
