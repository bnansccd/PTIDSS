package com.troy.common.core.exception;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:31
 * 内部认证异常
 */
public class InnerAuthException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InnerAuthException(String message) {
        super(message);
    }
}
