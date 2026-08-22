package com.ptidss.common.security;

import com.ptidss.common.constant.Constants;
import com.ptidss.common.exception.UnauthorizedException;
import com.ptidss.common.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 安全工具（对齐 low-code-dev SecurityUtils）：
 * 从请求头解析令牌 → 取登录用户 → 校验会话区域与区域授权（评审决议⑤）
 */
@Component
public class SecurityUtils {

    @Autowired
    private TokenService tokenService;

    /** 获取当前登录用户（未登录抛 14001） */
    public LoginUser getLoginUser() {
        LoginUser user = tokenService.getLoginUser();
        if (user == null) {
            throw new UnauthorizedException("登录状态已失效，请重新登录");
        }
        return user;
    }

    /** 获取当前用户 ID */
    public Long getUserId() {
        return getLoginUser().getUserid();
    }

    /** 获取当前用户名 */
    public String getUsername() {
        return getLoginUser().getUsername();
    }

    /** 获取当前会话区域编码（请求头 X-Region-Code） */
    public String getRegionCode() {
        return getLoginUser().getRegionCode();
    }

    /** 校验当前用户是否已授权指定区域（角色 × 区域双重授权） */
    public void checkRegionAccess(String regionCode) {
        if (StrUtils.isBlank(regionCode)) {
            throw new UnauthorizedException("缺少区域编码（X-Region-Code）");
        }
        LoginUser user = getLoginUser();
        if (user.isAdmin()) {
            return;
        }
        if (user.getRegions() == null || !user.getRegions().contains(regionCode)) {
            throw new UnauthorizedException("当前账号未授权区域 " + regionCode);
        }
    }

    /** 是否为超级管理员 */
    public boolean isAdmin() {
        return getLoginUser().isAdmin();
    }
}
