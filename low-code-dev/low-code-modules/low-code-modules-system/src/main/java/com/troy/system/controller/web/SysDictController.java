package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysDictVO;
import com.troy.system.domain.DTO.MenuPageDTO;
import com.troy.system.domain.DTO.SysDictDTO;
import com.troy.system.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 字典类型 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "字典类型")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    @ApiOperation(value = "字典分页")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/page")
    public ResultVO<PageVO<SysDictVO>> getSysMenuPage(MenuPageDTO dto) {
        return ResultVO.success(sysDictService.getDictPage(dto));
    }

    @ApiOperation(value = "字典树形列表(主要用于下拉框)")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/tree")
    public ResultVO<List<SysDictVO>> getSysDictTree() {
        return ResultVO.success(this.sysDictService.getSysDictTree());
    }

    @ApiOperation(value = "新增字典")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict")
    @Log(title = "新增字典", businessType = BusinessType.INSERT)
    public ResultVO insertSysDict(@Validated @RequestBody SysDictDTO dto) {
        return this.sysDictService.insertSysDict(dto);
    }

    @ApiOperation(value = "查询字典最大排序号")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/sort")
    public ResultVO sort(Long parentId) {
        return ResultVO.success(this.sysDictService.getCurrentSort(parentId));
    }

    @ApiOperation(value = "查看字典详情")
    @ApiImplicitParam(value = "id", name = "id", required = true, paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/{id}")
    public ResultVO<SysDictVO> getSysDictById(@PathVariable Long id) {
        return ResultVO.success(this.sysDictService.getSysDictById(id));
    }

    @ApiOperation(value = "编辑字典")
    @ApiImplicitParam(value = "id", name = "id", required = true, paramType = "path")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/{id}")
    @Log(title = "编辑字典", businessType = BusinessType.UPDATE)
    public ResultVO updateById(@PathVariable Long id, @RequestBody SysDictDTO dto) {
        return this.sysDictService.updateById(id, dto);
    }

    @ApiOperation(value = "批量删除字典")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/batch/{ids}")
    @ApiImplicitParam(value = "ids", name = "ids", required = true, paramType = "path")
    @Log(title = "批量删除字典", businessType = BusinessType.DELETE)
    public ResultVO deleteSysDictById(@PathVariable @NotEmpty(message = "请选择要删除的字典数据！") @Size(max = 1000, message = "一次最多删除1000条字典数据！") List<Long> ids) {
        return this.sysDictService.deleteSysDictById(ids);
    }

    @ApiOperation(value = "根据父编码查询")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/parentType/{parentType}")
    @ApiImplicitParam(value = "parentType", name = "parentType", required = true, paramType = "path")
    public ResultVO<List<SysDictVO>> getSysDictByParentType(@PathVariable String parentType) {
        return ResultVO.success(this.sysDictService.getSysDictByParentType(parentType));
    }

    @ApiOperation(value = "根据一批父编码查询")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/parentTypes/{parentTypes}")
    @ApiImplicitParam(value = "parentTypes", name = "parentTypes", required = true, paramType = "path")
    public ResultVO<List<SysDictVO>> getSysDictByParentTypeIn(@PathVariable List<String> parentTypes) {
        return ResultVO.success(this.sysDictService.getSysDictByParentTypeIn(parentTypes));

    }

}
