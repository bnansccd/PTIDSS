package com.ptidss.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求体
 */
@Data
public class LoginBody {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码键（GET /auth/captcha 返回） */
    private String captchaKey;

    /** 验证码值 */
    private String captchaCode;
}
