package com.ptidss.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智能体运行记录（DDL 7.2 agent_run；输入快照/输出/置信度/耗时，SRS FR-DM-02 契约化留痕）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_run")
public class AgentRun extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 运行 ID（run_id 唯一） */
    private String runId;

    /** 智能体编码 */
    private String agentCode;

    /** 决策会话编号（decision_session.session_no） */
    private String sessionId;

    /** 输入快照 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String inputSnapshot;

    /** 输出（含结论/关键指标） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String output;

    /** 置信度 0-1 */
    private java.math.BigDecimal confidence;

    /** 推理依据（可解释） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String reasoning;

    /** 耗时（毫秒） */
    private Integer elapsedMs;

    /** 状态：success/failed/timeout */
    private String status;
}
