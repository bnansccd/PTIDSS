package com.troy.common.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.text.Convert;
import com.troy.common.core.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:24
 */
public class SecurityContextHolder {
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? StringUtils.EMPTY : value);
    }

    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        return Convert.toStr(map.getOrDefault(key, StringUtils.EMPTY));
    }

    public static <T> T get(String key, Class<T> clazz) {
        Map<String, Object> map = getLocalMap();
        return StringUtils.cast(map.getOrDefault(key, null));
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static Long getUserId() {
        return Convert.toLong(get(SecurityConstants.DETAILS_USER_ID), 0L);
    }

    public static void setUserId(String account) {
        set(SecurityConstants.DETAILS_USER_ID, account);
    }

    public static String getUserName() {
        return get(SecurityConstants.DETAILS_USERNAME);
    }

    public static void setUserName(String username) {
        set(SecurityConstants.DETAILS_USERNAME, username);
    }

    public static String getUserKey() {
        return get(SecurityConstants.USER_KEY);
    }

    public static void setUserKey(String userKey) {
        set(SecurityConstants.USER_KEY, userKey);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static Long getTenantId(){
        return Convert.toLong(get(SecurityConstants.TENANT_ID), null);
    }

    public static void setTenantId(Long tenantId){
        set(SecurityConstants.TENANT_ID, tenantId);
    }

    public static Long getStartTime(){
        return Convert.toLong(get(SecurityConstants.START_TIME), 0L);
    }

    public static void setStartTime(Long time){
        set(SecurityConstants.START_TIME, time);
    }

    public static Long getDepartId() {
        return Convert.toLong(get(SecurityConstants.DEPART_ID), 0L);
    }

    public static void setDepartId(String departId) {
        set(SecurityConstants.DEPART_ID, departId);
    }

    public static String getDepartName() {
        return get(SecurityConstants.DEPART_NAME);
    }

    public static void setDepartName(String departId) {
        set(SecurityConstants.DEPART_NAME, departId);
    }
}
