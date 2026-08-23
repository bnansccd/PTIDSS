package com.troy.form.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author chenxl
 * @date 2023/10/18
 */
@Data
public class DbTableSearchDTO extends PageDTO {

    private String name;

    private String engine;

    private String comment;

    @NotNull(message = "请选择数据源")
    private Long datasourceId;

}
