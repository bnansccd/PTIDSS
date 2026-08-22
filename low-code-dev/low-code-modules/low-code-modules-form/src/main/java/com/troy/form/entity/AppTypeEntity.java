package com.troy.form.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_form_app_type")
public class AppTypeEntity extends TBaseEntity {

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
     * sql信息
     */
    private String icon;

    /**
     * 驱动
     */
    private String sort;

}
