package com.troy.form.domain.VO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
public class AppTypeVO {

    /**
     * 应用名称
     */

    private String name;

    /**
     * 编码
     */
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
     * 图标
     */
    private String icon;

    /**
     * 颜色
     */
    private String color;


}
