package com.troy.camunda.servcice.impl;

import com.troy.camunda.domian.DTO.CamundaTaskDTO;
import com.troy.camunda.servcice.InstanceService;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.security.utils.SecurityUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class InstanceServiceImpl implements InstanceService {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 根据流程定义id启动流程实例
     * @param procDefId
     * @param variables
     * @return
     */
    @Override
    public ResultVO startProcessInstanceById(String procDefId, Map<String, Object> variables) {
        try {
            // 设置流程发起人Id到流程中
            Long userid = SecurityUtils.getLoginUser().getUserid();
            identityService.setAuthenticatedUserId(userid.toString());
            variables.put("initiator",userid);
            variables.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
            runtimeService.startProcessInstanceById(procDefId, variables);
            return ResultVO.success("流程启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("流程启动错误");
        }
    }

    /**
     * 激活或挂起流程实例
     * @param state
     * @param instanceId
     */
    @Override
    public void updateState(Integer state, String instanceId) {
        // 激活
        if (state == 1) {
            runtimeService.activateProcessInstanceById(instanceId);
        }
        // 挂起
        if (state == 2) {
            runtimeService.suspendProcessInstanceById(instanceId);
        }
    }

    /**
     * 结束流程实例
     * @param dto
     */
    @Override
    public void stopProcessInstance(CamundaTaskDTO dto) {
        String taskId = dto.getTaskId();
        taskService.complete(taskId);
    }

    /**
     * 删除流程实例
     * @param instanceId
     * @param deleteReason
     */
    @Override
    public void delete(String instanceId, String deleteReason) {
        // 查询历史数据
        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstanceById(instanceId);
        if (historicProcessInstance.getEndTime() != null) {
            historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
            return;
        }
        // 删除流程实例
        runtimeService.deleteProcessInstance(instanceId, deleteReason);
        // 删除历史流程实例
        historyService.deleteHistoricProcessInstance(instanceId);
    }

    /**
     * 根据实例ID查询历史实例数据
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public HistoricProcessInstance getHistoricProcessInstanceById(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (Objects.isNull(historicProcessInstance)) {
            return null; //流程实例不存在
        }
        return historicProcessInstance;
    }
}
