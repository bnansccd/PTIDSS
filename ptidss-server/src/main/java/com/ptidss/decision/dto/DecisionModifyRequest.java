package com.ptidss.decision.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 交易员修改策略请求（对齐 OpenAPI V1.0 /decision/sessions/{id}/modify POST）
 */
@Data
public class DecisionModifyRequest {

    /** 修改明细（时段/量/价） */
    private List<Map<String, Object>> modifications;

    /** 修改依据（必填，FR-DM-05） */
    private String reason;

    /** 复核人（超阈值必填，双人复核） */
    private String secondReviewer;
}
