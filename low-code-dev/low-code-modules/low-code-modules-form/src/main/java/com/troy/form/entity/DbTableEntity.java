package com.troy.form.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * @author chenxl
 * @date 2023/10/18
 */
@Data
@Table(value = "t_form_db_table")
public class DbTableEntity extends TBaseEntity {

    @Column(value = "table_name")
    private String tableName;

    @Column(value = "table_comment")
    private String tableComment;

    @Column(value = "is_created")
    private String isCreated;

    @Column(value = "db_id")
    private Long dbId;

    @Column(value = "update_status")
    private String updateStatus;
}
