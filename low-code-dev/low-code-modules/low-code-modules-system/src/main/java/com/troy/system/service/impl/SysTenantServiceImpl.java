package com.troy.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.system.api.domain.VO.SysMenuVO;
import com.troy.system.api.domain.VO.SysTenantVO;
import com.troy.system.dao.*;
import com.troy.system.domain.DTO.*;
import com.troy.system.domain.VO.TenantAppVO;
import com.troy.system.domain.VO.TenantMenuVO;
import com.troy.system.entity.*;
import com.troy.system.service.SysDomainNameService;
import com.troy.system.service.SysRoleService;
import com.troy.system.service.SysTenantService;
import com.troy.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/22 10:10:25
 * @Description: 租户
 * @Version: 1.0.0
 */
@Service
public class SysTenantServiceImpl implements SysTenantService {

    @Autowired
    private SysTenantDao sysTenantDao;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysDomainNameService sysDomainNameService;

    @Autowired
    private TenantAppDao tenantAppDao;

    @Autowired
    private SysAppDao sysAppDao;

    @Autowired
    private TenantMenuDao tenantMenuDao;

    @Autowired
    private SysMenuDao sysMenuDao;

    @Override
    public PageVO<SysTenantVO> listPage(SysTenantSearchDTO dto) {
        Page<SysTenantEntity> page = this.sysTenantDao.listPage(dto);
        return PageUtils.convertPageVo(page, SysTenantVO.class);
    }

    @Transactional
    @Override
    public ResultVO insert(SysTenantInsertDTO dto) {
        this.validTenant(dto, null);
        SysTenantEntity sysTenantEntity;
        sysTenantEntity = new SysTenantEntity();
        BeanUtils.copyProperties(dto, sysTenantEntity);
        this.sysTenantDao.save(sysTenantEntity);
        Long tenantId = sysTenantEntity.getId();
        //租户初始化账号
        Long userId = this.sysUserService.tenantInitUser(tenantId, dto);
        //租户初始化角色
        if (StringUtils.isNotNull(userId)) {
            this.sysRoleService.tenantInitRole(tenantId, userId);
        }
        //初始化域名
        this.sysDomainNameService.domainInit(tenantId,dto.getCode());

        return ResultVO.success();
    }

    @Override
    public SysTenantVO findById(Long id) {
        SysTenantVO vo = null;
        if (StringUtils.isNotNull(id)) {
            SysTenantEntity sysTenantEntity = this.sysTenantDao.getById(id);
            if (StringUtils.isNotNull(sysTenantEntity)) {
                vo = new SysTenantVO();
                BeanUtils.copyProperties(sysTenantEntity, vo);
            }
        }
        return vo;
    }

