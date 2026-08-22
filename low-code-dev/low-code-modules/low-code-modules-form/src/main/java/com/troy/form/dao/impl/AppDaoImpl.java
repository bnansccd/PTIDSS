package com.troy.form.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.AppDao;
import com.troy.form.entity.AppEntity;
import com.troy.form.domain.DTO.AppSearchDTO;
import com.troy.form.mapper.AppMapper;
import org.springframework.stereotype.Component;


import java.util.List;

import static com.troy.form.entity.table.AppEntityTableDef.APP_ENTITY;

/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class AppDaoImpl extends BaseServiceImpl<AppMapper, AppEntity> implements AppDao {

    @Override
    public AppEntity findFirstByName(String name) {
        return getOne(QueryWrapper.create().where(APP_ENTITY.NAME.eq(name)));
    }

    @Override
    public List<AppEntity> findPageList(AppSearchDTO dto) {
        return list(QueryWrapper.create()
                .where(APP_ENTITY.NAME.like(dto.getName(), StringUtils::isNotBlank))
                .or(APP_ENTITY.CODE.like(dto.getCode(), StringUtils::isNotBlank))
                .and(APP_ENTITY.TYPE_ID.eq(dto.getTypeId(), StringUtils::isNotNull))
        );
    }
}
