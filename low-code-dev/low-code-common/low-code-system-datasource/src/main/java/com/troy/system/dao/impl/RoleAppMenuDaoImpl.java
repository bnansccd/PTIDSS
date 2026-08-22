package com.troy.system.dao.impl;

import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.RoleAppMenuDao;
import com.troy.system.entity.RoleAppMenuEntity;
import com.troy.system.entity.table.RoleAppMenuEntityTableDef;
import com.troy.system.mapper.RoleAppMenuMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleAppMenuDaoImpl extends BaseServiceImpl<RoleAppMenuMapper, RoleAppMenuEntity> implements RoleAppMenuDao {

    @Override
    public List<RoleAppMenuEntity> findByRoleId(Long roleId) {
        return mapper.selectListByQuery(query().where(RoleAppMenuEntityTableDef.ROLE_APP_MENU_ENTITY.ROLE_ID.eq(roleId)));
    }

    @Override
    public void removeByRoleIdAndAppsIn(Long roleId, List<Long> ids) {
        mapper.deleteByQuery(query()
                .where(RoleAppMenuEntityTableDef.ROLE_APP_MENU_ENTITY.ROLE_ID.eq(roleId))
                .and(RoleAppMenuEntityTableDef.ROLE_APP_MENU_ENTITY.APP_ID.in(ids))
        );
    }

    @Override
    public List<RoleAppMenuEntity> findByRoleIdsIn(List<Long> roleIds) {
        return mapper.selectListByQuery(query().where(RoleAppMenuEntityTableDef.ROLE_APP_MENU_ENTITY.ROLE_ID.in(roleIds)));
    }


}
