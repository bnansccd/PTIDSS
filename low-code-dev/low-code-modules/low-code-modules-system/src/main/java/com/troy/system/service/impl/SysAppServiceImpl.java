package com.troy.system.service.impl;


import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.DateUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.utils.uuid.IdUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.domain.VO.SysAppVO;
import com.troy.system.api.domain.VO.SysMenuVO;
import com.troy.system.api.model.LoginUser;
import com.troy.system.dao.*;
import com.troy.system.domain.DTO.SysAppDTO;
import com.troy.system.domain.DTO.SysAppQueryDTO;
import com.troy.system.entity.SysAppEntity;
import com.troy.system.entity.SysMenuEntity;
import com.troy.system.entity.SysTenantAppEntity;
import com.troy.system.entity.SysTenantMenuEntity;
import com.troy.system.service.SysAppService;
import com.troy.system.service.SysMenuService;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenxl
 * @date 2023/6/19
 */
@Service
public class SysAppServiceImpl implements SysAppService {

    @Autowired
    private SysAppDao sysAppDao;

    @Autowired
    private SysMenuDao sysMenuDao;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private TenantMenuDao tenantMenuDao;

    @Autowired
    private TenantAppDao tenantAppDao;

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 3000)
    public ResultVO addSysApp(SysAppDTO dto) {
        this.validCodeExit(dto.getCode(), null);
        SysAppEntity sysAppEntity = new SysAppEntity();
        BeanUtils.copyProperties(dto, sysAppEntity);
        if (StringUtils.equals(DictValueEnums.EXTERNAL_APP.getCode(), sysAppEntity.getType())) {
            sysAppEntity.setSecret(IdUtils.fastSimpleUUID());
        }
        sysAppDao.save(sysAppEntity);
        // handleAssociationMenu(sysAppEntity);
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO deleteSysApp(List<Long> ids) {
        if (StringUtils.isNotEmpty(ids)) {
            sysAppDao.deletePatch(ids);
            sysMenuDao.removeAPPIdIn(ids);
        }
        return ResultVO.success();
    }

    @Override
    public ResultVO updateSysApp(Long id, SysAppDTO dto) {
        this.validCodeExit(dto.getCode(), id);
        SysAppEntity sysAppEntity = sysAppDao.getById(id);
        if (StringUtils.isNull(sysAppEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.APP));
        }
        dto.setCode(sysAppEntity.getCode());
        BeanUtils.copyProperties(dto, sysAppEntity);
        if (StringUtils.isBlank(sysAppEntity.getSecret()) && StringUtils.equals(DictValueEnums.EXTERNAL_APP.getCode(), sysAppEntity.getType())) {
            sysAppEntity.setSecret(IdUtils.fastSimpleUUID());
        }
        sysAppDao.updateById(sysAppEntity);
        // handleAssociationMenu(sysAppEntity);
        return ResultVO.success();
    }

    @Override
    public PageVO getSysAppPage(SysAppQueryDTO dto) {
        Page<SysAppEntity> page = sysAppDao.getSysAppPage(dto);
        return PageUtils.convertPageVo(page, SysAppVO.class);
    }

    @Override
    public SysAppVO getSysApp(Long id) {
        SysAppEntity sysAppEntity = sysAppDao.getById(id);
        return copyAppVO(sysAppEntity);
    }

    @Transactional
    @Override
    public Boolean updateStatus(List<Long> ids, String status) {
        List<SysAppEntity> list = sysAppDao.listByIds(ids);
        if (StringUtils.isEmpty(list)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.APP));
        }

        list.forEach(e -> e.setStatus(status));
        return sysAppDao.updateBatch(list);
    }

    @Override
    public String reset(Long appId) {
        SysAppEntity dao = sysAppDao.getById(appId);
        if (dao == null) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.APP));
        }

        if (StringUtils.equals(DictValueEnums.INTERNAL_APP.getCode(), dao.getType())) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_SUPPORT_OPERATE,ResultConstants.APP_INTERNAL));
        }

        String string = IdUtils.fastSimpleUUID();
        dao.setSecret(string);
        sysAppDao.updateById(dao);
        return string;
    }

    @Override
    public void handleAssociationMenu(SysAppEntity sysAppEntity) {
        if (StringUtils.isNotNull(sysAppEntity)) {
            List<SysMenuEntity> list = sysMenuDao.findByAppId(sysAppEntity.getId());
            if (StringUtils.isEmpty(list)) {
                SysMenuEntity entity = new SysMenuEntity();
                entity.setMenuName(sysAppEntity.getName());
                entity.setIsBase(String.valueOf(Constants.ONE));
                entity.setSort(sysMenuService.getCurrentSort(null));
                entity.setMenuType(DictValueEnums.APPLY.getCode());
                entity.setIsShow(DictValueEnums.TRUE.getCode());
                entity.setAppId(sysAppEntity.getId());
                entity.setAppCode(sysAppEntity.getCode());
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < sysAppEntity.getName().length(); i++) {
                    char c = sysAppEntity.getName().charAt(i);
                    // 判断是否为汉字
                    if (isChineseCharacter(c)) {
                        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
                        // 获取拼音首字符
                        if (pinyinArray != null && pinyinArray.length > 0) {
                            stringBuilder.append(pinyinArray[0].charAt(0));
                        }
                    } else {
                        if (isEnglish(c)) {
                            stringBuilder.append(c);
                        }
                    }
                }
                entity.setHref("/" + stringBuilder + "_app");
                entity.setMenuCode("sys:" + stringBuilder + "_app");
                sysMenuDao.save(entity);
            } else {
                SysMenuEntity entity = list.get(0);
                if (StringUtils.equals(entity.getMenuName(), sysAppEntity.getName())) {
                    entity.setMenuName(sysAppEntity.getName());
                    sysMenuDao.updateById(entity);
                }
            }
        }
    }

    private static boolean isChineseCharacter(char c) {
        return String.valueOf(c).matches("[\\u4E00-\\u9FA5]+");
    }

    private static boolean isEnglish(char c) {
        return String.valueOf(c).matches("[a-zA-Z]+");
    }

    @Override
    public HashMap<String, String> getKey(Long appId) {
        SysAppEntity id = sysAppDao.getById(appId);
        if (id == null) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.APP));
        }

        Map<String, String> appSecret = new HashMap<>();
        appSecret.put("appId", appId.toString());
        appSecret.put("secret", id.getSecret());
        return new HashMap<>(appSecret);
    }

    @Override
    public List<SysAppVO> currentApp() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || StringUtils.isEmpty(loginUser.getAppIds())) {
            return new ArrayList<>();
        }
        return this.sysAppDao.findByCodeInOrderBySort(loginUser.getAppCodes()).stream()
                .map(this::copyAppVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysAppVO> getSysAppInfo(Long id) {
        // 1. 获取租户关联的应用
        List<SysTenantAppEntity> tenantAppList = tenantAppDao.findTenantId(id);
        if (StringUtils.isEmpty(tenantAppList)) {
            return Collections.emptyList();
        }

        // 2. 过滤有效租户应用 (时间 + 状态)
        Date now = new Date();
        List<SysTenantAppEntity> validTenantAppList = tenantAppList.stream()
                .filter(app -> DateUtils.isBetweenDate(now, app.getValidStartTime(), app.getValidEndTime())
                        && Constants.ONE_STR.equals(app.getStatus()))
                .collect(Collectors.toList());

        if (StringUtils.isEmpty(validTenantAppList)) {
            return Collections.emptyList();
        }

        // 3. 获取应用详情
        List<Long> appIds = validTenantAppList.stream()
                .map(SysTenantAppEntity::getAppId)
                .collect(Collectors.toList());

        List<SysAppEntity> sysAppList = sysAppDao.listByIds(appIds);
        if (StringUtils.isEmpty(sysAppList)) {
            return Collections.emptyList();
        }

        // 4. 过滤有效应用 (状态) -> 修复：后续逻辑应使用过滤后的列表
        List<SysAppEntity> validSysAppList = sysAppList.stream()
                .filter(app -> Constants.ONE_STR.equals(app.getStatus()))
                .collect(Collectors.toList());

        if (StringUtils.isEmpty(validSysAppList)) {
            return Collections.emptyList();
        }

        // 5. 获取租户菜单
        List<SysTenantMenuEntity> tenantMenuList = tenantMenuDao.findByTenantId(id);
        if (StringUtils.isEmpty(tenantMenuList)) {
            return Collections.emptyList();
        }

        // 6. 获取菜单详情
        List<Long> menuIds = tenantMenuList.stream()
                .map(SysTenantMenuEntity::getMenuId)
                .collect(Collectors.toList());

        List<SysMenuEntity> sysMenuList = sysMenuDao.listByIds(menuIds);
        // 如果菜单为空，可能意味着应用下无菜单，视业务需求而定，这里保持返回空列表
        if (StringUtils.isEmpty(sysMenuList)) {
            return Collections.emptyList();
        }

        // 7. 提取菜单关联的应用 ID 集合 (转为 Set 优化性能)
        Set<Long> menuAppIdSet = sysMenuList.stream()
                .map(SysMenuEntity::getAppId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 8. 组装 VO
        // 修复：使用过滤后的 validSysAppList 而不是原始的 sysAppList
        return validSysAppList.stream()
                // 优化：使用 Set.contains 代替 List.contains
                .filter(app -> menuAppIdSet.contains(app.getId()))
                .map(app -> {
                    SysAppVO appVO = new SysAppVO();
                    BeanUtils.copyProperties(app, appVO);

                    // 获取该应用下的菜单
                    List<SysMenuVO> menuVOS = sysMenuList.stream()
                            .filter(menu -> app.getId().equals(menu.getAppId()))
                            .map(menu -> {
                                SysMenuVO menuVO = new SysMenuVO();
                                // 修复：拷贝源对象应为菜单实体 (menu)，而不是应用实体 (app)
                                BeanUtils.copyProperties(menu, menuVO);
                                return menuVO;
                            }).collect(Collectors.toList());

                    // 构建树形结构
                    List<SysMenuVO> menuTree = buildMenuTree(menuVOS);
                    appVO.setSysMenuVOS(menuTree);

                    return appVO;
                }).sorted(Comparator.comparing(SysAppVO::getSort)).collect(Collectors.toList());
    }


    public List<SysMenuVO> buildMenuTree(List<SysMenuVO> allMenus) {
        if (allMenus == null || allMenus.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 使用 Map 存储所有节点，Key 为 ID，方便快速查找
        Map<Long, SysMenuVO> menuMap = allMenus.stream()
                .collect(Collectors.toMap(
                        SysMenuVO::getId,
                        menu -> menu,
                        (existing, replacement) -> replacement  // 冲突时保留新的
                ));

        List<SysMenuVO> rootMenus = new ArrayList<>();

        // 2. 遍历所有节点，组装父子关系
        for (SysMenuVO menu : allMenus) {
            Long parentId = menu.getParentId();

            // 如果 parentId 为 null 或 0（取决于你的业务约定），则是根节点
            if (parentId == null || parentId == 0L || !menuMap.containsKey(parentId)) {
                rootMenus.add(menu);
            } else {
                // 找到父节点，并将当前节点加入到父节点的 children 集合中
                SysMenuVO parent = menuMap.get(parentId);
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(menu);
            }
        }

        // 3. (可选) 对根节点及子节点进行排序
        return sortMenus(rootMenus);
    }

    // 辅助排序方法（如果需要按 sort 字段排序）
    private List<SysMenuVO> sortMenus(List<SysMenuVO> menus) {
        menus.sort(Comparator.comparing(SysMenuVO::getSort, Comparator.nullsLast(Integer::compareTo)));
        for (SysMenuVO menu : menus) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                sortMenus((List<SysMenuVO>) menu.getChildren());
            }
        }
        return menus;
    }

    /**
     * 验证应用编码是否重复
     *
     * @param code
     * @param id
     */
    private void validCodeExit(String code, Long id) {
        SysAppEntity sysAppEntity = this.sysAppDao.findByCode(code);
        if (StringUtils.isNotNull(sysAppEntity)) {
            if (StringUtils.isNull(id)) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST,ResultConstants.APP_CODE));
            } else if (!id.equals(sysAppEntity.getId())) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST,ResultConstants.APP_CODE));
            }
        }
    }

    private SysAppVO copyAppVO(SysAppEntity appEntity) {
        if (StringUtils.isNull(appEntity)) {
            return null;
        }
        SysAppVO vo = new SysAppVO();
        BeanUtils.copyProperties(appEntity, vo);
        return vo;
    }
}
