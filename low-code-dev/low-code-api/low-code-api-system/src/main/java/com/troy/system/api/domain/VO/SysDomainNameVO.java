package com.troy.system.api.domain.VO;

import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/8 14:14:09
 * @Description: DomainNameVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "域名管理")
public class SysDomainNameVO extends BaseVO {

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "泛域名")
    private String universalDomainName;

    @ApiModelProperty(value = "备案信息")
    private String recordInfo;

    @ApiModelProperty(value = "备案信息跳转地址")
    private String recordInfoUrl;

    @ApiModelProperty(value = "备注")
    private String remarks;
}
