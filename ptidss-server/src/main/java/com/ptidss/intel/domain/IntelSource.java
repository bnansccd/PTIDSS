package com.ptidss.intel.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 情报源（DDL 11.5 intel_source；FR-INT-04 情报中心，60+ 源统一台账，RE-01）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intel_source")
public class IntelSource extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 情报源编码 */
    private String sourceCode;

    /** 情报源名称 */
    private String sourceName;

    /** 类型：price/weather/supply_demand/policy/announcement/opinion */
    private String intelType;

    /** 采集方式：api/crawl/file */
    private String fetchMode;

    /** 对接方式：api/jwt/oauth2/basic/file/poll（V2.2 可配置，适配不同信息源认证形态） */
    private String connType;

    /** 连接参数（端点/令牌/密钥引用等，脱敏；JSONB） */
    @TableField(typeHandler = com.ptidss.common.config.JsonStringTypeHandler.class)
    private String connConfig;

    /** 采集频率描述 */
    private String frequency;

    /** 状态：enabled/disabled */
    private String status;

    /** V2.5 最近成功采集时间（行情接口状态监测） */
    private Date lastSuccessAt;

    /** V2.5 最近失败原因（重试/降级均失败后留痕） */
    private String lastError;

    /** V2.5 连续失败次数（≥10 自动置 disabled 并告警） */
    private Integer consecutiveFailures;
}
