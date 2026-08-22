package com.troy.common.core.config;

/**
 * @author chenxl
 * @description
 * @date 2024-07-25 14:02
 */

import com.troy.common.core.utils.mask.SensitiveUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DataMaskingAspect {

    @Around("mask()")
    public Object doAfterReturning(ProceedingJoinPoint point) throws Throwable {
        // 执行目标方法
        Object returnValue = point.proceed();
        SensitiveUtils.handle(returnValue);
        return returnValue;
    }

    @Pointcut("@annotation(com.troy.common.core.anotation.Sensitive)")
    public void mask() {
    }


}

