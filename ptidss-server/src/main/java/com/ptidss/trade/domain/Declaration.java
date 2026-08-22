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
 * 申报单（DDL 3.3 declaration；按月分区，复合主键 (id, trade_date)）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("declaration")
public class Declaration extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 申报单号（唯一，分区表唯一索引须含分区键） */
    private String declarationNo;

    /** 关联合同 */
    private Long contractId;

    /** 交易日期（分区键） */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate;

    /** 市场类型：intra_province/inter_province */
    private String marketType;

    /** 阶段：day_ahead/real_time/rolling */
    private String stage;

    /** 申报市场所属区域（评审决议⑤） */
    private String regionCode;

    /** 申报明细（段数/量价），合规校验后锁定 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String items;

    /** 合规校验结果（段数/限价/持仓比例） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String complianceCheck;

    private String fileUrl;

    /** 状态：draft/pending_submit/submitted/receipted/partially_matched */
    private String status;

    /** 交易中心回执号 */
    private String receiptNo;

    /** V2.4 网关推送状态：pending/success/failed/skipped（提交申报 → 交易系统推送监测） */
    private String gatewayPushStatus;

    /** V2.4 网关推送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gatewayPushTime;

    /** V2.4 网关推送回执/错误信息 */
    private String gatewayPushDetail;

    private String createdBy;
}
