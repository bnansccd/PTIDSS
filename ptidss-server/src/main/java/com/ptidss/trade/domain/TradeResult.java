package com.ptidss.trade.domain;

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
 * 成交结果（DDL 3.4 trade_result；按日分区，复合主键 (id, trade_date)）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("trade_result")
public class TradeResult extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 关联申报单 */
    private Long declarationId;

    /** 交易日期（分区键） */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate;

    /** 成交市场所属区域（评审决议⑤） */
    private String regionCode;

    /** 成交量 */
    private java.math.BigDecimal matchedVolume;

    /** 成交价 */
    private java.math.BigDecimal matchedPrice;

    /** 96 点成交曲线 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String matchedCurve;

    /** 结算影响测算 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String settlementImpact;

    /** 状态：matched/partially/unmatched */
    private String status;
}
