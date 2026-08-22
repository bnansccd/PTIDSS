package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnConvertEntity  {

    /**
     * 支持多种
     */
    private Long relationId;

    /**
     * 表达式
     */
    private String expression;

    /**
     * 时间格式化
     */
    private String format;

    /**
     * 配置静态属性
     */
    private String json;

    /**
     * 字典ID
     */
    private Long dictId;

    /**
     * sqlId
     */
    private Long sqlId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 页面编码
     */
    private String viewCode;

    /**
     * 页面Id
     */
    private Long viewCodeId;

    /**
     * 储存ID
     */
    private Long storeId;

    /**
     * 展示Id
     */
    private Long showId;

}
