package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysLogininforVO;
import com.troy.system.domain.DTO.DateRangeDTO;
import com.troy.system.domain.DTO.SysLogininfoQueryDTO;
import com.troy.system.domain.VO.LoginTimesAndChainVO;
import com.troy.system.service.SysLogininforService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 系统访问记录 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "系统访问记录")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysLogininforController {

    @Autowired
    private SysLogininforService sysLogininforService;

    @ApiOperation(value = "系统访问记录列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysLogininfor")
    public ResultVO<PageVO<SysLogininforVO>> getSysLogininforList(SysLogininfoQueryDTO dto){
        return ResultVO.success(this.sysLogininforService.getSysLogininforList(dto));
    }

    @ApiOperation(value = "查看系统访问记录")
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysLogininfor/{id}")
    public ResultVO<SysLogininforVO> getSysLogininforById(@PathVariable Long id){
        return ResultVO.success(this.sysLogininforService.getSysLogininforById(id));
    }

    @ApiOperation(value = "获取登录次数和环比")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysLogininfor/getTimesAndChain")
    public ResultVO<LoginTimesAndChainVO> getTimesAndChain(@RequestBody DateRangeDTO dto){
        return ResultVO.success(this.sysLogininforService.getTimesAndChain(dto));
    }

}
