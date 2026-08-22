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
@Table(value = "t_form_page")
public class PageEntity extends TBaseEntity {

    /**
     * 页面名称
     */
    private String name;

    /**
     * 页面code
     */
    private String code;

    /**
     * 数据源ID
     */
    private Long dbId;

    /**
     * 1 列表页面 2属性页面 3移动页面 4 组合页面
     */
    private String type;

    /**
     * 应用ID
     */
    private Long appId;

}
