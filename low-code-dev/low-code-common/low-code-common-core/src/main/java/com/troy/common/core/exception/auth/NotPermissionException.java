package com.troy.common.core.exception.auth;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:16
 * 未能通过的权限认证异常
 */
public class NotPermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotPermissionException(String permission) {
        super(permission);
    }

    public NotPermissionException(String[] permissions) {
        super(StringUtils.join(permissions, ","));
    }
}
