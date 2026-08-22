package com.ptidss.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptidss.auth.dto.CurrentUser;
import com.ptidss.auth.dto.LoginResult;
import com.ptidss.common.constant.Constants;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.LoginUser;
import com.ptidss.common.security.TokenService;
import com.ptidss.common.utils.IdUtils;
import com.ptidss.common.utils.ServletUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.SysPermission;
import com.ptidss.system.domain.SysRegion;
import com.ptidss.system.domain.SysRole;
import com.ptidss.system.domain.SysRoleRegion;
import com.ptidss.system.domain.SysUser;
import com.ptidss.system.domain.SysUserRegion;
import com.ptidss.system.mapper.SysPermissionMapper;
import com.ptidss.system.mapper.SysRegionMapper;
import com.ptidss.system.mapper.SysRoleMapper;
import com.ptidss.system.mapper.SysRoleRegionMapper;
import com.ptidss.system.mapper.SysUserMapper;
import com.ptidss.system.mapper.SysUserRegionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录服务（对齐 low-code-dev SysLoginService 流程）：
 * 校验用户/密码/状态 → 组装角色/权限/区域（角色 × 区域双重授权，评审决议⑤）→ 签发令牌
 */
@Slf4j
@Service
public class SysLoginService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRegionMapper sysRegionMapper;
    private final SysUserRegionMapper sysUserRegionMapper;
    private final SysRoleRegionMapper sysRoleRegionMapper;
    private final TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SysLoginService(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper,
                           SysPermissionMapper sysPermissionMapper, SysRegionMapper sysRegionMapper,
                           SysUserRegionMapper sysUserRegionMapper, SysRoleRegionMapper sysRoleRegionMapper,
                           TokenService tokenService) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysRegionMapper = sysRegionMapper;
        this.sysUserRegionMapper = sysUserRegionMapper;
        this.sysRoleRegionMapper = sysRoleRegionMapper;
        this.tokenService = tokenService;
    }

    /**
     * 登录：用户名 + 密码 → LoginResult
     */
    public LoginResult login(String username, String password) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new ServiceException("用户名或密码错误");
        }
        if (!Constants.SUPER_ADMIN.equals(user.getUsername())
                && StrUtils.equalsAny(user.getStatus(), "locked", "disabled")) {
            throw new ServiceException("账号已被锁定或禁用，请联系管理员");
        }
        if (StrUtils.isBlank(user.getPasswordHash()) || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ServiceException("用户名或密码错误");
        }

        // 组装登录用户（角色/权限/区域）
        LoginUser loginUser = buildLoginUser(user);
        String accessToken = tokenService.createToken(loginUser);

        // 更新最后登录时间
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setLastLoginAt(new Date());
        sysUserMapper.updateById(update);

        LoginResult result = new LoginResult();
        result.setAccessToken(accessToken);
        result.setExpiresIn(2 * 60 * 60L); // 与 ptidss.token.expire-minutes 保持一致
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        result.setDisplayName(user.getDisplayName());
        result.setRoles(loginUser.getRoles().stream().sorted().collect(Collectors.toList()));
        result.setPermissions(loginUser.getPermissions().stream().sorted().collect(Collectors.toList()));
        result.setRegions(loginUser.getRegions().stream().sorted().collect(Collectors.toList()));
        result.setCurrentRegion(loginUser.getRegions().isEmpty() ? null : loginUser.getRegions().iterator().next());
        return result;
    }

    /** 登出：删除令牌缓存 */
    public void logout() {
        LoginUser user = tokenService.getLoginUser();
        if (user != null) {
            tokenService.delLoginUser(user.getToken());
        }
    }

    /**
     * 令牌刷新：refreshToken 传当前有效 accessToken（JWT 自校验 + 缓存校验），
     * 剩余不足阈值自动续期后重新签发新令牌；旧令牌宽限期内仍可用（滑动续期语义）
     */
    public LoginResult refresh(String refreshToken) {
        LoginUser loginUser = tokenService.getLoginUser(refreshToken);
        if (loginUser == null) {
            throw new ServiceException("登录状态已失效，请重新登录");
        }
        String accessToken = tokenService.createToken(loginUser);
        LoginResult result = new LoginResult();
        result.setAccessToken(accessToken);
        result.setExpiresIn(2 * 60 * 60L); // 与 ptidss.token.expire-minutes 保持一致
        result.setUserId(loginUser.getUserid());
        result.setUsername(loginUser.getUsername());
        result.setDisplayName(loginUser.getDisplayName());
        result.setRoles(loginUser.getRoles().stream().sorted().collect(Collectors.toList()));
        result.setPermissions(loginUser.getPermissions().stream().sorted().collect(Collectors.toList()));
        result.setRegions(loginUser.getRegions().stream().sorted().collect(Collectors.toList()));
        result.setCurrentRegion(loginUser.getRegionCode());
        return result;
    }

    /** 当前用户信息 */
    public CurrentUser currentUser() {
        LoginUser user = tokenService.getLoginUser();
        if (user == null) {
            return null;
        }
        CurrentUser cu = new CurrentUser();
        cu.setUserId(user.getUserid());
        cu.setUsername(user.getUsername());
        cu.setDisplayName(user.getDisplayName());
        cu.setOrgCode(user.getOrgCode());
        cu.setRoles(user.getRoles().stream().sorted().collect(Collectors.toList()));
        cu.setPermissions(user.getPermissions().stream().sorted().collect(Collectors.toList()));
        cu.setRegions(user.getRegions().stream().sorted().collect(Collectors.toList()));
        cu.setCurrentRegion(user.getRegionCode());
        return cu;
    }

    /**
     * 组装 LoginUser：角色编码 + 权限编码（角色→角色权限→权限）+ 授权区域
     */
    public LoginUser buildLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setDisplayName(user.getDisplayName());
        loginUser.setOrgCode(user.getOrgCode());
        loginUser.setIpaddr(ServletUtils.getClientIp(ServletUtils.getRequest()));

        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        List<SysRole> roleList = user.getRoleIds() == null ? null
                : sysRoleMapper.selectBatchIds(user.getRoleIds());
        if (roleList != null) {
            for (SysRole role : roleList) {
                roles.add(role.getRoleCode());
                // 角色 → 权限
                List<SysPermission> perms = sysPermissionMapper.selectList(
                        new LambdaQueryWrapper<SysPermission>()
                                .eq(SysPermission::getStatus, "active")
                                .apply("EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.permission_id = sys_permission.id AND rp.role_id = {0})", role.getId()));
                for (SysPermission p : perms) {
                    permissions.add(p.getPermCode());
                }
            }
        }
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);

        // 授权区域（角色 × 区域双重授权：有效区域 = 用户授权区域 ∩ 角色授权区域；
        // 角色未配置区域时以用户授权区域为准（兼容存量）；管理员角色不受区域限制）
        Set<String> regions = sysUserRegionMapper.selectList(
                        new LambdaQueryWrapper<SysUserRegion>().eq(SysUserRegion::getUserId, user.getId()))
                .stream().map(SysUserRegion::getRegionCode).collect(Collectors.toSet());
        if (!roles.contains("admin") && !regions.isEmpty() && roleList != null) {
            Set<String> roleRegions = new HashSet<>();
            for (SysRole role : roleList) {
                roleRegions.addAll(sysRoleRegionMapper.selectList(
                                new LambdaQueryWrapper<SysRoleRegion>().eq(SysRoleRegion::getRoleId, role.getId()))
                        .stream().map(SysRoleRegion::getRegionCode).collect(Collectors.toSet()));
            }
            if (!roleRegions.isEmpty()) {
                regions.retainAll(roleRegions);
            }
        }
        loginUser.setRegions(regions);

        // 默认会话区域：首个授权区域（且区域有效）
        if (!regions.isEmpty()) {
            SysRegion region = sysRegionMapper.selectOne(new LambdaQueryWrapper<SysRegion>()
                    .eq(SysRegion::getRegionCode, regions.iterator().next())
                    .eq(SysRegion::getStatus, "enabled"));
            if (region != null) {
                loginUser.setRegionCode(region.getRegionCode());
            }
        }
        return loginUser;
    }

    /** 初始化：DDL 07 约定占位密码由应用首次启动重置（不落明文种子） */
    @PostConstruct
    public void initPlaceholderPasswords() {
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .like(SysUser::getPasswordHash, "PLACEHOLDER"));
        if (users.isEmpty()) {
            return;
        }
        String defaultPwd = defaultPassword;
        for (SysUser u : users) {
            SysUser update = new SysUser();
            update.setId(u.getId());
            update.setPasswordHash(passwordEncoder.encode(defaultPwd));
            sysUserMapper.updateById(update);
            log.info("初始化账号密码：{}（默认密码，请首次登录后修改）", u.getUsername());
        }
    }

    @Value("${ptidss.init.default-password:Ptidss@2026}")
    private String defaultPassword;
}
