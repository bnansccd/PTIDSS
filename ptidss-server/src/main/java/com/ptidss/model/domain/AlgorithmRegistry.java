package com.ptidss.model.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.config.JsonStringTypeHandler;
import lombok.Data;

import java.util.Date;

/**
 * 算法注册表（DDL 10.3 algorithm_registry；V2.2 产品化：专业算法注册/替换，决策过程按类目匹配）
 * 说明：version 为算法版本（VARCHAR 业务版本，非乐观锁），故不继承 BaseEntity（同 AgentRegistry 约定）
 */
@Data
@TableName("algorithm_registry")
public class AlgorithmRegistry {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 算法编码（LSTM-PRICE-96 等） */
    private String algCode;

    /** 算法名称 */
    private String algName;

    /** 类目：forecast/market_analysis/quote_strategy/risk_measure/optimize/settlement/review/rule_engine */
    private String category;

    /** 算法说明 */
    private String description;

    /** 参数模板（客户可调） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String paramsSchema;

    /** 算法版本（业务版本，如 v1.2.0） */
    private String version;

    /** SPI 执行器标识（P3 插件化执行：缺省按类目匹配内置执行器） */
    private String spiKey;

    /** 状态：enabled/disabled */
    private String status;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
