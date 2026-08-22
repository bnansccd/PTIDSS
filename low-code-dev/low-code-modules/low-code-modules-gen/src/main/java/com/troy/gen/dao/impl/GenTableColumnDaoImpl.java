package com.troy.gen.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.gen.dao.GenTableColumnDao;
import com.troy.gen.entity.GenTableColumnEntity;
import com.troy.gen.mapper.GenTableColumnMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.troy.gen.entity.table.GenTableColumnEntityTableDef.GEN_TABLE_COLUMN_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 16:16:25
 * @Description: GenTableColumnDaoImpl
 * @Version: 1.0.0
 */
@Component
public class GenTableColumnDaoImpl extends BaseServiceImpl<GenTableColumnMapper, GenTableColumnEntity> implements GenTableColumnDao {

    @Override
    public List<GenTableColumnEntity> findByTableId(Long tableId) {
        return super.list(
                QueryWrapper.create()
                        .where(GEN_TABLE_COLUMN_ENTITY.TABLE_ID.eq(tableId))
                        .orderBy(GEN_TABLE_COLUMN_ENTITY.SORT.asc())
        );
    }

    @Override
    public List<GenTableColumnEntity> selectDbTableColumnsByName(String tableName) {
        return super.mapper.selectDbTableColumnsByName(tableName);

    }

    @Transactional
    @Override
    public Boolean deleteByTableIds(List<Long> tableIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(GEN_TABLE_COLUMN_ENTITY.TABLE_ID.in(tableIds))
        );
    }

    @Override
    public List<GenTableColumnEntity> findByTableIds(List<Long> tableIds) {
        return super.list(
                QueryWrapper.create()
                        .where(GEN_TABLE_COLUMN_ENTITY.TABLE_ID.in(tableIds))
        );
    }
}
