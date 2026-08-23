package com.troy.file.controller.api;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.file.service.SysFileService;
import com.troy.system.api.domain.VO.SysFileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 10:10:27
 * @Description: SysFileController
 * @Version: 1.0.0
 */
@Api(tags = "文件请求处理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysFileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysFileController.class);

    @Resource(name = "minioService")
    private SysFileService sysFileService;

    @ApiOperation(value = "文件上传请求")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "file")
    public ResultVO<SysFileVO> upload(MultipartFile file) {
        try {
            // 上传并返回访问地址
            return ResultVO.success(sysFileService.uploadFile(file));
        } catch (Exception e) {
            LOGGER.error("上传文件失败", e);
            return ResultVO.fail(e.getMessage());
        }
    }
}
