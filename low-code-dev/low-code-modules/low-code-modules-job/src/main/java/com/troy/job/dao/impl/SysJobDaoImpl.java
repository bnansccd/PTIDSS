package com.troy.job.dao.impl;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.job.dao.SysJobDao;
import com.troy.job.domain.DTO.SysJobSearchDTO;
import com.troy.job.entity.SysJobEntity;
import com.troy.job.mapper.SysJobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:31
 * @Description: 调度任务信息 dao实现层
 * @Version: 1.0.0
 */
@Component
public class SysJobDaoImpl extends BaseServiceImpl<SysJobMapper, SysJobEntity> implements SysJobDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysJobDaoImpl.class);


    @Override
    public Page<SysJobEntity> selectJobListPage(SysJobSearchDTO dto) {
       return super.page(
                new Page<>(dto.getCurrent(),dto.getSize()),
                this.getQueryWrapper(dto)

        );
    }


    @Override
    public List<SysJobEntity> selectJobList(SysJobSearchDTO dto) {
        return super.list(
                this.getQueryWrapper(dto)
        );
    }

    /**
     * 构建查询条件
     *
     * @param dto
     * @return
     */
    private QueryWrapper getQueryWrapper(SysJobSearchDTO dto) {
        return QueryWrapper.create()
                .where(SysJobEntity::getJobName).like(dto.getJobName(), StringUtils.isNotBlank(dto.getJobName()))
                .and(SysJobEntity::getJobGroup).eq(dto.getJobGroup(), StringUtils.isNotBlank(dto.getJobGroup()))
                .and(SysJobEntity::getStatus).eq(dto.getStatus(), StringUtils.isNotBlank(dto.getStatus()))
                .and(SysJobEntity::getInvokeTarget).like(dto.getInvokeTarget(), StringUtils.isNotBlank(dto.getInvokeTarget()));
    }
}
