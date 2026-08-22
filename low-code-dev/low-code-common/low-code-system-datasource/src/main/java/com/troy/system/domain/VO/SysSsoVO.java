package com.troy.system.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxl
 * @date 2023/12/4
 */
@Data
@ApiModel("sso对象")
public class SysSsoVO {

    /**
     * clientId
     */
    @ApiModelProperty("clientId")
    private String clientId;

    /**
     * clientSecret
     */
    @ApiModelProperty("clientSecret")
    private String clientSecret;

    /**
     * 跳转路径
     */
    @ApiModelProperty("跳转路径")
    private String url;

    @ApiModelProperty("sso服务器路径")
    private String requestUrl;

    @ApiModelProperty("获取token")
    private String token;

    @ApiModelProperty("获取用户详情")
    private String profile;
}
