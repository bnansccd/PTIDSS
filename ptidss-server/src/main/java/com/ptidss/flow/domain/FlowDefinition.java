package com.ptidss.flow.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.config.JsonStringTypeHandler;
import lombok.Data;

import java.util.Date;

/**
 * 审批流程定义（DDL 10.4 flow_definition；V2.2 产品化：环节/角色/用户可配置）
 * steps JSONB：[{stepNo,stepName,approveMode(any/all),roleCodes[],userIds[],timeoutHours}]
 */
@Data
@TableName("flow_definition")
public class FlowDefinition {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 流程定义键（decision_confirm 等，唯一） */
    private String processKey;

    /** 流程名称 */
    private String processName;

    /** 业务类型：decision/declaration/ticket/appeal */
    private String bizType;

    /** 审批环节定义（JSONB 数组：节点/审批角色/审批人/模式/时限） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String steps;

    /** 状态：enabled/disabled（disabled 拒绝新发起） */
    private String status;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
