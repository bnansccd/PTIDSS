package com.ptidss.common.aspect;

import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.exception.ForbiddenException;
import com.ptidss.common.security.LoginUser;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * 权限校验切面（对齐 low-code-dev PreAuthorizeAspect）：
 * 1) 未登录 → 14001（由 SecurityUtils 抛出）；
 * 2) admin 角色全放行；否则按注解要求校验角色/权限。
 */
@Slf4j
@Aspect
@Component
public class PreAuthorizeAspect {

    private final SecurityUtils securityUtils;

    public PreAuthorizeAspect(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    /** 类级注解（@within）与方法级注解（@annotation）都需校验，避免重复执行则合并为同一切点 */
    @Before("@within(requiresPermissions)")
    public void checkClassPermissions(JoinPoint point, RequiresPermissions requiresPermissions) {
        doCheckPermissions(requiresPermissions);
    }

    @Before("@annotation(requiresPermissions)")
    public void checkMethodPermissions(JoinPoint point, RequiresPermissions requiresPermissions) {
        doCheckPermissions(requiresPermissions);
    }

    private void doCheckPermissions(RequiresPermissions requiresPermissions) {
        LoginUser user = securityUtils.getLoginUser();
        if (user.isAdmin()) {
            return;
        }
        Set<String> owned = user.getPermissions();
        boolean hit = Arrays.stream(requiresPermissions.value())
                .anyMatch(p -> owned != null && owned.contains(p));
        if (!hit) {
            throw new ForbiddenException("缺少权限：" + String.join("/", requiresPermissions.value()));
        }
    }

    @Before("@within(requiresRoles)")
    public void checkClassRoles(JoinPoint point, RequiresRoles requiresRoles) {
        doCheckRoles(requiresRoles);
    }

    @Before("@annotation(requiresRoles)")
    public void checkMethodRoles(JoinPoint point, RequiresRoles requiresRoles) {
        doCheckRoles(requiresRoles);
    }

    private void doCheckRoles(RequiresRoles requiresRoles) {
        LoginUser user = securityUtils.getLoginUser();
        if (user.isAdmin()) {
            return;
        }
        Set<String> roles = user.getRoles();
        boolean hit = Arrays.stream(requiresRoles.value())
                .anyMatch(r -> roles != null && roles.contains(r));
        if (!hit) {
            throw new ForbiddenException("缺少角色：" + String.join("/", requiresRoles.value()));
        }
    }
}
