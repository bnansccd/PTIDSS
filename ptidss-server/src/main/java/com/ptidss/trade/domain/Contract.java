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
 * 合同（DDL 3.1 contract；价格敏感，生产环境 AES-GCM 加密存储）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("contract")
public class Contract extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String contractNo;

    /** 品种：annual/monthly/intra_month/rolling */
    private String variety;

    /** 方向：buy/sell */
    private String direction;

    private String counterparty;

    private java.math.BigDecimal totalVolume;

    /** 96 点 × 周期 曲线 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String curveJson;

    /** 价格（加密存储） */
    private java.math.BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /** 状态：draft/active/executing/finished/terminated */
    private String status;

    /** 来源：marketing_platform/exchange/manual */
    private String source;

    /** 多省市场归属（评审决议⑤） */
    private String regionCode;
}
