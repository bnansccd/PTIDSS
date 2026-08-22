package com.ptidss.review.domain;

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
 * 考核指标（DDL 8.4 assess_indicator；权重/公式/评分规则，FR-DM-07）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assess_indicator")
public class AssessIndicator extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 指标编码（唯一） */
    private String code;

    /** 指标名称 */
    private String name;

    /** 计算公式 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String formula;

    /** 权重 0-1 */
    private BigDecimal weight;

    /** 目标值 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String targetValue;

    /** 评分规则 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String scoringRule;

    /** 数据来源表 */
    private String dataSource;

    /** 状态：active/disabled */
    private String status;
}
