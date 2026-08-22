package com.troy.common.core.exception;

import com.troy.common.core.enums.ResultEnum;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:32
 * 验证码错误异常类
 */
public class CaptchaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CaptchaException(String msg) {
        super(msg);
    }

    public CaptchaException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
    }
}
