package com.troy.form.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 *  实体类。
 *
 * @author zhuqing
 * @since 2023-10-19 14:19:33
 */
@Data
@Table(value = "t_form_db_er")
public class DbErEntity extends TBaseEntity {

    /**
     * 模型名称
     */
    private String name;

    /**
     * er模型标识
     */
    private String erModelMark;

    /**
     * 模型标识类型， 1 单表 2一对一 3一对多
     */
    private String type;

}
