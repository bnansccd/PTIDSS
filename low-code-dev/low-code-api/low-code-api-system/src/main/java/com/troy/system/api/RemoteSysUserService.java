package com.troy.system.api;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.DTO.AuditDTO;
import com.troy.system.api.domain.DTO.RegisterDTO;
import com.troy.system.api.domain.VO.*;
import com.troy.system.api.factory.RemoteSysUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/9 14:14:26
 * @Description: RemoteUserService
 * @Version: 1.0.0
 */
@FeignClient(contextId = "remoteSysUserService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysUserFallbackFactory.class)
public interface RemoteSysUserService {

    /**
     * 通过用户名查询用户
     *
     * @param username
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/username/{username}/tenantId/{tenantId}")
    ResultVO<SysUserDetailsVO> sysUserByUsernameAndTenantId(@PathVariable("username") String username, @PathVariable("tenantId")Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过手机号查询用户
     *
     * @param phone
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/phone/{phone}/tenantId/{tenantId}")
    ResultVO<SysUserDetailsVO> sysUserByPhoneAndTenantId(@PathVariable("phone") String phone, @PathVariable("tenantId")Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过ID查询用户详情
     * @param id
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/{id}")
    ResultVO<SysUserVO> findById(@PathVariable("id") Long id);

    /**
     * 用户注册
     *
     * @param dto
     * @return
     */
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/register")
    ResultVO sysUserRegister(@RequestBody @Validated RegisterDTO dto, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 查询审计信息
     *
     * @param dto
     * @return
     */
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "audit/info")
    ResultVO<AuditVO> findAuditInfo(@RequestBody AuditDTO dto, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 模糊查询用户
     * @param username
     * @param tenantId
     * @param source
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/realName/{realName}/tenantId/{tenantId}")
    ResultVO<List<SysUserVO>> sysUserByRealNameAndTenantId(@PathVariable("realName") String username, @PathVariable("tenantId")Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 根据id获取
     * @param ids
     * @param tenantId
     * @param source
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/byIds")
    ResultVO<List<SysUserVO>> sysUserByIds(@RequestParam("ids") List<Long> ids, @RequestParam("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/byDepartIdsAndUsername")
    ResultVO<List<SysUserVO>> byDepartIdsAndUsername(@RequestParam(value = "ids", required = false) List<Long> ids, @RequestParam(value = "username", required = false) String name, @RequestParam("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/group")
    ResultVO<List<SysSpecialGroupVO>> group(@RequestParam(value = "list") List<Long> list, @RequestParam("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/byDepartIdsAndRealName")
    ResultVO<List<SysUserVO>> byDepartIdsAndRealName(@RequestParam(value = "ids", required = false) List<Long> ids, @RequestParam(value = "username", required = false) String name, @RequestParam("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysUser/getAll")
    ResultVO<List<SysUserVO>> getAll(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysUser/getByRealNameIn")
    ResultVO<List<SysUserVO>> getByRealNameIn(@RequestBody List<String> names,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
