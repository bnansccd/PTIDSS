package com.troy.system.controller.rpc;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.system.entity.ApiSecretEntity;
import com.troy.system.service.ApiSecretService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * 用户管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "SSO登录")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RPCApiSecretController {

    @Autowired
    private ApiSecretService apiSecretService;

    @ApiOperation(value = "获取code")
    @InnerAuth
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "apiSecret/orgId")
    public ResultVO<ApiSecretEntity> code(@RequestParam("orgId") String orgId) throws UnsupportedEncodingException {
        return ResultVO.success(apiSecretService.getOneByOrgId(orgId));
    }
}
