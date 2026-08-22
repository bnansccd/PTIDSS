package com.troy.system.api.domain.VO;

import com.troy.common.core.web.VO.NodeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhuqing
 * @Date: 2022/7/29 16:16:42
 * @Description: SysDeptVO
 * @Version: 1.0.0
 */

@Data
@ApiModel(description = "部门信息")
public class SysDepartVO extends NodeVO {

    @ApiModelProperty(value = "部门名称")
    private String departName;

    @ApiModelProperty(value = "部门名称祖级")
    private String ancestorsDepartName;

    @ApiModelProperty(value = "组织负责人")
    private Long userId;

    @ApiModelProperty(value = "组织负责人信息")
    private SysUserVO sysUserVO;

    @ApiModelProperty(value = "部门编码")
    private String code;

    private String sfqy;

}
