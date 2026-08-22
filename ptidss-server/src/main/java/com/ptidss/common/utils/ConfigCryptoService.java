package com.ptidss.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * 连接参数字段级加密（P2：连接参数字段级加密）
 * 敏感字段白名单（精确匹配 key，递归覆盖嵌套结构）：password/secret/apiKey/accessKey/token/
 * refreshToken/clientSecret/appSecret/privateKey/secretKey/key 等；tokenRef/endpoint/url 等
 * 引用型与地址型字段不在白名单，保持明文可见。
 * 加密算法 AES-128-CBC/PKCS5Padding（密钥由 SHA-256 派生自 ptidss.security.config-secret-key，
 * 部署环境可覆盖；默认值为开发密钥，生产必须替换）。
 * 存储格式 enc:&lt;base64(iv+cipher)&gt;；以 enc: 前缀视为已加密（幂等，不重复加密）。
 * 对外 API 一律经 maskFields 脱敏（敏感字段 → ******），明文与密文均不外泄；
 * 采集执行方（内部）经 decryptFields 还原后再消费。
 */
@Slf4j
@Component
public class ConfigCryptoService {

    /** 敏感字段白名单（字段级加密/脱敏边界） */
    private static final Set<String> SENSITIVE_KEYS = new HashSet<>(Arrays.asList(
            "password", "pwd", "secret", "apiKey", "api_key", "accessKey", "access_key",
            "token", "refreshToken", "refresh_token", "clientSecret", "client_secret",
            "appSecret", "app_secret", "privateKey", "private_key", "secretKey", "secret_key", "key"));

    private static final String PREFIX = "enc:";
    private static final String MASK = "******";
    private static final String ALGO = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec keySpec;

    public ConfigCryptoService(
            @Value("${ptidss.security.config-secret-key:ptidss-config-secret-key-2026-dev}") String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            this.keySpec = new SecretKeySpec(Arrays.copyOf(md.digest(secret.getBytes(StandardCharsets.UTF_8)), 16), "AES");
        } catch (Exception e) {
            throw new IllegalStateException("连接参数加密密钥初始化失败", e);
        }
    }

    /** 保存侧：JSON 配置中敏感字段值加密（enc: 前缀，幂等；非 JSON 原样返回） */
    public String encryptFields(String jsonConfig) {
        if (StrUtils.isBlank(jsonConfig)) {
            return jsonConfig;
        }
        Object root = tryParse(jsonConfig);
        if (root == null) {
            return jsonConfig;
        }
        walk(root, true, false);
        return JSON.toJSONString(root);
    }

    /** 消费侧（内部）：解密 enc: 前缀字段值；解密失败保留原文（兼容旧明文数据） */
    public String decryptFields(String jsonConfig) {
        if (StrUtils.isBlank(jsonConfig)) {
            return jsonConfig;
        }
        Object root = tryParse(jsonConfig);
        if (root == null) {
            return jsonConfig;
        }
        walk(root, false, false);
        return JSON.toJSONString(root);
    }

    /** 对外脱敏：敏感字段值一律 ******（明文与密文均不外泄） */
    public String maskFields(String jsonConfig) {
        if (StrUtils.isBlank(jsonConfig)) {
            return jsonConfig;
        }
        Object root = tryParse(jsonConfig);
        if (root == null) {
            return jsonConfig;
        }
        walk(root, false, true);
        return JSON.toJSONString(root);
    }

    /**
     * 编辑回显合并：提交值中敏感字段为 ****** 表示"未修改"（保留库中原值），
     * 其余字段以提交值为准；返回合并后的 JSON（调用方再 encryptFields 落库）。
     */
    public String mergeMasked(String stored, String submitted) {
        if (StrUtils.isBlank(submitted)) {
            return submitted;
        }
        Object subRoot = tryParse(submitted);
        if (subRoot == null) {
            return submitted;
        }
        Object oldRoot = tryParse(stored);
        walk(subRoot, false, false);
        if (oldRoot != null) {
            mergeRecursive(oldRoot, subRoot);
        }
        return JSON.toJSONString(subRoot);
    }

    @SuppressWarnings("unchecked")
    private void mergeRecursive(Object oldNode, Object newNode) {
        if (oldNode instanceof JSONObject && newNode instanceof JSONObject) {
            JSONObject oldObj = (JSONObject) oldNode;
            JSONObject newObj = (JSONObject) newNode;
            for (String key : newObj.keySet()) {
                if (!newObj.containsKey(key)) {
                    continue;
                }
                Object newVal = newObj.get(key);
                if (MASK.equals(newVal)) {
                    // 脱敏占位 → 保留库中原值（含加密态）
                    newObj.put(key, oldObj.get(key));
                } else if (newVal instanceof JSONObject || newVal instanceof JSONArray) {
                    mergeRecursive(oldObj.get(key), newVal);
                }
            }
        } else if (oldNode instanceof JSONArray && newNode instanceof JSONArray) {
            JSONArray oldArr = (JSONArray) oldNode;
            JSONArray newArr = (JSONArray) newNode;
            for (int i = 0; i < newArr.size() && i < oldArr.size(); i++) {
                mergeRecursive(oldArr.get(i), newArr.get(i));
            }
        }
    }

    /** 递归遍历：encrypt 模式加密敏感字段值；mask 模式脱敏敏感字段值；否则尝试解密 enc: 前缀 */
    @SuppressWarnings("unchecked")
    private void walk(Object node, boolean encrypt, boolean mask) {
        if (node instanceof JSONObject) {
            JSONObject obj = (JSONObject) node;
            for (String key : obj.keySet()) {
                Object val = obj.get(key);
                if (val instanceof JSONObject || val instanceof JSONArray) {
                    walk(val, encrypt, mask);
                } else if (val instanceof String && isSensitive(key)) {
                    String s = (String) val;
                    if (StrUtils.isBlank(s)) {
                        continue;
                    }
                    if (encrypt) {
                        obj.put(key, s.startsWith(PREFIX) ? s : PREFIX + encryptValue(s));
                    } else if (mask) {
                        obj.put(key, MASK);
                    } else if (s.startsWith(PREFIX)) {
                        obj.put(key, decryptValue(s.substring(PREFIX.length())));
                    }
                }
            }
        } else if (node instanceof JSONArray) {
            for (Object item : (JSONArray) node) {
                walk(item, encrypt, mask);
            }
        }
    }

    private boolean isSensitive(String key) {
        return SENSITIVE_KEYS.contains(key);
    }

    private String encryptValue(String raw) {
        try {
            byte[] iv = new byte[16];
            new java.security.SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] out = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + out.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(out, 0, payload, iv.length, out.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            log.warn("连接参数字段加密失败，保留明文（key={}）", raw.length() > 4 ? raw.substring(0, 4) : "****", e);
            return raw;
        }
    }

    private String decryptValue(String b64) {
        try {
            byte[] payload = Base64.getDecoder().decode(b64);
            byte[] iv = Arrays.copyOfRange(payload, 0, 16);
            byte[] data = Arrays.copyOfRange(payload, 16, payload.length);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("连接参数字段解密失败，保留原文（可能为旧明文或密钥变更）");
            return PREFIX + b64;
        }
    }

    private Object tryParse(String json) {
        try {
            return JSON.parse(json);
        } catch (Exception e) {
            return null;
        }
    }
}
