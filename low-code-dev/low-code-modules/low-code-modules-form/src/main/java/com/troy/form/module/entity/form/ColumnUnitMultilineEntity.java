package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitMultilineEntity  {

    /**
     * 是否展示
     */
    private String isDisplay;

    /**
     * 最低高度
     */
    private Integer highMin;

    /**
     * 最大高度
     */
    private Integer highMax;

}
