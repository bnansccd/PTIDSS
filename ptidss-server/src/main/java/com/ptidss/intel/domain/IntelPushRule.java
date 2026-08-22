package com.ptidss.intel.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 情报推送规则（DDL 11.7 intel_push_rule；FR-INT-04，标签×重要度→角色/渠道）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intel_push_rule")
public class IntelPushRule extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 规则名称 */
    private String ruleName;

    /** 标签过滤条件 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String tagsFilter;

    /** 重要度过滤：high/medium/low */
    private String importanceFilter;

    /** 目标角色 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String targetRoles;

    /** 推送渠道 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String channel;

    /** 静默时段 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String silentPeriod;

    /** 状态：active/disabled */
    private String status;
}
