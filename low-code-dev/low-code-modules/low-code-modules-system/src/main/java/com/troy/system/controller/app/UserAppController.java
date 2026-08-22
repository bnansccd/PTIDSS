package com.troy.system.controller.app;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chenxl
 * @description
 * @date 2024-05-07 15:21
 */
@Api(tags = "APP端-用户查询")
@RestController
@RequestMapping(UrlConstants.APP_RESTFUL+"user/")
public class UserAppController {

    @Autowired
    private SysUserService userService;

    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "getOwnDepart")
    @ApiOperation(value = "获取巡查陪同人员")
    public ResultVO<List<SysUserVO>> getSso(){
        return ResultVO.success(userService.findByOwnerDepart());
    }

}
