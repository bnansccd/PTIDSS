package com.ptidss.review.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 策略回流（DDL 8.3 strategy_feedback；复盘→策略库反哺决策引擎，FR-RS-01）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("strategy_feedback")
public class StrategyFeedback extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 关联复盘报告 */
    private Long reviewId;

    /** 策略编码 */
    private String strategyCode;

    /** 反馈：effective/invalid/adjust */
    private String feedback;

    /** 调整参数 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String updatedParams;

    /** 状态：pending/confirmed/rejected */
    private String status;
}
