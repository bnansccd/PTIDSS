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
@Table(value = "t_form_page_param")
public class PageParamEntity extends TBaseEntity {

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 入参标识
     */
    private String paramMark;

    /**
     * 入参描述
     */
    private String paramDesc;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 非空校验
     */
    private String isCheckNull;

    /**
     * 默认值
     */
    private String defaultValue;

}
