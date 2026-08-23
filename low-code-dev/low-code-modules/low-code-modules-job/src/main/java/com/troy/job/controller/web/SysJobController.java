package com.troy.job.controller.web;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.exception.job.TaskException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.poi.ExcelUtil;
import com.troy.common.core.web.VO.PageVO;
import com.troy.job.service.SysJobService;
import com.troy.job.util.CronUtils;
import com.troy.job.util.ScheduleUtils;
import com.troy.job.domain.DTO.SysJobDTO;
import com.troy.job.domain.DTO.SysJobSearchDTO;
import com.troy.job.domain.VO.SysJobVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:04
 * @Description: SysJobController
 * @Version: 1.0.0
 */
@Api(tags = "调度任务信息操作处理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysJobController {

    @Autowired
    private SysJobService jobService;

    @ApiOperation(value = "查询定时任务分页列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJob")
    public ResultVO<PageVO<SysJobVO>> listPage(SysJobSearchDTO dto) {
        return ResultVO.success(this.jobService.selectJobListPage(dto));
    }


    @ApiOperation(value = "导出定时任务列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJob/export")
    public void export(HttpServletResponse response, SysJobSearchDTO dto) {
        List<SysJobVO> list = jobService.selectJobList(dto);
        ExcelUtil<SysJobVO> util = new ExcelUtil<SysJobVO>(SysJobVO.class);
        util.exportExcel(response, list, "定时任务");
    }


    @ApiOperation(value = "获取定时任务详细信息")
    @ApiImplicitParam(name = "id" , value = "主键" , paramType = "path" , required = true)
    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysJob/{id}")
    public ResultVO<SysJobVO> getInfo(@PathVariable Long id) {
        return ResultVO.success(jobService.selectJobById(id));
    }


    @ApiOperation(value = "新增定时任务")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysJob")
    public ResultVO add(@RequestBody @Validated SysJobDTO dto) throws SchedulerException, TaskException {
        this.validParam(dto);
        return jobService.insertJob(dto);
    }


    @ApiOperation(value = "修改定时任务")
    @ApiImplicitParam(name = "id" , value = "主键" , paramType = "path" , required = true)
    @PutMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysJob/{id}")
    public ResultVO edit(@PathVariable Long id, @RequestBody @Validated SysJobDTO dto) throws SchedulerException, TaskException {
        this.validParam(dto);
        return jobService.updateJob(id, dto);
    }

    @ApiOperation(value = "定时任务状态修改")
    @ApiImplicitParam(name = "id" , value = "主键" , paramType = "path" , required = true)
    @PatchMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysJob/{id}")
    public ResultVO changeStatus(@PathVariable Long id) throws SchedulerException {
        return jobService.changeStatus(id);
    }

    @ApiOperation(value = "定时任务立即执行一次")
    @ApiImplicitParam(name = "id" , value = "主键" , paramType = "path" , required = true)
    @PutMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysJob/run/{id}")
    public ResultVO run(@PathVariable Long id) throws SchedulerException {
        jobService.run(id);
        return ResultVO.success();
    }

    /**
     * 删除定时任务
     */
    @ApiOperation(value = "删除定时任务")
    @ApiImplicitParam(name = "ids" , value = "主键" , paramType = "path" , required = true)
    @DeleteMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysJob/{ids}")
    public ResultVO remove(@PathVariable List<Long> ids) throws SchedulerException, TaskException {
        jobService.deleteJobByIds(ids);
        return ResultVO.success();
    }

    /**
     * 参数验证
     *
     * @param dto
     * @return
     */
    private void validParam(SysJobDTO dto) {
        if (!CronUtils.isValid(dto.getCronExpression())) {
            throw new ServiceException("操作任务" + dto.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(dto.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            throw new ServiceException("操作任务" + dto.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(dto.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            throw new ServiceException("操作任务" + dto.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(dto.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            throw new ServiceException("操作任务" + dto.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(dto.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            throw new ServiceException("操作任务" + dto.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(dto.getInvokeTarget())) {
            throw new ServiceException("操作任务" + dto.getJobName() + "'失败，目标字符串不在白名单内");
        }
    }
}
