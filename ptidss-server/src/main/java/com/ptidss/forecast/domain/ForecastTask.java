package com.ptidss.forecast.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 预测任务（DDL 6.1 forecast_task；FR-TR-01~03 负荷/电价/新能源预测 P0）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("forecast_task")
public class ForecastTask extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 任务编号 */
    private String taskNo;

    /** 模型编码：generation/price/load */
    private String modelCode;

    /** 预测日 */
    private Date predictDate;

    /** 特征版本 */
    private String inputVersion;

    /** 预测目标区域（评审决议⑤） */
    private String regionCode;

    /** 状态：queued/running/success/failed/degraded */
    private String status;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 错误信息 */
    private String errorMsg;

    /** 降级原因 */
    private String degradeReason;
}
