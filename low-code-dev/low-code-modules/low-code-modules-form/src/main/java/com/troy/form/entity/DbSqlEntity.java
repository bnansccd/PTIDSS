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
@Table(value = "t_form_db_sql")
public class DbSqlEntity extends TBaseEntity {

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * sql编码
     */
    private String sql;

    /**
     * key
     */
    private String key;

    /**
     * text
     */
    private String text;

    /**
     * 数据源id
     */
    private Long dbId;

    /**
     * 排序号
     */
    private Integer sort;

}
