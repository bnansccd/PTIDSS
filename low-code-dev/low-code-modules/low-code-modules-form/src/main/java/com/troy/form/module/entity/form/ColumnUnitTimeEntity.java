package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitTimeEntity  {

    /**
     * 时间格式
     */
    private String format;

    /**
     * 是否带事件选择器
     */
    private Long isChooser;

    /**
     * 是否可清除
     */
    private String isClear;

}
