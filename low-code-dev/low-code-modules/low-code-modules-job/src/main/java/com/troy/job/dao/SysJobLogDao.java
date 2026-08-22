package com.troy.job.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.job.domain.DTO.SysJobLogSearchDTO;
import com.troy.job.entity.SysJobLogEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:30
 * @Description: 调度任务日志信息 dao层
 * @Version: 1.0.0
 */
public interface SysJobLogDao extends BaseService<SysJobLogEntity> {

    /**
     * 分页查询订时任务日志
     *
     * @param dto
     * @return
     */
    Page<SysJobLogEntity> selectJobLogPage(SysJobLogSearchDTO dto);

    /**
     * 根据条件查询定时任务日志
     *
     * @param dto
     * @return
     */
    List<SysJobLogEntity> selectJobLogList(SysJobLogSearchDTO dto);

    /**
     * 清空定时任务日志
     */
    void cleanJobLog();
}
