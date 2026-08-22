package com.ptidss.policy.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.config.JsonStringTypeHandler;
import lombok.Data;

import java.util.Date;

/**
 * 规则库（DDL 5.4 rule_config；版本化业务规则，version 为本表业务版本而非乐观锁，故不继承 BaseEntity）
 * 解析后的条款自动沉淀为可配置规则，供合规校验与决策引擎引用（FR-PD-01）
 */
@Data
@TableName("rule_config")
public class RuleConfig {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 规则编码（同编码多版本） */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 规则类型：compliance/decision/assessment */
    private String ruleType;

    /** 参数化配置：段数/限价/阈值等 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String params;

    /** 规则版本（业务版本，从 1 递增） */
    private Integer version;

    /** 生效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date effectiveDate;

    /** 失效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiredDate;

    /** 来源政策 */
    private Long sourcePolicyId;

    /** 状态：draft/active/expired */
    private String status;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
