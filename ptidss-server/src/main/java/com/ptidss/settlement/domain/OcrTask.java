package com.ptidss.settlement.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算单 OCR 识别任务（DDL 4.5 ocr_task；低置信进入人工复核 FR-DM-03）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ocr_task")
public class OcrTask extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** MinIO 文件 ID */
    private String fileId;

    /** 识别模板 */
    private Long templateId;

    /** 状态：pending/recognizing/success/low_confidence/failed */
    private String status;

    /** 置信度 0-1 */
    private BigDecimal confidence;

    /** 抽取字段：电量/电价/费用/考核科目 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String fields;

    /** 复核状态：not_required/pending/reviewed */
    private String reviewStatus;

    /** 复核人 */
    private String reviewer;

    /** 复核时间 */
    private Date reviewedAt;
}
