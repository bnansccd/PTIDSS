package com.troy.job.util;

import com.troy.common.core.constant.ScheduleConstants;
import com.troy.common.core.utils.ExceptionUtil;
import com.troy.common.core.utils.SpringUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.job.domain.DTO.SysJobLogDTO;
import com.troy.job.domain.VO.SysJobVO;
import com.troy.job.service.SysJobLogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 15:15:36
 * @Description: 抽象quartz调用
 * @Version: 1.0.0
 */
public abstract class AbstractQuartzJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysJobVO vo = new SysJobVO();
        BeanUtils.copyBeanProp(vo, context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES));
        try {
            before(context, vo);
            if (vo != null) {
                doExecute(context, vo);
            }
            after(context, vo, null);
        } catch (Exception e) {
            LOGGER.error("任务执行异常  - ：" , e);
            after(context, vo, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysJobVO vo) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void after(JobExecutionContext context, SysJobVO vo, Exception e) {
        Date startTime = threadLocal.get();
        threadLocal.remove();

        final SysJobLogDTO dto = new SysJobLogDTO();
        dto.setJobName(vo.getJobName());
        dto.setJobGroup(vo.getJobGroup());
        dto.setInvokeTarget(vo.getInvokeTarget());
        Date stopTime = new Date();
        long runMs = stopTime.getTime() - stopTime.getTime();
        dto.setJobMessage(dto.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null) {
            dto.setStatus("1");
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            dto.setExceptionInfo(errorMsg);
        } else {
            dto.setStatus("0");
        }

        // 写入数据库当中
        SpringUtils.getBean(SysJobLogService.class).addJobLog(dto);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJobVO vo) throws Exception;
}
