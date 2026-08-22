package com.troy.system.api.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/8 14:14:04
 * @Description: SysUserDetailsVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "用户列表返回实体")
public class SysUserDetailsVO implements Serializable {

    @ApiModelProperty(value = "用户信息")
    private SysUserVO sysUserVO;

    @ApiModelProperty(value = "用户对应部门信息")
    private SysDepartVO sysDepartVO;

    @ApiModelProperty(value = "用户对应角色信息")
    private List<SysRoleVO> sysRoleVOS = new ArrayList<>();

    @ApiModelProperty(value = "用户对应岗位信息")
    private List<SysPostVO> sysPostVOS = new ArrayList<>();

    @ApiModelProperty(value = "用户对应的权限")
    private List<SysMenuVO> sysMenuVOS = new ArrayList<>();

    @ApiModelProperty(value = "数据权限")
    private DataPermissionsVO dataPermissionsVO;

    @ApiModelProperty(value = "租户信息")
    private SysTenantVO sysTenantVO;

    @ApiModelProperty(value = "用户对应的权限")
    private List<SysAppVO> sysAppVOS = new ArrayList<>();


}
