package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.common.datasource.strategy.DeviceRegisterFactory;
import com.troy.system.dao.SysUserDao;
import com.troy.system.domain.DTO.SysUserPageQueryDTO;
import com.troy.system.entity.SysUserEntity;
import com.troy.system.entity.table.SysUserEntityTableDef;
import com.troy.system.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysUserEntityTableDef.SYS_USER_ENTITY;


/**
 * <p>
 * 用户管理 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Component
public class SysUserDaoImpl extends BaseServiceImpl<SysUserMapper, SysUserEntity> implements SysUserDao {

    @Override
    public SysUserEntity findByUsername(String username) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SysUserEntity::getUsername).eq(username, StringUtils.isNotBlank(username))
        );
    }

    @Override
    public SysUserEntity findByPhoneOrUserName(String phone, String userName) {
        return super.getOne(
                QueryWrapper.create()
                        .where(
                                SysUserEntityTableDef.SYS_USER_ENTITY.PHONE.eq(DeviceRegisterFactory.encryptData(phone), StringUtils.isNotBlank(phone))
                                        .or(SysUserEntityTableDef.SYS_USER_ENTITY.USERNAME.eq(userName, StringUtils.isNotBlank(userName)))
                        )
        );
    }

    @Override
    public SysUserEntity findByPhoneOrUserName(Long id, String phone, String userName) {
        return super.getOne(
                QueryWrapper.create()
                        .where(
                                SysUserEntityTableDef.SYS_USER_ENTITY.PHONE.eq(DeviceRegisterFactory.encryptData(phone), StringUtils.isNotBlank(phone))
                                        .or(SysUserEntityTableDef.SYS_USER_ENTITY.USERNAME.eq(userName, StringUtils.isNotBlank(userName)))
                        )
                        .and(SysUserEntityTableDef.SYS_USER_ENTITY.ID.ne(id, StringUtils.isNotNull(id)))
        );
    }

    @Override
    public Page<SysUserEntity> getSysUserPage(SysUserPageQueryDTO dto) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SysUserEntityTableDef.SYS_USER_ENTITY.PHONE.eq(DeviceRegisterFactory.encryptData(dto.getPhone()), StringUtils.isNotBlank(dto.getPhone())))
                .and(SysUserEntityTableDef.SYS_USER_ENTITY.REAL_NAME.like(StringUtils.escape(dto.getRealName()), StringUtils.isNotBlank(dto.getRealName())))
                .and(SysUserEntityTableDef.SYS_USER_ENTITY.USERNAME.like(StringUtils.escape(dto.getUsername()), StringUtils.isNotBlank(dto.getUsername())))
                .and(SysUserEntityTableDef.SYS_USER_ENTITY.DEPART_ID.eq(dto.getDepartId(), StringUtils.isNotNull(dto.getDepartId())));
        return super.page(dto, queryWrapper);

    }

    @Override
    public List<SysUserEntity> getListByCondition(String queryParams) {
        return super.dataAuthorityList(
                QueryWrapper.create()
                        .where(SysUserEntityTableDef.SYS_USER_ENTITY.USERNAME.like(StringUtils.escape(queryParams), StringUtils.isNotBlank(queryParams))
                                .or(SysUserEntityTableDef.SYS_USER_ENTITY.REAL_NAME.like(StringUtils.escape(queryParams), StringUtils.isNotBlank(queryParams)))
                                .or(SysUserEntityTableDef.SYS_USER_ENTITY.PHONE.eq(DeviceRegisterFactory.encryptData(queryParams), StringUtils.isNotBlank(queryParams)))
                        )
        );
    }

    @Override
    public SysUserEntity findByUsernameAndTenantId(String username, Long tenantId) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SysUserEntityTableDef.SYS_USER_ENTITY.USERNAME.eq(username))
                        .and(SysUserEntityTableDef.SYS_USER_ENTITY.TENANT_ID.eq(tenantId))
        );
    }

    @Override
    public List<SysUserEntity> findByDepartIdsAndUsername(List<Long> ids, String name) {
        return list(QueryWrapper.create().where(SysUserEntityTableDef.SYS_USER_ENTITY.DEPART_ID.in(ids, StringUtils::isNotEmpty)).and(SysUserEntityTableDef.SYS_USER_ENTITY.USERNAME.like(StringUtils.escape(name))));
    }

    @Override
    public List<SysUserEntity> findByOwnDepart() {
        return list(QueryWrapper.create().where(SysUserEntityTableDef.SYS_USER_ENTITY.DEPART_ID.eq(SecurityContextHolder.getDepartId())));
    }

    @Override
    public List<SysUserEntity> getByTenantId(Long tenantId) {
        return list(
                QueryWrapper.create()
                        .where(SysUserEntityTableDef.SYS_USER_ENTITY.TENANT_ID.eq(tenantId))
        );
    }

    @Override
    public List<SysUserEntity> findByDepartIdsAndRealName(List<Long> ids, String name) {
        return list(QueryWrapper.create().where(SysUserEntityTableDef.SYS_USER_ENTITY.DEPART_ID.in(ids, StringUtils::isNotEmpty)).and(SysUserEntityTableDef.SYS_USER_ENTITY.REAL_NAME.like(StringUtils.escape(name))));
    }

    @Override
    public SysUserEntity findByPhone(String phone) {
        return super.getOne(
                QueryWrapper.create()
                       .where(SysUserEntityTableDef.SYS_USER_ENTITY.PHONE.eq(DeviceRegisterFactory.encryptData(phone)))
        );
    }

    @Override
    public List<SysUserEntity> getByRealNameIn(List<String> names) {
        return super.list(
                QueryWrapper.create()
                        .where(SysUserEntityTableDef.SYS_USER_ENTITY.REAL_NAME.in(names))
        );
    }

    @Override
    public SysUserEntity sysUserByPhoneAndTenantId(String phone, Long tenantId) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SysUserEntityTableDef.SYS_USER_ENTITY.PHONE.eq(DeviceRegisterFactory.encryptData(phone)))
                        .and(SysUserEntityTableDef.SYS_USER_ENTITY.TENANT_ID.eq(tenantId))
        );
    }
}
