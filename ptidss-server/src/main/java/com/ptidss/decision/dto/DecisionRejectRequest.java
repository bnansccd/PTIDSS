package com.ptidss.decision.dto;

import lombok.Data;

/**
 * 交易员驳回策略请求（对齐 OpenAPI V1.0 /decision/sessions/{id}/reject POST；
 * FR-DM-05 人审驳回须记录原因，状态 pending/confirmed → rejected）
 */
@Data
public class DecisionRejectRequest {

    /** 驳回原因（必填，FR-DM-05） */
    private String reason;
}
