package com.troy.common.security.annotation;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/15 16:16:36
 * @Description: 字典验证
 * @Version: 1.0.0
 */

import com.troy.common.core.enums.DictTypeEnums;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { ValidDictConstraintValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ValidDict {

    String message() default "{com.troy.common.security.annotation.ValidDict.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    DictTypeEnums parentType();
}
