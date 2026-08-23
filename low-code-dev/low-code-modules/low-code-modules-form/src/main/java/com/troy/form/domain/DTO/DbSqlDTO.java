package com.troy.form.domain.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
public class DbSqlDTO {

    /**
     * 名称
     */
    @NotBlank(message = "name不能为空！")
    private String name;

    /**
     * 编码
     */
    @NotBlank(message = "code不能为空！")
    private String code;

    /**
     * sql编码
     */
    @NotBlank(message = "sql不能为空！")
    private String sql;

    /**
     * key
     */
    @NotBlank(message = "key不能为空！")
    private String key;

    /**
     * text
     */
    @NotBlank(message = "text不能为空！")
    private String text;

    /**
     * 数据源id
     */
    @NotBlank(message = "数据源不能为空！")
    private Long dbId;

    /**
     * 排序号
     */
    private Integer sort;

}
