package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptidss.common.config.JsonLongListTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 系统用户（DDL 10.1 sys_user）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user", autoResultMap = true)
public class SysUser extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 用户名 */
    private String username;

    /** 显示名 */
    private String displayName;

    /** 密码哈希（BCrypt；出参不回显） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    /** 角色 ID 列表（JSONB） */
    @TableField(typeHandler = JsonLongListTypeHandler.class)
    private List<Long> roleIds;

    /** 组织编码 */
    private String orgCode;

    /** 手机号（AES-GCM 加密，应用层） */
    private String phone;

    /** 邮箱（AES-GCM 加密，应用层） */
    private String email;

    /** 状态：active/locked/disabled */
    private String status;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginAt;
}
