package com.troy.system.controller.rpc;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.system.api.domain.DTO.SysLogininforDTO;
import com.troy.system.domain.DTO.SysOperLogDTO;
import com.troy.system.service.SysLogininforService;
import com.troy.system.service.SysOperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统访问记录 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "RPC-系统访问记录")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RPCSysLogininforController {

    @Autowired
    private SysLogininforService sysLogininforService;

    @Autowired
    private SysOperLogService sysOperLogService;

    @ApiOperation(value = "保存系统访问记录")
    @InnerAuth
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "logininfor")
    public ResultVO insertLogininfor(@RequestBody@Validated SysLogininforDTO dto) {
        return this.sysLogininforService.insertLogininfor(dto);
    }

    @ApiOperation(value = "保存操作记录")
    @InnerAuth
    @PostMapping("operlog")
    public ResultVO operlog(@RequestBody @Validated SysOperLogDTO dto) {
        return ResultVO.success(sysOperLogService.addSysOperLog(dto));
    }

}
