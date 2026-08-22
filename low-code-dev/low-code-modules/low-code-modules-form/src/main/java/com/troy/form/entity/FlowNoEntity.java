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
 * @since 2023-11-09 17:01:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_form_flow_no")
public class FlowNoEntity extends TBaseEntity {

    /**
     * 生成规则名称
     */
    private String name;

    /**
     * 生成规则编码
     */
    private String code;

    /**
     * 生成规则实现类
     */
    private String clazz;

}
