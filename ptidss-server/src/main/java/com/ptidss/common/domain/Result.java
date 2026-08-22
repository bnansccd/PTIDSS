package com.ptidss.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体（对齐 OpenAPI V1.0 ApiResponse 契约：code=0 成功）
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 业务码：0 成功；非 0 失败（14001 未认证/令牌失效） */
    private int code;

    /** 消息 */
    private String message;

    /** 数据负载 */
    private T data;

    /** 链路追踪 ID */
    private String traceId;

    public static <T> Result<T> success() {
        return build(0, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return build(0, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return build(0, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return build(500, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return build(code, message, null);
    }

    public static <T> Result<T> build(int code, String message, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    public boolean isSuccess() {
        return this.code == 0;
    }
}
