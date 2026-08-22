package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitRaidoEntity  {


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
     * 表达式
     */
    private String expression;

}
