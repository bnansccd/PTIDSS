package com.troy.camunda.servcice.impl;

import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.troy.camunda.domian.DTO.CamundaTaskDTO;
import com.troy.camunda.domian.VO.CamundaTaskVO;
import com.troy.camunda.servcice.TaskService;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.security.utils.SecurityUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.DelegationState;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {


    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private org.camunda.bpm.engine.TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;
    /**
     * 我发起的流程
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultVO myProcess(Integer pageNum, Integer pageSize) {
        Page<CamundaTaskVO> page = new Page<>();
        Long userId = 11L;
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId.toString())
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        page.setTotalPage(historicProcessInstanceQuery.count());
        List<CamundaTaskVO> flowList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            CamundaTaskVO flowTask = new CamundaTaskVO();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            } else {
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            }
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setCategory(pd.getCategory());
            flowTask.setProcDefVersion(pd.getVersion());
            // 当前所处流程 todo: 本地启动放开以下注释
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (StringUtils.isNotEmpty(taskList)) {
                flowTask.setTaskId(taskList.get(0).getId());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return ResultVO.success(page);
    }

    /**
     *  取消申请
     * @param dto
     * @return
     */
    @Override
    public ResultVO stopProcess(CamundaTaskDTO dto) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(dto.getInstanceId()).list();
        if (StringUtils.isEmpty(task)) {
            return ResultVO.fail("流程未启动或已执行完成，取消申请失败");
        }
        // 获取当前需撤回的流程实例
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(dto.getInstanceId())
                        .singleResult();
        BpmnModelInstance bpmnModel = repositoryService.getBpmnModelInstance(processInstance.getProcessDefinitionId());

        //BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
//        bpmnModel.
//        if (Objects.nonNull(bpmnModel)) {
//
//
//            Process process = bpmnModel.getMainProcess();
//            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
//            if (CollectionUtils.isNotEmpty(endNodes)) {
//                SysUser loginUser = SecurityUtils.getLoginUser().getUser();
//                Authentication.setAuthenticatedUserId(loginUser.getUserId().toString());
////                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.STOP.getType(),
////                        StringUtils.isBlank(flowTaskVo.getComment()) ? "取消申请" : flowTaskVo.getComment());
//                // 获取当前流程最后一个节点
//                String endId = endNodes.get(0).getId();
//                List<Execution> executions =
//                        runtimeService.createExecutionQuery().parentId(processInstance.getProcessInstanceId()).list();
//                List<String> executionIds = new ArrayList<>();
//                executions.forEach(execution -> executionIds.add(execution.getId()));
//                // 变更流程为已结束状态
//                runtimeService.createChangeActivityStateBuilder()
//                        .moveExecutionsToSingleActivityId(executionIds, endId).changeState();
//            }
//        }

        return ResultVO.success();
    }

    /**
     * 撤回流程
     * @param dto
     * @return
     */
    @Override
    public ResultVO revokeProcess(CamundaTaskDTO dto) {
        Task task = taskService.createTaskQuery().processInstanceId(dto.getInstanceId()).singleResult();
        if(task==null){
            return ResultVO.fail("流程未启动或已执行完成，无法撤回");
        }
        Long userId = 1111L;
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .orderByTaskDueDate()
                .asc()
                .list();
        String myTaskId = null;
        HistoricTaskInstance myTask = null;
        for (HistoricTaskInstance historicTaskInstance : list) {
            if(userId.toString().equals(historicTaskInstance.getAssignee())){
                myTaskId = historicTaskInstance.getId();
                myTask = historicTaskInstance;
            }
        }
        if (null == myTaskId) {
            return ResultVO.fail("该任务非当前用户提交，无法撤回");
        }
        String processDefinitionId = myTask.getProcessDefinitionId();
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        String myActivityId = null;
        List<HistoricActivityInstance> hisList = historyService.createHistoricActivityInstanceQuery()
                .executionId(myTask.getExecutionId()).finished().list();
        for (HistoricActivityInstance hai : hisList) {
            if (myTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }

        return null;
    }

    /**
     * 获取待办列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultVO todoList(Integer pageNum, Integer pageSize) {
        Page<CamundaTaskVO> page = new Page<>();
        Long userId = 111L;
        //Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
//        TaskQuery taskQuery = taskService.createTaskQuery()
//                .active()
//                //.includeProcessVariables()
////                .taskAssignee(userId.toString())
//                .orderByTaskCreateTime().desc();
        TaskQuery taskQuery =taskService.createTaskQuery()
                .active().includeAssignedTasks().taskAssignee(userId.toString()).orderByTaskCreateTime().desc();
        page.setTotalPage(taskQuery.count());
        List<Task> taskList = taskQuery.listPage(pageSize * (pageNum - 1), pageSize);
        List<CamundaTaskVO> flowList = new ArrayList<>();
        for (Task task : taskList) {
            CamundaTaskVO flowTask = new CamundaTaskVO();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setExecutionId(task.getExecutionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            //SysUser startUser = sysUserService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
//            SysUser startUser = sysUserService.selectUserById(Long.parseLong(task.getAssignee()));
            flowTask.setStartUserId("userId");
            flowTask.setStartUserName("userName");
            flowTask.setStartDeptName("deptName");
            flowList.add(flowTask);
        }

        page.setRecords(flowList);
        return ResultVO.success(page);
    }

    /**
     * 获取已办任务
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultVO finishedList(Integer pageNum, Integer pageSize) {
        Page<CamundaTaskVO> page = new Page<>();
        Long userId = 111L;
        //Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
//        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
//                .includeProcessVariables()
//                .finished()
//                .taskAssignee(userId.toString())
//                .orderByHistoricTaskInstanceEndTime()
//                .desc();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .matchVariableValuesIgnoreCase().finished().orderByHistoricTaskInstanceEndTime().desc();
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        List<CamundaTaskVO> hisTaskList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            CamundaTaskVO flowTask = new CamundaTaskVO();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(histTask.getStartTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(getDate(histTask.getDurationInMillis()));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());

            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(histTask.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(histTask.getProcessInstanceId())
                    .singleResult();
            // SysUser startUser = sysUserService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId("userId");
            flowTask.setStartUserName("userName");
            flowTask.setStartDeptName("deptName");
            hisTaskList.add(flowTask);
        }
        page.setTotalPage(taskInstanceQuery.count());
        page.setRecords(hisTaskList);
//        Map<String, Object> result = new HashMap<>();
//        result.put("result",page);
//        result.put("finished",true);
        return ResultVO.success(page);
    }

    /**
     * 流程历史流转记录
     * @param procInsId
     * @param deployId
     * @return
     */
    @Override
    public ResultVO flowRecord(String procInsId, String deployId) {
        //Long userId = 1111L;
        Long userId = SecurityUtils.getLoginUser().getUserid();
        Map<String, Object> map = new HashMap<String, Object>();
        if(StringUtils.isNotBlank(procInsId)){
            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(procInsId)
                    .orderByHistoricActivityInstanceStartTime()
                    .desc()
                    .list();
            List<CamundaTaskVO> hisFlowList = new ArrayList<>();
            CamundaTaskVO taskDto;
            for (HistoricActivityInstance histIns : list) {
                if(StringUtils.isNotBlank(histIns.getTaskId())){
                    taskDto = new CamundaTaskVO();
                    taskDto.setTaskId(histIns.getTaskId());
                    taskDto.setTaskName(histIns.getActivityName());
                    taskDto.setCreateTime(histIns.getStartTime());
                    taskDto.setFinishTime(histIns.getEndTime());
                    if(StringUtils.isNotBlank(histIns.getAssignee())){
                        //设置用户信息
                        taskDto.setAssigneeId(userId);
                        taskDto.setAssigneeName("userName");
                        taskDto.setDeptName("deptName");
                    }
                    //展示审批人员
                    List<HistoricIdentityLinkLog> identityLinkLogs = historyService.createHistoricIdentityLinkLogQuery()
                            .taskId(histIns.getTaskId())
                            .list();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HistoricIdentityLinkLog identityLinkLog : identityLinkLogs) {
                        // 获选人,候选组/角色(多个)
                        if("candidate".equals(identityLinkLog.getType())){
                            if (StringUtils.isNotBlank(identityLinkLog.getUserId())) {
                                stringBuilder.append("userName").append(",");
                            }
                            if (StringUtils.isNotBlank(identityLinkLog.getGroupId())) {
                                stringBuilder.append("roleName").append(",");
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(stringBuilder)) {
                        taskDto.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                    }
                    taskDto.setDuration(histIns.getDurationInMillis() == null || histIns.getDurationInMillis() == 0 ? null : getDate(histIns.getDurationInMillis()));
                    // 获取意见评论内容
//                    List<Comment> commentList = taskService.getProcessInstanceComments(histIns.getProcessInstanceId());
//                    FlowCommentDto dto;
//                    for (Comment comment : commentList) {
//                        if (histIns.getTaskId().equals(comment.getTaskId())) {
//                            dto = new FlowCommentDto();
//                            //todo 设置意见类型和内容
////                            dto.setType();
//                        }
//                    }
                    hisFlowList.add(taskDto);
                }
            }
            map.put("flowList", hisFlowList);
            // 查询当前任务是否完成
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
            if (StringUtils.isNotEmpty(taskList)) {
                map.put("finished", true);
            } else {
                map.put("finished", false);
            }
        }
        // 第一次申请获取初始化表单
        if (StringUtils.isNotBlank(deployId)) {
//            SysForm sysForm = sysInstanceFormService.selectSysDeployFormByDeployId(deployId);
//            if (Objects.isNull(sysForm)) {
//                return AjaxResult.error("请先配置流程表单");
//            }
//            map.put("formData", JSONObject.parseObject(sysForm.getFormContent()));
        }
        return ResultVO.success(map);
    }

    /**
     * 获取流程变量
     * @param taskId
     * @return
     */
    @Override
    public ResultVO processVariables(String taskId) {
        //String takey = "asa";
        // 流程变量
        Map<String, Object> variables = taskService.getVariables(taskId);
//        Task task = taskService.createTaskQuery().taskDefinitionKey(takey).singleResult();
//        Map<String, Object> variables1 = taskService.getVariables(task.getId());
        return ResultVO.success(variables);
    }

    /**
     * 审批任务
     * @param dto
     * @return
     */
    @Override
    public ResultVO complete(CamundaTaskDTO dto) {
        //Long userId = 1111L;
        Long userId = SecurityUtils.getLoginUser().getUserid();
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            return ResultVO.fail("任务不存在");
        }
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            taskService.createComment(dto.getTaskId(), dto.getInstanceId(), dto.getComment());
            taskService.resolveTask(dto.getTaskId(), dto.getValues());
        } else {
            taskService.createComment(dto.getTaskId(), dto.getInstanceId(), dto.getComment());
            //Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
            taskService.setAssignee(dto.getTaskId(), userId.toString());
            taskService.complete(dto.getTaskId(), dto.getValues());
        }
        return ResultVO.success();
    }

    /**
     * 驳回任务
     * @param dto
     */
    @Override
    public void taskReject(CamundaTaskDTO dto) {
        if (taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult().isSuspended()) {
            //任务处于挂起状态!;
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        //repositoryService.getBpmnModelInstance()
        //Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        //Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        //FlowElement source = null;
//        if (allElements != null) {
//            for (FlowElement flowElement : allElements) {
//                // 类型为用户节点
//                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
//                    // 获取节点信息
//                    source = flowElement;
//                }
//            }
//        }
    }

    /**
     * 退回任务
     * @param flowTaskVo
     */
    @Override
    public void taskReturn(CamundaTaskDTO flowTaskVo) {

    }

    /**
     * 获取所有可回退的节点
     * @param dto
     * @return
     */
    @Override
    public ResultVO findReturnTaskList(CamundaTaskDTO dto) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        // 获取流程定义信息

        return null;
    }

    /**
     * 删除任务
     * @param dto
     */
    @Override
    public void deleteTask(CamundaTaskDTO dto) {
        taskService.deleteTask(dto.getTaskId(), dto.getComment());
    }

    private String getDate(long ms) {

        long day = ms / (24 * 60 * 60 * 1000);
        long hour = (ms / (60 * 60 * 1000) - day * 24);
        long minute = ((ms / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (ms / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

        if (day > 0) {
            return day + "天" + hour + "小时" + minute + "分钟";
        }
        if (hour > 0) {
            return hour + "小时" + minute + "分钟";
        }
        if (minute > 0) {
            return minute + "分钟";
        }
        if (second > 0) {
            return second + "秒";
        } else {
            return 0 + "秒";
        }
    }
}
