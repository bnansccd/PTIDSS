package com.troy.gateway.config;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
public class TraceMethodWebFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String method = exchange.getRequest().getMethod().name(); // Spring 6.2+：getMethodValue() 已移除，改用 getMethod().name()
        ServerHttpResponse response = exchange.getResponse();

        // 禁用 TRACE 和 TRACK 方法
        if ("TRACE".equalsIgnoreCase(method) || "TRACK".equalsIgnoreCase(method)) {
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete(); // 直接拦截，终止请求处理
        }

        return chain.filter(exchange); // 继续处理其他请求
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;  // 设置高优先级，确保最先执行
    }
}
