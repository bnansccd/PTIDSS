package com.troy.system.service.impl;

import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.DataScopeConstants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.IterateUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.utils.uuid.IdUtils;
import com.troy.system.api.domain.VO.SysMenuVO;
import com.troy.system.dao.*;
import com.troy.system.domain.DTO.SysMenuDTO;
import com.troy.system.domain.DTO.SysMenuQueryDTO;
import com.troy.system.entity.*;
import com.troy.system.service.SysMenuService;
import com.troy.system.service.SysRoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>菜单管理服务实现类</p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Slf4j
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired private SysRoleMenuDao sysRoleMenuDao;
    @Autowired private SysRoleMenuService sysRoleMenuService;
    @Autowired private SysRoleDao sysRoleDao;
    @Autowired private SysMenuDao sysMenuDao;
    @Autowired private SysAppDao sysAppDao;
    @Autowired private SysUserRoleDao sysUserRoleDao;
    @Autowired private RoleAppMenuDao roleAppMenuDao;
    @Autowired private TenantAppDao tenantAppDao;
    @Autowired private TenantMenuDao tenantMenuDao;

    // ==================== 用户菜单查询 ====================

    @Override
    public List<SysMenuVO> findByUserId(Long userId) {
        List<SysMenuEntity> menuEntities = fetchAuthorizedMenuEntities(userId);
        if (StringUtils.isEmpty(menuEntities)) {
            return Collections.emptyList();
        }
        return buildMenuTree(menuEntities);
    }

    @Override
    public List<Long> findMenuIdsByUserId(Long userId) {
        List<SysUserRoleEntity> userRoles = sysUserRoleDao.findByUserId(userId);
        if (StringUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream()
                .map(SysUserRoleEntity::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<RoleAppMenuEntity> roleMenus = roleAppMenuDao.findByRoleIdsIn(new ArrayList<>(roleIds));
        return StringUtils.isEmpty(roleMenus) ? Collections.emptyList()
                : roleMenus.stream().map(RoleAppMenuEntity::getMenuId).collect(Collectors.toList());
    }

    // ==================== 菜单列表查询 ====================

    @Override
    public List<SysMenuVO> getSysMenuList(SysMenuQueryDTO dto) {
        List<SysMenuEntity> entities = sysMenuDao.listAll(dto.getMenuName(), dto.getAppId());
        return convertToMenuVos(entities);
    }

    @Override
    public List<SysMenuVO> findByAppIdOptional(Long appId) {
        List<SysMenuEntity> entities = sysMenuDao.findByAppIdOptional(appId);
        List<SysMenuVO> vos = convertToMenuVos(entities);
        return IterateUtils.getList(vos);
    }

    @Override
    public List<SysMenuVO> getSysMenuTree() {
        List<SysMenuEntity> entities = sysMenuDao.list();
        List<SysMenuVO> vos = convertToMenuVos(entities);
        return IterateUtils.getList(vos);
    }

    @Override
    public List<SysMenuVO> getSysMenuTree(Long appId) {
        List<SysMenuEntity> entities = sysMenuDao.findByAppId(appId);
        List<SysMenuVO> vos = convertToMenuVos(entities);
        return IterateUtils.getList(vos);
    }

    @Override
    public List<SysMenuVO> findByAppId(Long appId) {
        List<SysMenuEntity> entities = sysMenuDao.findByAppId(appId);
        if (StringUtils.isNotEmpty(entities)) {
            entities.removeIf(m -> Objects.equals(DictValueEnums.BUTTON.getCode(), m.getMenuType()));
        }
        return convertToMenuVos(entities);
    }

    @Override
    public List<SysMenuVO> findByTenantId(Long tenantId) {
        List<SysMenuEntity> entities = sysMenuDao.findByTenantId(tenantId);
        return convertToMenuVos(entities);
    }

    // ==================== 菜单增删改 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO insertSysMenu(SysMenuDTO dto) {
        validateMenuCodeUnique(dto.getMenuCode(), null);

        SysMenuEntity entity = new SysMenuEntity();
        BeanUtils.copyProperties(dto, entity);
        populateMenuParams(dto, entity);

        sysMenuDao.save(entity);
        return ResultVO.success();
    }

    @Override
    public SysMenuVO getSysMenuById(Long id) {
        SysMenuEntity entity = sysMenuDao.getById(id);
        return entity != null ? convertToMenuVo(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysMenuById(Long id, SysMenuDTO dto) {
        SysMenuEntity entity = verifyMenuExists(id);
        validateMenuCodeUnique(dto.getMenuCode(), id);

        BeanUtils.copyProperties(dto, entity);
        populateMenuParams(dto, entity);

        sysMenuDao.updateById(entity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteById(List<Long> ids) {
        List<SysMenuEntity> children = sysMenuDao.findChildrenByParentId(ids);
        if (StringUtils.isNotEmpty(children)) {
            return ResultVO.fail(ResultEnum.getMsg(ResultEnum.EXIST_CHILD, ResultConstants.MENU));
        }

        sysMenuDao.removeByIds(ids);
        sysRoleMenuDao.deleteByMenuId(ids);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysMenuStatus(Long id) {
        SysMenuEntity entity = verifyMenuExists(id);
        String newStatus = Objects.equals(DictValueEnums.ON_STATUS.getCode(), entity.getStatus())
                ? DictValueEnums.OFF_STATUS.getCode()
                : DictValueEnums.ON_STATUS.getCode();

        entity.setStatus(newStatus);
        sysMenuDao.updateById(entity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysMenuIsShow(Long id) {
        SysMenuEntity entity = verifyMenuExists(id);
        String newIsShow = Objects.equals(DictValueEnums.TRUE.getCode(), entity.getIsShow())
                ? DictValueEnums.FALSE.getCode()
                : DictValueEnums.TRUE.getCode();

        entity.setIsShow(newIsShow);
        sysMenuDao.updateById(entity);
        return ResultVO.success();
    }

    @Override
    public Integer getCurrentSort(Long parentId) {
        SysMenuEntity maxSortMenu = sysMenuDao.findMaxSort(parentId);
        return (maxSortMenu != null && maxSortMenu.getSort() != null)
                ? maxSortMenu.getSort() + Constants.TEN
                : Constants.ZERO;
    }

    // ==================== 应用绑定 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO bindApp(Long appId, Set<Long> menuIds) {
        SysAppEntity app = verifyAppExists(appId);

        // 清空原应用绑定
        sysMenuDao.updateAppIdIsNullByAppId(appId);

        if (StringUtils.isNotEmpty(menuIds)) {
            List<SysMenuEntity> menus = sysMenuDao.findByIdInOptional(menuIds);
            if (StringUtils.isNotEmpty(menus)) {
                menus.forEach(menu -> {
                    menu.setAppId(app.getId());
                    menu.setAppCode(app.getCode());
                });
                sysMenuDao.updateBatch(menus);
            }
        }
        return ResultVO.success();
    }

    // ==================== 租户菜单树（新逻辑） ====================

    @Override
    public List<SysMenuVO> getNewSysMenuTree() {
        // 1. 获取有效租户应用
        Set<Long> validAppIds = fetchValidTenantAppIds();
        if (validAppIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 获取租户关联菜单
        List<SysMenuEntity> allMenus = fetchTenantMenusByAppIds(validAppIds);
        if (StringUtils.isEmpty(allMenus)) {
            return Collections.emptyList();
        }

        // 3. 权限过滤
        List<SysMenuEntity> filteredMenus = filterMenusByPermission(allMenus);
        if (StringUtils.isEmpty(filteredMenus)) {
            return Collections.emptyList();
        }

        // 4. 转 VO 并构建树
        List<SysMenuVO> menuVos = convertToMenuVos(filteredMenus);

        // ⚠️ 关键：确保这行代码真正构建了树
        menuVos = IterateUtils.getList(menuVos);  // 需确认此方法内部调用了 buildMenuTree 类似逻辑
        Map<Long, List<SysMenuVO>> menusByAppId = groupMenusByAppId(menuVos);

        // 5. 组装应用节点
        return buildAppMenuTree(validAppIds, menusByAppId);
    }

    // ==================== 租户菜单绑定（核心复杂方法） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO bindTenant(Long tenantId, Set<String> menuCodes) {
        if (StringUtils.isEmpty(menuCodes)) {
            return ResultVO.success();
        }

        // 1. 清理租户多余菜单
        removeExtraTenantMenus(tenantId, menuCodes);

        // 2. 处理待绑定菜单
        List<SysMenuEntity> sourceMenus = sysMenuDao.findByMenuCodeIn(menuCodes);
        if (StringUtils.isEmpty(sourceMenus)) {
            return ResultVO.success();
        }

        // 3. 处理应用（创建/复用）
        List<SysAppEntity> appEntities = processTenantApps(tenantId, sourceMenus);

        // 4. 创建租户菜单副本
        List<SysMenuEntity> tenantMenus = createTenantMenuCopies(tenantId, sourceMenus, appEntities);

        // 5. 修复父子关系并授权
        fixTenantMenuRelations(tenantId, tenantMenus, sourceMenus);

        // 6. 清理多余应用
        removeExtraTenantApps(tenantId, tenantMenus);

        return ResultVO.success();
    }

    // ==================== 私有方法 - 查询类 ====================

    /**
     * 获取用户有权限的菜单实体列表
     */
    private List<SysMenuEntity> fetchAuthorizedMenuEntities(Long userId) {
        List<SysUserRoleEntity> userRoles = sysUserRoleDao.findByUserId(userId);
        if (StringUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream()
                .map(SysUserRoleEntity::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SysRoleMenuEntity> roleMenus = sysRoleMenuDao.findByRoleIdIn(new ArrayList<>(roleIds));
        if (StringUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }

        Set<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenuEntity::getMenuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return sysMenuDao.listByIds(new ArrayList<>(menuIds));
    }

    /**
     * 获取有效的租户应用 ID 集合
     */
    private Set<Long> fetchValidTenantAppIds() {
        List<SysTenantAppEntity> tenantApps = tenantAppDao.list();
        if (StringUtils.isEmpty(tenantApps)) {
            return Collections.emptySet();
        }

        // 过滤有效期
        Date now = new Date();
        tenantApps.removeIf(app -> !isValidPeriod(app.getValidStartTime(), app.getValidEndTime(), now));

        return tenantApps.stream()
                .map(SysTenantAppEntity::getAppId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 根据应用 ID 获取租户菜单
     */
    private List<SysMenuEntity> fetchTenantMenusByAppIds(Set<Long> appIds) {
        List<SysTenantMenuEntity> tenantMenus = tenantMenuDao.findByAppIdIn(new ArrayList<>(appIds));
        if (StringUtils.isEmpty(tenantMenus)) {
            return Collections.emptyList();
        }

        Set<Long> menuIds = tenantMenus.stream()
                .map(SysTenantMenuEntity::getMenuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return sysMenuDao.listByIds(new ArrayList<>(menuIds));
    }

    /**
     * 根据权限过滤菜单
     */
    private List<SysMenuEntity> filterMenusByPermission(List<SysMenuEntity> allMenus) {
        Long userId = SecurityContextHolder.getUserId();
        List<SysUserRoleEntity> userRoles = sysUserRoleDao.findByUserId(userId);
        if (StringUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream()
                .map(SysUserRoleEntity::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SysRoleEntity> roles = sysRoleDao.listByIds(new ArrayList<>(roleIds));
        boolean isSuperAdmin = roles.stream()
                .anyMatch(role -> Objects.equals(Constants.ONE_STR, role.getIsSuper()));

        if (isSuperAdmin) {
            return allMenus;
        }

        List<SysRoleMenuEntity> roleMenus = sysRoleMenuDao.findByRoleIdIn(new ArrayList<>(roleIds));
        if (StringUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }

        Set<Long> authorizedMenuIds = roleMenus.stream()
                .map(SysRoleMenuEntity::getMenuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return allMenus.stream()
                .filter(menu -> authorizedMenuIds.contains(menu.getId()))
                .collect(Collectors.toList());
    }

    // ==================== 私有方法 - 转换类 ====================

    /**
     * 批量转换菜单实体为 VO（带应用名称）
     */
    private List<SysMenuVO> convertToMenuVos(List<SysMenuEntity> entities) {
        if (StringUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        List<SysMenuVO> vos = entities.stream()
                .map(this::convertToMenuVo)
                .collect(Collectors.toList());

        // 批量填充应用名称
        fillAppNames(vos);
        return vos;
    }

    /**
     * 单个菜单实体转 VO
     */
    private SysMenuVO convertToMenuVo(SysMenuEntity entity) {
        if (entity == null) {
            return null;
        }
        SysMenuVO vo = new SysMenuVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 批量填充菜单 VO 的应用名称
     */
    private void fillAppNames(List<SysMenuVO> menuVos) {
        Set<Long> appIds = menuVos.stream()
                .map(SysMenuVO::getAppId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (appIds.isEmpty()) {
            return;
        }

        Map<Long, String> appNameMap = sysAppDao.listByIds(new ArrayList<>(appIds)).stream()
                .collect(Collectors.toMap(SysAppEntity::getId, SysAppEntity::getName, (k1, k2) -> k1));

        menuVos.forEach(vo -> {
            if (vo.getAppId() != null) {
                vo.setAppName(appNameMap.get(vo.getAppId()));
            }
        });
    }

    /**
     * 按应用 ID 分组菜单 VO
     */
    private Map<Long, List<SysMenuVO>> groupMenusByAppId(List<SysMenuVO> menuVos) {
        return menuVos.stream()
                .filter(vo -> vo.getAppId() != null)
                .collect(Collectors.groupingBy(SysMenuVO::getAppId));
    }

    // ==================== 私有方法 - 树构建类 ====================

    /**
     * 构建菜单树（递归）
     */
    private List<SysMenuVO> buildMenuTree(List<SysMenuEntity> entities) {
        List<SysMenuVO> vos = convertToMenuVos(entities);
        if (StringUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }

        // 收集当前列表中所有菜单的 ID
        Set<Long> currentMenuIds = vos.stream()
                .map(SysMenuVO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 按 parentId 分组
        Map<Long, List<SysMenuVO>> childrenMap = vos.stream()
                .filter(vo -> vo.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenuVO::getParentId));

        // 识别当前列表中的"临时根节点"
        List<SysMenuVO> tempRoots = vos.stream()
                .filter(vo -> vo.getParentId() == null || !currentMenuIds.contains(vo.getParentId()))
                .collect(Collectors.toList());

        // 向上追溯，补齐完整的祖级链路
        List<SysMenuVO> allVos = traceAncestors(tempRoots, vos, currentMenuIds, childrenMap);

        // 重新识别真正的根节点
        Set<Long> allIds = allVos.stream()
                .map(SysMenuVO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SysMenuVO> trueRoots = allVos.stream()
                .filter(vo -> vo.getParentId() == null || !allIds.contains(vo.getParentId()))
                .sorted(Comparator.comparing(SysMenuVO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        // 递归填充子节点
        trueRoots.forEach(root -> attachChildren(root, childrenMap));
        return trueRoots;
    }

    /**
     * 向上追溯，补齐祖级链路（利用 ancestors 字段批量查询）
     */
    private List<SysMenuVO> traceAncestors(List<SysMenuVO> tempRoots,
                                           List<SysMenuVO> existingVos,
                                           Set<Long> existingIds,
                                           Map<Long, List<SysMenuVO>> childrenMap) {

        // 1. 从临时根节点的 ancestors 中提取所有缺失的祖级 ID
        Set<Long> missingAncestorIds = new HashSet<>();
        for (SysMenuVO tempRoot : tempRoots) {
            if (tempRoot.getParentId() == null || existingIds.contains(tempRoot.getParentId())) {
                continue;
            }
            // parentId 不在当前列表中，需要查祖级
            List<Long> ancestorIds = parseAncestors(tempRoot.getAncestors());
            // 添加所有不在当前列表中的祖级 ID
            for (Long ancestorId : ancestorIds) {
                if (!existingIds.contains(ancestorId)) {
                    missingAncestorIds.add(ancestorId);
                }
            }
            // 也要把直接 parentId 加进去（ancestors 可能不包含自身 parent）
            if (!existingIds.contains(tempRoot.getParentId())) {
                missingAncestorIds.add(tempRoot.getParentId());
            }
        }

        if (missingAncestorIds.isEmpty()) {
            return new ArrayList<>(existingVos);
        }

        // 2. 批量查询缺失的祖级菜单
        List<SysMenuEntity> ancestorEntities = sysMenuDao.listByIds(new ArrayList<>(missingAncestorIds));
        if (StringUtils.isEmpty(ancestorEntities)) {
            return new ArrayList<>(existingVos);
        }

        List<SysMenuVO> ancestorVos = convertToMenuVos(ancestorEntities);
        existingIds.addAll(ancestorVos.stream().map(SysMenuVO::getId).collect(Collectors.toSet()));

        // 3. 补充 childrenMap
        for (SysMenuVO ancestor : ancestorVos) {
            childrenMap.computeIfAbsent(ancestor.getParentId(), k -> new ArrayList<>()).add(ancestor);
        }

        // 4. 合并结果
        List<SysMenuVO> allVos = new ArrayList<>(existingVos);
        allVos.addAll(ancestorVos);

        // 5. 检查是否还有未补齐的祖级（祖级的 ancestors 中可能还有更上层）
        Set<Long> allIdsAfterMerge = new HashSet<>(existingIds);
        List<SysMenuVO> newTempRoots = allVos.stream()
                .filter(vo -> vo.getParentId() != null && !allIdsAfterMerge.contains(vo.getParentId()))
                .collect(Collectors.toList());

        if (!newTempRoots.isEmpty()) {
            return traceAncestors(newTempRoots, allVos, allIdsAfterMerge, childrenMap);
        }

        return allVos;
    }

    /**
     * 解析 ancestors 字符串（格式："1,2,3"）为 Long 列表
     */
    private List<Long> parseAncestors(String ancestors) {
        if (StringUtils.isBlank(ancestors)) {
            return Collections.emptyList();
        }
        return Arrays.stream(ancestors.split(","))
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 递归附加子节点
     */
    private void attachChildren(SysMenuVO parent, Map<Long, List<SysMenuVO>> childrenMap) {
        List<SysMenuVO> children = childrenMap.get(parent.getId());
        if (StringUtils.isEmpty(children)) {
            return;
        }
        // 排序
        children.sort(Comparator.comparing(SysMenuVO::getSort, Comparator.nullsLast(Integer::compareTo)));
        parent.setChildren(children);
        children.forEach(child -> attachChildren(child, childrenMap));
    }

    /**
     * 构建应用 + 菜单树
     */
    private List<SysMenuVO> buildAppMenuTree(Set<Long> appIds, Map<Long, List<SysMenuVO>> menusByAppId) {
        List<SysAppEntity> apps = sysAppDao.listByIds(new ArrayList<>(appIds));
        if (StringUtils.isEmpty(apps)) {
            return Collections.emptyList();
        }

        return apps.stream()
                .filter(app -> Objects.equals(Constants.ONE_STR, app.getStatus()))
                .map(app -> {
                    SysMenuVO appNode = new SysMenuVO();
                    appNode.setId(app.getId());
                    appNode.setMenuType("4");  // 应用节点类型
                    appNode.setMenuName(app.getName());
                    appNode.setSort(app.getSort());
                    appNode.setChildren(menusByAppId.getOrDefault(app.getId(), Collections.emptyList()));
                    return appNode;
                })
                .sorted(Comparator.comparing(SysMenuVO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
    }

    // ==================== 私有方法 - 租户绑定逻辑 ====================

    /**
     * 清理租户多余菜单
     */
    private void removeExtraTenantMenus(Long tenantId, Set<String> keepMenuCodes) {
        List<SysMenuEntity> existingMenus = sysMenuDao.findByTenantId(tenantId);
        if (StringUtils.isEmpty(existingMenus)) {
            return;
        }

        List<Long> toRemoveIds = existingMenus.stream()
                .filter(menu -> !keepMenuCodes.contains(menu.getMenuCode()))
                .map(SysMenuEntity::getId)
                .collect(Collectors.toList());

        if (StringUtils.isNotEmpty(toRemoveIds)) {
            try {
                TenantManager.ignoreTenantCondition();
                sysMenuDao.removeByIds(toRemoveIds);
                sysRoleMenuDao.deleteByMenuId(toRemoveIds);
            } finally {
                TenantManager.restoreTenantCondition();
            }
        }

        // 从待处理集合中移除已存在的菜单
        existingMenus.stream()
                .map(SysMenuEntity::getMenuCode)
                .forEach(keepMenuCodes::remove);
    }

    /**
     * 处理租户应用（创建或复用）
     */
    private List<SysAppEntity> processTenantApps(Long tenantId, List<SysMenuEntity> sourceMenus) {
        Set<String> appCodes = sourceMenus.stream()
                .map(SysMenuEntity::getAppCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        if (appCodes.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询已存在的应用
        List<SysAppEntity> existingApps = sysAppDao.findByTenantIdAndCodeIn(tenantId, new ArrayList<>(appCodes));
        Map<String, SysAppEntity> existingAppMap = existingApps.stream()
                .collect(Collectors.toMap(SysAppEntity::getCode, app -> app));

        // 过滤出需要创建的应用代码
        List<String> toCreateCodes = appCodes.stream()
                .filter(code -> !existingAppMap.containsKey(code))
                .collect(Collectors.toList());

        if (StringUtils.isNotEmpty(toCreateCodes)) {
            List<SysAppEntity> sourceApps = sysAppDao.findByCodeIn(toCreateCodes);
            List<SysAppEntity> newApps = sourceApps.stream().map(source -> {
                SysAppEntity app = new SysAppEntity();
                BeanUtils.copyProperties(source, app);
                app.setId(null);  // 清空 ID 以便插入
                app.setSecret(IdUtils.fastSimpleUUID());
                return app;
            }).collect(Collectors.toList());

            try {
                TenantManager.ignoreTenantCondition();
                sysAppDao.saveBatch(newApps);
            } finally {
                TenantManager.restoreTenantCondition();
            }
            existingApps.addAll(newApps);
        }

        return existingApps;
    }

    /**
     * 创建租户菜单副本
     */
    private List<SysMenuEntity> createTenantMenuCopies(Long tenantId,
                                                       List<SysMenuEntity> sourceMenus,
                                                       List<SysAppEntity> appEntities) {
        Map<String, SysAppEntity> appCodeMap = appEntities.stream()
                .collect(Collectors.toMap(SysAppEntity::getCode, app -> app));

        List<SysMenuEntity> tenantMenus = new ArrayList<>();
        // 临时存储：原父ID -> 新菜单
        Map<Long, SysMenuEntity> tempParentMap = new HashMap<>();

        try {
            TenantManager.ignoreTenantCondition();

            for (SysMenuEntity source : sourceMenus) {
                SysMenuEntity copy = new SysMenuEntity();
                BeanUtils.copyProperties(source, copy);
                copy.setId(null);  // 清空 ID
//                copy.setTenantId(tenantId);

                // 设置应用信息
                if (StringUtils.isNotBlank(source.getAppCode()) && appCodeMap.containsKey(source.getAppCode())) {
                    SysAppEntity app = appCodeMap.get(source.getAppCode());
                    copy.setAppId(app.getId());
                    copy.setAppCode(app.getCode());
                }

                // 暂存父子关系映射
                if (source.getParentId() != null) {
                    tempParentMap.put(source.getParentId(), copy);
                }

                tenantMenus.add(copy);
            }

            sysMenuDao.saveBatch(tenantMenus);

        } finally {
            TenantManager.restoreTenantCondition();
        }

        // 修复父子关系（使用新 ID）
        Map<Long, Long> idMapping = buildIdMapping(sourceMenus, tenantMenus);
        tenantMenus.forEach(menu -> {
            if (menu.getParentId() != null && idMapping.containsKey(menu.getParentId())) {
                menu.setParentId(idMapping.get(menu.getParentId()));
            }
        });

        return tenantMenus;
    }

    /**
     * 构建原菜单 ID -> 新菜单 ID 映射
     */
    private Map<Long, Long> buildIdMapping(List<SysMenuEntity> source, List<SysMenuEntity> target) {
        Map<String, Long> codeToNewId = target.stream()
                .collect(Collectors.toMap(SysMenuEntity::getMenuCode, SysMenuEntity::getId));

        return source.stream()
                .filter(s -> codeToNewId.containsKey(s.getMenuCode()))
                .collect(Collectors.toMap(
                        SysMenuEntity::getId,
                        s -> codeToNewId.get(s.getMenuCode())
                ));
    }

    /**
     * 修复租户菜单的父子关系并授权
     */
    private void fixTenantMenuRelations(Long tenantId,
                                        List<SysMenuEntity> tenantMenus,
                                        List<SysMenuEntity> sourceMenus) {
        // 构建 code -> 新ID 映射
        Map<String, Long> codeToNewId = tenantMenus.stream()
                .collect(Collectors.toMap(SysMenuEntity::getMenuCode, SysMenuEntity::getId));

        // 修复 ancestors 和 parentId
        for (SysMenuEntity menu : tenantMenus) {
            // 修复 parentId
            if (StringUtils.isNotBlank(menu.getParentMenuCode()) && codeToNewId.containsKey(menu.getParentMenuCode())) {
                menu.setParentId(codeToNewId.get(menu.getParentMenuCode()));
            }
            // 修复 ancestors
            if (StringUtils.isNotBlank(menu.getAncestorsCode())) {
                String[] ancestorCodes = StringUtils.split(menu.getAncestorsCode(), ",");
                String newAncestors = Arrays.stream(ancestorCodes)
                        .filter(codeToNewId::containsKey)
                        .map(codeToNewId::get)
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                menu.setAncestors(newAncestors);
            }
        }

        // 批量更新
        try {
            TenantManager.ignoreTenantCondition();
            sysMenuDao.updateBatch(tenantMenus);

            // 授权给租户管理员角色
            SysRoleEntity adminRole = sysRoleDao.findByTenantIdAndRoleCode(tenantId, DataScopeConstants.ADMIN);
            if (adminRole != null) {
                List<Long> menuIds = tenantMenus.stream().map(SysMenuEntity::getId).collect(Collectors.toList());
                sysRoleMenuService.insertRoleMenu(adminRole.getId(), menuIds);
            }
        } finally {
            TenantManager.restoreTenantCondition();
        }
    }

    /**
     * 清理租户多余应用
     */
    private void removeExtraTenantApps(Long tenantId, List<SysMenuEntity> tenantMenus) {
        Set<String> usedAppCodes = tenantMenus.stream()
                .map(SysMenuEntity::getAppCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        if (usedAppCodes.isEmpty()) {
            return;
        }

        List<SysAppEntity> tenantApps = sysAppDao.findByTenantId(tenantId);
        if (StringUtils.isEmpty(tenantApps)) {
            return;
        }

        Set<Long> toRemoveIds = tenantApps.stream()
                .filter(app -> !usedAppCodes.contains(app.getCode()))
                .map(SysAppEntity::getId)
                .collect(Collectors.toSet());

        if (StringUtils.isNotEmpty(toRemoveIds)) {
            try {
                TenantManager.ignoreTenantCondition();
                sysAppDao.removeByIds(toRemoveIds);
            } finally {
                TenantManager.restoreTenantCondition();
            }
        }
    }

    // ==================== 私有方法 - 工具类 ====================

    /**
     * 验证菜单是否存在
     */
    private SysMenuEntity verifyMenuExists(Long id) {
        SysMenuEntity entity = sysMenuDao.getById(id);
        if (entity == null) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.MENU));
        }
        return entity;
    }

    /**
     * 验证应用是否存在
     */
    private SysAppEntity verifyAppExists(Long id) {
        SysAppEntity entity = sysAppDao.getById(id);
        if (entity == null) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.APP));
        }
        return entity;
    }

    /**
     * 校验菜单码唯一性
     */
    private void validateMenuCodeUnique(String menuCode, Long excludeId) {
        SysMenuEntity existing = sysMenuDao.findByMenuCode(menuCode);
        if (existing != null && !Objects.equals(existing.getId(), excludeId)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST, ResultConstants.MENU_CODE));
        }
    }

    /**
     * 填充菜单参数（parentId/ancestors/sort）
     */
    private void populateMenuParams(SysMenuDTO dto, SysMenuEntity entity) {
        // 处理父级信息
        if (dto.getParentId() != null) {
            SysMenuEntity parent = sysMenuDao.getById(dto.getParentId());
            if (parent == null) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.PARENT_NOT_FOUND, ResultConstants.MENU));
            }
            entity.setParentMenuCode(parent.getMenuCode());
            entity.setAncestors(buildAncestors(parent.getAncestors(), parent.getId()));
            entity.setAncestorsCode(buildAncestorsCode(parent.getAncestorsCode(), parent.getMenuCode()));
            entity.setAppId(parent.getAppId());
            entity.setAppCode(parent.getAppCode());
        }

        // 处理排序
        if (entity.getSort() == null) {
            SysMenuEntity maxSort = sysMenuDao.findMaxSort(dto.getParentId());
            entity.setSort(maxSort != null && maxSort.getSort() != null
                    ? maxSort.getSort() + Constants.TEN
                    : Constants.ZERO);
        }
    }

    private String buildAncestors(String parentAncestors, Long parentId) {
        return StringUtils.isBlank(parentAncestors)
                ? String.valueOf(parentId)
                : parentAncestors + "," + parentId;
    }

    private String buildAncestorsCode(String parentAncestorsCode, String parentCode) {
        return StringUtils.isBlank(parentAncestorsCode)
                ? parentCode
                : parentAncestorsCode + "," + parentCode;
    }

    /**
     * 判断日期是否在有效期内
     */
    public boolean isValidPeriod(Date validStartTime, Date validEndTime) {
        return isValidPeriod(validStartTime, validEndTime, new Date());
    }

    private boolean isValidPeriod(Date validStartTime, Date validEndTime, Date compareDate) {
        if (validStartTime == null || validEndTime == null || compareDate == null) {
            return false;
        }
        long time = compareDate.getTime();
        return time >= validStartTime.getTime() && time <= validEndTime.getTime();
    }
}
