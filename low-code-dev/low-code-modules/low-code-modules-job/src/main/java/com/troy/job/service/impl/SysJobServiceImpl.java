package com.troy.job.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.constant.ScheduleConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.exception.job.TaskException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.job.util.ScheduleUtils;
import com.troy.job.dao.SysJobDao;
import com.troy.job.domain.DTO.SysJobDTO;
import com.troy.job.domain.DTO.SysJobSearchDTO;
import com.troy.job.domain.VO.SysJobVO;
import com.troy.job.entity.SysJobEntity;
import com.troy.job.service.SysJobService;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 13:13:18
 * @Description: 调度任务信息 service 实现层
 * @Version: 1.0.0
 */
@Service
public class SysJobServiceImpl implements SysJobService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SysJobServiceImpl.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SysJobDao sysJobDao;

    @Override
    public PageVO<SysJobVO> selectJobListPage(SysJobSearchDTO dto) {
        Page<SysJobEntity> page = this.sysJobDao.selectJobListPage(dto);
        return PageUtils.convertPageVo(page, SysJobVO.class);
    }

    @Override
    public List<SysJobVO> selectJobList(SysJobSearchDTO dto) {
        List<SysJobEntity> sysJobEntities = this.sysJobDao.selectJobList(dto);
        List<SysJobVO> vos = new ArrayList<>();
        if (StringUtils.isNotEmpty(sysJobEntities)) {
            SysJobVO vo = null;
            for (SysJobEntity sysJobEntity : sysJobEntities) {
                vo = new SysJobVO();
                BeanUtils.copyProperties(sysJobEntity, vo);
                vos.add(vo);
            }
        }
        return vos;
    }

    @Override
    public SysJobVO selectJobById(Long id) {
        SysJobVO vo = null;
        SysJobEntity sysJobEntity = this.sysJobDao.getById(id);
        if (StringUtils.isNotNull(sysJobEntity)) {
            vo = new SysJobVO();
            BeanUtils.copyProperties(sysJobEntity, vo);
        }
        return vo;
    }

    @Transactional
    @Override
    public ResultVO insertJob(SysJobDTO dto) throws SchedulerException, TaskException {
        SysJobEntity sysJobEntity = new SysJobEntity();
        BeanUtils.copyProperties(dto, sysJobEntity);
        if (this.sysJobDao.save(sysJobEntity)) {
            SysJobVO vo = new SysJobVO();
            BeanUtils.copyProperties(sysJobEntity, vo);
            ScheduleUtils.createScheduleJob(scheduler, vo);
        }
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO updateJob(Long id, SysJobDTO dto) throws SchedulerException, TaskException {
        SysJobEntity sysJobEntity = this.validSysJobExit(id);
        BeanUtils.copyProperties(dto, sysJobEntity);
        if (this.sysJobDao.updateById(sysJobEntity)) {
            updateSchedulerJob(sysJobEntity, sysJobEntity.getJobGroup());
        }
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO changeStatus(Long id) throws SchedulerException {
        SysJobEntity sysJobEntity = this.validSysJobExit(id);
        if (StringUtils.equals(ScheduleConstants.Status.PAUSE.getValue(), sysJobEntity.getStatus())) {
            sysJobEntity.setStatus(ScheduleConstants.Status.NORMAL.getValue());
            if (this.sysJobDao.updateById(sysJobEntity)) {
                scheduler.resumeJob(ScheduleUtils.getJobKey(id, sysJobEntity.getJobGroup()));
            }
        } else {
            sysJobEntity.setStatus(ScheduleConstants.Status.PAUSE.getValue());
            if (this.sysJobDao.updateById(sysJobEntity)) {
                scheduler.pauseJob(ScheduleUtils.getJobKey(id, sysJobEntity.getJobGroup()));
            }
        }
        return ResultVO.success();
    }

    @Override
    public void run(Long id) throws SchedulerException {
        SysJobEntity sysJobEntity = this.validSysJobExit(id);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, sysJobEntity);
        scheduler.triggerJob(ScheduleUtils.getJobKey(sysJobEntity.getId(), sysJobEntity.getJobGroup()), dataMap);
    }

    @Transactional
    @Override
    public void deleteJobByIds(List<Long> ids) throws SchedulerException {
        List<SysJobEntity> sysJobEntities = this.sysJobDao.listByIds(ids);
        if (StringUtils.isNotEmpty(sysJobEntities)) {
            for (SysJobEntity sysJobEntity : sysJobEntities) {
                if (this.sysJobDao.removeById(sysJobEntity)) {
                    scheduler.deleteJob(ScheduleUtils.getJobKey(sysJobEntity.getId(), sysJobEntity.getJobGroup()));
                }
            }
        }
    }

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJobEntity job, String jobGroup) throws SchedulerException, TaskException {
        Long jobId = job.getId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        SysJobVO sysJobVO = new SysJobVO();
        BeanUtils.copyProperties(job, sysJobVO);
        ScheduleUtils.createScheduleJob(scheduler, sysJobVO);
    }

    /**
     * 验证定时任务是否存在
     *
     * @param id
     * @return
     */
    private SysJobEntity validSysJobExit(Long id) {
        SysJobEntity sysJobEntity = this.sysJobDao.getById(id);
        if (StringUtils.isNull(sysJobEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.QUARTZ_JOB));
        }
        return sysJobEntity;
    }
}
