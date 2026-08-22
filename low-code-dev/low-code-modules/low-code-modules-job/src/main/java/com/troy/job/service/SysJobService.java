package com.troy.job.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.exception.job.TaskException;
import com.troy.common.core.web.VO.PageVO;
import com.troy.job.domain.DTO.SysJobDTO;
import com.troy.job.domain.DTO.SysJobSearchDTO;
import com.troy.job.domain.VO.SysJobVO;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 13:13:18
 * @Description: 调度任务信息 service层
 * @Version: 1.0.0
 */
public interface SysJobService {

    /**
     * 查询定时任务分页列表
     *
     * @param dto
     * @return
     */
    public PageVO<SysJobVO> selectJobListPage(SysJobSearchDTO dto);

    /**
     * 查询所有定时任务
     *
     * @param dto
     * @return
     */
    List<SysJobVO> selectJobList(SysJobSearchDTO dto);

    /**
     * 根据id得到定时任务详情
     *
     * @param id
     * @return
     */
    SysJobVO selectJobById(Long id);

    /**
     * 新增定时任务
     *
     * @param dto
     * @return
     */
    ResultVO insertJob(SysJobDTO dto) throws SchedulerException, TaskException;

    /**
     * 修改定时任务
     *
     * @param id
     * @param dto
     * @return
     */
    ResultVO updateJob(Long id, SysJobDTO dto) throws SchedulerException, TaskException;

    /**
     * 定时任务状态修改
     *
     * @param id
     * @return
     */
    ResultVO changeStatus(Long id) throws SchedulerException;

    /**
     * 定时任务立即执行一次
     *
     * @param id
     */
    void run(Long id) throws SchedulerException;

    /**
     * 删除定时任务
     *
     * @param ids
     */
    void deleteJobByIds(List<Long> ids) throws SchedulerException;
}
