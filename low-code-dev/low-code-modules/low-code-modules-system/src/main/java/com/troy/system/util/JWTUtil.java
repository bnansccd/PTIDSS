package com.troy.system.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * @author sym
 * @description
 * @date 2023/11/3 14:24
 */

public class JWTUtil {
    public static String createJWT(Map<String, Object> headerClaims) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("zdwy");
        return JWT.create()
                .withIssuer("auth0")
                .withHeader(headerClaims)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() + 2*60 * 60 * 1000))
//                .withExpiresAt(new Date(new Date().getTime() + 1))
                .sign(algorithm);
    }

    /**
     * 解密 jwt
     *
     * @param token token
     * @return DecodedJWT
     * @throws JWTVerificationException     Invalid signature/claims
     * @throws UnsupportedEncodingException UTF-8 encoding not supported
     */
    public static DecodedJWT parseJWT(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("zdwy");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .acceptLeeway(1)
                .acceptExpiresAt(5)
                .build(); //Reusable verifier instance
        return verifier.verify(token);
    }
}
