package com.troy.common.security.annotation;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:18
 * @Description: 权限注解的验证模式
 * @Version: 1.0.0
 */
public enum Logical {

    /**
     * 必须具有所有的元素
     */
    AND,

    /**
     * 只需具有其中一个元素
     */
    OR
}
