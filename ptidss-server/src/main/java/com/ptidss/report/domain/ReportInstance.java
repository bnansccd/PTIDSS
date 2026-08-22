package com.ptidss.report.domain;

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
 * 报表实例（DDL 9.2 report_instance；FR-DM-02 自动生成/导出，数据快照口径可追溯，报表按省隔离）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_instance")
public class ReportInstance extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 模板 ID */
    private Long templateId;

    /** 报表周期（如 2026-08） */
    private String period;

    /** 区域编码（报表按省隔离，评审决议⑤） */
    private String regionCode;

    /** 生成时数据快照（口径可追溯） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String dataSnapshot;

    /** 导出文件地址 */
    private String fileUrl;

    /** 生成状态：pending/generating/success/failed */
    private String generateStatus;

    /** 推送状态：none/pushed */
    private String pushStatus;

    /** 生成时间 */
    private Date generatedAt;
}
