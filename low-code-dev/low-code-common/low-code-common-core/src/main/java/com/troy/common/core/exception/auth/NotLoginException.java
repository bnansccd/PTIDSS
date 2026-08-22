package com.troy.common.core.exception.auth;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:16
 * 未能通过的登录认证异常
 */
public class NotLoginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotLoginException(String message) {
        super(message);
    }
}
