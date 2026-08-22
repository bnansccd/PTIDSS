package com.troy.camunda.controller;

import com.mybatisflex.core.paginate.Page;
import com.troy.camunda.domian.DTO.CamundaSaveXmlDTO;
import com.troy.camunda.domian.DTO.ProcessDefinitionDTO;
import com.troy.camunda.domian.DTO.ProcessDefinitionSearchDTO;
import com.troy.camunda.domian.VO.CamundaProcDefVO;
import com.troy.camunda.servcice.ProcessDefinitionService;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Api(tags = "流程定义管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class ProcessDefinitionController {

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @GetMapping(value = "/processDefinition")
    @ApiOperation(value = "流程定义列表")
    public ResultVO<Page<CamundaProcDefVO>> list(@Validated ProcessDefinitionSearchDTO dto) {
        return ResultVO.success(processDefinitionService.list(dto));
    }

    @ApiOperation(value = "部署本地流程定义")
    @PostMapping("/processDefinition")
    public ResultVO<CamundaProcDefVO> deploy(@Validated ProcessDefinitionDTO dto) {
        return ResultVO.success(this.processDefinitionService.deploy(dto));
    }

    @ApiOperation(value = "导入流程文件", notes = "上传bpmn20的xml文件")
    @PostMapping("/import")
    public ResultVO importFile(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String category,
                               MultipartFile file) {
        InputStream in = null;
        try {
            in = file.getInputStream();
            processDefinitionService.importFile(name, category, in);
        } catch (Exception e) {
            return ResultVO.success(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }
        }

        return ResultVO.success("导入成功");
    }

    @ApiOperation(value = "读取xml文件")
    @GetMapping("/readXml/{deployId}")
    public ResultVO readXml(@ApiParam(value = "流程定义id") @PathVariable(value = "deployId") String deployId) {
        try {
            return processDefinitionService.readXml(deployId);
        } catch (Exception e) {
            return ResultVO.fail("加载xml文件异常");
        }
    }

    @ApiOperation(value = "保存流程设计器内的xml文件")
    @PostMapping("/save")
    public ResultVO save(@RequestBody CamundaSaveXmlDTO dto) {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(dto.getXml().getBytes(StandardCharsets.UTF_8));
            processDefinitionService.importFile(dto.getName(), dto.getCategory(), in);
        } catch (Exception e) {
            return ResultVO.success(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }
        }

        return ResultVO.success("导入成功");
    }


    @ApiOperation(value = "根据流程定义id启动流程实例")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(value = "流程定义id",name = "id",required = true,paramType = "path"),
            @ApiImplicitParam(value = "流程定义变量集合",name = "variables",paramType = "body"),
    })
    @PostMapping("/processDefinition/{id}")
    public ResultVO startProcessInstanceById(@ApiParam(value = "流程定义id") @PathVariable(value = "id") String id,
                          @ApiParam(value = "变量集合,json对象") @RequestBody(required = false) Map<String, Object> variables) {
        return processDefinitionService.startProcessInstanceById(id, variables);
    }

    @ApiOperation(value = "删除流程")
    @DeleteMapping(value = "/{deployIds}")
    public ResultVO delete(@PathVariable String[] deployIds) {
        for (String deployId : deployIds) {
            processDefinitionService.delete(deployId);
        }
        return ResultVO.success();
    }

    @ApiOperation(value = "激活或挂起流程定义")
    @PutMapping(value = "/updateState")
    public ResultVO updateState(@ApiParam(value = "1:激活,2:挂起", required = true) @RequestParam Integer state,
                                @ApiParam(value = "流程部署ID", required = true) @RequestParam String deployId) {
        processDefinitionService.updateState(state, deployId);
        return ResultVO.success();
    }
}
