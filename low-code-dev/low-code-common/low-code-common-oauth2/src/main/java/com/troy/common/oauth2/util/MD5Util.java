package com.troy.common.oauth2.util;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sym
 * @description
 * @date 2023/11/30 15:00
 */

public class MD5Util {

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Upper32(String source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update((source).getBytes("UTF-8"));
            byte b[] = md5.digest();

            int i;
            StringBuffer buf = new StringBuffer("");

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            return buf.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String MD5Upper(String source) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        md.update(source.getBytes(StandardCharsets.UTF_8), 0, source.length());
        String digest = new BigInteger(1, md.digest()).toString(16);
        return digest.toUpperCase();
    }
    public static String mapEncry(Map<String, Object> paramMap) {
        Map<String, Object> hashMap = new LinkedHashMap<>();
        paramMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).filter(e -> !e.getKey().equals("sign")).forEachOrdered(e -> hashMap.put(e.getKey(), e.getValue()));
        return MD5Upper(JSON.toJSONString(hashMap));
    }


    public static String signSort(Map<String, Object> param) {
        Map<String, Object> result = new LinkedHashMap<>();
        param.entrySet().stream().sorted(Map.Entry.<String, Object>comparingByKey())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return MD5Upper(JSON.toJSONString(param));
    }


    public static String encode(String origin) {
        return encode(origin, "UTF-8");
    }

    public static String encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

}
