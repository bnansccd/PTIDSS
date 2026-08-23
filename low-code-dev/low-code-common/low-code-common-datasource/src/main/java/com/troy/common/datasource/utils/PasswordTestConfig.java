package com.troy.common.datasource.utils;

import com.troy.common.core.utils.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description: 忠县密码机服务类（平替版）
 * @Author: zhuQing
 * @Date: 2026/4/1 16:00
 * @Version: 2.0
 * <p>
 * 原实现基于江南天安密码机 SDK（com.sansec.sdk，私有 nexus artifact，已无法获取），
 * 现平替为 Bouncy Castle 国密算法纯软件实现（SM4/SM3），对外方法签名完全兼容。
 * SM4 密钥通过环境变量/系统属性 ptidss.crypto.sm4.secret（Base64 编码 32 字节）注入，
 * 生产环境必须显式配置；未配置时使用内置开发密钥并输出告警日志。
 **/
public class PasswordTestConfig {
    private static final Logger log = LoggerFactory.getLogger(PasswordTestConfig.class);

    /** 国密算法提供者 */
    private static final String PROVIDER = "BC";
    /** SM4 密钥系统属性/环境变量名（Base64 编码 32 字节） */
    private static final String SM4_SECRET_PROP = "ptidss.crypto.sm4.secret";
    /** 开发用默认密钥（仅本地联调，生产必须通过环境变量注入） */
    private static final String DEV_SM4_SECRET = "ZGV2LXNtNC1rZXktMDAtZm9yLXB0aWRzcy1kZXZlbG9wbWVudA==";

    private static final SecretKeySpec SM4_KEY = loadSm4Key();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        if (Security.getProvider(PROVIDER) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private static SecretKeySpec loadSm4Key() {
        String secret = System.getProperty(SM4_SECRET_PROP);
        if (StringUtils.isBlank(secret)) {
            secret = System.getenv(SM4_SECRET_PROP);
        }
        byte[] keyBytes;
        if (StringUtils.isNotBlank(secret)) {
            keyBytes = Base64.getDecoder().decode(secret);
        } else {
            log.warn("未配置 SM4 密钥（{}），使用内置开发密钥，生产环境必须通过环境变量注入！", SM4_SECRET_PROP);
            keyBytes = Base64.getDecoder().decode(DEV_SM4_SECRET);
        }
        if (keyBytes.length != 32) {
            throw new IllegalStateException("SM4 密钥长度必须为 32 字节（Base64 解码后），当前 " + keyBytes.length);
        }
        return new SecretKeySpec(keyBytes, "SM4");
    }

    /**
     * 前置信息：开通加解密服务
     * SM4对称加密，用于机密性保护
     *
     * @param plainText 要加密的明文数据字符串（字节数组可自行封装）
     * @return 密文，base64编码（格式：Base64(随机IV[16字节] + 密文)）
     */
    public static String symmetricEncrypt(String plainText) {
        if (StringUtils.isBlank(plainText)) {
            return plainText;
        }
        try {
            // 随机 IV，每次加密生成，随密文一起返回（原密码机 SDK 由服务端管理 IV，平替版改为标准 CBC 模式）
            byte[] iv = new byte[16];
            SECURE_RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, SM4_KEY, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            log.error("SM4加密失败", e);
            throw new RuntimeException("SM4加密失败", e);
        }
    }

    /**
     * 前置信息：开通加解密服务
     * SM4对称解密
     *
     * @param cipherText 要解密的密文数据，base64编码（格式：Base64(随机IV[16字节] + 密文)）
     * @return 原文字符串
     */
    public static String symmetricDecrypt(String cipherText) {
        try {
            if (StringUtils.isBlank(cipherText)) {
                return cipherText;
            }
            byte[] data = Base64.getDecoder().decode(cipherText);
            if (data.length <= 16) {
                log.warn("SM4密文长度非法，无法解密");
                return cipherText;
            }
            byte[] iv = new byte[16];
            System.arraycopy(data, 0, iv, 0, 16);
            Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, SM4_KEY, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(data, 16, data.length - 16);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密失败", e);
        }
        return cipherText;
    }

    /**
     * 使用hmac算法生成完整性校验数据（HMAC-SM3）
     *
     * @param plainText 明文数据字符串
     * @return hmac值，base64编码
     */
    public static String hmac(String plainText) {
        if (StringUtils.isBlank(plainText)) {
            return plainText;
        }
        try {
            Mac mac = Mac.getInstance("HMAC-SM3", PROVIDER);
            mac.init(SM4_KEY);
            return Base64.getEncoder().encodeToString(mac.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("HMAC-SM3 计算失败", e);
            throw new RuntimeException("HMAC-SM3 计算失败", e);
        }
    }

    /**
     * 使用hmac算法进行完整性校验
     *
     * @param plainText 明文数据字符串
     * @param hmac      hmac值，base64编码
     * @return 是否验证通过
     */
    public static boolean hmacVerify(String plainText, String hmac) {
        if (StringUtils.isBlank(plainText) || StringUtils.isBlank(hmac)) {
            return false;
        }
        String expect = hmac(plainText);
        return MessageDigest.isEqual(expect.getBytes(StandardCharsets.UTF_8), hmac.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 使用SM3算法进行杂凑运算
     *
     * @param plainText 明文数据字符串
     * @return 摘要值（SM3 十六进制摘要）
     */
    public static String digest(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SM3", PROVIDER);
            byte[] bytes = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("SM3 摘要计算失败", e);
            throw new RuntimeException("SM3 摘要计算失败", e);
        }
    }

    /**
     * 使用sm4算法进行批量加密
     *
     * @param inDataMap 键为自定义字符串，值为要加密的明文数据（base64编码）
     * @return 键值对，键为自定义字符串值，值为加密结果（base64编码）
     */
    public static Map<String, String> symmetricBatchEncrypt(Map<String, String> inDataMap) {
        Map<String, String> outMap = new HashMap<>();
        if (inDataMap == null) {
            return outMap;
        }
        for (Map.Entry<String, String> entry : inDataMap.entrySet()) {
            String plain = entry.getValue();
            outMap.put(entry.getKey(), StringUtils.isBlank(plain) ? plain : symmetricEncrypt(plain));
        }
        return outMap;
    }

    /**
     * 使用sm4算法进行批量解密
     *
     * @param inDataMap 键为自定义字符串，值为要解密的密文数据（base64编码）
     * @return 键值对，键为自定义字符串值，值为解密结果（base64编码）
     */
    public static Map<String, String> symmetricBatchDecrypt(Map<String, String> inDataMap) {
        Map<String, String> outMap = new HashMap<>();
        if (inDataMap == null) {
            return outMap;
        }
        for (Map.Entry<String, String> entry : inDataMap.entrySet()) {
            String cipher = entry.getValue();
            outMap.put(entry.getKey(), StringUtils.isBlank(cipher) ? cipher : symmetricDecrypt(cipher));
        }
        return outMap;
    }
}
