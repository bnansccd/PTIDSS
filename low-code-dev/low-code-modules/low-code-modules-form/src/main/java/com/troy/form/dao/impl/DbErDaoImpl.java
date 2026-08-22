package com.troy.form.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.DbErDao;
import com.troy.form.mapper.DbErMapper;
import com.troy.form.entity.DbErEntity;
import com.troy.form.entity.table.DbErEntityTableDef;
import org.springframework.stereotype.Component;

/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class DbErDaoImpl extends BaseServiceImpl<DbErMapper, DbErEntity> implements DbErDao {


    @Override
    public boolean exists(Long id, String name, String erModelMark) {
        DbErEntity one = getOne(QueryWrapper.create().where(DbErEntityTableDef.DB_ER_ENTITY.NAME.eq(name).or(DbErEntityTableDef.DB_ER_ENTITY.ER_MODEL_MARK.eq(name))));
        if (one == null){
            return false;
        }
        return !one.getId().equals(id);
    }
}
