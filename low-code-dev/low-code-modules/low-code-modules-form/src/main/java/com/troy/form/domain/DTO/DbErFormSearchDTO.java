package com.troy.form.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-09 09:45:50
 */
@Data
public class DbErFormSearchDTO extends PageDTO {

    /**
     * 表单
     */
    private String name;

}
