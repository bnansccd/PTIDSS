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
 * 日滚动方案（DDL 3.2 rolling_plan；决策输出，三情景）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rolling_plan")
public class RollingPlan extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 交易日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate;

    /** 情景：baseline/conservative/aggressive */
    private String scenario;

    /** 方案类型：rolling_adjust/quote_plan */
    private String planType;

    /** 调整明细（时段/电量/价格） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String adjustments;

    /** 预期收益 */
    private java.math.BigDecimal expectedRevenue;

    /** 风险指标（CVaR/最大回撤/偏差风险） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String riskMetrics;

    /** 依据链引用（decision_session.session_no） */
    private String evidenceChainRef;

    /** 状态：generated/confirmed/modified/rejected/executed */
    private String status;

    private String createdBy;

    /** 多省市场归属（评审决议⑤） */
    private String regionCode;
}
