package com.troy.system.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Classname: SysPostDTO
 * @Description:
 * @Date 2022/9/5
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "新增岗位")
public class SysPostDTO implements Serializable {

    @ApiModelProperty(value = "岗位名称（最大长度30）", required = true)
    @NotBlank(message = "岗位名称不能为空！")
    @Length(max = 30,message = "岗位名称最大长度不超过30")
    private String postName;

    @ApiModelProperty(value = "岗位编码（最大长度30）")
    @Length(max = 30,message = "岗位编码最大长度不超过30")
    private String postCode;

    @ApiModelProperty(value = "排序（最大序号不超过100000）")
    @Max(value = 100000,message = "最大序号不超过100000")
    private Integer sort;

    @ApiModelProperty(value = "备注（最大长度200）")
    @Length(max = 200,message = "岗位编码最大长度不超过200")
    private String remarks;

    private String sfqy;

}
