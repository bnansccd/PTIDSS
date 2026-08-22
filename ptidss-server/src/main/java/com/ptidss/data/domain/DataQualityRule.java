package com.ptidss.data.domain;

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
 * 数据质量规则（DDL 11.3 data_quality_rule；FR-PD-05 数据质量 P1）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_quality_rule")
public class DataQualityRule extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 规则编码 */
    private String ruleCode;

    /** 类型：completeness/accuracy/timeliness */
    private String ruleType;

    /** 目标表 */
    private String targetTable;

    /** 目标字段 */
    private String targetField;

    /** 规则条件 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String condition;

    /** 阈值 0-1 */
    private BigDecimal threshold;

    /** 严重度：high/medium/low */
    private String severity;

    /** 状态：active/disabled */
    private String status;
}
