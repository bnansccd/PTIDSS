package com.troy.system.api.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/13 11:11:21
 * @Description: SysDepartDetailsVO
 * @Version: 1.0.0
 */
@ApiModel(description = "组织管理详情列表")
@Data
public class SysDepartDetailsVO implements Serializable {

    @ApiModelProperty(value = "组织信息")
    private SysDepartVO sysDepartVO;

    @ApiModelProperty(value = "用户信息")
    private SysUserVO sysUserVO;
}
