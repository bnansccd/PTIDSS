package com.troy.system.api.model;

import com.troy.system.api.domain.VO.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 16:16:43
 * @Description: 用户信息
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "用户登录信息")
public class LoginUser implements Serializable {

    @ApiModelProperty(value = "用户唯一标识")
    private String token;

    @ApiModelProperty(value = "用户名id")
    private Long userid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "登录时间")
    private Long loginTime;

    @ApiModelProperty(value = "过期时间")
    private Long expireTime;

    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "当前应用")
    private Long appId;

    private String appCode;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "权限列表")
    private Set<String> permissions = new HashSet<>();

    @ApiModelProperty(value = "角色列表")
    private Set<String> roles = new HashSet<>();

    @ApiModelProperty(value = "用户信息")
    private SysUserVO sysUserVO;

    @ApiModelProperty(value = "应用id")
    private Set<Long> appIds = new HashSet<>();

    private Set<String> appCodes = new HashSet<>();

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
}
