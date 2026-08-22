package com.troy.camunda.servcice;

import com.troy.camunda.domian.DTO.CamundaTaskDTO;
import com.troy.common.core.domain.ResultVO;

public interface TaskService {

    /**
     * 我发起的流程
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultVO myProcess(Integer pageNum, Integer pageSize);

    /**
     * 取消申请
     * @param dto
     * @return
     */
    ResultVO stopProcess(CamundaTaskDTO dto);

    /**
     * 撤回流程
     * @param dto
     * @return
     */
    ResultVO revokeProcess(CamundaTaskDTO dto);

    /**
     * 获取待办列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultVO todoList(Integer pageNum, Integer pageSize);

    /**
     * 获取已办任务
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultVO finishedList(Integer pageNum, Integer pageSize);

    /**
     * 流程历史流转记录
     * @param procInsId
     * @param deployId
     * @return
     */
    ResultVO flowRecord(String procInsId, String deployId);

    /**
     * 获取流程变量
     * @param taskId
     * @return
     */
    ResultVO processVariables(String taskId);

    /**
     * 审批任务
     * @param dto
     * @return
     */
    ResultVO complete(CamundaTaskDTO dto);

    /**
     * 驳回任务
     * @param dto
     */
    void taskReject(CamundaTaskDTO dto);

    /**
     * 退回任务
     * @param flowTaskVo
     */
    void taskReturn(CamundaTaskDTO flowTaskVo);

    /**
     * 获取所有可回退的节点
     * @param dto
     * @return
     */
    ResultVO findReturnTaskList(CamundaTaskDTO dto);

    /**
     * 删除任务
     * @param dto
     */
    void deleteTask(CamundaTaskDTO dto);
}
