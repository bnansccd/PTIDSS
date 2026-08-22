package com.troy.form.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.DbSqlDao;
import com.troy.form.mapper.DbSqlMapper;
import com.troy.form.domain.DTO.DbSqlSearchDTO;
import com.troy.form.entity.DbSqlEntity;
import com.troy.form.entity.table.DbSqlEntityTableDef;
import org.springframework.stereotype.Component;

/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class DbSqlDaoImpl extends BaseServiceImpl<DbSqlMapper, DbSqlEntity> implements DbSqlDao {

    @Override
    public boolean exists(Long id, String name, String code) {
        DbSqlEntity one = getOne(QueryWrapper.create().where(DbSqlEntityTableDef.DB_SQL_ENTITY.NAME.eq(name).or(DbSqlEntityTableDef.DB_SQL_ENTITY.CODE.eq(code))));
        if (one == null){
            return false;
        }
        return !one.getId().equals(id);
    }

    @Override
    public Page<DbSqlEntity> findPage(DbSqlSearchDTO dto) {
        return page(dto, QueryWrapper.create()
                .where(DbSqlEntityTableDef.DB_SQL_ENTITY.NAME.like(dto.getName(), StringUtils::isNotBlank))
                .or(DbSqlEntityTableDef.DB_SQL_ENTITY.CODE.like(dto.getCode(), StringUtils::isNotBlank))
                .or(DbSqlEntityTableDef.DB_SQL_ENTITY.KEY.like(dto.getKey(), StringUtils::isNotBlank))
                .or(DbSqlEntityTableDef.DB_SQL_ENTITY.TEXT.like(dto.getText(), StringUtils::isNotBlank))
        );
    }
}
