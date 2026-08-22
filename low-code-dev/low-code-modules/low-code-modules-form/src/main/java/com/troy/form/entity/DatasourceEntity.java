package com.troy.form.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
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
@Table(value = "t_form_db")
public class DatasourceEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(value = "identification")
    private String identification;

    @Column(value = "name")
    private String name;

    @Column(value = "type")
    private String type;

    @Column(value = "url")
    private String url;

    @Column(value = "username")
    private String username;

    @Column(value = "password")
    private String password;

    @Column(value = "driver")
    private String driver;

}
