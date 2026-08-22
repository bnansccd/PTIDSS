package com.troy.form.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.DbTableDao;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.mapper.DbTableMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import static com.troy.form.entity.table.DbTableEntityTableDef.DB_TABLE_ENTITY;

/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class DbTableDaoImpl extends BaseServiceImpl<DbTableMapper, DbTableEntity> implements DbTableDao {


    @Override
    public List<DbTableEntity> findAllTable() {
        List<Row> rows = Db.selectListBySql("SELECT table_name as name, table_comment as comment, ENGINE FROM information_schema.tables WHERE table_schema = (SELECT DATABASE())");
        return RowUtil.toEntityList(rows, DbTableEntity.class);
    }

    @Override
    public DbTableEntity findByTable(String tableName, Long tableId) {
        return getOne(QueryWrapper.create()
                .where(DB_TABLE_ENTITY.DB_ID.eq(tableId))
                .and(DB_TABLE_ENTITY.TABLE_NAME.eq(tableName))
        );
    }

    @Override
    public DbTableEntity findByTable(String tableName) {
        Row rows = Db.selectOneBySql("SELECT table_name as name, table_comment as comment, ENGINE FROM information_schema.tables WHERE table_schema = (SELECT DATABASE()) and table_name = ?", tableName);
        return RowUtil.toEntity(rows, DbTableEntity.class);
    }

}
