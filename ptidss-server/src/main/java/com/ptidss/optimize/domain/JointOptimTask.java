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
 * 联合优化任务（DDL 7.4 joint_optim_task；FR-TR-06 联合优化引擎 P0）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("joint_optim_task")
public class JointOptimTask extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 任务编号 */
    private String taskNo;

    /** 任务类型：daily/rolling_N/backtest */
    private String taskType;

    /** 优化周期天数 1-7 */
    private Integer horizonDays;

    /** 场景抽样配置 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String scenarios;

    /** 目标权重（收益/CVaR/偏差） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String objectiveWeights;

    /** 约束（申报段数/限价/持仓/爬坡/考核） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String constraints;

    /** 状态：queued/running/success/suboptimal/failed */
    private String status;

    /** 求解耗时 */
    private Integer elapsedMs;

    /** 求解器：HiGHS/SCIP/Gurobi */
    private String solver;

    /** 创建人 */
    private String createdBy;
}
