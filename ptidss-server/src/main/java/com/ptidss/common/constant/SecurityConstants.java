package com.ptidss.common.constant;

/**
 * 安全相关常量（JWT claims 键，对齐 low-code-dev SecurityConstants）
 */
public class SecurityConstants {

    /** JWT claim：令牌用户键（uuid） */
    public static final String USER_KEY = "user_key";

    /** JWT claim：用户 ID */
    public static final String DETAILS_USER_ID = "user_id";

    /** JWT claim：用户名 */
    public static final String DETAILS_USERNAME = "username";

    /** JWT claim：区域编码（多省路由） */
    public static final String DETAILS_REGION = "region_code";

    /** ThreadLocal 用户键 */
    public static final String USER_KEY_CONTEXT = "USER_CONTEXT";
}
