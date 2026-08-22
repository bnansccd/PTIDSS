package com.troy.common.core.exception.user;

import com.troy.common.core.exception.base.BaseException;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:35
 * 用户信息异常类
 */
public class UserException extends BaseException {

    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
