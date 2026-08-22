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

    /** 节点类型：table/task/report/model */
    private String nodeType;

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
