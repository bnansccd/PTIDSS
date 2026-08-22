package com.troy.system.api.domain.VO;

import com.troy.common.core.web.VO.NodeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxl
 * @description
 * @date 2024-06-05 13:54
 */
@Data
@ApiModel(description = "菜单模块")
public class SysMenuNewVO extends NodeVO {


    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "菜单类型（0左侧菜单1顶部菜单2按钮3外链）")
    private String menuType;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "路由地址")
    private String href;

    @ApiModelProperty(value = "权限标识")
    private String menuCode;

    @ApiModelProperty(value = "启用停用(0启用1停用)")
    private String status;

    @ApiModelProperty(value = "是否展示(0展示1隐藏)")
    private String isShow;

}
