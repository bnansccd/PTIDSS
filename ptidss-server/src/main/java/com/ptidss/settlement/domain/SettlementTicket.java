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
 * 差异工单（DDL 4.3 settlement_ticket；Flowable 流程挂接）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("settlement_ticket")
public class SettlementTicket extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 关联核对结果 */
    private Long reconcileId;

    /** 差异类型：amount_diff/missing_record/extra_record */
    private String diffType;

    /** 差异金额（元） */
    private BigDecimal diffAmount;

    /** 状态：pending/processing/reviewed/closed */
    private String status;

    /** 处理人 */
    private String handler;

    /** 处理留痕时间线（assign/process/review/close） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String history;

    /** Flowable 流程实例 ID */
    private String flowInstanceId;
}
