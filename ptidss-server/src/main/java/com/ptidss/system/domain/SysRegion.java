package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringListTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 区域注册表（DDL 10.5 sys_region，多省配置化核心：评审决议⑤，全国推广演进）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_region", autoResultMap = true)
public class SysRegion extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 区域编码（如 CN-32 江苏） */
    private String regionCode;

    /** 区域名称 */
    private String regionName;

    /** 支持市场类型（JSONB：spot/midlong/external） */
    @TableField(typeHandler = JsonStringListTypeHandler.class)
    private List<String> marketSupport;

    /** 交易中心通道：rest/sftp/both（评审决议①） */
    private String exchangeChannel;

    /** 结算周期口径：natural_month/trading_month（评审决议③） */
    private String settlementPeriod;

    /** 状态：enabled/disabled/pending */
    private String status;

    /** 全国推广接入顺序 */
    private Integer launchOrder;
}
