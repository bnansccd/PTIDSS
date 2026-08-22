package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysOperLogVO;
import com.troy.system.domain.DTO.SysOperLogQueryDTO;
import com.troy.system.service.SysOperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 操作日志记录 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "操作日志记录")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysOperLogController {

    @Autowired
    private SysOperLogService sysOperLogService;

    @ApiOperation(value = "操作日志列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysOperLog")
    public ResultVO<PageVO<SysOperLogVO>> getSysOperLogList(SysOperLogQueryDTO dto) {
        return ResultVO.success(this.sysOperLogService.getSysOperLogList(dto));
    }

    @ApiOperation(value = "查看操作日志详情")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysOperLog/{id}")
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    public ResultVO<SysOperLogVO> getSysOperLogById(@PathVariable Long id) {
        return ResultVO.success(this.sysOperLogService.getSysOperLogById(id));
    }

}