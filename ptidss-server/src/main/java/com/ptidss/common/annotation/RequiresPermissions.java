package com.ptidss.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解（对齐 low-code-dev @RequiresPermissions）：
 * 作用于 Controller 方法，拥有任一 perm 即可通过；admin 角色放行
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {

    /** 权限编码（如 menu:admin / api:declaration） */
    String[] value();
}
