package com.ptidss.data.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 采集任务（DDL 11.2 collect_task；FR-PD-04 数据底座 P0）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("collect_task")
public class CollectTask extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 数据源 ID */
    private Long sourceId;

    /** 任务类型：market/trade/settlement/weather/intel */
    private String taskType;

    /** cron 表达式 */
    private String cronExpr;

    /** 最近运行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastRunTime;

    /** 最近状态：success/failed */
    private String lastStatus;

    /** 最近采集记录数 */
    private Long recordsCount;

    /** 错误日志 */
    private String errorLog;
}
