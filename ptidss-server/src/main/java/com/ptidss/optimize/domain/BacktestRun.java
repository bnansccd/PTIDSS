package com.ptidss.optimize.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 策略回测（DDL 7.5 backtest_run；FR-TR-06 回测验证与收益量化 P0）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("backtest_run")
public class BacktestRun extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 策略编码 */
    private String strategyCode;

    /** 回测区间 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String dateRange;

    /** 数据版本锁定 */
    private String marketDataVersion;

    /** 基准方案（分步决策） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String basePlan;

    /** 优化方案 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String optimizedPlan;

    /** 收益增量（验收核心） */
    private BigDecimal revenueDelta;

    /** 指标集（夏普/回撤/命中率） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metrics;

    /** 状态：running/success/failed */
    private String status;

    /** 报告地址 */
    private String reportUrl;
}
