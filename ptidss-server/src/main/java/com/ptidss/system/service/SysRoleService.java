package com.ptidss.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.SysPermission;
import com.ptidss.system.domain.SysRole;
import com.ptidss.system.domain.SysUser;
import com.ptidss.system.mapper.SysPermissionMapper;
import com.ptidss.system.mapper.SysRoleMapper;
import com.ptidss.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理（DDL 10.2 sys_role + sys_role_permission；固定 7 类角色：评审决议）
 */
@Service
public class SysRoleService {

    /** 固定角色编码白名单（与 DDL CHECK 约束 sys_role_role_code_check 一致） */
    private static final List<String> ROLE_CODES = java.util.Arrays.asList(
            "trader", "analyst", "settlement", "admin", "manager", "compliance", "mobile");

    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysUserMapper sysUserMapper;

    public SysRoleService(SysRoleMapper sysRoleMapper, SysPermissionMapper sysPermissionMapper,
                          SysUserMapper sysUserMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysUserMapper = sysUserMapper;
    }

    public List<SysRole> list(String keyword, String status) {
        LambdaQueryWrapper<SysRole> qw = new LambdaQueryWrapper<>();
        qw.like(StrUtils.isNotBlank(keyword), SysRole::getRoleName, keyword)
                .eq(StrUtils.isNotBlank(status), SysRole::getStatus, status)
                .orderByAsc(SysRole::getId);
        return sysRoleMapper.selectList(qw);
    }

    public SysRole getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new ServiceException("角色不存在");
        }
        return role;
    }

    public void create(SysRole role) {
        checkRoleCode(role);
        Long count = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, role.getRoleCode()));
        if (count != null && count > 0) {
            throw new ServiceException("角色编码已存在：" + role.getRoleCode());
        }
        sysRoleMapper.insert(role);
    }

    public void update(SysRole role) {
        checkRoleCode(role);
        sysRoleMapper.updateById(role);
    }

    /** 固定 7 类角色（DDL CHECK 约束前置校验，返回友好提示） */
    private void checkRoleCode(SysRole role) {
        if (StrUtils.isBlank(role.getRoleCode())) {
            throw new ServiceException("角色编码不能为空");
        }
        if (!ROLE_CODES.contains(role.getRoleCode())) {
            throw new ServiceException("角色编码仅支持固定 7 类：" + String.join("/", ROLE_CODES));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            return;
        }
        if ("admin".equals(role.getRoleCode())) {
            throw new ServiceException("管理员角色不可删除");
        }
        Long used = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .apply("role_ids @> '[" + id + "]'::jsonb"));
        if (used != null && used > 0) {
            throw new ServiceException("角色已分配给用户，不可删除");
        }
        sysRoleMapper.deleteById(id);
        sysRoleMapper.deleteRolePermission(id);
    }

    /** 查询角色已分配权限 ID 列表 */
    public List<Long> permissionsOf(Long roleId) {
        return sysPermissionMapper.selectPermissionIdsByRole(roleId);
    }

    /** 保存角色-权限关联（全量覆盖） */
    @Transactional(rollbackFor = Exception.class)
    public void savePermissions(Long roleId, List<Long> permissionIds) {
        sysRoleMapper.deleteRolePermission(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permId : permissionIds) {
                sysRoleMapper.insertRolePermission(roleId, permId);
            }
        }
    }
}
