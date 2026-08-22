package com.troy.common.oauth2.annotation;

import java.lang.annotation.*;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 11:11:44
 * @Description: 自定义操作日志记录注解
 * @Version: 1.0.0
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OauthApi {

}
