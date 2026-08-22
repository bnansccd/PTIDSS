package com.ptidss.trade.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 创建申报单响应（对齐 OpenAPI V1.0：declarationId + complianceCheck）
 */
@Data
public class TradeDeclarationResponse {

    /** 申报单 ID（雪花 ID，JSON 序列化为字符串） */
    private String declarationId;

    /** 合规校验结果（段数/限价/持仓校验） */
    private Map<String, Object> complianceCheck;
}
