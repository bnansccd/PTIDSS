package com.ptidss.agent.domain;

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
 * 智能体注册（DDL 7.1 agent_registry；LangGraph 多智能体，版本化）
 * 七大智能体：forecast 预测 / market 行情 / quote 报价 / risk 风险
 *            / compliance 合规 / settlement 结算 / review 复盘
 * 说明：version 为智能体版本（VARCHAR 业务版本，非乐观锁），故不继承 BaseEntity（同 RuleConfig 约定）
 */
@Data
@TableName("agent_registry")
public class AgentRegistry {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 智能体编码：forecast/market/quote/risk/compliance/settlement/review */
    private String agentCode;

    /** 智能体名称 */
    private String agentName;

    /** 职责描述 */
    private String role;

    /** 输入契约 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String inputSchema;

    /** 输出契约 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String outputSchema;

    /** 智能体版本（业务版本，如 v0.1.0） */
    private String version;

    /** 模型配置（模型编码/框架/超参） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String modelConfig;

    /** 状态：active/disabled/maintenance */
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
