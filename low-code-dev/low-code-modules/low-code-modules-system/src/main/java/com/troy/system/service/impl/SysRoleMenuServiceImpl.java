package com.troy.system.service.impl;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.system.dao.RoleAppMenuDao;
import com.troy.system.dao.SysMenuDao;
import com.troy.system.dao.SysRoleMenuDao;
import com.troy.system.domain.DTO.TenantMenuDTO;
import com.troy.system.entity.RoleAppMenuEntity;
import com.troy.system.entity.SysMenuEntity;
import com.troy.system.entity.SysRoleMenuEntity;
import com.troy.system.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色菜单关系表 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    @Autowired
    private SysMenuDao sysMenuDao;

    @Autowired
    private RoleAppMenuDao roleAppMenuDao;

    @Override
    @Transactional
    public ResultVO insertRoleMenu(Long roleId, List<Long> menuIds) {
        List<SysMenuEntity> menuEntities = this.sysMenuDao.listByIds(menuIds);
        if (StringUtils.isEmpty(menuEntities)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.MENU));
        }
        menuIds = menuEntities.stream().map(SysMenuEntity::getId).collect(Collectors.toList());
        this.sysRoleMenuDao.deleteByRoleId(roleId);
        if (StringUtils.isNotEmpty(menuIds)) {
            List<SysRoleMenuEntity> sysRoleMenuEntities = menuIds.stream().map(menuId -> {
                SysRoleMenuEntity relation = new SysRoleMenuEntity();
                relation.setMenuId(menuId);
                relation.setRoleId(roleId);
                return relation;
            }).collect(Collectors.toList());
            this.sysRoleMenuDao.saveBatch(sysRoleMenuEntities);
        }
        return ResultVO.success();

    }

    @Override
    public List<Long> SysRoleMenuByRoleId(Long roleId) {
        List<SysRoleMenuEntity> sysRoleMenuEntities = this.sysRoleMenuDao.findByRoleId(roleId);
        if (StringUtils.isEmpty(sysRoleMenuEntities)) {
            return new ArrayList<>();
        }
        return sysRoleMenuEntities.stream().map(SysRoleMenuEntity::getMenuId).distinct().collect(Collectors.toList());
    }

    @Override
    public void insertAppMenu(Long roleId, List<TenantMenuDTO> list) {
        if (StringUtils.isNotEmpty(list)){
            List<Long> longs = list.stream().map(TenantMenuDTO::getAppId).collect(Collectors.toList());
            roleAppMenuDao.removeByRoleIdAndAppsIn(roleId, longs);

            List<RoleAppMenuEntity> collect = list.stream().map(e -> {
                RoleAppMenuEntity roleAppMenu = new RoleAppMenuEntity();
                roleAppMenu.setAppId(e.getAppId());
                roleAppMenu.setRoleId(roleId);
                roleAppMenu.setMenuId(e.getMenuId());
                return roleAppMenu;
            }).collect(Collectors.toList());

            roleAppMenuDao.saveBatch(collect);
        }
    }

    @Override
    public List<Long> appMenuByRoleId(Long roleId) {
        List<RoleAppMenuEntity> byRoleId = roleAppMenuDao.findByRoleId(roleId);
        if (StringUtils.isNotEmpty(byRoleId)) {
            return byRoleId.stream().map(RoleAppMenuEntity::getMenuId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
