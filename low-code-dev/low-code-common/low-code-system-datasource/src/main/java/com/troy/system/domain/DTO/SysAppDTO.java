package com.troy.system.domain.DTO;

import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.security.annotation.ValidDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

/**
 * @author chenxl
 * @Date 2023/3/15
 */
@Data
@ApiModel(description = "公司应用")
public class SysAppDTO {

    @ApiModelProperty(value = "图标（最大长度200）", required = true)
    @NotBlank(message = "请上传应用图标")
    @Length(max = 200, message = "应用图标长度最大为200")
    private String icon;

    @ApiModelProperty(value = "启用状态 0未启用 1启用", required = true)
    @NotBlank(message = "请选择启停状态")
    @ValidDict(parentType = DictTypeEnums.STATUS_TYPE,message = "请选择正确的启停状态")
    private String status;

    @ApiModelProperty(value = "地址（最大长度200）", required = true)
    @NotBlank(message = "请输入应用地址")
    @Length(max = 200, message = "应用地址长度最大为200")
    private String url;

    @ApiModelProperty(value = "应用背景图（最大长度200）", required = true)
    @NotBlank(message = "请输入应用背景图")
    @Length(max = 200, message = "应用背景图长度最大为200")
    private String background;

    @ApiModelProperty(value = "应用名称（最大长度30）", required = true)
    @NotBlank(message = "请输入应用名称")
    @Length(max = 30, message = "应用名称长度最大为30")
    private String name;

    @ApiModelProperty(value = "应用编码（最大长度30）", required = true)
    @NotBlank(message = "请输入应用编码")
    @Length(max = 30, message = "应用编码长度最大为30")
    private String code;

    @ApiModelProperty(value = "排序（最大序号不超过100000）")
    @Max(value = 100000, message = "最大序号不超过100000")
    private Integer sort;

    @ApiModelProperty(value = "应用类型1 内部 2外部", required = true)
    @NotBlank(message = "请选择应用类型")
    @ValidDict(parentType = DictTypeEnums.APP_TYPE,message = "请选择正确的应用类型")
    private String type;

}
