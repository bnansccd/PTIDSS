package com.troy.system.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Classname: SysDictDTO
 * @Description:
 * @Date 2022/9/11
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "新增字典")
public class SysDictDTO implements Serializable {

    @ApiModelProperty(value = "字典名称（最大长度60）",required = true)
    @NotBlank(message = "请填写字典名称")
    @Length(max = 60,message = "字典名称输入长度不能超过30位")
    private String dictName;

    @ApiModelProperty(value = "字典类型（最大长度60）",required = true)
    @NotBlank(message = "请填写字典类型")
    @Length(max = 60,message = "字典类型输入长度不能超过30位")
    private String dictType;

    @ApiModelProperty(value = "字典父id")
    private Long parentId;

    @ApiModelProperty(value = "排序（最大序号不超过100000）")
    @Max(value = 100000,message = "最大序号不超过100000")
    private Integer sort;

    @ApiModelProperty(value = "备注（最大长度200）")
    @Length(max = 200,message = "备注最大长度不能超过200位")
    private String remarks;
}
