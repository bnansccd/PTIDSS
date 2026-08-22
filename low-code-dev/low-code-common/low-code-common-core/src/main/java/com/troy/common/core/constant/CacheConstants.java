package com.troy.common.core.constant;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:05
 * 缓存的key 常量
 */
public class CacheConstants {
    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;

    /**
     * 刷新token缓存有效期，默认43200（分钟）
     */
    public final static long REFRESH_TOKEN_EXPIRE = 43200;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 锁定标识
     */
    public final static String LOGIN_LOCK_KEY = "login_lock:";

    /**
     * 重试次数
     */
    public final static Integer LOCK_TIMES = 3;

    /**
     * 黑名单标识
     */
    public final static String MONITOR_BLACK_LIST = "MONITOR_BLACK_LIST";

}
