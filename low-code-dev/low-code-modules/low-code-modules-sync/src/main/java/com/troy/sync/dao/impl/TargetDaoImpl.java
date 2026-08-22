package com.troy.sync.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.sync.dao.TargetDao;
import com.troy.sync.entity.TargetEntity;
import com.troy.sync.entity.table.TargetEntityTableDef;
import com.troy.sync.mapper.TargetMapper;
import org.springframework.stereotype.Service;

/**
 * @author chenxl
 * @description
 * @date 2024-06-20 10:25
 */
@Service
public class TargetDaoImpl extends BaseServiceImpl<TargetMapper, TargetEntity> implements TargetDao {


    @Override
    public TargetEntity findByTarget(String target) {
        return getOne(QueryWrapper.create().where(TargetEntityTableDef.TARGET_ENTITY.TARGET.eq(target)));
    }


}
