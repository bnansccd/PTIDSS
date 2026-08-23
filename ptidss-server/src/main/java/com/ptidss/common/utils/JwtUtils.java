package com.ptidss.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具（jjwt 0.13 API：HS256、SecretKey 签名、claims 承载用户键）
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
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(signingKey())
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String getUserKey(String token) {
        try {
            return parseToken(token).get(SecurityConstantsKeys.USER_KEY, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * HS256 签名密钥：密钥 >= 32 字节直接用；不足则 SHA-256 派生（兼容任意长度 TOKEN_SECRET）
     */
    private static SecretKey signingKey() {
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            try {
                raw = MessageDigest.getInstance("SHA-256").digest(raw);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 不可用", e);
            }
        }
        return Keys.hmacShaKeyFor(raw);
    }

    /** 内联常量避免循环依赖 */
    private static final class SecurityConstantsKeys {
        static final String USER_KEY = "user_key";
    }
}
