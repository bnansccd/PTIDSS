package com.troy.system.api.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 16:16:49
 * @Description: SysRoleVO
 * @Version: 1.0.0
 */

@Data
@ApiModel(description = "角色管理")
public class SysRoleVO implements Serializable {

    @ApiModelProperty(value = "角色ID")
    private Long id;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "角色排序")
    private Integer sort;

    @ApiModelProperty(value = "remark")
    private String remark;

    @ApiModelProperty(value = "数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）")
    private String dataRange;

    @ApiModelProperty(value = "角色所绑定的用户id")
    private List<Long> userIds = new ArrayList<>();

    @ApiModelProperty(value = "数据权限部门Id")
    private List<Long> departIds = new ArrayList<>();

    private String isSuper;
}
