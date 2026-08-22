package com.troy.system.api.domain.VO;

import com.troy.common.core.web.VO.NodeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/9 11:11:50
 * @Description: SysMenuVO
 * @Version: 1.0.0
 */

@Data
@ApiModel(description = "菜单模块")
public class SysMenuVO extends NodeVO {


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

    @ApiModelProperty(value = "应用ID")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "是否是基础0是1否")
    private String isBase;

    private String ancestors;
}
