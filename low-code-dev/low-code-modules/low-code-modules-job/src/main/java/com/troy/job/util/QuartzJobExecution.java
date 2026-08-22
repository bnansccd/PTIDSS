package com.troy.job.util;

import com.troy.job.domain.VO.SysJobVO;
import org.quartz.JobExecutionContext;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 15:15:58
 * @Description: 定时任务处理（允许并发执行）
 * @Version: 1.0.0
 */
public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SysJobVO sysJobVO) throws Exception {
        JobInvokeUtil.invokeMethod(sysJobVO);
    }
}
