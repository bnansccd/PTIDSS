package com.troy.form.module.entity.form;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitCascadeEntity {

    private Long columnFormId;

    private String multiple;

    private String viewCode;

    private Long viewCodeId;

    private Long storeId;

    private Long showId;

}
