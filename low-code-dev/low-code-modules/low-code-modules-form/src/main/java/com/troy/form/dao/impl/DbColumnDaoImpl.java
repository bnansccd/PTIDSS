package com.troy.form.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.DbColumnDao;
import com.troy.form.mapper.DbColumnMapper;
import com.troy.form.entity.DbColumnEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.troy.form.entity.table.DbColumnEntityTableDef.DB_COLUMN_ENTITY;
/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class DbColumnDaoImpl extends BaseServiceImpl<DbColumnMapper, DbColumnEntity> implements DbColumnDao {

    @Override
    public List<DbColumnEntity> findAll() {
        List<Row> rows = Db.selectListBySql("SELECT * FROM information_schema.columns WHERE table_schema = (SELECT DATABASE())");
        return RowUtil.toEntityList(rows, DbColumnEntity.class);
    }

    @Override
    public List<DbColumnEntity> findByTableName(String tableName) {
        List<Row> rows = Db.selectListBySql("SELECT * FROM information_schema.columns WHERE table_schema = (SELECT DATABASE()) and table_name = ?", tableName);
        return RowUtil.toEntityList(rows, DbColumnEntity.class);
    }

    @Override
    public List<DbColumnEntity> findByTableId(Long tableId) {
        return list(QueryWrapper.create().where(DB_COLUMN_ENTITY.TABLE_ID.eq(tableId)));
    }

    @Override
    public Long countByTable(String tableName) {
        return Db.selectCount("select * from ?", tableName);
    }

    @Override
    public List<DbColumnEntity> findByTableIdIn(Collection<Long> list) {
        return list(QueryWrapper.create().where(DB_COLUMN_ENTITY.TABLE_ID.in(list)));
    }

}
