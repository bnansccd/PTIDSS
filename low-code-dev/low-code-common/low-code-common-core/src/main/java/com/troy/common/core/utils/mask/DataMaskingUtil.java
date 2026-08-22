package com.troy.common.core.utils.mask;

import com.troy.common.core.anotation.SensitiveData;
import com.troy.common.core.utils.StringUtils;

/**
 * @author chenxl
 * @description
 * @date 2024-07-24 15:33
 */
public class DataMaskingUtil {

    public static String mask(String value, SensitiveData.SensitiveType type) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        switch (type) {
            case PHONE:
                return maskPhone(value);
            case ID_CARD:
                return maskIdCard(value);
            case LICENSE_PLATE:
                return maskLicensePlate(value);
            case EMAIL:
                return maskEmail(value);
            case CREDIT_CODE:
                return maskCreditCode(value);
            case DEFAULT:
            default:
                return replaceMiddleWithAsterisks(value);
        }
    }

    private static String maskPhone(String phone) {
       if (StringUtils.isNotEmpty(phone) && phone.length() > 10){
           return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
       } else {
           return phone;
       }
    }


    private static String maskEmail(String idCard) {
        if (StringUtils.isNotEmpty(idCard)){
            return idCard.replaceAll("(^\\w)[^@]*(@.*$)", "$1****$2");
        } else {
            return idCard;
        }
    }

    private static String maskIdCard(String idCard) {
        return idCard.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }

    private static String maskLicensePlate(String carNumber) {
        if (carNumber == null || carNumber.length() < 7) {
            return carNumber;
        }
        // 示例中简单处理，仅保留号牌的前两位和后两位，中间用星号(*)替换
        return carNumber.substring(0, 4) + "**" + carNumber.substring(carNumber.length() - 2);
    }

    private static String maskCreditCode(String creditCode) {
        if (creditCode == null || creditCode.length() != 18) {
            return creditCode;
        }
        return creditCode.substring(0, 3) + "***********" + creditCode.substring(14);
    }


    public static String replaceMiddleWithAsterisks(String input) {

        int length = input.length();

        // 如果字符串长度小于等于2，不需要脱敏
        if (length <= 2) {
            if (length == 2){
                return input.charAt(0)+"*";
            }
            return input;
        }

        // 计算需要替换的字符数
        int numToMask = length - 2;

        // 构建脱敏后的字符串
        StringBuilder masked = new StringBuilder();
        masked.append(input.charAt(0)); // 保留开头字符
        for (int i = 0; i < numToMask; i++) {
            masked.append('*'); // 中间部分用*替换
        }
        masked.append(input.charAt(length - 1)); // 保留结尾字符

        return masked.toString();

    }

}

