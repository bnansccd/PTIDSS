package com.troy.common.core.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2024/12/18 13:49
 * @Version: 1.0
 **/
@Data
@ApiModel(description = "word报表封装")
public class ChartVO implements Serializable {

    /**
     * y轴值
     */
    private List<Number[]> barValues = new ArrayList<>();
    /**
     * x轴值
     */
    private String[] xValues;
    /**
     * 复核统计维度:即有柱状图也有折线图
     */
    private String[][] seriesNameMore;
    /**
     * 统计维度
     */
    private String[] seriesName;
    /**
     * 图表名称
     */
    private String chartTitle;
}
