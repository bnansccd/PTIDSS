package com.ptidss.flow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 流程实例（DDL 11.6 flow_instance，v1.0.3 条目 12 新增；OpenAPI V1.1 /flow/**
 * 审批流：决策确认/申报审批/差异工单/考核申诉；轻量状态机，M7 移动端审批依赖）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_instance")
public class FlowInstance extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 流程实例号 */
    private String instanceNo;

    /** 流程定义键（settlement_ticket_review 等） */
    private String processKey;

    /** 业务类型：decision/declaration/ticket/appeal */
    private String bizType;

    /** 业务单据号（与 settlement_ticket.flow_instance_id 同域） */
    private String bizId;

    /** 流程变量（发起人/金额/紧急度） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String variables;

    /** 状态：running/completed/terminated */
    private String status;

    /** 当前节点：apply/review/approve/archive */
    private String currentNode;

    /** 当前处理人 */
    private String currentAssignee;

    /** 发起人 */
    private String startBy;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
}
