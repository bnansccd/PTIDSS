package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysDomainNameVO;
import com.troy.system.domain.DTO.SysDomainNameDTO;
import com.troy.system.service.SysDomainNameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 控制层。
 *
 * @author zhuqing
 * @since 2023-10-08 13:54:15
 */
@Api(tags = "租户域名管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class SysDomainNameController {

    @Autowired
    private SysDomainNameService sysDomainNameService;

    @ApiOperation(value = "查询域名列表")
    @ApiImplicitParam(name = "tenantId", value = "租户id", paramType = "path", required = true)
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "domain/tenantId/{tenantId}")
    public ResultVO<SysDomainNameVO> findByTenantId(@PathVariable Long tenantId) {
        return ResultVO.success(this.sysDomainNameService.findByTenantId(tenantId));
    }

    @Log(title = "编辑租户域名", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改域名")
    @ApiImplicitParam(name = "id", value = "主键", paramType = "path", required = true)
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "domain/{id}")
    public ResultVO editDomain(@PathVariable Long id, @Validated @RequestBody SysDomainNameDTO dto) {
        return this.sysDomainNameService.editDomain(id,dto);
    }

}
