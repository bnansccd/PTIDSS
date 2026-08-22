package com.troy.camunda.servcice;

import com.troy.camunda.domian.DTO.CamundaTaskDTO;
import com.troy.common.core.domain.ResultVO;
import org.camunda.bpm.engine.history.HistoricProcessInstance;

import java.util.Map;

public interface InstanceService {

    /**
     * 根据流程定义id启动流程实例
     * @param procDefId
     * @param variables
     * @return
     */
    ResultVO startProcessInstanceById(String procDefId, Map<String, Object> variables);

    /**
     * 激活或挂起流程实例
     * @param state
     * @param instanceId
     */
    void updateState(Integer state, String instanceId);

    /**
     * 结束流程实例
     * @param dto
     */
    void stopProcessInstance(CamundaTaskDTO dto);

    /**
     * 删除流程实例
     * @param instanceId
     * @param deleteReason
     */
    void delete(String instanceId, String deleteReason);

    /**
     * 根据实例ID查询历史实例数据
     *
     * @param processInstanceId
     * @return
     */
    HistoricProcessInstance getHistoricProcessInstanceById(String processInstanceId);
}
