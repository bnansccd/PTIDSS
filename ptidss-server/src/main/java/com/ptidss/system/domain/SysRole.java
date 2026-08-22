package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统角色（DDL 10.2 sys_role，固定 7 类：trader/analyst/settlement/admin/manager/compliance/mobile）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 角色编码 */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 描述 */
    private String description;

    /** 状态：active/disabled */
    private String status;

    /** 授权区域编码列表（瞬态，sys_role_region；角色 × 区域双重授权，有效区域取交集） */
    @TableField(exist = false)
    private List<String> regionCodes;
}
