package com.ptidss.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 训练任务（DDL 6.4 training_task；FR-PD-03 模型训练）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("training_task")
public class TrainingTask extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 模型编码：price/load/generation 等 */
    private String modelCode;

    /** 数据集区间 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String datasetRange;

    /** 超参配置 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String config;

    /** 状态：queued/training/success/failed */
    private String status;

    /** 训练指标 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metrics;

    /** 产物地址（MLflow 运行） */
    private String artifactUrl;

    /** 触发方式：daily_increment/weekly_full/manual */
    private String triggeredBy;
}
