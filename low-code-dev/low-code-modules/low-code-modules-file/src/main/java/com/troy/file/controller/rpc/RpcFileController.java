package com.troy.file.controller.rpc;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.file.FileUtils;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.file.service.SysFileService;
import com.troy.system.api.domain.VO.SysFileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author chenxl
 */
@Api(tags = "文件请求处理")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
@Validated
@Slf4j
public class RpcFileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcFileController.class);

    @Resource(name = "minioService")
    private SysFileService sysFileService;

    @InnerAuth
    @ApiOperation(value = "文件上传请求")
    @PostMapping("file")
    public ResultVO<SysFileVO> upload(@RequestPart("file") MultipartFile file) {
        try {
            // 上传并返回访问地址
            return ResultVO.success(sysFileService.uploadFile(file));
        } catch (Exception e) {
            LOGGER.error("上传文件失败", e);
            return ResultVO.fail(e.getMessage());
        }
    }

    @InnerAuth
    @ApiOperation(value = "文件下载请求")
    @GetMapping("download")
    public ResultVO download(@RequestParam("name") String file) throws Exception {
        return ResultVO.success(sysFileService.getFile(file));
    }

    @InnerAuth
    @ApiOperation(value = "文件删除请求")
    @GetMapping("delete")
    public ResultVO delete(@RequestParam("name") String file) throws Exception {
        return sysFileService.deleteFile(file);
    }

    @ApiOperation(value = "文件下载请求")
    @GetMapping("download1")
    public void download(@RequestParam("name") String file, HttpServletResponse response) throws Exception {
        try {
            // 从数据库或其他来源获取数据
            byte[] fileData = sysFileService.getFile(file);
            String fileName = FileUtils.getName(file);

            if (fileData == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "数据不存在");
                return;
            }

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode(fileName, "UTF-8") + "\"");
            response.setContentLength(fileData.length);

            // 直接写入响应流
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(fileData);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            log.error("下载数据失败", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "下载失败");
            } catch (IOException ex) {
                log.error("发送错误响应失败", ex);
            }
        }
    }
}
