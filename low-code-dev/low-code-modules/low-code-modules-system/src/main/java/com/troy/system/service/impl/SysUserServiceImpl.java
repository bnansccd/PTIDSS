package com.troy.system.service.impl;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.constant.CacheConstants;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.RegexConstants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.common.datasource.utils.TableAuditUtils;
import com.troy.common.redis.service.RedisService;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.domain.DTO.AuditDTO;
import com.troy.system.api.domain.DTO.RegisterDTO;
import com.troy.system.api.domain.VO.*;
import com.troy.system.api.model.LoginUser;
import com.troy.system.dao.*;
import com.troy.system.domain.DTO.SysTenantInsertDTO;
import com.troy.system.domain.DTO.SysUserDTO;
import com.troy.system.domain.DTO.SysUserPageQueryDTO;
import com.troy.system.entity.*;
import com.troy.system.service.*;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户管理 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Value("${system.password}")
    private String password;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysPostUserDao sysPostUserDao;

    @Autowired
    private SysDepartService sysDepartService;

    @Autowired
    private SysPostService sysPostService;

    @Autowired
    private SysPostUserService sysPostUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysUserThirdAuthDao sysUserThirdAuthDao;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysTenantDao sysTenantDao;

    @Lazy
    @Autowired
    private SysTenantService sysTenantService;

    @Autowired
    private SysAppService sysAppService;

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysAppDao sysAppDao;

    @Override
    public SysUserDetailsVO sysUserByUsernameAndTenantId(String username, Long tenantId) {
        SysUserDetailsVO vo = null;
        try {
            TenantManager.ignoreTenantCondition();
            SysUserEntity sysUserEntity = this.sysUserDao.findByUsernameAndTenantId(username, tenantId);
            vo = this.setUserDetails(sysUserEntity);
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return vo;
    }

    @Transactional
    @Override
    public ResultVO sysUserRegister(RegisterDTO dto) {
        try {
            TenantManager.ignoreTenantCondition();
            SysTenantEntity sysTenant = this.sysTenantDao.getById(dto.getTenantId());
            if (StringUtils.isNull(sysTenant)){
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.TENANT));
            }
            SysUserEntity sysUserEntity = this.sysUserDao.findByUsernameAndTenantId(dto.getUsername(),dto.getTenantId());
            if (StringUtils.isNotNull(sysUserEntity)) {
               throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST,ResultConstants.USERNAME));
            }
            sysUserEntity = new SysUserEntity();
            BeanUtils.copyProperties(dto, sysUserEntity);
            sysUserEntity.setRealName(dto.getUsername());
            sysUserEntity.setPassword(SecurityUtils.encryptPassword(dto.getPassword()));
            this.sysUserDao.save(sysUserEntity);
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return ResultVO.success();
    }

    @Override
    public SysUserDetailsVO getSysUserDetail(Long userId) {
        SysUserEntity sysUserEntity = this.sysUserDao.getById(userId);
        return setUserDetails(sysUserEntity);
    }

    @Override
    public PageVO<SysUserDetailsVO> getSysUserList(SysUserPageQueryDTO dto) {
        Page<SysUserEntity> page = this.sysUserDao.getSysUserPage(dto);
        List<SysUserEntity> sysUserEntities = page.getRecords();
        List<SysUserDetailsVO> vos = new ArrayList<>();
        PageVO pageVO = PageUtils.convertPageVo(page);
        if (StringUtils.isNotEmpty(sysUserEntities)) {
            TableAuditUtils.setAuditInfo(sysUserEntities);
            vos = setUserDetailsBatch(sysUserEntities);
        }
        pageVO.setRecords(vos);
//        SensitiveUtils.handle(vos);
        return pageVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO insertSysUser(SysUserDTO dto) {
        SysUserEntity entity = this.sysUserDao.findByPhoneOrUserName(dto.getPhone(), dto.getUsername());
        verifySysUser(dto, entity);
        entity = new SysUserEntity();
        BeanUtils.copyProperties(dto, entity);

        entity.setPassword(SecurityUtils.encryptPassword(password));
        this.sysUserDao.save(entity);
        sysPostUserService.insertPostUserByUserId(entity.getId(), dto.getPostIds());
        sysUserRoleService.updateUserRoleByUserId(entity.getId(), dto.getRoles());
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysUserById(Long id, SysUserDTO dto) {
        SysUserEntity entity = verifySysUserExist(id);
        SysUserEntity sysUserEntity = this.sysUserDao.findByPhoneOrUserName(id, dto.getPhone(), dto.getUsername());
        this.verifySysUser(dto, sysUserEntity);

        sysPostUserService.insertPostUserByUserId(entity.getId(), dto.getPostIds());
        sysUserRoleService.updateUserRoleByUserId(entity.getId(), dto.getRoles());
        BeanUtils.copyProperties(dto, entity);
        this.sysUserDao.updateById(entity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysUserStatus(Long id) {
        SysUserEntity sysUserEntity = verifySysUserExist(id);
        String status = DictValueEnums.ON_STATUS.getCode().equals(sysUserEntity.getStatus()) ? DictValueEnums.OFF_STATUS.getCode().toString() : DictValueEnums.ON_STATUS.getCode().toString();
        sysUserEntity.setStatus(status);
        this.sysUserDao.updateById(sysUserEntity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysUserStatus(String status, List<Long> ids) {
        List<SysUserEntity> sysUserEntities = this.sysUserDao.listByIds(ids);
        if (StringUtils.isEmpty(sysUserEntities)) {
            return ResultVO.fail(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.USER));
        }
        for (SysUserEntity sysUserEntity : sysUserEntities) {
            sysUserEntity.setStatus(status);
        }
        this.sysUserDao.updateBatch(sysUserEntities);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteSysUserById(List<Long> ids) {
        this.sysUserDao.removeByIds(ids);
        this.sysPostUserDao.deleteByUserId(ids);
        this.sysUserRoleDao.deleteByUserId(ids);
        this.sysUserThirdAuthDao.deleteByUserId(ids);
        return ResultVO.success();
    }

    @Override
    public List<SysUserVO> getListByCondition(String queryParams) {
        List<SysUserEntity> sysUserEntities = sysUserDao.getListByCondition(queryParams);
        List<SysUserVO> vos = getSysUserVOs(sysUserEntities);
        return vos;
    }

    @Override
    public List<SysUserVO> getListByConditionAndTenantId(String queryParams, Long tenantId) {
        SecurityContextHolder.setTenantId(tenantId);

        List<SysUserEntity> sysUserEntities = sysUserDao.getListByCondition(queryParams);
        return getSysUserVOs(sysUserEntities);
    }

    @Override
    public List<SysUserVO> getByIds(List<Long> ids, Long tenantId) {
        SecurityContextHolder.setTenantId(tenantId);
        List<SysUserEntity> entities = sysUserDao.listByIds(ids);
        return getSysUserVOs(entities);
    }

    @Override
    @Transactional
    public ResultVO resetPassword(Long id) {
        SysUserEntity user = verifySysUserExist(id);
        String encryptPassword = SecurityUtils.encryptPassword(password);
        user.setPassword(encryptPassword);
        sysUserDao.updateById(user);
        return ResultVO.success(password);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String old, String newPassword) {
        boolean matches = RegexConstants.matches(newPassword, RegexConstants.PASSWORD_REGX);
        if (!matches){
            throw new ServiceException(ResultEnum.ERROR, ResultConstants.USER_PASSWORD_FORMAT);
        }

        SysUserEntity dao = sysUserDao.getById(SecurityUtils.getUserId());
        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, ResultConstants.USER);
        }

        if (SecurityUtils.matchesPassword(old,dao.getPassword())){
            dao.setPassword(SecurityUtils.encryptPassword(newPassword));
            sysUserDao.updateById(dao);
        } else {
            throw new ServiceException(ResultEnum.ERROR, ResultConstants.USER_PASSWORD);
        }

    }

    @Override
    public SysUserDetailsVO current(Long appId) {
        SysUserDetailsVO vo = null;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)) {
            String userKey = CacheConstants.LOGIN_TOKEN_KEY + loginUser.getToken();
            loginUser = this.redisService.getCacheObject(userKey);
            loginUser.setAppId(appId);
            redisService.setCacheObject(userKey, loginUser, CacheConstants.EXPIRATION, TimeUnit.MINUTES);
             vo = new SysUserDetailsVO();
            vo.setSysUserVO(loginUser.getSysUserVO());
            vo.setSysDepartVO(loginUser.getSysDepartVO());
            vo.setSysPostVOS(loginUser.getSysPostVOS());
            vo.setSysRoleVOS(loginUser.getSysRoleVOS());
            List<SysMenuVO> sysMenuVOS = new ArrayList<>();
            if (StringUtils.isNotNull(appId) && loginUser.getAppIds().contains(appId)) {
                sysMenuVOS = loginUser.getSysMenuVOS().stream()
                        .filter(s -> appId.equals(s.getAppId()))
                        .filter(s -> StringUtils.equals(DictValueEnums.ON_STATUS.getCode(), s.getStatus()))
                        .filter(s -> StringUtils.equals(DictValueEnums.TRUE.getCode(), s.getIsShow()))
                        .collect(Collectors.toList());

            } else {

                List<SysAppEntity> list = sysAppDao.list();
                list.sort(Comparator.comparing(SysAppEntity::getSort));
                Set<Long> appIds = loginUser.getAppIds();

                // 方式1：使用 Stream API
                SysAppEntity minSortApp = list.stream()
                        .filter(app -> appIds.contains(app.getId()))
                        .min(Comparator.comparing(SysAppEntity::getSort))
                        .orElse(null);

                if (minSortApp != null){
                    sysMenuVOS = loginUser.getSysMenuVOS().stream()
                            .filter(s -> minSortApp.getId().equals(s.getAppId()))
                            .filter(s -> StringUtils.equals(DictValueEnums.ON_STATUS.getCode(), s.getStatus()))
                            .filter(s -> StringUtils.equals(DictValueEnums.TRUE.getCode(), s.getIsShow()))
                            .collect(Collectors.toList());
                }
            }
            vo.setSysMenuVOS(sysMenuVOS);
            vo.setDataPermissionsVO(loginUser.getDataPermissionsVO());
        }
        return vo;
    }

    @Override
    public SysUserDetailsVO current(String appCode) {
        SysUserDetailsVO vo = null;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)) {
            String userKey = CacheConstants.LOGIN_TOKEN_KEY + loginUser.getToken();
            loginUser = this.redisService.getCacheObject(userKey);
            loginUser.setAppCode(appCode);
            redisService.setCacheObject(userKey, loginUser, CacheConstants.EXPIRATION, TimeUnit.MINUTES);
            vo = new SysUserDetailsVO();
            vo.setSysUserVO(loginUser.getSysUserVO());
            vo.setSysDepartVO(loginUser.getSysDepartVO());
            vo.setSysPostVOS(loginUser.getSysPostVOS());
            vo.setSysRoleVOS(loginUser.getSysRoleVOS());
            List<SysMenuVO> sysMenuVOS = new ArrayList<>();
            if (StringUtils.isNotNull(appCode) && loginUser.getAppCodes().contains(appCode)) {
                SysAppEntity byCode = sysAppDao.findByCode(appCode);
                if (byCode != null){

                    Long appId = byCode.getId();

                    sysMenuVOS = loginUser.getSysMenuVOS().stream()
                            .filter(s -> appId.equals(s.getAppId()))
                            .filter(s -> StringUtils.equals(DictValueEnums.ON_STATUS.getCode(), s.getStatus()))
                            .filter(s -> StringUtils.equals(DictValueEnums.TRUE.getCode(), s.getIsShow()))
                            .collect(Collectors.toList());
                }
            } else {

                List<SysAppEntity> list = sysAppDao.list();
                list.sort(Comparator.comparing(SysAppEntity::getSort));
                Set<Long> appIds = loginUser.getAppIds();

                // 方式1：使用 Stream API
                SysAppEntity minSortApp = list.stream()
                        .filter(app -> appIds.contains(app.getId()))
                        .min(Comparator.comparing(SysAppEntity::getSort))
                        .orElse(null);

                if (minSortApp != null){
                    sysMenuVOS = loginUser.getSysMenuVOS().stream()
                            .filter(s -> minSortApp.getId().equals(s.getAppId()))
                            .filter(s -> StringUtils.equals(DictValueEnums.ON_STATUS.getCode(), s.getStatus()))
                            .filter(s -> StringUtils.equals(DictValueEnums.TRUE.getCode(), s.getIsShow()))
                            .collect(Collectors.toList());
                }
            }
            vo.setSysMenuVOS(sysMenuVOS);
            vo.setDataPermissionsVO(loginUser.getDataPermissionsVO());
        }
        return vo;
    }

    @Override
    public List<SysUserVO> findByIdIn(List<Long> ids) {
        List<SysUserVO> sysUserVOS = new ArrayList<>();
        if (StringUtils.isNotEmpty(ids)) {
            List<SysUserEntity> sysUserEntities = this.sysUserDao.listByIds(ids);
            sysUserVOS = this.getSysUserVOs(sysUserEntities);
        }
        return sysUserVOS;
    }

    @Override
    public SysUserVO findById(Long id) {
        SysUserVO vo = null;
        if (StringUtils.isNotNull(id)) {
            SysUserEntity sysUserEntity = this.sysUserDao.getById(id);
            vo = getSysUserVO(sysUserEntity);
        }
        return vo;
    }

    @Override
    public AuditVO findAuditInfo(AuditDTO dto) {
        AuditVO vo = new AuditVO();
        List<SysUserVO> sysUserVOS = this.findByIdIn(dto.getUserIds());
        List<SysDepartVO> sysDepartVOS = this.sysDepartService.findById(dto.getDepartIds());
        vo.setSysUserVOS(sysUserVOS);
        vo.setSysDepartVOS(sysDepartVOS);
        return vo;
    }

    @Transactional
    @Override
    public Long tenantInitUser(Long tenantId, SysTenantInsertDTO dto) {
        Long userId = null;
        try {
            TenantManager.ignoreTenantCondition();
            SysUserEntity sysUserEntity = new SysUserEntity();
            sysUserEntity.setPassword(SecurityUtils.encryptPassword(this.password));
            sysUserEntity.setPhone(dto.getPhone());
            sysUserEntity.setUsername(dto.getUsername());
            sysUserEntity.setRealName(dto.getRealName());
            sysUserEntity.setStatus(DictValueEnums.ON_STATUS.getCode());
            sysUserEntity.setTenantId(tenantId);
            this.sysUserDao.save(sysUserEntity);
            userId = sysUserEntity.getId();
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return userId;
    }

    @Override
    public List<SysUserVO> byDepartIdsAndUsername(List<Long> departIds, String name) {
        List<SysUserEntity> list = sysUserDao.findByDepartIdsAndUsername(departIds, name);
        return copyUserVOList(list);
    }

    @Override
    public List<SysUserVO> byDepartIdsAndRealName(List<Long> departIds, String name) {
        List<SysUserEntity> list = sysUserDao.findByDepartIdsAndRealName(departIds, name);
        return copyUserVOList(list);
    }

    @Override
    public List<SysUserVO> findByOwnerDepart() {
        List<SysUserEntity> depart = sysUserDao.findByOwnDepart();
        if (StringUtils.isNotEmpty(depart)){
            depart.removeIf(e->e.getId().equals(SecurityUtils.getUserId()));

            return depart.stream().map(e->{
                SysUserVO vo = new SysUserVO();
                BeanUtils.copyProperties(e, vo);

                char c = e.getRealName().charAt(0);
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
                // 获取拼音首字符
                if (pinyinArray != null && pinyinArray.length > 0) {
                    vo.setFirst(Character.toUpperCase(pinyinArray[0].charAt(0)));
                } else {
                    if (c != ' '){
                        vo.setFirst(Character.toUpperCase(c));
                    }
                }
                return vo;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public SysUserVO getByUserName(String userName) {
        SysUserEntity byUsername = sysUserDao.findByUsername(userName);
        SysUserVO sysUserVO = new SysUserVO();
        BeanUtils.copyProperties(byUsername,sysUserVO);
        return sysUserVO;
    }

    @Override
    public List<SysUserVO> getAll() {
        List<SysUserEntity> list = sysUserDao.list();
        return copyUserVOList(list);
    }

    @Override
    public List<SysUserVO> getByRealNameIn(List<String> names) {
        List<SysUserEntity> sysUserEntities = sysUserDao.getByRealNameIn(names);
        return copyUserVOList(sysUserEntities);
    }

    @Override
    public SysUserDetailsVO sysUserByPhoneAndTenantId(String phone, Long tenantId) {
        SysUserDetailsVO vo = null;
        try {
            TenantManager.ignoreTenantCondition();
            SysUserEntity sysUserEntity = this.sysUserDao.sysUserByPhoneAndTenantId(phone, tenantId);
            vo = this.setUserDetails(sysUserEntity);
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return vo;
    }

    /**
     * 给一批用户设置详情
     *
     * @param sysUserEntities
     * @return
     */
    private List<SysUserDetailsVO> setUserDetailsBatch(List<SysUserEntity> sysUserEntities) {
        List<SysUserDetailsVO> vos = new ArrayList<>();

        List<Long> userIds = new ArrayList<>();
        List<Long> departIds = new ArrayList<>();
        for (SysUserEntity sysUserEntity : sysUserEntities) {
            userIds.add(sysUserEntity.getId());
            departIds.add(sysUserEntity.getDepartId());
        }

        //岗位
        List<SysPostVO> sysPostVOS = this.sysPostService.findByUserIds(userIds);
        //角色
        List<SysRoleVO> sysRoleVOS = this.sysRoleService.findByUserIdIn(userIds);
        //部门
        List<SysDepartVO> sysDepartVOS = this.sysDepartService.findById(departIds);
        Map<Long, SysDepartVO> departMap = sysDepartVOS.stream()
                .collect(Collectors.toMap(SysDepartVO::getId, depart -> depart, (left, right) -> left));
        Map<Long, List<SysPostVO>> postMap = buildPostMapByUserId(sysPostVOS);
        Map<Long, List<SysRoleVO>> roleMap = buildRoleMapByUserId(sysRoleVOS);
        //数据copy
        SysUserDetailsVO vo = null;
        SysUserVO sysUserVO = null;
        for (SysUserEntity sysUserEntity : sysUserEntities) {
            vo = new SysUserDetailsVO();
            //copy用户属性
            sysUserVO = new SysUserVO();
            BeanUtils.copyProperties(sysUserEntity, sysUserVO);
            sysUserVO.setPassword(null);
            vo.setSysUserVO(sysUserVO);
            //设置部门属性
            vo.setSysDepartVO(departMap.get(sysUserEntity.getDepartId()));
            //设置岗位属性
            vo.setSysPostVOS(postMap.getOrDefault(sysUserEntity.getId(), Collections.emptyList()));
            //设置角色属性
            vo.setSysRoleVOS(roleMap.getOrDefault(sysUserEntity.getId(), Collections.emptyList()));
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 给单个用户设置详情
     *
     * @param sysUserEntity
     * @return
     */
    private SysUserDetailsVO setUserDetails(SysUserEntity sysUserEntity) {
        SysUserDetailsVO vo = null;
        if (StringUtils.isNotNull(sysUserEntity)) {
            vo = new SysUserDetailsVO();
            SysUserVO sysUserVO = new SysUserVO();
            BeanUtils.copyProperties(sysUserEntity, sysUserVO);
            vo.setSysUserVO(sysUserVO);
            Long id = sysUserEntity.getId();
            //查询部门
            SysDepartVO sysDepartVO = this.sysDepartService.findById(sysUserEntity.getDepartId());
            vo.setSysDepartVO(sysDepartVO);
            //查询岗位
            List<SysPostVO> sysPostVOS = this.sysPostService.findByUserId(id);
            vo.setSysPostVOS(sysPostVOS);
            //查询角色
            List<SysRoleVO> sysRoleVOS = this.sysRoleService.findByUserId(id);
            vo.setSysRoleVOS(sysRoleVOS);
            //查询菜单权限
            List<SysMenuVO> sysMenuVOS = this.sysMenuService.findByUserId(id);
            vo.setSysMenuVOS(sysMenuVOS);
            //设置租户信息
            SysTenantVO sysTenantVO = this.sysTenantService.findById(sysUserEntity.getTenantId());
            vo.setSysTenantVO(sysTenantVO);

            List<SysAppVO> sysAppInfo = sysAppService.getSysAppInfo(sysUserEntity.getTenantId());
            if (StringUtils.isNotEmpty(sysAppInfo)){

                List<SysUserRoleEntity> dao = sysUserRoleDao.findByUserId(sysUserEntity.getId());
                if (StringUtils.isEmpty(dao)){
                    return vo;
                }

                List<Long> roleIds = dao.stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toList());
                List<SysRoleEntity> roleEntities = sysRoleDao.listByIds(roleIds);

                boolean match = roleEntities.stream().anyMatch(e -> Constants.ONE_STR.equals(e.getIsSuper()));
                if (!match){
                    List<SysRoleMenuEntity> roleIdAndMenuIds = sysRoleMenuDao.findByRoleIdIn(roleIds);
                    List<Long> menuIds = roleIdAndMenuIds.stream().map(SysRoleMenuEntity::getMenuId).distinct().collect(Collectors.toList());

                    List<SysAppVO> filteredSysAppInfo = new ArrayList<>();
                    for (SysAppVO appVO : sysAppInfo) {
                        List<SysMenuVO> vos = appVO.getSysMenuVOS();
                        List<SysMenuVO> menuVOS = pruneTree(vos, menuIds);
                        if (StringUtils.isNotEmpty(menuVOS)) {
                            appVO.setSysMenuVOS(menuVOS);
                            filteredSysAppInfo.add(appVO);
                        }
                    }
                    sysAppInfo = filteredSysAppInfo;

                } else {
                    List<SysMenuVO> list = new ArrayList<>();
                    for (SysAppVO appVO : sysAppInfo) {
                        List<SysMenuVO> vos = appVO.getSysMenuVOS();
                        list.addAll(vos);
                    }
                    vo.setSysMenuVOS(list);
                }
            }
            vo.setSysAppVOS(sysAppInfo);

            //查询数据权限
            List<String> dataRanges = sysRoleVOS.stream().map(SysRoleVO::getDataRange).filter(StringUtils::isNotNull).collect(Collectors.toList());
            DataPermissionsVO dataPermissionsVO = new DataPermissionsVO();
            if (StringUtils.isNotEmpty(dataRanges)) {
                for (String dataRange : dataRanges) {
                    if (StringUtils.equals(DictValueEnums.DATA_SCOPE_ALL.getCode(), dataRange)) {
                        dataPermissionsVO = null;
                        break;
                    } else if (StringUtils.equals(DictValueEnums.DATA_SCOPE_CUSTOM.getCode(), dataRange)) {
                        dataPermissionsVO.getDepartIds().addAll(
                                this.sysRoleService.findDataRangeByUserId(id)
                        );
                    } else if (StringUtils.equals(DictValueEnums.DATA_SCOPE_DEPT.getCode(), dataRange)) {
                        dataPermissionsVO.getDepartIds().add(sysUserEntity.getDepartId());
                    } else if (StringUtils.equals(DictValueEnums.DATA_SCOPE_DEPT_AND_CHILD.getCode(), dataRange)) {
                        dataPermissionsVO.getDepartIds().addAll(
                                this.sysDepartService.findDepartAndChildById(sysUserEntity.getDepartId())
                        );
                    } else if (StringUtils.equals(DictValueEnums.DATA_SCOPE_SELF.getCode(), dataRange)) {
                        dataPermissionsVO.setUserId(id);
                    }
                }
            } else {
                dataPermissionsVO.setUserId(id);
            }
            vo.setDataPermissionsVO(dataPermissionsVO);
        }
        return vo;
    }

    public List<SysMenuVO> pruneTree(List<SysMenuVO> treeNodes, List<Long> ids) {
        if (treeNodes == null) return new ArrayList<>();

        // 使用 Iterator 安全删除列表元素
        Iterator<SysMenuVO> iterator = treeNodes.iterator();
        while (iterator.hasNext()) {
            SysMenuVO node = iterator.next();

            // 1. 递归处理子节点
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                pruneTree((List<SysMenuVO>) node.getChildren(), ids);
            }

            // 2. 检查当前节点是否保留
            // 逻辑：如果当前 ID 不在 ids 中，则剔除
            if (!ids.contains(node.getId())) {
                iterator.remove();
            }
        }
        return treeNodes;
    }

    /**
     * 获取一批用户基础信息
     *
     * @param
     * @return
     */
    private Map<Long, List<SysPostVO>> buildPostMapByUserId(List<SysPostVO> posts) {
        Map<Long, List<SysPostVO>> postMap = new HashMap<>();
        if (StringUtils.isEmpty(posts)) {
            return postMap;
        }
        for (SysPostVO post : posts) {
            if (StringUtils.isEmpty(post.getUserIds())) {
                continue;
            }
            for (Long userId : post.getUserIds()) {
                postMap.computeIfAbsent(userId, key -> new ArrayList<>()).add(post);
            }
        }
        return postMap;
    }

    private Map<Long, List<SysRoleVO>> buildRoleMapByUserId(List<SysRoleVO> roles) {
        Map<Long, List<SysRoleVO>> roleMap = new HashMap<>();
        if (StringUtils.isEmpty(roles)) {
            return roleMap;
        }
        for (SysRoleVO role : roles) {
            if (StringUtils.isEmpty(role.getUserIds())) {
                continue;
            }
            for (Long userId : role.getUserIds()) {
                roleMap.computeIfAbsent(userId, key -> new ArrayList<>()).add(role);
            }
        }
        return roleMap;
    }

    private List<SysUserVO> copyUserVOList(List<SysUserEntity> userEntities) {
        if (StringUtils.isEmpty(userEntities)) {
            return new ArrayList<>();
        }
        return userEntities.stream().map(userEntity -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(userEntity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private List<SysUserVO> getSysUserVOs(List<SysUserEntity> sysUserEntities) {
        List<SysUserVO> vos = new ArrayList<>();
        if (StringUtils.isNotEmpty(sysUserEntities)) {
            SysUserVO vo = null;
            for (SysUserEntity sysUserEntity : sysUserEntities) {
                vo = new SysUserVO();
                BeanUtils.copyProperties(sysUserEntity, vo);
                vo.setPassword(null);
                vos.add(vo);
            }
        }
        return vos;
    }

    /**
     * 获取用户基础信息
     *
     * @param sysUserEntity
     * @return
     */
    private SysUserVO getSysUserVO(SysUserEntity sysUserEntity) {
        SysUserVO vo = new SysUserVO();
        if (StringUtils.isNotNull(sysUserEntity)) {
            BeanUtils.copyProperties(sysUserEntity, vo);
            vo.setPassword(null);
        }
        return vo;
    }

    /**
     * 添加修改时数据验证
     *
     * @param dto
     * @param entity
     */
    private void verifySysUser(SysUserDTO dto, SysUserEntity entity) {
        if (StringUtils.isNotNull(entity)) {
            if (StringUtils.equals(entity.getUsername(), dto.getUsername())) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST,ResultConstants.USERNAME));
            }
            if (StringUtils.equals(entity.getPhone(), dto.getPhone())) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST,ResultConstants.USER_PHONE));
            }
        }
    }

    /**
     * 验证用户是否存在
     *
     * @param id
     * @return
     */
    private SysUserEntity verifySysUserExist(Long id) {
        SysUserEntity sysUserEntity = this.sysUserDao.getById(id);
        if (StringUtils.isNull(sysUserEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.USER));
        }
        return sysUserEntity;
    }
}
