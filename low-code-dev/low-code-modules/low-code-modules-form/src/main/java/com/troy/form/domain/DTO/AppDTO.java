package com.troy.form.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
public class AppDTO {

    /**
     * 应用名称

     */
    @NotBlank(message = "应用名称不能为空")
    private String name;

    /**
     * 编码
     */
    @NotBlank(message = "应用编码不能为空")
    private String code;

    /**
     * 描述
     */

    private String desc;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 类型ID
     */
    private Long typeId;

    /**
     * 图标
     */
    private String icon;

    /**
     * 颜色
     */
    private String color;

    /**
     * 是否公共
     */
    private String common;

}
