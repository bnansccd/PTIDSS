package com.troy.system.api.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Classname: SysDepartDto
 * @Description:
 * @Date 2022/9/6
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "新增岗位")
public class SysDepartDTO implements Serializable {

    @ApiModelProperty(value = "部门名字(长度不超过30)", required = true)
    @NotBlank(message = "部门名字不能为空！")
    @Length(max = 30,message = "部门名称最大长度不超过30个字符")
    private String departName;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "排序（最大序号不超过100000）")
    @Max(value = 100000,message = "最大序号不超过100000")
    private Integer sort;

    @ApiModelProperty(value = "组织负责人")
    private Long userId;

    private String sfqy;

}
