package com.ptidss.auth.dto;

import lombok.Data;

import java.util.List;

/**
 * 当前用户信息（GET /auth/current）
 */
@Data
public class CurrentUser {

    private Long userId;
    private String username;
    private String displayName;
    private String orgCode;
    private String status;
    private List<String> roles;
    private List<String> permissions;
    private List<String> regions;
    private String currentRegion;
}
