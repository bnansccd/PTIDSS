package com.troy.job.dao.impl;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.job.dao.SysJobLogDao;
import com.troy.job.domain.DTO.SysJobLogSearchDTO;
import com.troy.job.entity.SysJobLogEntity;
import com.troy.job.mapper.SysJobLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:33
 * @Description: 调度任务日志信息 dao实现层
 * @Version: 1.0.0
 */
@Component
public class SysJobLogDaoImpl extends BaseServiceImpl<SysJobLogMapper, SysJobLogEntity> implements SysJobLogDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysJobLogDaoImpl.class);

    @Override
    public Page<SysJobLogEntity> selectJobLogPage(SysJobLogSearchDTO dto) {
        return super.page(new Page<>(dto.getCurrent(),dto.getSize()),
                this.getQueryWrapper(dto)
        );
    }

    @Override
    public List<SysJobLogEntity> selectJobLogList(SysJobLogSearchDTO dto) {
        return super.list(this.getQueryWrapper(dto));
    }

    @Override
    public void cleanJobLog() {
        super.mapper.cleanJobLog();
    }

    /**
     * 组装查询条件
     *
     * @param dto
     * @return
     */
    private QueryWrapper getQueryWrapper(SysJobLogSearchDTO dto) {
        return QueryWrapper.create()
                .where(SysJobLogEntity::getJobName).like(dto.getJobName(),StringUtils.isNotBlank(dto.getJobName()))
                .and(SysJobLogEntity::getJobGroup).like(dto.getJobGroup(),StringUtils.isNotBlank(dto.getJobGroup()))
                .and(SysJobLogEntity::getStatus).eq(dto.getStatus(),StringUtils.isNotBlank(dto.getStatus()))
                .and(SysJobLogEntity::getInvokeTarget).like(dto.getInvokeTarget(),StringUtils.isNotBlank(dto.getInvokeTarget()))
                .and(SysJobLogEntity::getCreateTime).ge(dto.getStartTime(),StringUtils.isNull(dto.getStartTime()))
                .and(SysJobLogEntity::getCreateTime).le(dto.getEndTime(),StringUtils.isNull(dto.getEndTime()));
    }
}
