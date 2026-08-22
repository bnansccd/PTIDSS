package com.troy.form.domain.DTO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
public class AppTypeSearchDTO {

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
    private String desc;

    /**
     * 驱动
     */
    private String sort;

}
