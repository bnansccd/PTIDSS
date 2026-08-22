package com.troy.common.security.annotation;

import java.lang.annotation.*;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:18
 * @Description: 内部认证注解
 * @Version: 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerAuth {
    /**
     * 是否校验用户信息
     */
    boolean isUser() default false;
}
