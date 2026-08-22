package com.ptidss.market.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 价格时序点（对齐 OpenAPI V1.0 PricePoint schema）
 */
@Data
public class PricePoint {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ts;

    private java.math.BigDecimal price;

    private java.math.BigDecimal volume;

    /** 阶段：day_ahead/real_time */
    private String stage;
}
