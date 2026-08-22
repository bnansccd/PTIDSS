package com.troy.common.datasource.utils;

import com.sansec.sdk.common.util.Base64Util;
import com.sansec.sdk.core.builder.DataEncryptionServiceBuilder;
import com.sansec.sdk.core.builder.SignatureAndVerifyServiceBuilder;
import com.sansec.sdk.core.config.ApplicationContextConfig;
import com.sansec.sdk.core.config.ServiceConfig;
import com.sansec.sdk.core.config.ServiceInstanceDirector;
import com.sansec.sdk.core.engine.ApplicationTokenEngine;
import com.sansec.sdk.core.enums.IvModeEnum;
import com.sansec.sdk.core.enums.PkiCallModeEnum;
import com.sansec.sdk.core.service.DataEncryptionService;
import com.sansec.sdk.core.service.SignatureAndVerifyService;
import com.sansec.sdk.core.token.config.BaseAuthConfig;
import com.sansec.sdk.pki.enums.DigestAlgTypeEnum;
import com.sansec.sdk.pki.enums.HmacAlgEnum;
import com.sansec.sdk.pki.enums.SymmAlgEnum;
import com.sansec.sdk.pki.request.*;
import com.sansec.sdk.pki.response.HMACInternalVO;
import com.sansec.sdk.pki.response.SymmetricInternalDecryptVO;
import com.sansec.sdk.pki.response.SymmetricInternalEncryptVO;
import com.sansec.sdk.svs.request.DigestDTO;
import com.sansec.sdk.svs.response.DigestVO;
import com.troy.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * @Description: 忠县密码机服务类
 * @Author: zhuQing
 * @Date: 2026/4/1 16:00
 * @Version: 1.0
 **/
public class PasswordTestConfig {
    private static final Logger log = LoggerFactory.getLogger(PasswordTestConfig.class);
    private static DataEncryptionService dataEncryptionService;
    private static SignatureAndVerifyService signatureAndVerifyService;

    // ================== 以下配置请根据excel表格进行修改 ==================
    private static final String MMY_IP = "100.91.130.73";
    private static final String TENANT_CODE = "lc_ljegs";
    private static final String APP_CODE = "fbzdltx_01";
    private static final String APP_PASSWD = "ZDGCjxJG@1111";
    private static final String SM4_KEY_NAME = "sm4_01_fbzdltx";
    private static final String SM4_PROTECT_KEY_NAME = "sm4_bh_01_fbzdltx";
    private static final String SM3_KEY_NAME = "sm3_01_fbzdltx";
    // ================================================================

    static {        // 初始化操作，全局执行一次！
        initToken(); // 1.token认证初始化配置
        initService(); // 2.初始化服务配置信息
    }

    /**
     * 初始化租户和应用信息，请勿修改
     */
    private static void initToken() {
        BaseAuthConfig baseAuthConfig = new BaseAuthConfig();
        // 租户标识@应用标识
        baseAuthConfig.setUserName(TENANT_CODE + "@" + APP_CODE);
        // 应用连接口令
        baseAuthConfig.setUserPwd(APP_PASSWD);
        // 使用构造的配置信息初始化token
        ApplicationTokenEngine.buildInstance().initConfig(baseAuthConfig);
    }

    /**
     * 初始化密码服务、保护密钥信息，请勿修改
     */
    private static void initService() {
        ServiceConfig serviceConfig = new ServiceConfig();
        // 密码服务调用地址，多个可以多次调用addService方法
        serviceConfig.addService(MMY_IP, 8867);
        // 配置密钥保护密钥名称。需要是一个在密服平台、密钥管理服务中生成的SM4密钥。
        serviceConfig.setSM4ProtectKeyName(SM4_PROTECT_KEY_NAME);
        // 初始化实例
        ApplicationContextConfig.initServiceConfig(serviceConfig);
        // 获取指导者对象。
        ServiceInstanceDirector director = ServiceInstanceDirector.getInstance();
        // 获取加解密服务建造者
        DataEncryptionServiceBuilder encBuilder = new DataEncryptionServiceBuilder();
        // 获取签名验签服务建造者
        SignatureAndVerifyServiceBuilder signBuilder = new SignatureAndVerifyServiceBuilder();
        // 获取加解密服务实例，
        dataEncryptionService = director.getInstance(encBuilder, PkiCallModeEnum.HARD);
        // 获取签名验签服务实例
        signatureAndVerifyService = director.getInstance(signBuilder, PkiCallModeEnum.HARD);
    }

    /**
     * 前置信息：开通加解密服务
     * SM4对称加密，用于机密性保护
     *
     * @param plainText 要加密的明文数据字符串（字节数组可自行封装）
     * @return 密文，base64编码
     */
    public static String symmetricEncrypt(String plainText) {
        if (StringUtils.isBlank(plainText)) {
            return plainText;
        }
        SymmetricInternalEncryptWithSecIVDTO dto = new SymmetricInternalEncryptWithSecIVDTO();
        // 设置IV模式。支持以下枚举：
        //   IvModeEnum.SM3 -- 根据明文生成IV
        //   IvModeEnum.RANDOM -- 随机IV
        //   IvModeEnum.TRUE_RANDOM -- 真随机IV（性能稍低）
        dto.setIvMode(IvModeEnum.SM3);
        // 设置密钥名称
        dto.setKeyName(SM4_KEY_NAME);
        // 设置模式
        dto.setAlgType(SymmAlgEnum.SGD_SM4_CBC);
        // 设置数据
        dto.setInData(plainText.getBytes());
        // 计算
        SymmetricInternalEncryptVO vo = dataEncryptionService.internalSymmetricEncryptWithSecIV(dto);
        return vo.getOutData();
    }

