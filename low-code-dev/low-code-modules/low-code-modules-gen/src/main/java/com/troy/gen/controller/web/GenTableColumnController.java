package com.troy.gen.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.gen.service.GenTableColumnService;
import com.troy.gen.domain.VO.GenTableColumnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Api(tags = "代码生成字段管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class GenTableColumnController {

    @Autowired
    private GenTableColumnService genTableColumnService;

    @ApiOperation(value = "通过表id查询数据表字段列表")
    @ApiImplicitParam(value = "表id", name = "tableId", required = true)
    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "genTableColumn/tableId/{tableId}")
    public ResultVO<List<GenTableColumnVO>> findByTableId(Long tableId) {
        return ResultVO.success(this.genTableColumnService.findByTableId(tableId));
    }

}
