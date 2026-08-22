package com.troy.sync.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.sync.dao.FieldDao;
import com.troy.sync.entity.FieldEntity;
import com.troy.sync.entity.table.FieldEntityTableDef;
import com.troy.sync.mapper.FieldMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FieldDaoImpl extends BaseServiceImpl<FieldMapper, FieldEntity> implements FieldDao {


    @Override
    public List<FieldEntity> findByTableId(Long tableId) {
        return list(QueryWrapper.create().where(FieldEntityTableDef.FIELD_ENTITY.TABLE_ID.eq(tableId)));
    }
}
