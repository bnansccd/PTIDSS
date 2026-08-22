package com.troy.form.domain.DTO;


import com.troy.common.core.web.DTO.PageDTO;
import lombok.Data;

/**
 * <p>
 * 公司产品表
 * </p>
 *
 * @author chenxl
 * @since 2023-03-14
 */
@Data
public class DatasourceSearchDTO extends PageDTO {

    private String identification;

    private String name;

}
