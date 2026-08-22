package com.troy.camunda.controller;

import com.troy.camunda.domian.DTO.CamundaTaskDTO;
import com.troy.camunda.servcice.InstanceService;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "流程实例管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class InstanceController {

    @Autowired
    private InstanceService instanceService;

    @ApiOperation(value = "根据流程定义id启动流程实例")
    @ApiImplicitParam(name = "procDefId", value = "流程定义id", dataTypeClass = String.class, required = true, paramType = "path")
    @PostMapping("/startBy/{procDefId}")
    public ResultVO startById(@PathVariable(value = "procDefId") String procDefId,
                              @ApiParam(value = "变量集合,json对象") @RequestBody Map<String, Object> variables) {
        return instanceService.startProcessInstanceById(procDefId, variables);
    }

    @ApiOperation(value = "激活或挂起流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "1:激活,2:挂起", dataTypeClass = Integer.class, required = true, paramType = "query"),
            @ApiImplicitParam(name = "instanceId", value = "流程实例ID", dataTypeClass = String.class, required = true, paramType = "query")
    })
    @PostMapping(value = "/updateState")
    public ResultVO updateState(@RequestParam Integer state, @RequestParam String instanceId) {
        instanceService.updateState(state,instanceId);
        return ResultVO.success();
    }

    @ApiOperation("结束流程实例")
    @PostMapping(value = "/stopProcessInstance")
    public ResultVO stopProcessInstance(@RequestBody CamundaTaskDTO dto) {
        instanceService.stopProcessInstance(dto);
        return ResultVO.success();
    }

    @ApiOperation(value = "删除流程实例")
    @DeleteMapping(value = "/instance/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceId", value = "流程实例ID", dataTypeClass = String.class, required = true, paramType = "query"),
            @ApiImplicitParam(name = "deleteReason", value = "删除原因", dataTypeClass = String.class, required = true, paramType = "query")
    })
    public ResultVO delete(@RequestParam String instanceId, @RequestParam String deleteReason) {
        instanceService.delete(instanceId,deleteReason);
        return ResultVO.success();
    }
}
