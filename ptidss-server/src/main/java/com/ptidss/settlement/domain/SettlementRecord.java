package com.ptidss.settlement.domain;

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
 * 结算记录（DDL 4.1 settlement_record；按月分区，复合主键 (id, created_at)；
 * 周期口径随 settlement.periodMode 配置，评审决议③）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("settlement_record")
public class SettlementRecord extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 结算周期（如 2026-08，口径随配置） */
    private String settlementPeriod;

    /** 区域（评审决议⑤） */
    private String regionCode;

    /** 来源：system（系统结算）/ exchange（交易中心结算单） */
    private String source;

    /** 费用项：电能量/偏差考核/辅助服务/输配电价 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String items;

    /** 总金额（元，AES-GCM 加密存储） */
    private BigDecimal totalAmount;

    /** 同步状态：synced/pending/diff */
    private String syncStatus;
}
