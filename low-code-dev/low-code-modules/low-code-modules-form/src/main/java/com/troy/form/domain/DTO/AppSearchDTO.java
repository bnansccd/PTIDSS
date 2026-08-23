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
public class AppSearchDTO {

    /**
     * 应用名称

     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 类型ID
     */
    private Long typeId;

}
