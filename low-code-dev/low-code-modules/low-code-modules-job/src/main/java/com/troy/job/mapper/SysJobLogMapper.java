package com.troy.job.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.job.entity.SysJobLogEntity;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:27
 * @Description: 调度任务日志信息 数据层
 * @Version: 1.0.0
 */
public interface SysJobLogMapper extends MyBaseMapper<SysJobLogEntity> {

    /**
     * 清空定时任务日志
     */
    void cleanJobLog();
}
