package com.troy.form.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.form.service.RequestService;
import com.troy.form.domain.DTO.FormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenxl
 * @date 2023/11/14
 */
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"request/addForm")
    public ResultVO save(@RequestBody FormDTO formDTO) {
        requestService.addRequest(formDTO);
        return ResultVO.success();
    }
}
