package com.troy.form.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.DbErRelationDao;
import com.troy.form.mapper.DbErRelationMapper;
import com.troy.form.entity.DbErRelationEntity;
import com.troy.form.entity.table.DbErRelationEntityTableDef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author chenxl
 * @date 2023/11/8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DbErRelationDaoImpl extends BaseServiceImpl<DbErRelationMapper, DbErRelationEntity> implements DbErRelationDao {

    @Override
    public void removeByErId(Long id) {
        remove(QueryWrapper.create().where(DbErRelationEntityTableDef.DB_ER_RELATION_ENTITY.ER_ID.eq(id)));
    }

    @Override
    public List<DbErRelationEntity> listByErId(Long erId) {
        return list(QueryWrapper.create().where(DbErRelationEntityTableDef.DB_ER_RELATION_ENTITY.ER_ID.eq(erId)));
    }
}
