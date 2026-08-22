package com.troy.common.core.exception.user;


/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:34
 * 验证码失效异常类
 */
public class CaptchaExpireException extends UserException {

    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire", null);
    }
}
