package com.troy.common.core.exception.user;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:36
 * 用户密码不正确或不符合规范异常类
 */
public class UserPasswordNotMatchException extends UserException {

    private static final long serialVersionUID = 1L;

    public UserPasswordNotMatchException() {
        super("user.password.not.match", null);
    }
}
