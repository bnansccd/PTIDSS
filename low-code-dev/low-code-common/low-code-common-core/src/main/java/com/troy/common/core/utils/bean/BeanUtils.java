package com.troy.common.core.utils.bean;

import com.troy.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  13:42
 * Bean 工具类
 */
@Slf4j
public class BeanUtils extends org.springframework.beans.BeanUtils {
    /**
     * Bean方法名中属性名开始的下标
     */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /**
     * 匹配getter方法的正则表达式
     */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /**
     * 匹配setter方法的正则表达式
     */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制工具方法。
     *
     * @param dest 目标对象
     * @param src  源对象
     */
    public static void copyBeanProp(Object dest, Object src) {
        try {
            copyProperties(src, dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的setter方法。
     *
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj) {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>();

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();

        // 查找setter方法

        for (Method method : methods) {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1)) {
                setterMethods.add(method);
            }
        }
        // 返回setter方法列表
        return setterMethods;
    }

    /**
     * 获取对象的getter方法。
     *
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj) {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>();
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();
        // 查找getter方法
        for (Method method : methods) {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0)) {
                getterMethods.add(method);
            }
        }
        // 返回getter方法列表
        return getterMethods;
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     *
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2) {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }

    /**
     * 实体转map
     *
     * @param object
     * @return
     */
    public static Map<String, String> objectToMap(Object object) {
        Map<String, String> map = new HashMap<>();
        Class<?> clazz = object.getClass();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object o = field.get(object);
                if (StringUtils.isNull(o)){
                    continue;
                }
                String value = o.toString();
                map.put(fieldName, value);
            }
        } catch (Exception e) {
            log.error("实体map，转化失败", e);
        }
        return map;
    }

    public static Map<String, Object> objectToMapNotToString(Object object) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = object.getClass();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object o = field.get(object);
                map.put(fieldName, o);
            }
        } catch (Exception e) {
            log.error("实体map，转化失败", e);
        }
        return map;
    }

    /**
     * 获取内容为null的属性名
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        emptyNames.add("version");
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static Map<String, Object> deeplyConvertToMap(Object obj) {
        Map<String, Object> resultMap = new HashMap<>();
        Queue<Map.Entry<String, Object>> queue = new LinkedList<>();
        queue.add(new HashMap.SimpleEntry<>("", obj));

        while (!queue.isEmpty()) {
            Map.Entry<String, Object> entry = queue.poll();
            String currentPrefix = entry.getKey();
            Object currentObject = entry.getValue();

            if (currentObject == null
                    || isPrimitiveOrWrapper(currentObject)
                    || currentObject instanceof String
                    || currentObject instanceof Number
                    || currentObject instanceof Date) {
                // 基本类型、包装类型、字符串或 null 直接放入 Map
                resultMap.put(currentPrefix, currentObject);
            } else if (!(currentObject instanceof Map) && !(currentObject instanceof Collection)) {
                // 处理非基本类型且非 Map 或 Collection 的对象
                Field[] fields = currentObject.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object fieldValue = null;
                    try {
                        fieldValue = field.get(currentObject);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    String fieldName = field.getName();
                    String fullFieldName = currentPrefix.isEmpty() ? fieldName : currentPrefix + "." + fieldName;

                    queue.add(new HashMap.SimpleEntry<>(fullFieldName, fieldValue));
                }
            }
        }

        return resultMap;
    }

    private static boolean isPrimitiveOrWrapper(Object obj) {
        return obj instanceof Boolean || obj instanceof Byte || obj instanceof Character ||
                obj instanceof Short || obj instanceof Integer || obj instanceof Long ||
                obj instanceof Float || obj instanceof Double;
    }


    /**
     * 合并source的非空属性到target
     * @param target 目标对象
     * @param source 源对象
     * @param <T> 对象类型
     */
    public static <T> void mergeNonNull(T target, T source) {
        if (target == null || source == null) {
            return;
        }

        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object sourceValue = field.get(source);

                // 只合并非空值
                if (sourceValue != null) {
                    field.set(target, sourceValue);
                }
            } catch (Exception e) {
                // 忽略无法访问的字段
            }
        }
    }

    /**
     * 合并多个对象的非空属性到目标对象
     * @param target 目标对象
     * @param sources 多个源对象
     * @param <T> 对象类型
     */
    @SafeVarargs
    public static <T> void mergeMultiple(T target, T... sources) {
        if (target == null || sources == null) {
            return;
        }

        for (T source : sources) {
            mergeNonNull(target, source);
        }
    }

    /**
     * 排除指定字段进行合并
     * @param target 目标对象
     * @param source 源对象
     * @param excludeFields 排除的字段名
     * @param <T> 对象类型
     */
    public static <T> void mergeExclude(T target, T source, String... excludeFields) {
        if (target == null || source == null) {
            return;
        }

        List<String> excludeList = Arrays.asList(excludeFields);
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (excludeList.contains(field.getName())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object sourceValue = field.get(source);

                if (sourceValue != null) {
                    field.set(target, sourceValue);
                }
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }
    }

    /**
     * 自定义合并策略
     * @param target 目标对象
     * @param source 源对象
     * @param strategy 合并策略接口
     * @param <T> 对象类型
     */
    public static <T> void mergeWithStrategy(T target, T source, MergeStrategy strategy) {
        if (target == null || source == null || strategy == null) {
            return;
        }

        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object targetValue = field.get(target);
                Object sourceValue = field.get(source);

                // 使用策略决定是否合并
                if (strategy.shouldMerge(field.getName(), targetValue, sourceValue)) {
                    field.set(target, sourceValue);
                }
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }
    }

    /**
     * 合并策略接口
     */
    @FunctionalInterface
    public interface MergeStrategy {
        boolean shouldMerge(String fieldName, Object targetValue, Object sourceValue);
    }

}

