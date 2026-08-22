package com.troy.gateway.filter;

import com.troy.common.core.constant.CacheConstants;
import com.troy.common.core.constant.HttpStatus;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.TokenConstants;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.utils.JwtUtils;
import com.troy.common.core.utils.ServletUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.redis.service.RedisService;
import com.troy.gateway.config.properties.IgnoreWhiteProperties;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 17:17:34
 * @Description: 网关鉴权
 * @Version: 1.0.0
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    // 排除过滤的 uri 地址，nacos自行添加
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Autowired
    private RedisService redisService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        String url = request.getURI().getPath();

        // 跳过不需要验证的路径
        if (StringUtils.matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        } else {
            String token = getToken(request);
            if (StringUtils.isEmpty(token)) {
                return unauthorizedResponse(exchange, ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.TOKEN).getMsg());
            }
            Claims claims = null;

            try {
                claims = JwtUtils.parseToken(token);
            } catch (Exception e) {
                return unauthorizedResponse(exchange, ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.TOKEN).getMsg());
            }

            if (claims == null) {
                return unauthorizedResponse(exchange, ResultEnum.getMsg(ResultEnum.EXPIRE,ResultConstants.TOKEN).getMsg());
            }
            String userkey = JwtUtils.getUserKey(claims);
            boolean islogin = redisService.hasKey(getTokenKey(userkey));
            if (!islogin) {
                return unauthorizedResponse(exchange, ResultEnum.getMsg(ResultEnum.EXPIRE,ResultConstants.LOGIN_STATUS).getMsg());
            }
            String userid = JwtUtils.getUserId(claims);
            String username = JwtUtils.getUserName(claims);
            if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(username)) {
                return unauthorizedResponse(exchange, ResultEnum.getMsg(ResultEnum.ERROR,ResultConstants.TOKEN).getMsg());
            }

            String departId = JwtUtils.getDepartId(claims);
            String departName = JwtUtils.getDepartName(claims);

            // 设置用户信息到请求
            addHeader(mutate, SecurityConstants.USER_KEY, userkey);
            addHeader(mutate, SecurityConstants.DETAILS_USER_ID, userid);
            addHeader(mutate, SecurityConstants.DETAILS_USERNAME, username);
            addHeader(mutate, SecurityConstants.DEPART_ID, departId);
            addHeader(mutate, SecurityConstants.DEPART_NAME, departName);
            // 内部请求来源参数清除
            removeHeader(mutate, SecurityConstants.FROM_SOURCE);
        }
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    private Mono<String>  resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){
        return serverHttpRequest.getBody()
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);  // 释放 DataBuffer
                    return new String(bytes);
                })
                .reduce(String::concat);
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtils.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    private void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        LOGGER.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 获取缓存key
     */
    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }

    /**
     * 获取请求token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, StringUtils.EMPTY);
        }
        return token;
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
