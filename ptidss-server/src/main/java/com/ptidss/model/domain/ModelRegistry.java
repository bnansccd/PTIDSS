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
 * 模型注册（DDL 6.3 model_registry；MLflow 同步，FR-PD-03 预测模型预研）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_registry")
public class ModelRegistry extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 模型编码：price/load/generation 等 */
    private String modelCode;

    /** 模型名称 */
    private String modelName;

    /** 版本 v{主}.{次}.{补}（避开 BaseEntity 乐观锁 version 冲突，DDL 列 model_version） */
    private String modelVersion;

    /** 框架：pytorch/xgboost/lightgbm */
    private String framework;

    /** 指标（MAPE/方向准确率） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metrics;

    /** 状态：training/evaluating/online/rolled_back */
    private String status;

    /** 权重文件（MinIO） */
    private String fileUrl;

    /** 训练完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date trainedAt;
}
