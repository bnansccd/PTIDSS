package com.troy.sync.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.sync.dao.DatasourceDao;
import com.troy.sync.entity.DatasourceEntity;
import com.troy.sync.entity.table.DatasourceEntityTableDef;
import com.troy.sync.mapper.DatasourceMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatasourceDaoImpl extends BaseServiceImpl<DatasourceMapper, DatasourceEntity> implements DatasourceDao {

    @Override
    public List<DatasourceEntity> findAll() {
        return list();
    }

    @Override
    public List<DatasourceEntity> findBySourceIdIn(List<Long> list) {
        return list(QueryWrapper.create().where(DatasourceEntityTableDef.DATASOURCE_ENTITY.ID.in(list)));
    }

    @Override
    public List<DatasourceEntity> findByTargetIn(List<String> list) {
        return list(QueryWrapper.create().where(DatasourceEntityTableDef.DATASOURCE_ENTITY.TARGET.in(list)));
    }

    @Override
    public DatasourceEntity findByTarget(String target) {
        return getOne(QueryWrapper.create().where(DatasourceEntityTableDef.DATASOURCE_ENTITY.TARGET.eq(target)));
    }

}
