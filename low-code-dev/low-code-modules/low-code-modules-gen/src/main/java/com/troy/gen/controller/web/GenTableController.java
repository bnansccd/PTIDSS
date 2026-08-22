package com.troy.gen.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.DTO.PageDTO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.gen.service.GenTableService;
import com.troy.gen.domain.DTO.DbTableDTO;
import com.troy.gen.domain.DTO.GenTableDTO;
import com.troy.gen.domain.VO.DbTableVO;
import com.troy.gen.domain.VO.GenTableVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 代码生成业务表 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Api(tags = "代码生成")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class GenTableController {

    @Autowired
    private GenTableService genTableService;

    @ApiOperation(value = "查询代码生成列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable")
    public ResultVO<PageVO<GenTableVO>> genTableList(PageDTO dto) {
        return ResultVO.success(this.genTableService.genTableList(dto));
    }

    @ApiOperation(value = "修改代码生成业务（回显）")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/{id}")
    public ResultVO<GenTableVO> findById(@PathVariable Long id) {
        return ResultVO.success(this.genTableService.findById(id));
    }


    @ApiOperation(value = "查询数据库列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "db/list")
    public ResultVO<PageVO<DbTableVO>> dataList(@Validated DbTableDTO dto) {
        return ResultVO.success(this.genTableService.dataList(dto));
    }

    @ApiOperation(value = "导入表结构（保存）")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/import")
    @ApiImplicitParam(value = "表名" , name = "tableNames" , paramType = "body")
    public ResultVO importTableSave(@Validated @RequestBody @NotEmpty(message = "数据表名不能为空") List<String> tableNames) {
        return this.genTableService.importTableSave(tableNames);
    }

    @ApiOperation(value = "修改保存代码生成业务")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/{id}")
    @ApiImplicitParam(value = "主键" , name = "id" , paramType = "path")
    public ResultVO editGenTable(@PathVariable Long id, @Validated @RequestBody GenTableDTO dto) {
        return this.genTableService.editGenTable(id, dto);
    }

    @ApiOperation(value = "删除代码生成")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/ids/{ids}")
    @ApiImplicitParam(value = "主键" , name = "ids" , paramType = "path")
    public ResultVO deleteByIds(@PathVariable List<Long> ids) {
        return this.genTableService.deleteByIds(ids);
    }

    @ApiOperation(value = "预览代码")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/preview/{id}")
    @ApiImplicitParam(value = "主键" , name = "id" , paramType = "path")
    public ResultVO preview(@PathVariable Long id) {
        return ResultVO.success(this.genTableService.previewCode(id));
    }

    @ApiOperation(value = "生成代码（下载方式）")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/download/{id}")
    @ApiImplicitParam(value = "主键" , name = "id" , paramType = "path")
    public void download(HttpServletResponse response, @PathVariable Long id) throws IOException {
        byte[] bytes = this.genTableService.downloadCode(id);
        genCode(response, bytes);
    }

    @ApiOperation(value = "生成代码（自定义路径）")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/genCode/{id}")
    @ApiImplicitParam(value = "主键" , name = "id" , paramType = "path")
    public ResultVO genCode(@PathVariable Long id) {
        return this.genTableService.generatorCode(id);
    }

    @ApiOperation(value = "同步数据库")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/synchDb/{id}")
    @ApiImplicitParam(value = "主键" , name = "id" , paramType = "path")
    public ResultVO synchDb(@PathVariable Long id) {
        return this.genTableService.synchDb(id);
    }

    @ApiOperation(value = "批量生成代码")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "genTable/batchGenCode/{ids}")
    @ApiImplicitParam(value = "主键" , name = "ids" , paramType = "path")
    public void batchGenCode(HttpServletResponse response, @PathVariable List<Long> ids) throws IOException {
        byte[] bytes = this.genTableService.batchGenCode(ids);
        this.genCode(response, bytes);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition" , "attachment; filename=\"zdwy.zip\"");
        response.addHeader("Content-Length" , "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}
