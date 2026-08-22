package com.ptidss.common.constant;

/**
 * 通用常量（对齐 low-code-dev CommonConstants 语义）
 */
public class Constants {

    /** 请求头：认证令牌 */
    public static final String AUTHENTICATION = "Authorization";

    /** 请求头：区域编码（多省路由，方案 V1.2 5.1） */
    public static final String REGION_HEADER = "X-Region-Code";

    /** 令牌前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 响应头：滑动续期后的新令牌（V2.4 刷新即退出修复：剩余不足阈值时重签 JWT 并回传） */
    public static final String NEW_TOKEN_HEADER = "X-New-Token";

    /** 超级管理员角色编码 */
    public static final String SUPER_ADMIN = "admin";

    /** 登录成功/失败状态 */
    public static final String LOGIN_SUCCESS = "success";
    public static final String LOGIN_FAIL = "fail";

    /** 系统用户 ID（平台级操作） */
    public static final Long SYSTEM_USER_ID = 0L;

    /** 通用布尔 */
    public static final String TRUE = "true";
    public static final String FALSE = "false";
}
