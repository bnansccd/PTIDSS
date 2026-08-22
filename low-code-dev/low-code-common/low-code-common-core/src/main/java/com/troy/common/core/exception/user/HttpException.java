package com.troy.common.core.exception.user;

import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.utils.StringUtils;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:30
 * 业务异常
 */
public class HttpException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     * <p>
     * 和 {@link CommonResult#getDetailMessage()} 一致的设计
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public HttpException() {
    }

    public HttpException(String message) {
        this.message = message;
    }

    public HttpException(ResultEnum resultEnum) {
        this.message = resultEnum.getMsg();
        this.code = resultEnum.getCode();
    }

    public HttpException(ResultEnum resultEnum, Object... obj) {
        this.message = StringUtils.format(resultEnum.getMsg(), obj);
        this.code = resultEnum.getCode();
    }

    public HttpException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public HttpException setMessage(String message) {
        this.message = message;
        return this;
    }

    public HttpException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }
}
