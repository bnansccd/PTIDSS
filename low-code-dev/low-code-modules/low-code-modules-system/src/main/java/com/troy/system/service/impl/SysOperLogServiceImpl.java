package com.troy.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.system.api.domain.VO.SysOperLogVO;
import com.troy.system.dao.SysOperLogDao;
import com.troy.system.domain.DTO.SysOperLogDTO;
import com.troy.system.domain.DTO.SysOperLogQueryDTO;
import com.troy.system.entity.SysOperLogEntity;
import com.troy.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogDao sysOperLogDao;

    @Override
    public PageVO<SysOperLogVO> getSysOperLogList(SysOperLogQueryDTO dto) {
        Page<SysOperLogEntity> page = this.sysOperLogDao.getSysOperLogPage(dto);
        return PageUtils.convertPageVo(page, SysOperLogVO.class);
    }


    @Override
    public SysOperLogVO getSysOperLogById(Long id) {
        SysOperLogEntity sysOperLogEntity = this.sysOperLogDao.getById(id);
        return copyToVO(sysOperLogEntity);
    }

    @Override
    public Boolean addSysOperLog(SysOperLogDTO dto) {
        SysOperLogEntity sysOperLogEntity = new SysOperLogEntity();
        BeanUtils.copyProperties(dto, sysOperLogEntity);
        try {
            TenantManager.ignoreTenantCondition();
            sysOperLogDao.save(sysOperLogEntity);
        }finally {
            TenantManager.restoreTenantCondition();
        }
        return Boolean.TRUE;
    }

    private SysOperLogVO copyToVO(SysOperLogEntity entity) {
        if (StringUtils.isNull(entity)) {
            return null;
        }
        SysOperLogVO vo = new SysOperLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
