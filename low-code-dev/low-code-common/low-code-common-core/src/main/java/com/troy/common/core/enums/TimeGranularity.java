package com.troy.common.core.enums;

/**
 * @Description: 时间粒度枚举
 * @Author: zhuQing
 * @Date: 2026/1/8 15:06
 * @Version: 1.0
 **/
public enum TimeGranularity {

    DAY("天"),
    WEEK("周"),
    MONTH("月"),
    YEAR("年");

    private final String desc;

    TimeGranularity(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
