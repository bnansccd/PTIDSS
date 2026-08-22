package com.ptidss.policy.domain;

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
 * 政策解析条款（DDL 5.2 policy_article；LLM 结构化解析 + 人工确认，置信度标注）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("policy_article")
public class PolicyArticle extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 所属政策 */
    private Long policyId;

    /** 条款类型：trade_rule/price_mechanism/assessment/settlement */
    private String clauseType;

    /** 条款原文 */
    private String originalText;

    /** 参数化结构（抽取字段） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String parsedStructure;

    /** 解析置信度 0-1 */
    private BigDecimal confidence;

    /** 关联沉淀规则（rule_config.id） */
    private Long relatedRuleId;

    /** 人工确认状态：pending/confirmed/revised */
    private String reviewStatus;
}
