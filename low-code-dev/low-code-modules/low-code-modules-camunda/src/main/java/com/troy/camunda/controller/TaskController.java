package com.troy.camunda.controller;

import com.troy.camunda.domian.DTO.CamundaTaskDTO;
import com.troy.camunda.servcice.TaskService;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "任务管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ApiOperation(value = "我发起的流程")
    @GetMapping(value = "/myProcess")
    public ResultVO myProcess(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                              @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return taskService.myProcess(pageNum, pageSize);
    }

    @ApiOperation(value = "取消申请")
    @PostMapping(value = "/stopProcess")
    public ResultVO stopProcess(@RequestBody CamundaTaskDTO dto) {
        return taskService.stopProcess(dto);
    }

    @ApiOperation(value = "撤回流程")
    @PostMapping(value = "/revokeProcess")
    public ResultVO revokeProcess(@RequestBody CamundaTaskDTO dto) {
        return taskService.revokeProcess(dto);
    }

    @ApiOperation(value = "获取待办列表")
    @GetMapping(value = "/todoList")
    public ResultVO todoList(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                             @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return taskService.todoList(pageNum, pageSize);
    }

    @ApiOperation(value = "获取已办任务")
    @GetMapping(value = "/finishedList")
    public ResultVO finishedList(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                                 @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return taskService.finishedList(pageNum, pageSize);
    }

    @ApiOperation(value = "流程历史流转记录")
    @GetMapping(value = "/flowRecord")
    public ResultVO flowRecord(String procInsId, String deployId) {
        return taskService.flowRecord(procInsId, deployId);
    }

    @ApiOperation(value = "获取流程变量")
    @GetMapping(value = "/processVariables/{taskId}")
    public ResultVO processVariables(@ApiParam(value = "流程任务Id") @PathVariable(value = "taskId") String taskId) {
        return taskService.processVariables(taskId);
    }

    @ApiOperation(value = "审批任务")
    @PostMapping(value = "/complete")
    public ResultVO complete(@RequestBody CamundaTaskDTO dto) {
        return taskService.complete(dto);
    }

    @ApiOperation(value = "驳回任务")
    @PostMapping(value = "/reject")
    public ResultVO taskReject(@RequestBody CamundaTaskDTO dto) {
        taskService.taskReject(dto);
        return ResultVO.success();
    }

    @ApiOperation(value = "退回任务")
    @PostMapping(value = "/return")
    public ResultVO taskReturn(@RequestBody CamundaTaskDTO dto) {
        taskService.taskReturn(dto);
        return ResultVO.success();
    }

    @ApiOperation(value = "获取所有可回退的节点")
    @PostMapping(value = "/returnList")
    public ResultVO findReturnTaskList(@RequestBody CamundaTaskDTO dto) {
        return taskService.findReturnTaskList(dto);
    }

    @ApiOperation(value = "删除任务")
    @DeleteMapping(value = "/delete")
    public ResultVO delete(@RequestBody CamundaTaskDTO dto) {
        taskService.deleteTask(dto);
        return ResultVO.success();
    }
}
