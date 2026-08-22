package com.troy.common.core.utils;

import java.util.regex.Pattern;

/**
 * @Description: 港澳台来内地通行证校验
 * @Author: zhuQing
 * @Date: 2025/4/22 16:43
 * @Version: 1.0
 **/
public class PassportValidator {
    /**
     * 港澳通行证验证规则：
     * 1. 大陆居民往来港澳通行证：C开头 + 8位数字（如C12345678）
     * 2. 港澳居民来往内地通行证：H/M开头 + 10位数字（如H1234567890）,若为10位可以忽略后两位
     *
     * @param number
     * @return
     */
    public static boolean validateHongKongMacauPassport(String number) {
        String regex = "^(C\\d{8}|[HM]\\d{8,10})$";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                .matcher(number.trim())
                .matches();
    }

    /**
     * 台湾通行证验证规则：
     * 1. 旧版：T开头 + 7位数字 + 1位字母或数字（如T1234567A）
     * 2. 新版：T开头 + 8位数字（如T12345678）
     *
     * @param number
     * @return
     */
    public static boolean validateTaiwanPassport(String number) {
        String regex = "^(T\\d{7}[A-Z0-9]|T\\d{8})$";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                .matcher(number.trim())
                .matches();
    }

    public static boolean validate(String passportNumber) {
        return validateHongKongMacauPassport(passportNumber)
                || validateTaiwanPassport(passportNumber);
    }
}
