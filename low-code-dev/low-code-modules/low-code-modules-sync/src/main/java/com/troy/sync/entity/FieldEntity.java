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
@Table("t_sync_data_table_field")
public class FieldEntity extends BaseEntity {

    @Column("table_id")
    private Long tableId;

    @Column("field_name")
    private String fieldName;

    @Column("type")
    private String type;

    @Column("id_key")
    private Boolean idKey;

    @Column("date_key")
    private Boolean dateKey;

    @Column("lessee_key")
    private Boolean lesseeKey;

    @Column("data_flag")
    private Boolean dataFlag;

    /**
     * 1 idUtil, 2 字段映射
     */
    @Column("id_formation_strategy")
    private String idFormationStrategy;

    @Column("id_key_from")
    private String idKeyFrom;

    @Column("field_name_default")
    private String fieldNameDefault;

    @Column("alias_name")
    private String aliasName;

    @Column("data_del_flag")
    private Boolean dataDelFlag;

    @Column("data_del_flag_default")
    private String dataDelFlagDefault;

}
