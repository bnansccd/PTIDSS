package com.troy.common.core.utils;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.text.StrFormatter;
import com.troy.common.core.utils.reflect.ReflectUtils;
import org.springframework.util.AntPathMatcher;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:43
 * 字符串工具类
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 空字符串
     */
    private static final String NULLSTR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    /**
     * 获取参数不为空值
     *
     * @param value defaultValue 要判断的value
     * @return value 返回值
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     *
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * * 判断一个对象是否是数组类型（Java基本型别的数组）
     *
     * @param object 对象
     * @return true：是数组 false：不是数组
     */
    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return NULLSTR;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 判断是否为空，并且不是空白字符
     *
     * @param str 要判断的value
     * @return 结果
     */
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 是否为http(s)://开头
     *
     * @param link 链接
     * @return 结果
     */
    public static boolean ishttp(String link) {
        return StringUtils.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs) {
        if (isEmpty(str) || isEmpty(strs)) {
            return false;
        }
        for (String pattern : strs) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @return
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    public static String valueOf(Object object) {
        return object == null ? null : String.valueOf(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * 数字左边补齐0，使之达到指定长度。注意，如果数字转换为字符串后，长度大于size，则只保留 最后size个字符。
     *
     * @param num  数字对象
     * @param size 字符串指定长度
     * @return 返回数字的字符串格式，该字符串为指定长度。
     */
    public static String padl(final Number num, final int size) {
        return padl(num.toString(), size, '0');
    }

    /**
     * 字符串左补齐。如果原始字符串s长度大于size，则只保留最后size个字符。
     *
     * @param s    原始字符串
     * @param size 字符串指定长度
     * @param c    用于补齐的字符
     * @return 返回指定长度的字符串，由原字符串左补齐或截取得到。
     */
    public static String padl(final String s, final int size, final char c) {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null) {
            final int len = s.length();
            if (s.length() <= size) {
                for (int i = size - len; i > 0; i--) {
                    sb.append(c);
                }
                sb.append(s);
            } else {
                return s.substring(len - size, len);
            }
        } else {
            for (int i = size; i > 0; i--) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void beanFieldTrim(Object bean) {
        if (bean != null) {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f.getType().getName().equals("java.lang.String")) {
                    String key = f.getName();
                    Object value = ReflectUtils.getFieldValue(bean, key);
                    if (value == null) {
                        continue;
                    }
                    String trim = value.toString().trim();
                    String replace = trim.replace(" ", "");
                    ReflectUtils.setFieldValue(bean, key, replace);
                }
            }
        }
    }

    public static boolean isTrimEquals(String value1, String value2) {
        if (value1 == null || value2 == null) {
            return false;
        }

        return value1.trim().equals(value2.trim());
    }

    public static String escape(String value1) {
        if (isNotBlank(value1)) {
            value1 = value1.replace("%", "\\%");
            return value1.replace("_", "\\_");
        }
        return value1;
    }

    public static String newString(byte[] bytes, String charsetName) {
        String str = "";
        try {
            str = new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * obj 中为 null 的字符串变为 ""
     *
     * @param obj
     * @throws IllegalAccessException
     */
    public static void convertNullStringsToEmpty(Object obj) throws IllegalAccessException {
        // 获取对象的Class对象
        Class<?> objClass = obj.getClass();

        // 获取对象的所有字段（包括私有字段）
        Field[] fields = objClass.getDeclaredFields();

        // 遍历每个字段
        for (Field field : fields) {
            // 设置字段为可访问（如果字段是私有的）
            field.setAccessible(true);

            // 检查字段类型是否为String
            if (field.getType().equals(String.class)) {
                // 获取字段的值
                Object fieldValue = field.get(obj);

                // 如果字段值为null，则设置为空字符串
                if (fieldValue == null) {
                    field.set(obj, "");
                }
            }
        }
    }

    /**
     * obj 中为 "" 的字符串变为 null
     *
     * @param obj
     * @throws IllegalAccessException
     */
    public static void convertEmptyStringsToNull(Object obj) {
        try {
            // 获取对象的Class对象
            Class<?> objClass = obj.getClass();
            // 获取对象的所有字段（包括私有字段）
            Field[] fields = objClass.getDeclaredFields();
            // 遍历每个字段
            for (Field field : fields) {
                // 设置字段为可访问（如果字段是私有的）
                field.setAccessible(true);

                // 检查字段类型是否为String
                if (field.getType().equals(String.class)) {
                    // 获取字段的值
                    Object fieldValue = field.get(obj);

                    // 如果字段值为null，则设置为空字符串
                    if ("".equals(fieldValue)) {
                        field.set(obj, null);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转标准装号，例如 0.0转为K0+0
     *
     * @param pile
     * @return
     */
    public static String convertStandardPileNumber(BigDecimal pile) {
        String standardPileNumber = null;
        String s = String.valueOf(pile);
        if (s != null) {
            String[] split = s.split("\\.");
            if (split.length > 1) {
                standardPileNumber = "K" + split[0] + "+" + split[1];
            }
        }
        return standardPileNumber;
    }


    public static String mergePluses(String input) {
        if (isEmpty(input)) {
            return input;
        }
        String[] parts = input.split("\\+");
        if (parts.length <= 2) {
            return input; // 无加号或只有一个加号时返回原字符串
        } else {
            // 合并前n-1个部分
            StringBuilder merged = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                merged.append(parts[i]);
            }
            // 添加最后一个部分并用单个+连接
            return merged.toString() + "+" + parts[parts.length - 1];
        }
    }

    /**
     * 转标准装号，例如 0.0转为K0+0
     *
     * @param pile
     * @return
     */
    public static String convertStandardPileNumber(String pile) {
        String standardPileNumber = null;
        if (pile != null) {
            String[] split = pile.split("\\.");
            if (split.length > 1) {
                standardPileNumber = "K" + split[0] + "+" + split[1];
            }
        }
        return standardPileNumber;
    }

    /**
     * 标准桩号转 bigdecimal
     *
     * @return
     */
    public static BigDecimal convertToBigDecimal(String pileNumber) {

        pileNumber = mergePluses(pileNumber);

        if (pileNumber == null || !pileNumber.matches("[Kk]\\d+\\+\\d+")) {
            throw new ServiceException(ResultEnum.BE_CURRENT, "桩号（{" + pileNumber + "}）格式不正确，应为 Kxxxx+xxx 形式，例如 K1516+195");
        }

        try {
            // 移除开头的 'K'
            pileNumber = pileNumber.substring(1);

            // 按 '+' 分割桩号
            String[] parts = pileNumber.split("\\+");
            if (parts.length != 2) {
                throw new ServiceException(ResultEnum.BE_CURRENT, "桩号格式不正确，应为 Kxxxx+xxx 形式");
            }

            // 主桩号部分（如 1516）
            String mainPart = parts[0];

            // 子桩号部分（如 195）
            String subPart = parts[1];

            // 拼接成小数格式
            String decimalString = mainPart + "." + subPart;

            // 转换为 BigDecimal 并返回
            return new BigDecimal(decimalString);
        } catch (Exception e) {
            throw new ServiceException(ResultEnum.BE_CURRENT, "桩号转换失败：" + e.getMessage());
        }
    }

    /**
     * 判断字符串中是否包含指定的子串，且该子串前面不能是数字
     */
    public static boolean containsExactMatch(String text, String target) {

        if (StringUtils.isBlank(text) || StringUtils.isBlank(target)) {
            return false;
        }
        try {

            int index = 0;
            int targetLength = target.length();

            while ((index = text.indexOf(target, index)) != -1) {
                // 检查目标子串前面是否不是数字
                if (index == 0) {
                    // 如果是在字符串开头，前面没有字符，符合条件
                    return true;
                }

                char prevChar = text.charAt(index - 1);
                if (!Character.isDigit(prevChar)) {
                    return true;
                }

                // 如果前面是数字，继续向后搜索
                index += targetLength;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

