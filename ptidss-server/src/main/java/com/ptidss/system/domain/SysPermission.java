package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统权限（DDL 10.2 sys_permission，三级权限：menu 菜单 / api 接口 / data 数据）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 权限编码（如 menu:market / api:declaration / data:region） */
    private String permCode;

    /** 权限名称 */
    private String permName;

    /** 资源类型：menu/api/data */
    private String resourceType;

    /** 资源匹配模式（如 /market/**） */
    private String resourcePattern;

    /** 状态：active/disabled */
    private String status;
}
