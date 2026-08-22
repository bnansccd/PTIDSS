package com.ptidss.auth.dto;

import lombok.Data;

/**
 * 验证码响应
 */
@Data
public class CaptchaResult {

    /** 验证码键（登录时回传） */
    private String captchaKey;

    /** 验证码图片（Base64，data:image/png;base64, 前缀） */
    private String image;
}
