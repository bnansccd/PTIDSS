package com.ptidss.common.config;

import com.ptidss.common.security.HeaderInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web MVC 配置：注册请求头拦截器 + 跨域（开发环境前端 Vite 直连；生产经 CORS_ALLOWED_ORIGINS 收紧白名单）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HeaderInterceptor headerInterceptor;

    /**
     * 允许跨域来源白名单（逗号分隔）：
     * - 开发/未配置：* （仅允许无凭据场景，安全起见生产必须显式配置）
     * - 生产：CORS_ALLOWED_ORIGINS=https://ptidss.example.com,https://admin.example.com
     */
    @Value("${ptidss.security.cors-allowed-origins:*}")
    private String allowedOrigins;

    public WebMvcConfig(HeaderInterceptor headerInterceptor) {
        this.headerInterceptor = headerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/captcha", "/auth/login", "/error");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
        registry.addMapping("/**")
                .allowedOriginPatterns(origins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
