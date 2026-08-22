package com.troy.file.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.VO.SysFileVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 10:10:34
 * @Description: 文件上传接口
 * @Version: 1.0.0
 */
public interface SysFileService {

    /**
     * 文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    SysFileVO uploadFile(MultipartFile file) throws Exception;

    /**
     * 获取文件
     * @param name
     * @return
     * @throws Exception
     */
    byte[] getFile(String name) throws Exception;

    /**
     * 删除文件
     * @param name
     * @return
     * @throws Exception
     */
    ResultVO deleteFile(String name) throws Exception;
}
