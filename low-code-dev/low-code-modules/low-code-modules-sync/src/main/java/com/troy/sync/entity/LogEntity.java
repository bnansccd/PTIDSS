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
@Table("t_sync_log")
public class LogEntity extends BaseEntity {

    @Column("log_info")
    private String logInfo;

    @Column("param")
    private String param;

    @Column("execution_time")
    private Long executionTime;

    @Column("is_success")
    private Boolean isSuccess;

}
