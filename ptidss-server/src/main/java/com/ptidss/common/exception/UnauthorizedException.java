package com.ptidss.common.exception;

/**
 * 未认证/令牌失效异常（映射业务码 14001）
 */
public class UnauthorizedException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public static final int UNAUTHORIZED_CODE = 14001;

    public UnauthorizedException(String message) {
        super(UNAUTHORIZED_CODE, message);
    }
}
