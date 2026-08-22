package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysAreaDao;
import com.troy.system.entity.SysAreaEntity;
import com.troy.system.mapper.SysAreaMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysAreaEntityTableDef.SYS_AREA_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/16 20:20:14
 * @Description: SysAreaDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysAreaDaoImpl extends BaseServiceImpl<SysAreaMapper, SysAreaEntity> implements SysAreaDao {

    @Override
    public SysAreaEntity findByAdcode(String adcode) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_AREA_ENTITY.ADCODE.eq(adcode))
                        .and(SYS_AREA_ENTITY.LEVEL.ne("street"))
        );
    }

    @Override
    public List<SysAreaEntity> findByParentCode(String parentCode) {
        return super.list(
                QueryWrapper.create()
                        .where(SYS_AREA_ENTITY.PARENT_CODE.eq(parentCode))
        );
    }
}
