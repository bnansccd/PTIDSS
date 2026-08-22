package com.ptidss.optimize.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 策略库（DDL 7.6 strategy_library；FR-TR-06 策略库，回测/复盘/人工沉淀）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("strategy_library")
public class StrategyLibrary extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 策略编码 */
    private String strategyCode;

    /** 策略名称 */
    private String strategyName;

    /** 策略参数 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String params;

    /** 历史绩效（收益/胜率/回撤） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String performance;

    /** 状态：effective/invalid/evaluating */
    private String status;

    /** 来源：backtest/review/manual */
    private String source;
}
