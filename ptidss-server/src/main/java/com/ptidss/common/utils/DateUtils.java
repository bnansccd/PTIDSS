package com.ptidss.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间日期宽松解析（操作友好性：前端 datetime-local 输入兼容多种格式）
 * 支持格式：yyyy-MM-dd / yyyy-MM-dd HH:mm / yyyy-MM-dd HH:mm:ss /
 * yyyy-MM-dd'T'HH:mm / yyyy-MM-dd'T'HH:mm:ss；解析失败返回 null（由调用方决定报错）。
 */
public final class DateUtils {

    private static final String[] PATTERNS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd",
    };

    private DateUtils() {
    }

    /** 宽松解析（支持日期/时间日期多格式；失败返回 null） */
    public static Date parseLenient(String value) {
        if (StrUtils.isBlank(value)) {
            return null;
        }
        String v = value.trim();
        for (String pattern : PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(v);
            } catch (Exception ignored) {
                // 尝试下一种格式
            }
        }
        return null;
    }
}
