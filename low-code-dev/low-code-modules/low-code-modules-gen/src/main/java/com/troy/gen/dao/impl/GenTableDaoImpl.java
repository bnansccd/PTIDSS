package com.troy.gen.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.gen.dao.GenTableDao;
import com.troy.gen.domain.DTO.DbTableDTO;
import com.troy.gen.entity.GenTableEntity;
import com.troy.gen.mapper.GenTableMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.gen.entity.table.GenTableEntityTableDef.GEN_TABLE_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 16:16:20
 * @Description: GenTableDaoImpl
 * @Version: 1.0.0
 */
@Component
public class GenTableDaoImpl extends BaseServiceImpl<GenTableMapper, GenTableEntity> implements GenTableDao {
    @Override
    public Page<GenTableEntity> dataList(DbTableDTO dto) {
        return super.page(
                new Page<>(dto.getCurrent(), dto.getSize()),
                QueryWrapper.create()
                        .where(GEN_TABLE_ENTITY.TABLE_NAME.like(dto.getTableName(), StringUtils.isNotBlank(dto.getTableName())))
                        .and(GEN_TABLE_ENTITY.TABLE_COMMENT.like(dto.getTableComment(), StringUtils.isNotBlank(dto.getTableComment())))
                        .and(GEN_TABLE_ENTITY.CREATE_TIME.ge(dto.getBeginTime(), StringUtils.isNotNull(dto.getBeginTime())))
                        .and(GEN_TABLE_ENTITY.CREATE_TIME.le(dto.getEndTime(), StringUtils.isNotNull(dto.getEndTime())))
        );
    }

    @Override
    public List<GenTableEntity> selectDbTableListByNames(List<String> tableNames) {
        return super.mapper.selectDbTableListByNames(tableNames);
    }

    @Override
    public GenTableEntity findByTableName(String tableName) {
        return super.getOne(
                QueryWrapper.create()
                        .where(GEN_TABLE_ENTITY.TABLE_NAME.eq(tableName))
        );
    }

    @Override
    public List<GenTableEntity> findBySubTableName(String tableName) {
        return super.list(
                QueryWrapper.create()
                        .where(GEN_TABLE_ENTITY.SUB_TABLE_NAME.eq(tableName))
        );
    }
}
