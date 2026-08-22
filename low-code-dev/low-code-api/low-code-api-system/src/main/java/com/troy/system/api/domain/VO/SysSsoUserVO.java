package com.troy.system.api.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxl
 * @date 2023/12/4
 */
@Data
@ApiModel("账号绑定关系")
public class SysSsoUserVO {

    /**
     * sso-登录账号
     */
    @ApiModelProperty("第三方username")
    private String username;

    /**
     * 用户ID
     */
    @ApiModelProperty("本系统ID")
    private Long userId;


    @ApiModelProperty("本系统username")
    private String loginUsername;

    @ApiModelProperty("第三方账号备注")
    private String content;

    @ApiModelProperty("第三方系统标识")
    private String sysTarget;

    private Long id;
}
