package com.troy.form.domain.DTO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
public class AppTypeDTO {

    /**
     * sql编码
     */
    private String code;

    /**
     * sql名称
     */
    private String name;

    /**
     * sql信息
     */
    private String sql;

    /**
     * sql信息key（key）
     */
    private String key;

    /**
     * text（value）
     */
    private String text;

    /**
     * 数据源

     */
    private Long dbId;

    /**
     * 驱动
     */
    private String sort;

}