    /**
     * 前置信息：开通加解密服务
     * SM4对称解密
     *
     * @param cipherText 要解密的密文数据，base64编码
     * @return 原文字符串
     */
    public static String symmetricDecrypt(String cipherText) {
        try {
            SymmetricInternalDecryptDTO dto = new SymmetricInternalDecryptDTO();
            // 设置密钥名称
            dto.setKeyName(SM4_KEY_NAME);
            // 设置模式
            dto.setAlgType(SymmAlgEnum.SGD_SM4_CBC);
            // 设置数据
            dto.setInData(cipherText);
            // 计算
            SymmetricInternalDecryptVO vo = dataEncryptionService.internalSymmetricDecrypt(dto);
            return (vo.getOutData());
        } catch (Exception e) {
            log.error("解密失败", e);
        }
        return cipherText;
    }

    /**
     * 使用hmac算法生成完整性校验数据
     *
     * @param plainText 明文数据字符串
     * @return hmac值，base64编码
     */
    public static String hmac(String plainText) {
        if (StringUtils.isBlank(plainText)) {
            return plainText;
        }
        HMACInternalDTO dto = new HMACInternalDTO();
        dto.setInData(plainText.getBytes()); // 传入需要做hmac的数据
        dto.setKeyName(SM3_KEY_NAME);
        dto.setAlgType(HmacAlgEnum.SGD_SM3);
        HMACInternalVO vo = dataEncryptionService.internalHMAC(dto);
        return (vo.getOutData());
    }

    /**
     * 使用hmac算法进行完整性校验
     *
     * @param plainText 明文数据字符串
     * @param hmac      hmac值，base64编码
     * @return 是否验证通过
     */
    public static boolean hmacVerify(String plainText, String hmac) {
        HMACInternalVerifyDTO dto = new HMACInternalVerifyDTO();
        dto.setInData(plainText.getBytes());
        dto.setAlgType(HmacAlgEnum.SGD_SM3);
        dto.setKeyName(SM3_KEY_NAME);
        dto.setHmacData(hmac);
        return dataEncryptionService.internalVerifyHMAC(dto);
    }

    /**
     * 使用SM3算法进行杂凑运算
     *
     * @param plainText 明文数据字符串
     * @return 摘要值
     */
    public static String digest(String plainText) {
        DigestDTO dto = new DigestDTO();
        dto.setInData(Base64Util.encodeToString(plainText));
        // 设置摘要算法，支持：SHA1/SHA224/SHA256/SHA384/SHA512/SM3
        dto.setAlgType(DigestAlgTypeEnum.SM3);
        DigestVO vo = signatureAndVerifyService.digest(dto);
        return vo.getOutData();
    }

    /**
     * 使用sm4算法进行批量加密
     *
     * @param inDataMap 键为自定义字符串，值为要加密的明文数据（base64编码）
     * @return 键值对，键为自定义字符串值，值为加密结果（base64编码）
     * @return
     */
    public static Map<String, String> symmetricBatchEncrypt(Map<String, String> inDataMap) {
        SymmetricInternalEncryptBatchWithSecIVDTO dto = new SymmetricInternalEncryptBatchWithSecIVDTO();
        dto.setKeyName(SM4_KEY_NAME);
        //dto.setIv(iv);
        // IvMode支持以下枚举：
        //   IvModeEnum.SM3 -- 根据明文生成IV
        //   IvModeEnum.RANDOM -- 随机IV
        //   IvModeEnum.TRUE_RANDOM -- 真随机IV（性能稍低）
        dto.setIvMode(IvModeEnum.SM3);
        dto.setAlgType(SymmAlgEnum.SGD_SM4_CBC);
        dto.setInDataMap(inDataMap);
        return dataEncryptionService.internalSymmetricEncryptBatchWithSecIV(dto).getOutData();
    }

    /**
     * 使用sm4算法进行批量解密
     *
     * @param inDataMap 键为自定义字符串，值为要解密的密文数据（base64编码）
     * @return 键值对，键为自定义字符串值，值为解密结果（base64编码）
     */
    public static Map<String, String> symmetricBatchDecrypt(Map<String, String> inDataMap) {
        SymmetricInternalDecryptBatchDTO dto = new SymmetricInternalDecryptBatchDTO();
        dto.setKeyName(SM4_KEY_NAME);
        dto.setAlgType(SymmAlgEnum.SGD_SM4_CBC);
        dto.setInDataMap(inDataMap);
        return dataEncryptionService.internalSymmetricDecryptBatch(dto).getOutData();
    }
}

