package com.troy.job.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.job.domain.DTO.SysJobSearchDTO;
import com.troy.job.entity.SysJobEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:29
 * @Description: 调度任务信息 dao层
 * @Version: 1.0.0
 */
public interface SysJobDao extends BaseService<SysJobEntity> {


    /**
     * 分页查询调度任务
     *
     * @param job
     * @return
     */
     Page<SysJobEntity> selectJobListPage(SysJobSearchDTO dto);

    /**
     * 根据条件查询所有定时任务列表
     *
     * @param dto
     * @return
     */
     List<SysJobEntity> selectJobList(SysJobSearchDTO dto);
}
