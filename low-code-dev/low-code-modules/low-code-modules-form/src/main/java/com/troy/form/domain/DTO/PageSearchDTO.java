package com.troy.form.domain.DTO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Data
public class PageSearchDTO {

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
