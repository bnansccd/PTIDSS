package com.troy.camunda.servcice.impl;

import com.mybatisflex.core.paginate.Page;
import com.troy.camunda.domian.DTO.ProcessDefinitionDTO;
import com.troy.camunda.domian.DTO.ProcessDefinitionSearchDTO;
import com.troy.camunda.domian.VO.CamundaProcDefVO;
import com.troy.camunda.servcice.ProcessDefinitionService;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProcessDefinitionImpl implements ProcessDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    private static final String BPMN_FILE_SUFFIX = ".bpmn";

    @Override
    public Page<CamundaProcDefVO> list(ProcessDefinitionSearchDTO dto) {
        Page<CamundaProcDefVO> page = new Page<>();
        ProcessDefinitionQuery processDefinitionQuery = this.repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotBlank(dto.getProcessDefinitionName())) {
            processDefinitionQuery.processDefinitionNameLike(dto.getProcessDefinitionName());
        }
//        List<ProcessDefinition> processDefinitions = processDefinitionQuery.orderByDeploymentTime().desc()
//                .listPage((int) (dto.getSize() * (dto.getCurrent() - 1)), (int) dto.getSize());
                List<ProcessDefinition> processDefinitions = processDefinitionQuery.orderByDeploymentTime().desc()
                .listPage((int) (dto.getSize() * (dto.getCurrent() - 1)), 5);
        page.setTotalPage(processDefinitionQuery.count());
        List<CamundaProcDefVO> dataList = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitions) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            CamundaProcDefVO reProcDef = new CamundaProcDefVO();
            BeanUtils.copyProperties(processDefinition, reProcDef);
            // 流程定义时间
            reProcDef.setDeploymentTime(deployment.getDeploymentTime());
            dataList.add(reProcDef);
        }
        page.setRecords(dataList);
        return page;
    }

    @Override
    public void importFile(String name, String category, InputStream in) {
        Deployment deploy = repositoryService.createDeployment().addInputStream(name + BPMN_FILE_SUFFIX, in).name(name).deploy();
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
    }

    /**
     * 读取xml文件
     *
     * @param deployId
     * @return
     */
    @Override
    public ResultVO readXml(String deployId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
        String result;
        try {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
            return ResultVO.success(result);
        } catch (Exception e) {
            return ResultVO.fail("读取失败");
        }
    }

    /**
     * 根据流程定义id启动流程实例
     *
     * @param id
     * @param variables
     * @return
     */
    @Override
    public ResultVO startProcessInstanceById(String id, Map<String, Object> variables) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(id)
                    .singleResult();
            if (StringUtils.isNull(processDefinition)){
                throw new ServiceException(ResultEnum.NOT_FOUND, ResultConstants.PROCESS_DEFINITION);
            }
            if (processDefinition.isSuspended()) {
                throw new ServiceException(ResultEnum.NOT_ACTIVATION,ResultConstants.PROCESS_DEFINITION);
            }
            // 设置流程发起人Id到流程中
            String userId = "ww";
            identityService.setAuthenticatedUserId(userId);

            ProcessInstance processInstance = runtimeService.startProcessInstanceById(id, variables);
            // 给第一步申请人节点设置任务执行人和意见
//            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
//            if (StringUtils.isNotNull(task)) {
//                taskService.createComment(task.getId(), processInstance.getProcessInstanceId(), CamundaComment.NORMAL.getType());
//                taskService.complete(task.getId(), variables);
//            }
        } catch (Exception e) {
            log.error("流程定义启动失败",e);
            throw new ServiceException(ResultEnum.OPERATE_FAIL,ResultConstants.PROCESS_DEFINITION_START);
        }
        return ResultVO.success("流程启动成功");
    }

    @Override
    public void delete(String deployId) {
        //联级删除
        repositoryService.deleteDeployment(deployId, true);
    }

    /**
     * 激活或挂起流程定义
     *
     * @param state
     * @param deployId
     */
    @Override
    public void updateState(Integer state, String deployId) {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        // 激活
        if (state == 1) {
            repositoryService.activateProcessDefinitionById(procDef.getId(), true, null);
        }
        // 挂起
        if (state == 2) {
            repositoryService.suspendProcessDefinitionById(procDef.getId(), true, null);
        }
    }

    @Transactional
    @Override
    public CamundaProcDefVO deploy(ProcessDefinitionDTO dto) {
        CamundaProcDefVO vo = null;
        try {
            Deployment deploy = this.repositoryService.createDeployment()
                    .addClasspathResource(dto.getResourcePath())
                    .name(dto.getName())
                    .deploy();
            vo = new CamundaProcDefVO();
            vo.setId(deploy.getId());
            vo.setDeploymentTime(deploy.getDeploymentTime());
            vo.setName(deploy.getName());
        } catch (Exception e) {
            log.error("流程定义部署失败", e);
            throw new ServiceException(ResultEnum.OPERATE_FAIL,ResultConstants.PROCESS_DEFINITION_DEPLOY);
        }
        return vo;
    }
}
