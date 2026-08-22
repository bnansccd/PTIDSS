package com.troy.system.controller.rpc;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.system.api.domain.VO.SysDepartVO;
import com.troy.system.service.SysDepartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"RPC-部门管理"})
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RPCSysDepartController {

    @Autowired
    private SysDepartService sysDepartService;

    @ApiOperation(value = "通过id查询")
    @InnerAuth
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart/{id}")
    @ApiImplicitParam(value = "id", name = "id", required = true, paramType = "path")
    public ResultVO<SysDepartVO> findById(@PathVariable Long id) {
        return ResultVO.success(this.sysDepartService.findById(id));
    }

    @ApiOperation(value = "获取部门列表")
    @InnerAuth
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart/byIds")
    public ResultVO<List<SysDepartVO>> byIds(@RequestParam("ids") List<Long> ids){
        return ResultVO.success(this.sysDepartService.findById(ids));
    }

    @ApiOperation(value = "通过一批id查询部门")
    @InnerAuth
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart/ids/{ids}")
    public ResultVO<List<SysDepartVO>> findByIdIn(@PathVariable("ids") List<Long> ids) {
        return ResultVO.success(this.sysDepartService.findById(ids));
    }

    @ApiOperation(value = "部门列表")
    @InnerAuth
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart")
    public ResultVO<List<SysDepartVO>> findAll(){
        return ResultVO.success(this.sysDepartService.findAll());
    }

    @ApiOperation(value = "根据部门名称查询")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/findByDeptNameLike")
    public ResultVO<List<SysDepartVO>> findByDeptNameLike(@RequestParam("deptName") String deptName){
        return ResultVO.success(this.sysDepartService.findByDeptNameLike(deptName));
    }

}
