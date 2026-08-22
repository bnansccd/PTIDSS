package com.troy.form.domain.DTO;

import lombok.Data;

/**
 * @author chenxl
 * @date 2023/11/10
 */
@Data
public class ColumnDTO {

    private Long columnId;

    private String validated;

    private String required;

    private String columnName;
}
