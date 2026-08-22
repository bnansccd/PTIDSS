package com.troy.form.module.entity.sql;

import lombok.Data;

/**
 * @author chenxl
 * @date 2023/11/10
 */
@Data
public class RowEntity {

    private String name;

    /**
     * 字符 String 数字 Long 小数 BigDecimal  日期Date
     */
    private String type;
}
