package com.troy.system.domain.DTO;

import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.security.annotation.ValidDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Classname: SysMenuDTO
 * @Description:
 * @Date 2022/9/8
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "新增菜单")
public class SysMenuDTO implements Serializable {

    @ApiModelProperty(value = "菜单名称(长度30)", required = true)
    @NotBlank(message = "请填写菜单名称！")
    @Length(max = 30, message = "菜单名称最大长度不能超过30")
    private String menuName;

    @ApiModelProperty(value = "路由地址(长度100)")
    @Length(max = 100, message = "路由地址最大长度不能超过100")
    private String href;

    @ApiModelProperty(value = "菜单标识(长度100)", required = true)
    @NotBlank(message = "请填写菜单标识！")
    @Length(max = 100, message = "菜单标识最大长度不能超过100")
    private String menuCode;

    @ApiModelProperty(value = "菜单类型（参考字典类型：MENU_TYPE）", allowableValues = "0,1,2,3", required = true)
    @NotBlank(message = "请选择菜单类型！")
    @ValidDict(parentType = DictTypeEnums.MENU_TYPE,message = "请选择正确的菜单类型")
    private String menuType;

    @ApiModelProperty(value = "图标(长度30)")
    @Length(max = 30, message = "图标最大长度不能超过30")
    private String icon;

    @ApiModelProperty(value = "父级菜单id")
    private Long parentId;

    @ApiModelProperty(value = "排序（最大序号不超过100000）")
    @Max(value = 100000,message = "最大序号不超过100000")
    private Integer sort;

    @ApiModelProperty(value = "是否是基础类型(参考字典类型：TRUE_FALSE)", allowableValues = "0,1", required = true)
    @NotBlank(message = "请选择是否是基础类型")
    @ValidDict(parentType = DictTypeEnums.TRUE_FALSE,message = "请选择正确的基础类型")
    private String isBase;

    private Long appId;

}
