package com.troy.job.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.core.constant.ScheduleConstants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.entity.BaseEntity;
import com.troy.job.util.CronUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:08
 * @Description: 定时任务调度表 sys_job
 * @Version: 1.0.0
 */
@Getter
@Setter
@Table("sys_job")
public class SysJobEntity extends BaseEntity {

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
     * cron执行表达式
     */
    @Column("cron_expression")
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    @Column("misfire_policy")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Column("concurrent")
    private String concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @Column("status")
    private String status;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime() {
        if (StringUtils.isNotEmpty(cronExpression)) {
            return CronUtils.getNextExecution(cronExpression);
        }
        return null;
    }
}
