package com.troy.system.emnus;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2025/6/9 19:47
 * @Version: 1.0
 **/
public enum ThirdLoginTypeEmnus {

    YKZ("YKZ", "渝快政"),

    YKB("YKB", "渝快办"),

    BCG("BCG", "业务协同网关"),

    ;

    private ThirdLoginTypeEmnus(String loginType, String loginName) {
        this.loginType = loginType;
        this.loginName = loginName;
    }

    private String loginType;

    private String loginName;

    public String getLoginType() {
        return loginType;
    }

    public String getLoginName() {
        return loginName;
    }
}
