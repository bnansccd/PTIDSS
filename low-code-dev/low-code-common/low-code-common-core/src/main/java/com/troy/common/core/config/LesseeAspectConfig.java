package com.troy.common.core.config;

import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenxl
 * @date 2024-06-18 16:11
 */
@Aspect
@Component
public class LesseeAspectConfig {


    @Pointcut("@annotation(com.troy.common.core.anotation.Lessee)")
    public void lessee() {
    }
    @Before("lessee()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String lessee = request.getParameter("lessee_x");
            if (StringUtils.isNotEmpty(lessee)){
                long l = Long.parseLong(lessee);
                SecurityContextHolder.setTenantId(l);
            }
        }
    }

}
