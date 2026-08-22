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
 * 数据源（DDL 11.1 data_source；FR-PD-04 数据底座 P0，exchange 双通道建模评审决议①）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_source")
public class DataSource extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 数据源编码 */
    private String sourceCode;

    /** 类型：marketing/exchange/weather/file/intel */
    private String sourceType;

    /** 连接配置（脱敏；REST/SFTP 双通道） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String connectConfig;

    /** 对接方式：api/jwt/oauth2/basic/file/poll（V2.2 可配置，适配不同数据源认证形态） */
    private String connType;

    /** 同步模式：realtime/timed */
    private String syncMode;

    /** cron 表达式 */
    private String frequency;

    /** 状态：enabled/disabled/error */
    private String status;
}
