package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitPhotoEntity  {

    /**
     * 是否可以多选

     */
    private String isMultiple;

    /**
     * 最大数据量
     */
    private Integer maxNum;

    /**
     * 存储目录
     */
    private String storeUrl;

}
