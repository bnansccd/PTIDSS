package com.troy.form.module.entity.form;

import lombok.Data;

import java.math.BigDecimal;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitNumberEntity  {

    /**
     * 步长

     */
    private BigDecimal step;

    /**
     * 最低高度
     */
    private Integer highMin;

    /**
     * 最大高度
     */
    private Integer highMax;

}
