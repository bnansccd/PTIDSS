package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitTreeEntity  {

    /**
     * 是否多选
     */
    private String isMultiple;

    /**
     * 页面编码
     */
    private String viewCode;

    /**
     * 页面ID
     */
    private Long viewCodeId;

    /**
     * 储存ID
     */
    private Long storeId;

    /**
     * 展示ID
     */
    private Long showId;

}
