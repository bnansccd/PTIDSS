package com.ptidss.review.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 复盘报告（DDL 8.1 review_report；三层归因：预测/决策/执行，FR-RS-01）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_report")
public class ReviewReport extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 报告类型：weekly/monthly/special */
    private String reportType;

    /** 周期起 */
    private Date periodStart;

    /** 周期止 */
    private Date periodEnd;

    /** 摘要：收益/成交/偏差 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String summary;

    /** 三层归因：预测/决策/执行 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String deviationAnalysis;

    /** 策略评估 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String strategyEval;

    /** 改进建议 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String suggestions;

    /** 状态：generating/completed/failed */
    private String status;

    /** 报告文件 URL */
    private String fileUrl;
}
