package com.troy.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 时间段返回DTO
 * @Author: zhuQing
 * @Date: 2026/1/8 15:07
 * @Version: 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimePeriodVO implements Serializable {

    /** 时间段名称（如：2025年9月第一周） */
    private String periodName;
    /** 时间段开始时间（如：2025-09-01 00:00:00） */
    private Date startTime;
    /** 时间段结束时间（如：2025-09-07 23:59:59） */
    private Date endTime;
}
