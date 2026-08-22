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
@Table(value = "t_form_page_search")
public class PageSearchEntity extends TBaseEntity {

    /**
     * 列ID
     */
    private Long columnId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 查询操作符
     */
    private String queryOperators;

    /**
     * 默认查询值
     */
    private String defaultValue;

    /**
     * 显示类型
     */
    private String type;

    /**
     * 默认展开
     */
    private String isExpand;

    /**
     * 定制排序
     */
    private String customSort;

    /**
     * 自定义sql
     */
    private String customSql;

}
