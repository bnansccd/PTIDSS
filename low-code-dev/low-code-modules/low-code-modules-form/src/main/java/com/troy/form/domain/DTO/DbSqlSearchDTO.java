package com.troy.form.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
public class DbSqlSearchDTO extends PageDTO {

    /**
     * 名称
     */
    private String name;


    /**
     * 编码
     */
    private String code;

    /**
     * key
     */
    private String key;

    /**
     * text
     */
    private String text;

}
