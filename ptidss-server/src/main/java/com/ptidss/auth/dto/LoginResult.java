package com.ptidss.auth.dto;

import lombok.Data;

import java.util.List;

/**
 * 登录成功响应（access_token + 用户信息 + 角色/权限/区域）
 */
@Data
public class LoginResult {

    /** 访问令牌 */
    private String accessToken;

    /** 令牌有效期（秒） */
    private Long expiresIn;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 显示名 */
    private String displayName;

    /** 角色编码 */
    private List<String> roles;

    /** 权限编码 */
    private List<String> permissions;

    /** 已授权区域 */
    private List<String> regions;

    /** 当前会话区域（默认首个授权区域） */
    private String currentRegion;
}
