package com.troy.common.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2024/6/18 14:46
 * @Version: 1.0
 **/
public class MD5Encry {
    /**
     * 获取MD5
     *
     * @throws UnsupportedEncodingException
     */
    public static String getMD5(String sourceStr) {
        if (StringUtils.isBlank(sourceStr)) {
            return "";
        }
        String resultStr = null;
        try {
            byte abyte0[] = sourceStr.getBytes("utf-8");

            char ac[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(abyte0);
            byte abyte1[] = messagedigest.digest();
            char ac1[] = new char[32];
            int i = 0;
            for (int j = 0; j < 16; j++) {
                byte byte0 = abyte1[j];
                ac1[i++] = ac[byte0 >>> 4 & 15];
                ac1[i++] = ac[byte0 & 15];
            }
            resultStr = new String(ac1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }
}
