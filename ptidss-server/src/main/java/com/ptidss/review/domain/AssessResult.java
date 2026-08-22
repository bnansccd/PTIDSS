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
 * 考核结果（DDL 8.5 assess_result；周期/范围，FR-DM-07）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assess_result")
public class AssessResult extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 考核周期（如 2026-08） */
    private String period;

    /** 范围：personal/team */
    private String scope;

    /** 被考核人 */
    private Long userId;

    /** 分项得分 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String scores;

    /** 总分 */
    private BigDecimal totalScore;

    /** 排名 */
    private Integer rank;

    /** 状态：pending/confirmed/appealing/corrected */
    private String status;
}
