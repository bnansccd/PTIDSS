package com.troy.job.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;


/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:20
 * @Description: 定时任务调度日志表 sys_job_log
 * @Version: 1.0.0
 */
@Getter
@Setter
@Table("sys_job_log")
public class SysJobLogEntity extends BaseEntity {

    /**
     * 任务名称
     */
    @Column("job_name")
    private String jobName;

    /**
     * 任务组名
     */
    @Column("job_group")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @Column("invoke_target")
    private String invokeTarget;

    /**
     * 日志信息
     */
    @Column("job_message")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @Column("status")
    private String status;

    /**
     * 异常信息
     */
    @Column("exception_info")
    private String exceptionInfo;
}
