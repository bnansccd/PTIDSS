package com.ptidss.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.TokenService;
import com.ptidss.common.utils.SnowflakeIdGenerator;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.SysRole;
import com.ptidss.system.domain.SysUser;
import com.ptidss.system.domain.SysUserRegion;
import com.ptidss.system.mapper.SysRoleMapper;
import com.ptidss.system.mapper.SysUserMapper;
import com.ptidss.system.mapper.SysUserRegionMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理（DDL 10.1 sys_user + sys_user_region 区域授权；评审决议⑤）
 */
@Service
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRegionMapper sysUserRegionMapper;
    private final TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SysUserService(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper,
                          SysUserRegionMapper sysUserRegionMapper, TokenService tokenService) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRegionMapper = sysUserRegionMapper;
        this.tokenService = tokenService;
    }

    public Page<SysUser> page(long pageNum, long pageSize, String keyword, String status) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.and(StrUtils.isNotBlank(keyword), w -> w.like(SysUser::getUsername, keyword)
                        .or().like(SysUser::getDisplayName, keyword))
                .eq(StrUtils.isNotBlank(status), SysUser::getStatus, status)
                .orderByAsc(SysUser::getId);
        return sysUserMapper.selectPage(new Page<>(pageNum, pageSize), qw);
    }

    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(SysUser user, String password, List<String> regions) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()));
        if (count != null && count > 0) {
            throw new ServiceException("用户名已存在：" + user.getUsername());
        }
        user.setId(SnowflakeIdGenerator.nextId());
        user.setPasswordHash(passwordEncoder.encode(StrUtils.isBlank(password) ? "Ptidss@2026" : password));
        if (StrUtils.isBlank(user.getStatus())) {
            user.setStatus("active");
        }
        sysUserMapper.insert(user);
        saveRegions(user.getId(), regions);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(SysUser user, List<String> regions) {
        SysUser exists = sysUserMapper.selectById(user.getId());
        if (exists == null) {
            throw new ServiceException("用户不存在");
        }
        // 角色合法性校验
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            List<SysRole> roles = sysRoleMapper.selectBatchIds(user.getRoleIds());
            if (roles.size() != user.getRoleIds().size()) {
                throw new ServiceException("存在无效的角色");
            }
        }
        sysUserMapper.updateById(user);
        if (regions != null) {
            sysUserRegionMapper.delete(new LambdaQueryWrapper<SysUserRegion>()
                    .eq(SysUserRegion::getUserId, user.getId()));
            saveRegions(user.getId(), regions);
        }
        // 区域/角色授权变更：该用户在线会话立即失效（下次请求重新登录生效）
        tokenService.removeByUserId(user.getId());
    }

    /** 重置密码（管理员） */
    public void resetPassword(Long id, String newPassword) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setPasswordHash(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return;
        }
        if ("admin".equals(user.getUsername())) {
            throw new ServiceException("系统管理员账号不可删除");
        }
        sysUserMapper.deleteById(id);
        sysUserRegionMapper.delete(new LambdaQueryWrapper<SysUserRegion>()
                .eq(SysUserRegion::getUserId, id));
        tokenService.removeByUserId(id);
    }

    public List<String> regionsOf(Long userId) {
        return sysUserRegionMapper.selectList(new LambdaQueryWrapper<SysUserRegion>()
                        .eq(SysUserRegion::getUserId, userId))
                .stream().map(SysUserRegion::getRegionCode).collect(Collectors.toList());
    }

    private void saveRegions(Long userId, List<String> regions) {
        if (regions == null || regions.isEmpty()) {
            return;
        }
        for (String code : regions) {
            SysUserRegion ur = new SysUserRegion();
            ur.setUserId(userId);
            ur.setRegionCode(code);
            sysUserRegionMapper.insert(ur);
        }
    }
}
