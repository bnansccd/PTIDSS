package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringListTypeHandler;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统配置项（DDL 17 sys_config，系统管理--系统配置）
 * 对标 PRD："申报段数、限价参数可配置""规则参数化配置，快速适配各省规则变化"
 * 评审决议（通道/周期口径/多省模式）与等保三级安全参数全部参数化；
 * 敏感项（is_sensitive）value 以 {"secret":"enc:..."} 加密落库，列表脱敏 ******。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_config", autoResultMap = true)
public class SysConfig extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 配置键（业务代码读取标识，如 rule.declareSegments） */
    private String configKey;

    /** 中文名 */
    private String configName;

    /** 说明 */
    private String description;

    /** 分组：trade_rule/settlement/region/optimize/forecast/model/agent/security/notification */
    private String configGroup;

    /** 类型：string/number/boolean/select/json */
    private String configType;

    /** select 类型枚举候选（JSONB 数组） */
    @TableField(typeHandler = JsonStringListTypeHandler.class)
    private List<String> enumValues;

    /** 当前值（按类型：字符串/数值/布尔/枚举值/JSON 对象；敏感项加密存储） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String value;

    /** 敏感项：加密存储 + 列表脱敏 ****** */
    private Boolean isSensitive;

    /** 系统内置：禁止删除（PRD 基线项，可改值可禁用） */
    private Boolean isBuiltin;

    /** 状态：enabled/disabled */
    private String status;

    /** 组内排序 */
    private Integer sortOrder;
}
