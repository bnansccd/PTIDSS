package com.ptidss.trade.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 交易网关配置（DDL 12.1 trade_gateway_config；按区域隔离，region_code 唯一）
 * V2.4：申报单 → 交易系统接口配置与状态监测；敏感字段（appKey/appSecret）AES 加密存储，
 * 对外仅脱敏展示（ConfigCryptoService.maskFields）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("trade_gateway_config")
public class TradeGatewayConfig extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 申报市场所属区域（评审决议⑤ 多省隔离） */
    private String regionCode;

    private String gatewayName;

    /** 接口地址（如 https://trade.center/api/declaration） */
    private String endpoint;

    /** {appKey, appSecret,...} 敏感字段加密存储 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String connConfig;

    /** 状态：enabled/disabled */
    private String status;

    /** 最近连通性测试时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastTestAt;

    /** 最近测试结果（ok/fail + 延迟） */
    private String lastTestResult;
}
