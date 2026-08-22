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
@Table(value = "t_form_db_er_relation")
public class DbErRelationEntity extends TBaseEntity {

    /**
     * E-R模型ID
     */
    private Long erId;

    /**
     * 表ID
     */
    private Long tableId;

    /**
     * 列ID
     */
    private Long columnId;

    /**
     * 父级主键ID
     */
    private Long parentId;

    /**
     * 表ID （上一级）
     */
    private Long relationTableId;

    /**
     * 列ID （上一级）
     */
    private Long relationColumnId;

    /**
     * 1 单表 2一对一 3一对多
     */
    private String type;

}
