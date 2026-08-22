package com.troy.common.core.enums;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:28
 * 用户状态
 */
public enum UserStatus {

    OK("0", "正常"), DISABLE("1", "停用");

    private final String code;
    private final String info;

    UserStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
