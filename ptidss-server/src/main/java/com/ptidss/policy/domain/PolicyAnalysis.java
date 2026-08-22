package com.ptidss.policy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 政策影响研判（DDL 5.3 policy_analysis；政策变化点→影响环节→影响程度，可追溯）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("policy_analysis")
public class PolicyAnalysis extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 所属政策 */
    private Long policyId;

    /** 政策变化点 */
    private String changePoint;

    /** 影响环节：预测/决策/申报/结算/考核 */
    private String affectedLink;

    /** 影响程度：high/medium/low */
    private String impactLevel;

    /** 研判结论（关联历史数据分析） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String analysisResult;

    /** 研判分析师 */
    private String analyst;

    /** 简报文件地址 */
    private String briefFileUrl;
}
