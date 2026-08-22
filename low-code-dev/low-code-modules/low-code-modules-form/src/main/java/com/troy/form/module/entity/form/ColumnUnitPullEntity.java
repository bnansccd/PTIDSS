package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitPullEntity  {

    /**
     * 静态选项
     */
    private String json;

    /**
     * sql_id
     */
    private Long sqlId;

    /**
     * 字典ID
     */
    private Long dictId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 是否可清除
     */
    private String isClear;

    /**
     * 是否可多选
     */
    private String isMultiple;

    /**
     * 是否可搜索
     */
    private String isSearch;

    /**
     * 表达式
     */
    private String expression;

}
