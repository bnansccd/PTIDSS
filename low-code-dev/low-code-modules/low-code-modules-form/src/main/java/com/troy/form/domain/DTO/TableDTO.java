package com.troy.form.domain.DTO;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chenxl
 * @date 2023/11/2
 */
@Data
public class TableDTO {

    @NotBlank(message = "表名不能为空！")
    private String tableName;

    @NotBlank(message = "表描述不能为空！")
    private String tableComment;

    @NotBlank(message = "数据库ID")
    private Long dbId;

    private Long sort;

    @NotNull(message = "表字段不能为空")
    @Valid
    private List<TableColumnDTO> list;

}
