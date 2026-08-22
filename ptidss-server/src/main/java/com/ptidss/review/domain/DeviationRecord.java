package com.ptidss.review.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 偏差归因记录（DDL 8.2 deviation_record；预测/决策/执行分层归因）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("deviation_record")
public class DeviationRecord extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 关联复盘报告 */
    private Long reportId;

    /** 归因层：forecast/decision/execution */
    private String layer;

    /** 归因项（如 电价预测偏差） */
    private String item;

    /** 偏差值 */
    private BigDecimal value;

    /** 收益影响（元） */
    private BigDecimal impactAmount;

    /** 原因分析 */
    private String reason;

    /** 影响方向：positive/negative */
    private String direction;
}
