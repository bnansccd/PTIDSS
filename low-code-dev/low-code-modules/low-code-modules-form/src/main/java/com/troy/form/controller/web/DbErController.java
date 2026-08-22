package com.troy.form.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.form.service.DbErService;
import com.troy.form.domain.DTO.DbErDTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *  控制层。
 *
 * @author zhuqing
 * @since 2023-10-19 14:19:33
 */
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Api(tags = "E-R模型管理")
public class DbErController {

    @Autowired
    private DbErService dbErService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"er")
    public ResultVO save(@Validated  @RequestBody DbErDTO dbErDTO) {
        dbErService.addErModel(dbErDTO);
        return ResultVO.success();
    }


    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"er/{id}")
    public ResultVO update(@PathVariable Long id,  @Validated  @RequestBody DbErDTO dbErDTO) {
        dbErService.updateErModel(id, dbErDTO);
        return ResultVO.success();
    }
}
