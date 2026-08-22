package com.ptidss.trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 创建申报单请求（对齐 OpenAPI V1.0 /trade/declarations POST）
 */
@Data
public class TradeDeclarationRequest {

    /** 交易日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate;

    /** 市场类型：intra_province/inter_province */
    private String marketType;

    /** 阶段：day_ahead/real_time/rolling */
    private String stage;

    /** 申报段（时段/量/价） */
    private List<Map<String, Object>> items;

    /** 来源方案/会话 */
    private String sourcePlanId;
}
