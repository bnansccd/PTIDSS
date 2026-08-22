package com.troy.common.core.utils.mask;

/**
 * @author chenxl
 * @description
 * @date 2024-07-24 15:34
 */

import com.troy.common.core.anotation.SensitiveData;
import com.troy.common.core.domain.ResultVO;

import java.lang.reflect.Field;
import java.util.Collection;

public class SensitiveUtils {

    public static void handle(Object object) {
        if (object == null) {
            return;
        }

        if (object.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(object);
            for (int i = 0; i < length; i++) {
                handle(java.lang.reflect.Array.get(object, i));
            }
        } else if (object instanceof Collection) {
            for (Object item : (Collection<?>) object) {
                handle(item);
            }
        } else if (object instanceof ResultVO) {
            ResultVO<?> resultVO = (ResultVO<?>) object;
            handle(resultVO.getData());
        } else {
            handleFields(object);
        }
    }

    private static void handleFields(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(SensitiveData.class)) {
                try {
                    Object value = field.get(object);
                    if (value instanceof String) {
                        SensitiveData annotation = field.getAnnotation(SensitiveData.class);
                        field.set(object, DataMaskingUtil.mask((String) value, annotation.type()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (field.getType().isArray()) {
                try {
                    Object array = field.get(object);
                    if (array != null) {
                        int length = java.lang.reflect.Array.getLength(array);
                        for (int i = 0; i < length; i++) {
                            handle(java.lang.reflect.Array.get(array, i));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                try {
                    Collection<?> collection = (Collection<?>) field.get(object);
                    if (collection != null) {
                        for (Object item : collection) {
                            handle(item);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                // 递归处理嵌套对象
                try {
                    Object nestedObject = field.get(object);
                    if (nestedObject != null) {
                        handle(nestedObject);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
