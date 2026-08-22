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
 * 结算核对结果（DDL 4.2 settlement_reconcile；v1.0.2：record_id 外键移除，
 * 分区表外键业界惯例，应用层校验）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("settlement_reconcile")
public class SettlementReconcile extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 关联结算记录（v1.0.2：分区表外键移约束） */
    private Long recordId;

    /** 逐科目核对明细 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String checkItems;

    /** 差异记录 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String diffRecords;

    /** 状态：consistent/diff/pending */
    private String status;

    /** 差异金额（元） */
    private BigDecimal diffAmount;
}
