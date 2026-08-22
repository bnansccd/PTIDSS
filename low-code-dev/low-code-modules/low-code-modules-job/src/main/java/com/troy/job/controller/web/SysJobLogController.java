package com.troy.job.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.poi.ExcelUtil;
import com.troy.common.core.web.VO.PageVO;
import com.troy.job.service.SysJobLogService;
import com.troy.job.domain.DTO.SysJobLogSearchDTO;
import com.troy.job.domain.VO.SysJobLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:06
 * @Description: SysJobLogController
 * @Version: 1.0.0
 */
@Api(tags = "调度日志操作处理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysJobLogController {

    @Autowired
    private SysJobLogService jobLogService;


    @ApiOperation(value = "查询定时任务调度日志列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJobLog")
    public ResultVO<PageVO<SysJobLogVO>> list(@Validated SysJobLogSearchDTO dto) {
        return ResultVO.success(jobLogService.selectJobLogPage(dto));
    }

    @ApiOperation(value = "导出定时任务调度日志列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJobLog/export")
    public void export(HttpServletResponse response, SysJobLogSearchDTO dto) {
        List<SysJobLogVO> list = jobLogService.selectJobLogList(dto);
        ExcelUtil<SysJobLogVO> util = new ExcelUtil<SysJobLogVO>(SysJobLogVO.class);
        util.exportExcel(response, list, "调度日志");
    }

    @ApiOperation(value = "根据调度编号获取详细信息")
    @ApiImplicitParam(name = "id" , value = "主键" , paramType = "path" , required = true)
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJobLog/{id}")
    public ResultVO<SysJobLogVO> getInfo(@PathVariable Long id) {
        return ResultVO.success(jobLogService.selectJobLogById(id));
    }

    @ApiOperation(value = "删除定时任务调度日志")
    @ApiImplicitParam(name = "ids" , value = "主键" , paramType = "path" , required = true)
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJobLog/{ids}")
    public ResultVO remove(@PathVariable List<Long> ids) {
        return jobLogService.deleteJobLogByIds(ids);
    }

    @ApiOperation(value = "清空定时任务调度日志")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJobLog/clean")
    public ResultVO clean() {
        return jobLogService.cleanJobLog();
    }
}
