package com.troy.form.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.form.dao.AppTypeDao;
import com.troy.form.domain.DTO.AppTypeSearchDTO;
import com.troy.form.entity.AppTypeEntity;
import com.troy.form.mapper.AppTypeMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.form.entity.table.AppTypeEntityTableDef.APP_TYPE_ENTITY;


/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class AppTypeDaoImpl extends BaseServiceImpl<AppTypeMapper, AppTypeEntity> implements AppTypeDao {


    @Override
    public AppTypeEntity findFirstByName(String name) {
        return getOne(QueryWrapper.create().where(APP_TYPE_ENTITY.NAME.eq(name)));
    }

    @Override
    public List<AppTypeEntity> findPageList(AppTypeSearchDTO dto) {
        return list(QueryWrapper.create()
                .where(APP_TYPE_ENTITY.NAME.like(dto.getName(), StringUtils::isNotBlank))
                .or(APP_TYPE_ENTITY.CODE.like(dto.getCode(), StringUtils::isNotBlank))
        );
    }
}
