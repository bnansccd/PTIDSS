package com.troy.form.utils;

import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.cglib.beans.BeanGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2022/10/9 16:16:35
 * @Description: 动态beans
 * @Version: 1.0.0
 */
public class DynamicBeanEntity {

    Object dynamicBean;
    Class clazz;
    public DynamicBeanEntity(Map dynAttrMap) {
        this.dynamicBean = generateBean(dynAttrMap);
        clazz = dynamicBean.getClass();
    }
    /**
     * 获取所有属性值
     *
     * @return
     * @throws IllegalAccessException
     */
    public Map<String, Object> getValues() throws IllegalAccessException {
        Map<String, Object> fieldValuesMap = new HashMap(16);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(dynamicBean);
            fieldValuesMap.put(field.getName().split("\\$cglib_prop_")[1], fieldValue);
        }
        return fieldValuesMap;
    }
    /**
     * 获取动态bean所有方法信息
     *
     * @return
     */
    public MultiValueMap getMethods() {
        MultiValueMap map = new MultiValueMap();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            if (genericParameterTypes.length > 0) {
                for (Type type : genericParameterTypes) {
                    map.put(method.getName(), type);
                }
            } else {
                map.put(method.getName(), null);
            }
        }
        return map;
    }
    /**
     * 执行某个方法
     *
     * @param methodName
     * @param parameters
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public Object executeMethod(String methodName, Object... parameters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ArrayList<Class> paramTypeList = new ArrayList();
        for (Object paramType : parameters) {
            paramTypeList.add(paramType.getClass());
        }
        Class[] classArray = new Class[paramTypeList.size()];
        Method method = clazz.getMethod(methodName, paramTypeList.toArray(classArray));
        Object invoke = method.invoke(dynamicBean, parameters);
        return invoke;
    }
    /**
     * 设置属性值
     *
     * @param property
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void setValue(String property, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = clazz.getDeclaredField("$cglib_prop_" + property);
        declaredField.setAccessible(true);
        declaredField.set(dynamicBean, value);
    }
    /**
     * 获取属性值
     *
     * @param property
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public Object getValue(String property) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = clazz.getDeclaredField("$cglib_prop_" + property);
        declaredField.setAccessible(true);
        Object value = declaredField.get(dynamicBean);
        return value;
    }
    public Object getEntity() {
        return this.dynamicBean;
    }
    /**
     * 利用cglib的BeanGenerator创建对象
     *
     * @param dynAttrMap
     * @return
     */
    private Object generateBean(Map dynAttrMap) {
        BeanGenerator generator = new BeanGenerator();
        Iterator iterator = dynAttrMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            generator.addProperty(key, (Class) dynAttrMap.get(key));
        }
        return generator.create();
    }
}