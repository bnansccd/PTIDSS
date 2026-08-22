package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.config.JsonStringTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 审计日志（DDL 10.3 audit_log，按月分区；关键操作前后快照；等保三级审计）
 */
@Data
@TableName(value = "audit_log", autoResultMap = true)
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 链路追踪 ID */
    private String traceId;

    /** 操作人 ID */
    private Long userId;

    /** 操作人用户名（冗余落库，v1.0.2 条目 9：未认证操作如 login 由应用层从入参提取） */
    private String username;

    /** 操作动作 */
    private String action;

    /** 目标类型 */
    private String targetType;

    /** 目标 ID */
    private String targetId;

    /** 操作前快照（JSONB） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String beforeSnapshot;

    /** 操作后快照（JSONB） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String afterSnapshot;

    /** 客户端 IP */
    private String ip;

    /** 用户代理 */
    private String userAgent;

    /** 结果：success/fail */
    private String result;

    /** 操作归属区域（平台级操作可空；等保三级按省检索，v1.0.1 勘误条目 3） */
    private String regionCode;

    /** 操作时间（分区键） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
}
