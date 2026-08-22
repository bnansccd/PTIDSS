package com.troy.job.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.job.dao.SysJobLogDao;
import com.troy.job.domain.DTO.SysJobLogDTO;
import com.troy.job.domain.DTO.SysJobLogSearchDTO;
import com.troy.job.domain.VO.SysJobLogVO;
import com.troy.job.entity.SysJobLogEntity;
import com.troy.job.service.SysJobLogService;
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
 * @Date: 2022/9/28 14:14:46
 * @Description: 定时任务日志实现层
 * @Version: 1.0.0
 */
@Service
public class SysJobLogServiceImpl implements SysJobLogService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SysJobLogServiceImpl.class);

    @Autowired
    private SysJobLogDao sysJobLogDao;

    @Override
    public PageVO<SysJobLogVO> selectJobLogPage(SysJobLogSearchDTO dto) {
        Page<SysJobLogEntity> page = this.sysJobLogDao.selectJobLogPage(dto);
        return PageUtils.convertPageVo(page, SysJobLogVO.class);
    }

    @Override
    public List<SysJobLogVO> selectJobLogList(SysJobLogSearchDTO dto) {
        List<SysJobLogEntity> sysJobLogEntities = this.sysJobLogDao.selectJobLogList(dto);
        List<SysJobLogVO> vos = new ArrayList<>();
        if (StringUtils.isNotEmpty(sysJobLogEntities)) {
            SysJobLogVO vo = null;
            for (SysJobLogEntity sysJobLogEntity : sysJobLogEntities) {
                vo = new SysJobLogVO();
                BeanUtils.copyProperties(sysJobLogEntity, vo);
                vos.add(vo);
            }
        }
        return vos;
    }

    @Override
    public SysJobLogVO selectJobLogById(Long id) {
        SysJobLogEntity sysJobLogEntity = this.sysJobLogDao.getById(id);
        SysJobLogVO vo = null;
        if (StringUtils.isNotNull(sysJobLogEntity)) {
            vo = new SysJobLogVO();
            BeanUtils.copyProperties(sysJobLogEntity, vo);
        }
        return vo;
    }

    @Transactional
    @Override
    public ResultVO deleteJobLogByIds(List<Long> ids) {
        this.sysJobLogDao.removeByIds(ids);
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO cleanJobLog() {
        this.sysJobLogDao.cleanJobLog();
        return ResultVO.success();
    }

    @Transactional
    @Override
    public void addJobLog(SysJobLogDTO dto) {
        SysJobLogEntity sysJobLogEntity = new SysJobLogEntity();
        BeanUtils.copyProperties(dto, sysJobLogEntity);
        this.sysJobLogDao.save(sysJobLogEntity);
    }
}
