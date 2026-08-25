package com.ptidss.data.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据血缘（DDL 11.4 data_lineage；FR-PD-05 数据血缘 P1）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_lineage")
public class DataLineage extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 节点 ID */
    private String nodeId;

    /** 节点类型：table/task/report/model/business（V3.0 扩展 business 业务应用节点） */
    private String nodeType;

    /** 节点中文名（V3.0 血缘全景） */
    private String nodeName;

    /** 中文说明（V3.0 血缘全景：该节点承载的业务/数据内容） */
    private String description;

    /** 业务域（marketing/exchange/weather/common/trade/settle/policy/intel/forecast/decision/optimize/report/assess/model/system） */
    private String domain;

    /** 数据分层（source/collect/detail/indicator/model/report/business，数据视角分层；business 为业务应用层） */
    private String layer;

    /** 上游节点 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String upstream;

    /** 下游节点 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String downstream;

    /** 字段级映射 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String fieldMapping;
}
