package com.troy.common.datasource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 用于字段的数据一致性校验（忠县）
 * @Author: zhuQing
 * @Date: 2026/4/1 15:51
 * @Version: 1.0
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consistency {

}

