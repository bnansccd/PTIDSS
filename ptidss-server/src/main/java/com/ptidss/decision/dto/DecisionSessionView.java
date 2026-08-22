package com.ptidss.decision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 决策会话详情（对齐 OpenAPI V1.0 DecisionSession schema）
 */
@Data
public class DecisionSessionView {

    /** 会话 ID（雪花 ID，JSON 序列化为字符串） */
    private String sessionId;

    private String sessionNo;

    /** 类型：rolling/spot_quote/joint_optimize */
    private String sessionType;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate;

    /** 编排状态（本实现同步完成） */
    private String status;

    /** 最终策略 */
    private Map<String, Object> finalStrategy;

    /** 参与智能体 */
    private List<String> agents;

    /** 人审状态：pending/confirmed/modified/rejected */
    private String humanReviewStatus;

    /** 依据摘要（agent 输出/置信度） */
    private Map<String, Object> evidenceSummary;

    private String reviewedBy;

    private String modifyReason;

    private String reviewer2;
}
