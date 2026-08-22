package com.ptidss.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计日志注解（对齐 low-code-dev @Log + low-code-common-log LogAspect）：
 * 标注后由 LogAspect 异步写入 audit_log 表（含操作人/IP/入参/出参/结果/区域）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /** 操作动作（如 user_create / role_update / login） */
    String action();

    /** 目标类型（如 sys_user / sys_role / contract） */
    String targetType() default "";

    /** 是否记录入参快照（默认记录，含敏感字段时置 false） */
    boolean recordArgs() default true;
}
