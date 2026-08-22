package com.ptidss.review.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考核申诉（DDL 8.6 assess_appeal；提交→审核→重算结果，FR-DM-07）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assess_appeal")
public class AssessAppeal extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 关联考核结果 */
    private Long resultId;

    /** 申诉理由 */
    private String appealReason;

    /** 证据材料 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String evidence;

    /** 状态：pending/processing/approved/rejected */
    private String status;

    /** 处理人 */
    private String handler;

    /** 审核意见 */
    private String decision;

    /** 处理留痕 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String history;
}
