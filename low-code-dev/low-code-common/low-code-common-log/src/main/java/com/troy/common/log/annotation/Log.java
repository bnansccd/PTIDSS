package com.troy.common.log.annotation;

import com.troy.common.log.enums.BusinessType;
import com.troy.common.log.enums.OperatorType;

import java.lang.annotation.*;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 11:11:44
 * @Description: 自定义操作日志记录注解
 * @Version: 1.0.0
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块
     */
    public String title() default "";

    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;
}
