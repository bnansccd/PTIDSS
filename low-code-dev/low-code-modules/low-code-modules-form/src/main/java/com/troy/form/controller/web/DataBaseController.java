package com.troy.form.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.form.service.DatasourceService;
import com.troy.form.domain.DTO.DatasourceDTO;
import com.troy.form.domain.DTO.DatasourceSearchDTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Api(tags = "数据源管理")
public class DataBaseController {

    @Autowired
    private DatasourceService datasourceService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"database")
    public ResultVO save(@RequestBody DatasourceDTO datasourceDTO) {
        datasourceService.addDatasource(datasourceDTO);
        return ResultVO.success();
    }

    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1+"database/{ids}")
    public ResultVO remove(@PathVariable List<Long> ids) {
        datasourceService.deleteDatasource(ids.get(0));
        return ResultVO.success();
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"database/{id}")
    public ResultVO getInfo(@PathVariable Serializable id) {
        return ResultVO.success(datasourceService.getById(id));
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"database")
    public ResultVO page(DatasourceSearchDTO dto){
        return ResultVO.success(datasourceService.findPage(dto));
    }

}
