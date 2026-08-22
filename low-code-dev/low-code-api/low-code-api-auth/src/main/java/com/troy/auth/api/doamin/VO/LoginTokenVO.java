package com.troy.auth.api.doamin.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 登录令牌信息
 * @Author: zhuQing
 * @Date: 2026/3/31 11:40
 * @Version: 1.0
 **/
@Data
public class LoginTokenVO implements Serializable {

    /**
     * 令牌
     */
    private String access_token;

    /**
     * 令牌有效期(分钟)
     */
    private Long expires_in;
}

