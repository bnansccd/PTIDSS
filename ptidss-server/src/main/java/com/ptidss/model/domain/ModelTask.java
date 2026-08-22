package com.ptidss.model.domain;

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
 * 模型任务报告（DDL 13 model_task；V2.4 训练触发/离线评估/在线推理 → 详细报告和过程）
 * 记录任务输入快照、执行过程步骤、结果指标、与前一次同任务对标（delta），方便用户理解。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_task")
public class ModelTask extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 任务类型：train/evaluate/inference */
    private String taskType;

    /** 模型编码（price/load/generation 或算法编码） */
    private String modelCode;

    /** 任务关联模型版本（推理/评估） */
    private String modelVersion;

    /** 任务名称（如 价格预测 日增量训练） */
    private String taskName;

    /** 任务输入快照 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String inputJson;

    /** 执行过程步骤 [{step,detail,timeMs}] */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String processSteps;

    /** 结果（指标/序列摘要/回执） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String resultJson;

    /** 与前面对标 {baselineTaskId, baselineMetrics, delta} */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String compareJson;

    /** 状态：queued/running/success/failed */
    private String status;

    /** 执行耗时（毫秒） */
    private Integer latencyMs;

    private String createdBy;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishedAt;
}
