package com.troy.sync.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.sync.dao.TableDao;
import com.troy.sync.entity.TableEntity;
import com.troy.sync.entity.table.TableEntityTableDef;
import com.troy.sync.mapper.TableMapper;
import org.springframework.stereotype.Component;

@Component
public class TableDaoImpl extends BaseServiceImpl<TableMapper, TableEntity> implements TableDao {


    @Override
    public TableEntity findByTableNameAndSourceId(String tableName, Long sourceId) {
        return getOne(
                QueryWrapper.create()
                        .where(TableEntityTableDef.TABLE_ENTITY.TABLE_NAME.eq(tableName))
                        .and(TableEntityTableDef.TABLE_ENTITY.SOURCE_ID.eq(sourceId))
        );
    }

}
