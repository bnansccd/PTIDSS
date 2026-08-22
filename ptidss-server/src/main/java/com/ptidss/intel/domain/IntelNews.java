package com.ptidss.intel.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 情报条目（DDL 11.6 intel_news；FR-INT-04，归一化标签/重要度分级，high 级实时推送 ≤30s）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intel_news")
public class IntelNews extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 情报源编码（应用层校验，DDL v1.0.2 条目 8） */
    private String sourceCode;

    /** 标题 */
    private String title;

    /** 正文 */
    private String content;

    /** 关联区域（全国情报可空，评审决议⑤） */
    private String regionCode;

    /** 归一化标签：市场/品种/影响 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String normalizedTags;

    /** 重要度：high/medium/low */
    private String importance;

    /** 发布时间 */
    private Date publishedAt;

    /** 推送状态：none/pushed */
    private String pushStatus;
}
