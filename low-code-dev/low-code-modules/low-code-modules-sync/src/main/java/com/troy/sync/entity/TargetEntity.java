package com.troy.sync.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @Author: chenxl
 * @Date: 2023-05-17
 * @Description: 数据源表
 * @Version: 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@Table("t_sync_target")
public class TargetEntity extends BaseEntity {

    private String target;

    private String expression;

    private Integer times;

    private Integer type;

    private Boolean isRpc;

}
