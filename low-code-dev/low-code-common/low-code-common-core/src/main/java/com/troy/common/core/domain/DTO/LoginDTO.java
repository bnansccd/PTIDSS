package com.troy.common.core.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/2 17:17:31
 * @Description: 用户登录对象
 * @Version: 1.0.0
 */

@Data
@ApiModel(description = "用户登录对象")
public class LoginDTO implements Serializable {

    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @ApiModelProperty(value = "用户密码", required = true)
    private String password;

    @ApiModelProperty(value = "应用ID")
    private Long appId;

    private String code;

    private String requestId;

    private String capCode;

}
