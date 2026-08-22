package com.ptidss.policy.domain;

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
 * 政策文件（DDL 5.1 policy_document；FR-PD-01 政策研判 P0：分类/标签/版本管理）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("policy_document")
public class PolicyDocument extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 政策标题 */
    private String title;

    /** 发布机构 */
    private String issuingBody;

    /** 分类：national/regional/provincial */
    private String category;

    /** 标签：现货/中长期/结算/考核/信息披露 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String tags;

    /** 同一政策多版本 */
    private Integer versionNo;

    /** MinIO/PDF 原文地址 */
    private String fileUrl;

    /** 发布日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date publishDate;

    /** 生效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date effectiveDate;

    /** 状态：draft/published/expired */
    private String status;
}
