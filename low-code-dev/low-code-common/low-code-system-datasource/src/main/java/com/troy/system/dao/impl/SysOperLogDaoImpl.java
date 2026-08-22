package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysOperLogDao;
import com.troy.system.domain.DTO.SysOperLogQueryDTO;
import com.troy.system.entity.SysOperLogEntity;
import com.troy.system.mapper.SysOperLogMapper;
import org.springframework.stereotype.Component;

import static com.troy.system.entity.table.SysOperLogEntityTableDef.SYS_OPER_LOG_ENTITY;


/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:10
 * @Description: SysOperLogDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysOperLogDaoImpl extends BaseServiceImpl<SysOperLogMapper, SysOperLogEntity> implements SysOperLogDao {
    @Override
    public Page<SysOperLogEntity> getSysOperLogPage(SysOperLogQueryDTO dto) {
       return super.page(
               dto,
                QueryWrapper.create()
                        .where(SYS_OPER_LOG_ENTITY.TITLE.like(dto.getTitle(),StringUtils.isNotBlank(dto.getTitle())))
                        .and(SYS_OPER_LOG_ENTITY.METHOD.like(dto.getMethod(),StringUtils.isNotBlank(dto.getMethod())))
                        .and(SYS_OPER_LOG_ENTITY.REQUEST_METHOD.eq(dto.getRequestMethod(),StringUtils.isNotBlank(dto.getRequestMethod())))
                        .and(SYS_OPER_LOG_ENTITY.OPER_NAME.like(dto.getOperName(),StringUtils.isNotBlank(dto.getOperName())))
                        .and(SYS_OPER_LOG_ENTITY.OPER_URL.like(dto.getOperUrl(),StringUtils.isNotBlank(dto.getOperUrl())))
                        .and(SysOperLogEntity::getCreateTime).gt(dto.getStartTime(),StringUtils.isNotNull(dto.getStartTime()))
                        .and(SysOperLogEntity::getCreateTime).lt(dto.getEndTime(),StringUtils.isNotNull(dto.getEndTime()))
        );
    }

}
