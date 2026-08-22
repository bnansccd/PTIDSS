package com.troy.form.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_form_page_action")
public class PageActionEntity extends TBaseEntity {

    /**
     * 1 编辑 2流程详情 3详情 4 查看子表 5自定义
     */
    private String operationType;

    /**
     * 1默认 2 全屏 3大 4中 5小
     */
    private String openType;

    /**
     * 表单ID
     */
    private Long formId;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 出入参数配置
     */
    private String importExportParam;

    /**
     * 前处理脚本
     */
    private String beforeScript;

    /**
     * 后处理脚本
     */
    private String afterScript;

    /**
     * 自定义脚本
     */
    private String customScript;

}
