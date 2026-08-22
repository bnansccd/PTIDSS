package com.troy.system.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Classname: SysRoleDTO
 * @Description:
 * @Date 2022/9/7
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "新增角色")
public class SysRoleDTO implements Serializable {

    @ApiModelProperty(value = "角色名称（长度最大30）")
    @NotBlank(message = "请填写角色名称！")
    @Length(max = 30,message = "角色名称长度不超过30")
    private String roleName;

    @ApiModelProperty(value = "角色编码（长度最大30）")
    @NotBlank(message = "请填写角色编码！")
    @Length(max = 30,message = "角色编码长度不超过30")
    private String roleCode;

    @ApiModelProperty(value = "排序（最大序号不超过100000）")
    @Max(value = 100000,message = "最大序号不超过100000")
    private Integer sort;

    @ApiModelProperty(value = "备注（长度最大200）")
    @Length(max = 200,message = "备注长度不超过200")
    private String remark;

    private String isSuper;

}
