package com.troy.common.core.exception.user;

import lombok.Data;

/**
 * @Description: 电子证照接口调用失败异常
 * @Author: zhuQing
 * @Date: 2024/6/25 15:47
 * @Version: 1.0
 **/
@Data
public class DZZZCallException extends RuntimeException{

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;


    public DZZZCallException(Integer code,String message) {
        this.message = message;
        this.code = code;
    }

    /**
     * 空构造方法，避免反序列化问题
     */
    public DZZZCallException() {
    }

    public DZZZCallException(String message) {
        this.message = message;
    }

}
