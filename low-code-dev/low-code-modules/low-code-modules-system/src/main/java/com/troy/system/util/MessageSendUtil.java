package com.troy.system.util;


import com.troy.common.core.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenxl
 * @description
 * @date 2024-05-28 11:48
 */
@Component
public class MessageSendUtil {
    private static String appId = "i8APxQMafwzqF0bdnXyv2U3EY31meiGV";

    private static String appKey = "JvUURGLVe3jSHC7QfAE1qeiYp7o31mSe";

    private static String url = "http://47.96.186.52:9998";

    @Value("${message.appId:i8APxQMafwzqF0bdnXyv2U3EY31meiGV}")
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Value("${message.appKey:JvUURGLVe3jSHC7QfAE1qeiYp7o31mSe}")
    public  void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Value("${message.url:http://47.96.186.52:9998/yktsms/send}")
    public void setUrl(String url) {
        this.url = url;
    }

    public static String sendMessage(String message, List<String> phones){
        StringBuilder sb = new StringBuilder();
        phones.forEach(e-> sb.append(e).append(","));
        sb.deleteCharAt(sb.length() - 1);
        String hash = getMD5Hash(appId + sb + message + appKey);
        HashMap<String, String> param = new HashMap<>();
        param.put("appid", appId);
        param.put("mobile", sb.toString());
        param.put("msg", message);
        param.put("sign", hash);
        return HttpUtil.doGet(url, param, null);
    }


    public static String getMD5Hash(String input) {
        try {
            // 获取一个MD5转换器（消息摘要）
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 转换输入字符串为字节数组并更新摘要
            md.update(input.getBytes());

            // 获取哈希的字节数组
            byte[] digest = md.digest();

            // 创建一个StringBuilder来保存转换后的字符串
            StringBuilder sb = new StringBuilder();

            // 将每个字节转换为两位的16进制数
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            // 返回32位小写的哈希值
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
