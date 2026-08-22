package com.troy.common.core.exception.auth;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:18
 * 未能通过的角色认证异常
 */
public class NotRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotRoleException(String role) {
        super(role);
    }

    public NotRoleException(String[] roles) {
        super(StringUtils.join(roles, ","));
    }
}
