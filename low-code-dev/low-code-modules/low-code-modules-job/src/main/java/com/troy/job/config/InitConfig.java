package com.troy.job.config;

import com.troy.common.core.exception.job.TaskException;
import com.troy.common.core.utils.StringUtils;
import com.troy.job.dao.SysJobDao;
import com.troy.job.domain.VO.SysJobVO;
import com.troy.job.entity.SysJobEntity;
import com.troy.job.util.ScheduleUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxl
 * @description
 * @date 2024-06-18 15:10
 */
@Component
public class InitConfig {

    @Autowired
    private SysJobDao sysJobDao;

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    void init() throws SchedulerException, TaskException {
        List<SysJobEntity> list = sysJobDao.list();
        if (StringUtils.isNotEmpty(list)){
            List<SysJobVO> collect = list.stream().map(e -> {
                SysJobVO vo = new SysJobVO();
                BeanUtils.copyProperties(e, vo);
                return vo;
            }).collect(Collectors.toList());

            for (SysJobVO sysJobVO : collect) {
                ScheduleUtils.createScheduleJob(scheduler, sysJobVO);
            }
        }
    }

}
