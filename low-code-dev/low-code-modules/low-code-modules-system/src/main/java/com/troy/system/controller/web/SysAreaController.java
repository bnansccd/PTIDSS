package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.domain.DTO.SysAreaQueryDTO;
import com.troy.system.domain.VO.SysAreaVO;
import com.troy.system.service.SysAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/17 10:10:11
 * @Description: SysAreaController
 * @Version: 1.0.0
 */
@Api(tags = "区域管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysAreaController {

    @Autowired
    private SysAreaService sysAreaService;

    @ApiOperation(value = "获取区域列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysArea")
    public ResultVO<List<SysAreaVO>> sysAreaList(SysAreaQueryDTO dto){
        return ResultVO.success(this.sysAreaService.sysAreaList(dto));
    }

}
