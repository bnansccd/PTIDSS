package com.troy.form.domain.VO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Data
public class DbSqlVO {

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


    private String dbName;

    /**
     * 排序号
     */
    private Integer sort;

}