    @Transactional
    @Override
    public ResultVO edit(Long id, SysTenantDTO dto) {
        SysTenantEntity sysTenantEntity = this.validateSysTenantExit(id);
        dto.setCode(sysTenantEntity.getCode());
        this.validTenant(dto, id);
        BeanUtils.copyProperties(dto, sysTenantEntity);
        this.sysTenantDao.updateById(sysTenantEntity);
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO editStatus(Long id) {
        SysTenantEntity sysTenantEntity = this.validateSysTenantExit(id);
        sysTenantEntity.setStatus(
                StringUtils.equals(DictValueEnums.ON_STATUS.getCode(), sysTenantEntity.getStatus()) ?
                        DictValueEnums.OFF_STATUS.getCode() : DictValueEnums.ON_STATUS.getCode()
        );
        this.sysTenantDao.updateById(sysTenantEntity);
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO deleteByIdIn(List<Long> ids) {
        this.sysTenantDao.removeByIds(ids);
        return ResultVO.success();
    }

    @Override
    public List<SysTenantVO> tenantList(SysTenantSearchDTO dto) {
        List<SysTenantVO> vos = new ArrayList<>();
        try {
            TenantManager.ignoreTenantCondition();
            List<SysTenantEntity> sysTenantEntities = this.sysTenantDao.tenantList(dto);
            vos = this.tenantBatchCopy(sysTenantEntities);
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return vos;
    }

    @Override
    public void bindTenant(TenantAppDTO dto) {

        try {
            TenantManager.ignoreTenantCondition();

            SysTenantAppEntity app = tenantAppDao.findTenantIdAndAppId(dto.getAppId(), dto.getTenantId());
            if (app != null){
                throw new ServiceException(ResultEnum.BE_CURRENT, "应用已绑定！");
            }

            app = new SysTenantAppEntity();
            BeanUtils.copyProperties( dto, app);

            tenantAppDao.save(app);
        } finally {
            TenantManager.restoreTenantCondition();
        }

    }

    @Override
    public void bindTenant(Long id, TenantAppDTO dto) {
        try {
            TenantManager.ignoreTenantCondition();

            SysTenantAppEntity app = tenantAppDao.getById(id);
            if (app == null){
                throw new ServiceException(ResultEnum.BE_CURRENT, "应用绑定信息不存在！");
            }

            app.setAppId(dto.getAppId());
            app.setStatus(dto.getStatus());
            app.setValidEndTime(dto.getValidEndTime());
            app.setValidStartTime(dto.getValidStartTime());

            tenantAppDao.updateById(app);
        } finally {
            TenantManager.restoreTenantCondition();
        }
    }

    @Override
    public void deleteTenant(TenantAppDTO dto) {
        try {
            TenantManager.ignoreTenantCondition();

            SysTenantAppEntity app = tenantAppDao.findTenantIdAndAppId(dto.getAppId(), dto.getTenantId());
            if (app == null){
                throw new ServiceException(ResultEnum.BE_CURRENT, "绑定关系不存在！");
            }

            tenantAppDao.deleteByTenantIdAndAppId(dto.getAppId(), dto.getTenantId());
        } finally {
            TenantManager.restoreTenantCondition();
        }

    }

    @Override
    public PageVO<TenantAppVO> findTenantAppPage(TenantAppSearchDTO dto) {

        try {
            TenantManager.ignoreTenantCondition();

            dto.setSize(100L);
            Page<SysTenantAppEntity> page = tenantAppDao.findPage(dto);
            return fillTenantAppPage(page);
        } finally {
            TenantManager.restoreTenantCondition();
        }

    }

    @Override
    public PageVO<TenantAppVO> findCurrentTenantAppPage(TenantAppSearchDTO dto) {
        Page<SysTenantAppEntity> page = tenantAppDao.findPage(dto);
        return fillTenantAppPage(page);
    }

    @Override
    public List<TenantMenuVO> getTenantMenu(TenantAppDTO dto) {
        try {
            TenantManager.ignoreTenantCondition();

            List<SysTenantMenuEntity> list = tenantMenuDao.findByTenantIdAndAppId(dto.getTenantId(), dto.getAppId());

            return list.stream().map(e -> {
                TenantMenuVO vo = new TenantMenuVO();
                BeanUtils.copyProperties(e, vo);
                return vo;
            }).collect(Collectors.toList());
        } finally {
            TenantManager.restoreTenantCondition();
        }
    }

    @Override
    public void updateTenantMenu(Long tenantId, List<TenantMenuDTO> list) {
        try {
            TenantManager.ignoreTenantCondition();

            List<Long> ids = list.stream().map(TenantMenuDTO::getAppId).distinct().collect(Collectors.toList());
            if (ids.size() != 1){
                throw new ServiceException(ResultEnum.BE_CURRENT, "权限配置错误！");
            }

            Long appId = ids.get(0);
            tenantMenuDao.removeByTenantIdAndAppId(tenantId, appId);

            List<SysTenantMenuEntity> collect = list.stream().map(e -> {
                SysTenantMenuEntity tenantMenu = new SysTenantMenuEntity();
                BeanUtils.copyProperties(e, tenantMenu);
                tenantMenu.setTenantId(tenantId);
                return tenantMenu;
            }).collect(Collectors.toList());

            tenantMenuDao.saveBatch(collect);
        } finally {
            TenantManager.restoreTenantCondition();
        }
    }

    @Override
    public List<SysMenuVO> getCurrentAppMenu(TenantAppDTO dto) {
        List<SysTenantMenuEntity> list = tenantMenuDao.findByAppId(dto.getAppId());
        if (StringUtils.isNotEmpty(list)){
            List<Long> longs = list.stream().map(SysTenantMenuEntity::getMenuId).collect(Collectors.toList());
            List<SysMenuEntity> entities = sysMenuDao.listByIds(longs);
            if (StringUtils.isNotEmpty(entities)){
                return entities.stream().map(e -> {
                    SysMenuVO sysMenuVO = new SysMenuVO();
                    BeanUtils.copyProperties(e, sysMenuVO);
                    return sysMenuVO;
                }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }


    /**
     * 验证租户信息
     *
     * @param dto
     */
    private void validTenant(SysTenantDTO dto, Long id) {
        if (dto.getStartTime().compareTo(dto.getEndTime()) >= Constants.ZERO) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.BE_CURRENT,ResultConstants.ENT_TIME_NOT_GE_START_TIME));
        }
        try {
            TenantManager.ignoreTenantCondition();
            SysTenantEntity sysTenantEntity = this.sysTenantDao.findByCode(dto.getCode());
            if (StringUtils.isNotNull(sysTenantEntity)) {
                if (StringUtils.isNull(id)) {
                    throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST, ResultConstants.TENANT_CODE));
                } else if (!sysTenantEntity.getId().equals(id)) {
                    throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST, ResultConstants.TENANT_CODE));
                }
            }
        }finally {
            TenantManager.restoreTenantCondition();
        }
    }

    /**
     * 验证租户是否存在
     *
     * @param id
     * @return
     */
    private SysTenantEntity validateSysTenantExit(Long id) {
        SysTenantEntity sysTenantEntity = this.sysTenantDao.getById(id);
        if (StringUtils.isNull(sysTenantEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.TENANT));
        }
        return sysTenantEntity;
    }

    /**
     * 数据copy
     *
     * @param sysTenantEntities
     * @return
     */
    private List<SysTenantVO> tenantBatchCopy(List<SysTenantEntity> sysTenantEntities) {
        if (StringUtils.isEmpty(sysTenantEntities)) {
            return new ArrayList<>();
        }
        return sysTenantEntities.stream().map(tenantEntity -> {
            SysTenantVO vo = new SysTenantVO();
            BeanUtils.copyProperties(tenantEntity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private PageVO<TenantAppVO> fillTenantAppPage(Page<SysTenantAppEntity> page) {
        PageVO<TenantAppVO> pageVO = PageUtils.convertPageVo(page, TenantAppVO.class);
        if (StringUtils.isEmpty(pageVO.getRecords())) {
            return pageVO;
        }
        List<Long> appIds = pageVO.getRecords().stream().map(TenantAppVO::getAppId).collect(Collectors.toList());
        List<SysAppEntity> appList = sysAppDao.listByIds(appIds);
        if (StringUtils.isEmpty(appList)) {
            return pageVO;
        }
        Map<Long, SysAppEntity> appMap = appList.stream()
                .collect(Collectors.toMap(SysAppEntity::getId, app -> app, (left, right) -> left));
        pageVO.getRecords().forEach(record -> fillTenantAppInfo(record, appMap.get(record.getAppId())));
        return pageVO;
    }

    private void fillTenantAppInfo(TenantAppVO record, SysAppEntity appEntity) {
        if (appEntity == null) {
            return;
        }
        record.setName(appEntity.getName());
        record.setIcon(appEntity.getIcon());
        record.setCode(appEntity.getCode());
        record.setBackground(appEntity.getBackground());
        record.setSort(appEntity.getSort());
    }

}
