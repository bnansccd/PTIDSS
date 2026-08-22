package com.ptidss.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具（对齐 low-code-dev JwtUtils：HS256、secret 签名、claims 承载用户键）
 */
public class JwtUtils {

    private static String secret = "ptidss-jwt-secret-key-2026-change-me-in-production";

    private static long expireMillis = 120 * 60 * 1000L;

    public static void configure(String secretKey, long expireMinutes) {
        if (secretKey != null && !secretKey.isEmpty()) {
            secret = secretKey;
        }
        if (expireMinutes > 0) {
            expireMillis = expireMinutes * 60 * 1000L;
        }
    }

    public static String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public static String getUserKey(String token) {
        try {
            return parseToken(token).get(SecurityConstantsKeys.USER_KEY, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /** 内联常量避免循环依赖 */
    private static final class SecurityConstantsKeys {
        static final String USER_KEY = "user_key";
    }
}
