package com.ptidss.decision.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 决策会话（DDL 7.3 decision_session；人机协同：人审/修改依据/双人复核）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("decision_session")
public class DecisionSession extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 会话编号（evidence_chain_ref 引用） */
    private String sessionNo;

    /** 类型：rolling/spot_quote/joint_optimize */
    private String sessionType;

    /** 交易日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate;

    /** 编排器版本 */
    private String orchestratorVersion;

    /** 参与智能体列表 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String agents;

    /** 最终策略（决策输出） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String finalStrategy;

    /** 依据链全量快照 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String evidenceChain;

    /** 冲突仲裁记录 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String conflictRecords;

    /** 人审状态：pending/confirmed/modified/rejected */
    private String humanReviewStatus;

    private String reviewedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reviewedAt;

    /** 修改必须记录依据（FR-DM-05） */
    private String modifyReason;

    /** 双人复核 */
    private String reviewer2;
}
