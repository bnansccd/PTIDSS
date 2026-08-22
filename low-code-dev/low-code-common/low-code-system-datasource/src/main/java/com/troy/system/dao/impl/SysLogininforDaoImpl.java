package com.troy.system.dao.impl;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysLogininforDao;
import com.troy.system.domain.DTO.SysLogininfoQueryDTO;
import com.troy.system.entity.SysLogininforEntity;
import com.troy.system.entity.table.SysLogininforEntityTableDef;
import com.troy.system.mapper.SysLogininforMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:57
 * @Description: SysLogininforDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysLogininforDaoImpl extends BaseServiceImpl<SysLogininforMapper, SysLogininforEntity> implements SysLogininforDao {
    @Override
    public Page<SysLogininforEntity> getSysLogininforPage(SysLogininfoQueryDTO dto) {
       return super.page(
                dto,
                QueryWrapper.create()
                        .where(SysLogininforEntity::getUsername).like(dto.getUserName(),StringUtils.isNotBlank(dto.getUserName()))
                        .and(SysLogininforEntity::getCreateTime).gt(dto.getStartTime(),StringUtils.isNotNull(dto.getStartTime()))
                        .and(SysLogininforEntity::getCreateTime).lt(dto.getEndTime(),StringUtils.isNotNull(dto.getEndTime()))
        );
    }

    @Override
    public List<SysLogininforEntity> getSysLogininforList(String userName, Date startDate, Date endDate) {
        return super.list(
                query().where(SysLogininforEntityTableDef.SYS_LOGININFOR_ENTITY.USERNAME.eq(userName,StringUtils::isNotEmpty))
                        .and(SysLogininforEntityTableDef.SYS_LOGININFOR_ENTITY.ACCESS_TIME.ge(startDate))
                        .and(SysLogininforEntityTableDef.SYS_LOGININFOR_ENTITY.ACCESS_TIME.le(endDate))
        );
    }

}
