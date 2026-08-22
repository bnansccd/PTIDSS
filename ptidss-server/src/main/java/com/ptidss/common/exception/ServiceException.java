package com.ptidss.common.exception;

/**
 * 业务异常（对齐 low-code-dev ServiceException）
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 业务码 */
    private final int code;

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
