package com.ptidss.forecast.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 预测结果（DDL 6.2 forecast_result；FR-TR-06 预测结果可视化与置信区间 P0）
 * 96 点明细在 TDengine st_forecast_series；本表为元数据/汇总口径
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("forecast_result")
public class ForecastResult extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 预测任务 ID */
    private Long taskId;

    /** 模型版本 v{主}.{次}.{补} */
    private String modelVersion;

    /** 预测类型：generation/price/load */
    private String predictType;

    /** 市场类型（电价预测）：intra_province/inter_province */
    private String marketType;

    /** 预测目标区域（评审决议⑤） */
    private String regionCode;

    /** 交易日期 */
    private Date tradeDate;

    /** 预测值 */
    private BigDecimal value;

    /** 90% 置信区间下界 */
    private BigDecimal lowerBound;

    /** 90% 置信区间上界 */
    private BigDecimal upperBound;

    /** 置信度 0-1 */
    private BigDecimal confidence;
}
