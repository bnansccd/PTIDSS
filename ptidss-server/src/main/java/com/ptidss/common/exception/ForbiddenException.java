package com.ptidss.common.exception;

/**
 * 无权限访问异常（映射业务码 14003）
 */
public class ForbiddenException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public static final int FORBIDDEN_CODE = 14003;

    public ForbiddenException(String message) {
        super(FORBIDDEN_CODE, message);
    }
}
