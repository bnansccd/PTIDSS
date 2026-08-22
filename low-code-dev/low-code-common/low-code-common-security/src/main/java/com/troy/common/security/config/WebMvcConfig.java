package com.troy.common.security.config;

import com.troy.common.security.interceptor.HeaderInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:39
 * @Description: 拦截器配置
 * @Version: 1.0.0
 */
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${system.enable.tenantId:true}")
    private Boolean isOpen;

    /**
     * 不需要拦截地址
     */
    public static final String[] excludeUrls = {"/api/web/v1/login", "/api/web/v1/logout", "/api/web/v1/refresh"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (isOpen){
            registry.addInterceptor(getHeaderInterceptor())
                    .addPathPatterns("/**")
                    .excludePathPatterns(excludeUrls)
                    .order(-10);
        }
    }

    /**
     * 自定义请求头拦截器
     */
    public HeaderInterceptor getHeaderInterceptor() {
        return new HeaderInterceptor();
    }
}
