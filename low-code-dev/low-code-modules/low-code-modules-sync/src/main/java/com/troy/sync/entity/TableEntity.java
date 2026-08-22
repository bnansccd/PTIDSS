package com.troy.sync.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @Author: chenxl
 * @Date: 2023-05-17
 * @Description: 铁路线表
 * @Version: 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@Table("t_sync_data_table")
public class TableEntity extends BaseEntity {

    @Column("source_id")
    private Long sourceId;

    @Column("table_name")
    private String tableName;

}
