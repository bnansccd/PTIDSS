package com.troy.common.security.annotation;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:28
 * @Description: 角色认证：必须具有指定角色标识才能进入该方法
 * @Version: 1.0.0
 */
public @interface RequiresRoles {

    /**
     * 需要校验的角色标识
     */
    String[] value() default {};

    /**
     * 验证逻辑：AND | OR，默认AND
     */
    Logical logical() default Logical.AND;
}
