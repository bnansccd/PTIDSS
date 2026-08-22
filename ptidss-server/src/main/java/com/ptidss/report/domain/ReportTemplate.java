package com.ptidss.report.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表模板（DDL 9.1 report_template；FR-DM-02 报表自动生成，模板在线配置/报送格式）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_template")
public class ReportTemplate extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 模板编码 */
    private String code;

    /** 模板名称 */
    private String name;

    /** 类型：trade/settlement/forecast/assessment/business */
    private String type;

    /** 周期：daily/weekly/monthly/yearly */
    private String periodType;

    /** 数据源/指标定义 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String datasourceConfig;

    /** 布局定义 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String layout;

    /** 表头/口径说明（报送格式） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String headerConfig;

    /** 状态：draft/active/disabled */
    private String status;
}
