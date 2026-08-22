package com.ptidss.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户上下文（对齐 low-code-dev LoginUser：令牌键 + 身份 + 角色/权限 + 数据权限）
 * 说明：令牌缓存于本地 Caffeine（开发环境）；生产环境可整体切换 Redis 实现，模型不变。
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 令牌键（uuid，JWT user_key claim） */
    private String token;

    /** 用户 ID */
    private Long userid;

    /** 用户名 */
    private String username;

    /** 显示名 */
    private String displayName;

    /** 登录 IP */
    private String ipaddr;

    /** 登录时间 */
    private Long loginTime;

    /** 过期时间 */
    private Long expireTime;

    /** 角色编码集合（trader/analyst/settlement/admin/manager/compliance/mobile） */
    private Set<String> roles;

    /** 权限编码集合（menu:market/api:declaration/...） */
    private Set<String> permissions;

    /** 已授权区域编码集合（评审决议⑤：角色 × 区域双重授权） */
    private Set<String> regions;

    /** 当前会话区域编码（X-Region-Code 请求头，多省路由） */
    private String regionCode;

    /** 组织编码 */
    private String orgCode;

    /** V2.4 滑动续期后的新 JWT（剩余不足阈值时重签；由拦截器写入 X-New-Token 响应头，前端替换本地令牌） */
    @JsonIgnore
    private String newToken;

    /** 是否超级管理员（admin 角色） */
    @JsonIgnore
    public boolean isAdmin() {
        return roles != null && roles.contains("admin");
    }
}
