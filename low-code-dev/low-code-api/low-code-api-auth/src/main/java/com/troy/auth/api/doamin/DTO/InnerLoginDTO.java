package com.troy.auth.api.doamin.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description: 用户内部登录对象
 * @Author: zhuQing
 * @Date: 2026/3/31 11:38
 * @Version: 1.0
 **/
@Data
public class InnerLoginDTO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 域名称
     */
    @NotBlank(message = "域名称不能为空")
    private String domainName;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 是否验证用户名与密码
     */
    private boolean isCheck = false;


}

