package com.ptidss.common.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ptidss.common.constant.Constants;
import com.ptidss.common.constant.SecurityConstants;
import com.ptidss.common.utils.IdUtils;
import com.ptidss.common.utils.JwtUtils;
import com.ptidss.common.utils.ServletUtils;
import com.ptidss.common.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 令牌服务（对齐 low-code-dev TokenService）：
 * 1) JWT 仅承载 user_key（uuid），用户会话实体缓存于本地 Caffeine（key=login_tokens:{uuid}）；
 * 2) 剩余有效期不足阈值时自动续期（滑动刷新）；
 * 3) 生产环境可将缓存实现切换为 Redis，模型与接口不变。
 */
@Slf4j
@Component
public class TokenService {

    private static final String TOKEN_KEY_PREFIX = "login_tokens:";

    @Value("${ptidss.token.secret}")
    private String secret;

    @Value("${ptidss.token.expire-minutes:120}")
    private long expireMinutes;

    @Value("${ptidss.token.refresh-threshold-minutes:10}")
    private long refreshThresholdMinutes;

    /** 令牌缓存（uuid → LoginUser） */
    private Cache<String, LoginUser> tokenCache;

    @PostConstruct
    public void init() {
        JwtUtils.configure(secret, expireMinutes);
        tokenCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(expireMinutes + refreshThresholdMinutes, TimeUnit.MINUTES)
                .build();
        log.info("TokenService 初始化完成：expire={}min, refreshThreshold={}min", expireMinutes, refreshThresholdMinutes);
    }

    /** 创建令牌：生成 uuid → 缓存 LoginUser → JWT 携带 user_key */
    public String createToken(LoginUser loginUser) {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireMinutes * 60 * 1000L);
        tokenCache.put(token, loginUser);
        return JwtUtils.createToken(buildClaims(loginUser));
    }

    /** 组装 JWT claims（user_key/用户ID/用户名/区域），创建与滑动续期重签共用 */
    private Map<String, Object> buildClaims(LoginUser loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.USER_KEY, loginUser.getToken());
        claims.put(SecurityConstants.DETAILS_USER_ID, loginUser.getUserid());
        claims.put(SecurityConstants.DETAILS_USERNAME, loginUser.getUsername());
        claims.put(SecurityConstants.DETAILS_REGION, loginUser.getRegionCode());
        return claims;
    }

    /** 从当前请求解析登录用户 */
    public LoginUser getLoginUser() {
        HttpServletRequest request = ServletUtils.getRequest();
        return request == null ? null : getLoginUser(request);
    }

    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        return token == null ? null : getLoginUser(token);
    }

    public LoginUser getLoginUser(String token) {
        if (StrUtils.isBlank(token)) {
            return null;
        }
        String userKey = JwtUtils.getUserKey(token);
        if (userKey == null) {
            return null;
        }
        LoginUser user = tokenCache.getIfPresent(userKey);
        if (user != null) {
            verifyToken(user);
        }
        return user;
    }

    /** 删除令牌缓存（登出）：参数为缓存 key（即 LoginUser.token / JWT 中 user_key） */
    public void delLoginUser(String token) {
        if (StrUtils.isNotBlank(token)) {
            tokenCache.invalidate(token);
        }
    }

    /**
     * 滑动续期：剩余有效期不足阈值时刷新缓存过期时间并重签 JWT（新 token 写入
     * loginUser.newToken，由拦截器经 X-New-Token 响应头回传前端替换），返回新 JWT；
     * 无需续期返回 null。修复"长时间操作点击刷新即退出"：JWT exp 与缓存同步滑动。
     */
    public String verifyToken(LoginUser loginUser) {
        loginUser.setNewToken(null);
        long remain = loginUser.getExpireTime() - System.currentTimeMillis();
        if (remain <= refreshThresholdMinutes * 60 * 1000L) {
            loginUser.setExpireTime(System.currentTimeMillis() + expireMinutes * 60 * 1000L);
            tokenCache.put(loginUser.getToken(), loginUser);
            String newToken = JwtUtils.createToken(buildClaims(loginUser));
            loginUser.setNewToken(newToken);
            return newToken;
        }
        return null;
    }

    /** 从请求头提取令牌（去掉 Bearer 前缀） */
    private String getToken(HttpServletRequest request) {
        String auth = request.getHeader(Constants.AUTHENTICATION);
        if (StrUtils.isNotBlank(auth) && auth.startsWith(Constants.TOKEN_PREFIX)) {
            return auth.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }
}
