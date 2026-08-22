package com.troy.common.core.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenxl
 * @description
 * @date 2024-07-24 15:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SensitiveData {
    SensitiveType type();

    enum SensitiveType {
        PHONE, ID_CARD, LICENSE_PLATE, EMAIL, CREDIT_CODE, DEFAULT
    }
}
