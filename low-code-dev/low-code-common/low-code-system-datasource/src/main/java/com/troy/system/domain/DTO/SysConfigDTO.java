package com.troy.system.domain.DTO;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Classname: SysConfigDto
 * @Description:
 * @Date 2022/9/12
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "参数配置实体")
public class SysConfigDTO implements Serializable {

    @ApiModelProperty(value = "参数名称(最大长度不超过100)",required = true)
    @NotBlank(message = "请填写参数名称！")
    @Length(max = 100,message = "参数名称最大长度不能超过100")
    private String configName;

    @ApiModelProperty(value = "参数键名（最大长度长度不超过100）",required = true)
    @NotBlank(message = "请填写参数键名！")
    @Length(max = 100,message = "参数键名最大不长度能超过100")
    private String configKey;

    @ApiModelProperty(value = "参数键值（最大长度长度不超过500）",required = true)
    @NotBlank(message = "请填写参数键值！")
    @Length(max = 500,message = "参数键值最大长度不能超过500")
    private String configValue;

    @ApiModelProperty(value = "备注（最大长度长度不超过500）")
    @Length(max = 500,message = "备注最大长度不能超过500")
    private String remarks;

    @JSONField(name = "basic")
    @JsonProperty(value = "basic")
    private String isBasic;

}
