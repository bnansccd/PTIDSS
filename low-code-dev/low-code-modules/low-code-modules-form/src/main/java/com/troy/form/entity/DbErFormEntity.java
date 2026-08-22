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
 * @since 2023-11-09 09:45:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_form_db_er_form")
public class DbErFormEntity extends TBaseEntity {

    /**
     * 表单
     */
    private String name;

    /**
     * 标识
     */
    private String mark;

    /**
     * 版本
     */
    private String edition;

    /**
     * er关系模型Id
     */
    private Long erId;

    /**
     * 排序类型
     */
    private Integer sort;

    /**
     * 应用ID
     */
    private Long appId;


    private String isLock;


    private Long userId;

}
