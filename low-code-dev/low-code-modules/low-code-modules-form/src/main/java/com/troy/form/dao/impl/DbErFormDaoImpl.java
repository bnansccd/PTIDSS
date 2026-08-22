package com.troy.form.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.DbErFormDao;
import com.troy.form.mapper.DbErFormMapper;
import com.troy.form.domain.DTO.DbErFormSearchDTO;
import com.troy.form.entity.DbErFormEntity;
import com.troy.form.entity.table.DbErFormEntityTableDef;
import org.springframework.stereotype.Component;

/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class DbErFormDaoImpl extends BaseServiceImpl<DbErFormMapper, DbErFormEntity> implements DbErFormDao {

    @Override
    public boolean exists(Long id, String name, String mark) {
        DbErFormEntity one = getOne(QueryWrapper.create().where(DbErFormEntityTableDef.DB_ER_FORM_ENTITY.NAME.eq(name)).or(DbErFormEntityTableDef.DB_ER_FORM_ENTITY.MARK.eq(mark)));
        if (one == null){
            return false;
        }
        return !one.getId().equals(id);
    }

    @Override
    public Page<DbErFormEntity> findPage(DbErFormSearchDTO dto) {
        return page(dto, QueryWrapper.create().where(DbErFormEntityTableDef.DB_ER_FORM_ENTITY.NAME.eq(dto.getName(), StringUtils::isNotBlank)).or(DbErFormEntityTableDef.DB_ER_FORM_ENTITY.MARK.eq(dto.getName(), StringUtils::isNotBlank)));
    }

    @Override
    public DbErFormEntity findByMark(String mark) {
        return getOne(QueryWrapper.create().where(DbErFormEntityTableDef.DB_ER_FORM_ENTITY.MARK.eq(mark)));
    }
}
