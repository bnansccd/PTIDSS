package com.troy.camunda.servcice;

import com.mybatisflex.core.paginate.Page;
import com.troy.camunda.domian.DTO.ProcessDefinitionDTO;
import com.troy.camunda.domian.DTO.ProcessDefinitionSearchDTO;
import com.troy.camunda.domian.VO.CamundaProcDefVO;
import com.troy.common.core.domain.ResultVO;

import java.io.InputStream;
import java.util.Map;

public interface ProcessDefinitionService {
    /**
     * 流程定义列表
     *
     * @param dto
     * @return
     */
    Page<CamundaProcDefVO> list(ProcessDefinitionSearchDTO dto);

    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     */
    void importFile(String name, String category, InputStream in);

    /**
     * 读取xml文件
     *
     * @param deployId
     * @return
     */
    ResultVO readXml(String deployId);

    /**
     * 根据流程定义id启动流程实例
     *
     * @param id
     * @param variables
     * @return
     */
    ResultVO startProcessInstanceById(String id, Map<String, Object> variables);

    /**
     * 删除流程
     *
     * @param deployId
     */
    void delete(String deployId);

    /**
     * 激活或挂起流程定义
     *
     * @param state
     * @param deployId
     */
    void updateState(Integer state, String deployId);

    /**
     * 部署本地流程定义
     *
     * @param dto
     * @return
     */
    CamundaProcDefVO deploy(ProcessDefinitionDTO dto);
}
