package com.troy.file.api;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.file.api.domain.VO.SysFileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** @author chenxl */
@FeignClient(
    contextId = "remoteFileService",
    path = UrlConstants.RPC_RESTFUL,
    value = ServiceNameConstants.FILE_SERVICE)
public interface RemoteFileService {

  /**
  * 上传文件
  *
  * @param file
  * @return
  */
  @PostMapping(value = "file", headers = "content-type=" + MediaType.MULTIPART_FORM_DATA_VALUE)
  ResultVO<SysFileVO> upload(@RequestPart("file") MultipartFile file, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

  /**
  * 文件下载
  * @param name
  * @return
  */
  @GetMapping(value = "download")
  ResultVO<byte[]> download(@RequestParam("name") String name, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

  /**
   * 文件删除
   * @param name
   * @return
   */
  @GetMapping(value = "delete")
  ResultVO delete(@RequestParam("name") String name, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
