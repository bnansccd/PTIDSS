package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色-区域授权（DDL 10.2 sys_role_region，评审决议⑤：角色 × 区域双重授权；
 * 有效区域 = 用户授权区域 ∩ 角色授权区域）
 */
@Data
@TableName("sys_role_region")
public class SysRoleRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private String regionCode;
}
