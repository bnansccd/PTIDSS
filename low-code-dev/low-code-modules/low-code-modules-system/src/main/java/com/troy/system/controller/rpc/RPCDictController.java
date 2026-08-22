package com.troy.system.controller.rpc;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.system.api.domain.VO.SysDictVO;
import com.troy.system.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "RPC-字典管理")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RPCDictController {


    @Autowired
    private SysDictService sysDictService;
    @ApiOperation(value = "根据父编码查询")
    @InnerAuth
    @ApiImplicitParam(value = "parentType", name = "parentType", required = true, paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/parentType/{parentType}")
    public ResultVO<List<SysDictVO>> getSysDictByParentType(@PathVariable String parentType) {
        return ResultVO.success(this.sysDictService.getSysDictByParentType(parentType));
    }
}
