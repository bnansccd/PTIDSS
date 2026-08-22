package com.ptidss.common.utils;

import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 字符串工具（轻量封装，避免直接依赖三方工具散落各处）
 */
public class StrUtils {

    public static boolean isBlank(String str) {
        return !StringUtils.hasText(str);
    }

    public static boolean isNotBlank(String str) {
        return StringUtils.hasText(str);
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static boolean equalsAny(String src, String... targets) {
        if (src == null) {
            return false;
        }
        for (String t : targets) {
            if (src.equals(t)) {
                return true;
            }
        }
        return false;
    }
}
