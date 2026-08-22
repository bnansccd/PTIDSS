package com.troy.auth.api.doamin.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 登录信息
 * @Author: zhuQing
 * @Date: 2026/3/31 11:39
 * @Version: 1.0
 **/
@Data
public class LoginInfoVO implements Serializable {

    /**
     * 登录令牌信息
     */
    private LoginTokenVO loginTokenVO;

    /**
     * 用户名id
     */
    private Long userid;

    /**
     * 用户名
     */
    private String username;
}

