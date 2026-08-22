package com.troy.system.api.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/18 13:13:52
 * @Description: AuditVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "审计返回实体")
public class AuditVO implements Serializable {

    @ApiModelProperty(value = "用户信息")
    private List<SysUserVO> sysUserVOS = new ArrayList<>();

    @ApiModelProperty(value = "部门信息")
    private List<SysDepartVO> sysDepartVOS = new ArrayList<>();
}
