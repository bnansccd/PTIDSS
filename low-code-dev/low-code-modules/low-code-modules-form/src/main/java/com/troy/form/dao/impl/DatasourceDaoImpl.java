package com.troy.form.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.DatasourceDao;
import com.troy.form.mapper.DatasourceMapper;
import com.troy.form.domain.DTO.DatasourceSearchDTO;
import com.troy.form.entity.DatasourceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.form.entity.table.DatasourceEntityTableDef.DATASOURCE_ENTITY;

/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class DatasourceDaoImpl extends BaseServiceImpl<DatasourceMapper, DatasourceEntity> implements DatasourceDao {

    @Override
    public DatasourceEntity findByIdentification(String identifier, String name) {
        return getOne(QueryWrapper.create().where(DATASOURCE_ENTITY.IDENTIFICATION.eq(identifier)).or(DATASOURCE_ENTITY.NAME.eq(identifier)));
    }

    @Override
    public List<DatasourceEntity> findAll() {
        List<Row> rows = Db.selectListBySql("select * from t_form_db where del_flag = 0");
        return RowUtil.toEntityList(rows, DatasourceEntity.class);
    }

    @Override
    public Page<DatasourceEntity> findPage(DatasourceSearchDTO dto) {
        return page(dto, QueryWrapper.create().where(DATASOURCE_ENTITY.IDENTIFICATION.eq(dto.getIdentification(), StringUtils::isNotBlank)).or(DATASOURCE_ENTITY.NAME.eq(dto.getName(), StringUtils::isNotBlank)));
    }


}
