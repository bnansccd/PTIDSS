package com.troy.auth.service;

import com.alibaba.fastjson2.JSON;
import com.troy.system.api.domain.VO.*;
import com.troy.common.core.constant.CacheConstants;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.UserConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.enums.UserStatus;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.DateUtils;
import com.troy.common.core.utils.ServletUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.ip.IpUtils;
import com.troy.common.redis.service.RedisService;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.RemoteLogService;
import com.troy.system.api.RemoteSysDomainNameService;
import com.troy.system.api.RemoteSysUserService;
import com.troy.system.api.domain.DTO.RegisterDTO;
import com.troy.system.api.domain.DTO.SysLogininforDTO;
import com.troy.system.api.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/2 17:17:33
 * @Description: 登录校验方法
 * @Version: 1.0.0
 */
@Slf4j
@Component
public class SysLoginService {

    @Autowired
    private RemoteSysUserService remoteSysUserService;

    @Autowired
    private RemoteLogService remoteLogService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteSysDomainNameService remoteSysDomainNameService;


    /**
     * 登录
     */
    public LoginUser login(String username, String phone, String password, String domainName, boolean isCheck) {
        judgeTime(username);
        ResultVO<SysDomainNameVO> resultVO = this.remoteSysDomainNameService.findByDomainNameOrUniversalDomainName(domainName, SecurityConstants.INNER);
        if (!ResultVO.isSuccess(resultVO)) {
            throw new ServiceException(ResultEnum.ILLEGAL_LINK);
        }
        SysDomainNameVO vo = resultVO.getData();
        if (StringUtils.isNull(vo)) {
            throw new ServiceException(ResultEnum.ILLEGAL_LINK);
        }
        Long tenantId = vo.getTenantId();
        if (StringUtils.isNull(tenantId)) {
            throw new ServiceException(ResultEnum.ILLEGAL_LINK);
        }


        if (isCheck) {
            // 用户名或密码为空 错误
            if (StringUtils.isAnyBlank(username, password)) {
                recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.USERNAME_PASSWORD).getMsg());
                addLock(username);
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.USERNAME_PASSWORD)));
            }
            // 密码如果不在指定范围内 错误
            if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                    || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
                recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.NO_RANGE, ResultConstants.USERNAME_PASSWORD).getMsg());
                addLock(username);
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.NO_RANGE, ResultConstants.USERNAME_PASSWORD));
            }
        }

        if (StringUtils.isBlank(username) && StringUtils.isBlank(phone)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.USERNAME_PASSWORD));
        }

        if (isCheck && StringUtils.isNotBlank(username)) {
            // 用户名不在指定范围内 错误
            if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                    || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
                recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.NO_RANGE, ResultConstants.USERNAME).getMsg());
                addLock(username);
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.NO_RANGE, ResultConstants.USERNAME));
            }
        }


        LoginUser userInfo = new LoginUser();
        SysUserVO sysUserVO = getSysUserVO(username, phone, tenantId, userInfo);
        if (StringUtils.isBlank(username)) {
            username = sysUserVO.getUsername();
        }
        if (isCheck && !SecurityUtils.matchesPassword(password, sysUserVO.getPassword())) {
            recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.ERROR, ResultConstants.USERNAME_PASSWORD).getMsg());
            addLock(username);
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.ERROR, ResultConstants.USERNAME_PASSWORD));
        }
        recordLogininfor(username, tenantId, Constants.LOGIN_SUCCESS, ResultEnum.getMsg(ResultEnum.OPERATE_SUCCESS, ResultConstants.LOGIN).getMsg(), sysUserVO.getId());
        return userInfo;
    }

    public void logout(String loginName, Long tenantId) {
        recordLogininfor(loginName, tenantId, Constants.LOGOUT, ResultEnum.getMsg(ResultEnum.OPERATE_SUCCESS, ResultConstants.LOGIN_OUT).getMsg());
    }

    private void addLock(String username) {
        redisService.getRedisTemplate().opsForValue().increment(CacheConstants.LOGIN_LOCK_KEY + username);
        redisService.getRedisTemplate().expire(CacheConstants.LOGIN_LOCK_KEY + username, 12, TimeUnit.HOURS);
    }

    private void judgeTime(String username) {
        Integer tm = (Integer) redisService.getRedisTemplate().opsForValue().get(CacheConstants.LOGIN_LOCK_KEY + username);
        if (tm != null && tm >= CacheConstants.LOCK_TIMES) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.BE_CURRENT, ResultConstants.LOGIN_EXCEED_MAX_NUM));
        }
    }


    /**
     * 注册
     */
    public void register(RegisterDTO dto) {
        // 注册用户信息
        ResultVO resultVo = remoteSysUserService.sysUserRegister(dto, SecurityConstants.INNER);

        if (ResultVO.FAIL == resultVo.getCode()) {
            throw new ServiceException(resultVo.getMsg());
        }
        recordLogininfor(dto.getUsername(), dto.getTenantId(), Constants.REGISTER, ResultEnum.getMsg(ResultEnum.OPERATE_SUCCESS, ResultConstants.REGISTER).getMsg());
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String username, Long tenantId, String status, String message) {
        SysLogininforDTO dto = new SysLogininforDTO();
        dto.setUsername(username);
        dto.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        dto.setMsg(message);
        dto.setAccessTime(new Date());
        dto.setTenantId(tenantId);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            dto.setStatus(Constants.LOGIN_SUCCESS_STATUS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            dto.setStatus(Constants.LOGIN_FAIL_STATUS);
        }
        log.info("登录日志记录：{}", JSON.toJSONString(dto));
        remoteLogService.saveLogininfor(dto, SecurityConstants.INNER);

    }

    public void recordLogininfor(String username, Long tenantId, String status, String message, Long userId) {
        SysLogininforDTO dto = new SysLogininforDTO();
        dto.setUsername(username);
        dto.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        dto.setMsg(message);
        dto.setAccessTime(new Date());
        dto.setTenantId(tenantId);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            dto.setStatus(Constants.LOGIN_SUCCESS_STATUS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            dto.setStatus(Constants.LOGIN_FAIL_STATUS);
        }

        dto.setUserId(userId);
        remoteLogService.saveLogininfor(dto, SecurityConstants.INNER);
    }

    /**
     * 刷新用户登录信息
     *
     * @return
     */
    public ResultVO refreshUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        this.getSysUserVO(loginUser.getUsername(), null, loginUser.getTenantId(), loginUser);
        String userKey = CacheConstants.LOGIN_TOKEN_KEY + loginUser.getToken();
        this.redisService.setCacheObject(userKey, loginUser, CacheConstants.EXPIRATION, TimeUnit.MINUTES);
        return ResultVO.success();
    }


    /**
     * 查询用户信息
     *
     * @param username
     * @param userInfo
     * @return
     */
    private SysUserVO getSysUserVO(String username, String phone, Long tenantId, LoginUser userInfo) {


        ResultVO<SysUserDetailsVO> resultVo = null;
        if (StringUtils.isNotBlank(phone)) {
            resultVo = this.remoteSysUserService.sysUserByPhoneAndTenantId(phone, tenantId, SecurityConstants.INNER);

        }else if (StringUtils.isNotBlank(username)) {
            // 查询用户信息
            resultVo = remoteSysUserService.sysUserByUsernameAndTenantId(username, tenantId, SecurityConstants.INNER);
        }else {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.USERNAME_PASSWORD));
        }

        if (ResultVO.FAIL == resultVo.getCode()) {
            throw new ServiceException(resultVo.getMsg());
        }

        if (StringUtils.isNull(resultVo.getData())) {
            recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.NOT_FOUND, username).getMsg());
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, username));
        }
        SysUserDetailsVO sysUserDetailsVO = resultVo.getData();
        SysUserVO sysUserVO = sysUserDetailsVO.getSysUserVO();
        userInfo.setPhone(sysUserVO.getPhone());
        userInfo.setTenantId(sysUserVO.getTenantId());
        userInfo.setSysUserVO(sysUserVO);

        SysTenantVO sysTenantVO = sysUserDetailsVO.getSysTenantVO();
        if (StringUtils.isNotNull(sysTenantVO)) {
            if (StringUtils.equals(DictValueEnums.OFF_STATUS.getCode(), sysTenantVO.getStatus())) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.STOP, ResultConstants.TENANT));
            }
            Date date = DateUtils.getNowDate();
            if (date.compareTo(sysTenantVO.getStartTime()) < Constants.ZERO) {
                recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.NOT_EFFECTIVE, ResultConstants.TENANT).getMsg());
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_EFFECTIVE, ResultConstants.TENANT));
            }
            if (date.compareTo(sysTenantVO.getEndTime()) > Constants.ZERO) {
                recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.EXPIRE, ResultConstants.TENANT).getMsg());
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXPIRE, ResultConstants.TENANT));
            }
        }

        List<SysRoleVO> sysRoleVOS = sysUserDetailsVO.getSysRoleVOS();
        if (StringUtils.isNotEmpty(sysRoleVOS)) {
            Set<String> roleCodes = sysRoleVOS.stream().filter(r -> StringUtils.isNotBlank(r.getRoleCode())).map(SysRoleVO::getRoleCode).collect(Collectors.toSet());
            userInfo.setRoles(roleCodes);
            userInfo.setSysRoleVOS(sysRoleVOS);
        }
        List<SysMenuVO> sysMenuVOS = sysUserDetailsVO.getSysMenuVOS();
        sysMenuVOS.removeIf(m -> StringUtils.equals(DictValueEnums.OFF_STATUS.getCode(), m.getStatus()));
        if (StringUtils.isNotEmpty(sysMenuVOS)) {
            Set<String> menuCodes = new HashSet<>();
            Set<Long> appIds = new HashSet<>();
            for (SysMenuVO sysMenuVO : sysMenuVOS) {
                menuCodes.add(sysMenuVO.getMenuCode());
                if (StringUtils.isNotNull(sysMenuVO.getAppId())) {
                    appIds.add(sysMenuVO.getAppId());
                }
            }
            userInfo.setSysMenuVOS(sysMenuVOS);
            userInfo.setAppIds(appIds);
            userInfo.setPermissions(menuCodes);

            userInfo.setAppCodes(sysUserDetailsVO.getSysAppVOS().stream().map(SysAppVO::getCode).collect(Collectors.toSet()));
        }

        userInfo.setSysDepartVO(sysUserDetailsVO.getSysDepartVO());
        userInfo.setSysPostVOS(sysUserDetailsVO.getSysPostVOS());
        userInfo.setDataPermissionsVO(sysUserDetailsVO.getDataPermissionsVO());

        if (UserStatus.DISABLE.getCode().equals(sysUserVO.getStatus())) {
            recordLogininfor(username, tenantId, Constants.LOGIN_FAIL, ResultEnum.getMsg(ResultEnum.STOP, ResultConstants.USERNAME + username).getMsg());
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.STOP, ResultConstants.USERNAME + username));
        }
        return sysUserVO;
    }

}
