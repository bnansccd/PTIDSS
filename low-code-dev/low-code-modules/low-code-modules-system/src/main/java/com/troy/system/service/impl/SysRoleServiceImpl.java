package com.troy.system.service.impl;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.DataScopeConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.system.api.domain.VO.SysRoleVO;
import com.troy.system.dao.SysDepartRoleDao;
import com.troy.system.dao.SysRoleDao;
import com.troy.system.dao.SysRoleMenuDao;
import com.troy.system.dao.SysUserRoleDao;
import com.troy.system.domain.DTO.SysRoleDTO;
import com.troy.system.domain.DTO.SysRoleDataRangeDTO;
import com.troy.system.domain.DTO.SysRoleQueryDTO;
import com.troy.system.entity.SysDepartRoleEntity;
import com.troy.system.entity.SysRoleEntity;
import com.troy.system.entity.SysUserRoleEntity;
import com.troy.system.service.SysDepartService;
import com.troy.system.service.SysRoleService;
import com.troy.system.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色管理 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    @Autowired
    private SysDepartRoleDao sysDepartRoleDao;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    public SysDepartService sysDepartService;

    @Override
    public List<SysRoleVO> findByUserId(Long userId) {
        List<SysRoleVO> vos = new ArrayList<>();
        List<SysUserRoleEntity> sysUserRoleEntities = this.sysUserRoleDao.findByUserId(userId);
        if (StringUtils.isNotEmpty(sysUserRoleEntities)) {
            List<Long> roleIds = sysUserRoleEntities.stream().map(SysUserRoleEntity::getRoleId).distinct().collect(Collectors.toList());
            List<SysRoleEntity> sysRoleEntities = this.sysRoleDao.listByIds(roleIds);
            vos = getSysRoleVOs(sysRoleEntities, null);
        }
        return vos;
    }

    @Override
    public PageVO<SysRoleVO> getSysRoleList(SysRoleQueryDTO dto) {
        Page<SysRoleEntity> page = this.sysRoleDao.getSysRolePage(dto);
        return PageUtils.convertPageVo(page, SysRoleVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO insertSysRole(SysRoleDTO dto) {
        SysRoleEntity sysRoleEntity = this.sysRoleDao.findByRoleCode(dto.getRoleCode());
        if (StringUtils.isNotNull(sysRoleEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST, ResultConstants.ROLE_CODE));
        }
        sysRoleEntity = new SysRoleEntity();
        BeanUtils.copyProperties(dto, sysRoleEntity);
        ensureSort(sysRoleEntity);
        this.sysRoleDao.save(sysRoleEntity);
        return ResultVO.success();
    }

    @Override
    public SysRoleVO getSysRoleById(Long id) {
        SysRoleVO vo = null;
        SysRoleEntity sysRoleEntity = this.sysRoleDao.getById(id);
        if (StringUtils.isNotNull(sysRoleEntity)) {
            vo = new SysRoleVO();
            BeanUtils.copyProperties(sysRoleEntity, vo);
            List<SysDepartRoleEntity> sysDepartRoleEntities = this.sysDepartRoleDao.findByRoleId(id);
            if (StringUtils.isNotEmpty(sysDepartRoleEntities)) {
                vo.setDepartIds(
                        sysDepartRoleEntities.stream().map(SysDepartRoleEntity::getDepartId).distinct().collect(Collectors.toList())
                );
            }
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysRoleById(Long id, SysRoleDTO dto) {
        SysRoleEntity sysRoleEntity = this.sysRoleDao.getById(id);
        if (StringUtils.isNull(sysRoleEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.ROLE));
        }

        SysRoleEntity sysRole = this.sysRoleDao.findByRoleCode(dto.getRoleCode());
        if (StringUtils.isNotNull(sysRole) && !sysRole.getId().equals(id)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST, ResultConstants.ROLE_CODE));
        }
        BeanUtils.copyProperties(dto, sysRoleEntity);
        ensureSort(sysRoleEntity);
        this.sysRoleDao.updateById(sysRoleEntity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteSysRoleById(List<Long> ids) {
        this.sysUserRoleDao.deleteByRoleId(ids);
        this.sysDepartRoleDao.deleteSysDepartRoleByRoleId(ids);
        this.sysRoleMenuDao.deleteByRoleId(ids);
        this.sysRoleDao.removeByIds(ids);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysRoleDataRange(Long id, SysRoleDataRangeDTO dto) {
        SysRoleEntity sysRoleEntity = this.sysRoleDao.getById(id);
        if (StringUtils.isNull(sysRoleEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.ROLE));
        }

        sysRoleEntity.setDataRange(dto.getDataRange());
        this.sysDepartRoleDao.deleteSysDepartRoleByRoleId(id);
        //自定义数据权限字典表
        if (StringUtils.equals(DictValueEnums.DATA_SCOPE_CUSTOM.getCode(), dto.getDataRange())) {
            if (StringUtils.isEmpty(dto.getDepartIds())) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.PLEASE_SELECT,ResultConstants.DEPART));
            }
            SysDepartRoleEntity sysDepartRoleEntity = null;
            List<SysDepartRoleEntity> sysDepartRoleEntities = new ArrayList<>();
            for (Long departId : dto.getDepartIds()) {
                sysDepartRoleEntity = new SysDepartRoleEntity();
                sysDepartRoleEntity.setDepartId(departId);
                sysDepartRoleEntity.setRoleId(id);
                sysDepartRoleEntities.add(sysDepartRoleEntity);
            }
            this.sysDepartRoleDao.saveBatch(sysDepartRoleEntities);
        }
        this.sysRoleDao.updateById(sysRoleEntity);
        return ResultVO.success();
    }

    @Override
    public Integer getCurrentSort() {
        SysRoleEntity sysRoleEntity = sysRoleDao.findMaxSort();
        if (StringUtils.isNotNull(sysRoleEntity) && StringUtils.isNotNull(sysRoleEntity.getSort())) {
            return sysRoleEntity.getSort() + Constants.TEN;
        } else {
            return Constants.ZERO;
        }
    }

    @Override
    public List<SysRoleVO> findByUserIdIn(List<Long> userIds) {
        List<SysUserRoleEntity> sysUserRoleEntities = this.sysUserRoleDao.findByUserIdIn(userIds);
        if (StringUtils.isEmpty(sysUserRoleEntities)) {
            return new ArrayList<>();
        }
        Map<Long, List<Long>> roleUserMap = sysUserRoleEntities.stream()
                .collect(Collectors.groupingBy(SysUserRoleEntity::getRoleId,
                        Collectors.mapping(SysUserRoleEntity::getUserId, Collectors.toList())));
        List<SysRoleEntity> sysRoleEntities = this.sysRoleDao.listByIds(new ArrayList<>(roleUserMap.keySet()));
        return getSysRoleVOs(sysRoleEntities, roleUserMap);
    }

    @Override
    public List<Long> findDataRangeByUserId(Long userId) {
        List<Long> departIds = new ArrayList<>();
        List<SysUserRoleEntity> sysUserRoleEntities = this.sysUserRoleDao.findByUserId(userId);
        if (StringUtils.isNotEmpty(sysUserRoleEntities)) {
            List<Long> roleIds = sysUserRoleEntities.stream().map(SysUserRoleEntity::getRoleId).distinct().collect(Collectors.toList());
            List<SysDepartRoleEntity> sysDepartRoleEntities = this.sysDepartRoleDao.findByRoleIdIn(roleIds);
            if (StringUtils.isNotEmpty(sysDepartRoleEntities)) {
                departIds = sysDepartRoleEntities.stream().map(SysDepartRoleEntity::getDepartId).distinct().collect(Collectors.toList());
            }
        }
        return departIds;
    }

    @Transactional
    @Override
    public Long tenantInitRole(Long tenantId, Long userId) {
        Long roleId = null;
        try {
            TenantManager.ignoreTenantCondition();
            SysRoleEntity sysRoleEntity = new SysRoleEntity();
            sysRoleEntity.setRoleName("管理员");
            sysRoleEntity.setRoleCode(DataScopeConstants.ADMIN);
            sysRoleEntity.setSort(Constants.ZERO);
            sysRoleEntity.setDataRange(DictValueEnums.DATA_SCOPE_ALL.getCode());
            sysRoleEntity.setTenantId(tenantId);
            sysRoleEntity.setIsSuper(Constants.ONE_STR);
            this.sysRoleDao.save(sysRoleEntity);
            roleId = sysRoleEntity.getId();
            this.sysUserRoleService.insertUserRoleByUserId(userId, Arrays.asList(roleId));

        } finally {
            TenantManager.restoreTenantCondition();
        }
        return roleId;
    }

    private List<SysRoleVO> getSysRoleVOs(List<SysRoleEntity> sysRoleEntities, Map<Long, List<Long>> roleUserMap) {
        List<SysRoleVO> vos = new ArrayList<>();
        if (StringUtils.isNotEmpty(sysRoleEntities)) {
            SysRoleVO vo = null;
            for (SysRoleEntity sysRoleEntity : sysRoleEntities) {
                vo = new SysRoleVO();
                BeanUtils.copyProperties(sysRoleEntity, vo);
                if (StringUtils.isNotEmpty(roleUserMap)) {
                    vo.setUserIds(roleUserMap.get(vo.getId()));
                }
                vos.add(vo);
            }
        }
        return vos;
    }

    private void ensureSort(SysRoleEntity roleEntity) {
        if (StringUtils.isNotNull(roleEntity.getSort())) {
            return;
        }
        SysRoleEntity maxSortEntity = this.sysRoleDao.findMaxSort();
        if (StringUtils.isNotNull(maxSortEntity) && StringUtils.isNotNull(maxSortEntity.getSort())) {
            roleEntity.setSort(maxSortEntity.getSort() + Constants.TEN);
        } else {
            roleEntity.setSort(Constants.ZERO);
        }
    }

}
