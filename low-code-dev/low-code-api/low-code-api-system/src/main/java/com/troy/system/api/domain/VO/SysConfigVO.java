package com.troy.system.api.domain.VO;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname: SysConfigVo
 * @Description:
 * @Date 2022/9/12
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "参数配置回显实体")
public class SysConfigVO extends BaseVO {

    @ApiModelProperty(value = "参数名称")
    private String configName;

    @ApiModelProperty(value = "参数键名")
    private String configKey;

    @ApiModelProperty(value = "参数键值")
    private String configValue;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @JSONField(name = "basic")
    @JsonProperty(value = "basic")
    private String isBasic;

}
