package com.troy.file.service.impl;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.file.FileTypeUtils;
import com.troy.common.core.utils.file.FileUtils;
import com.troy.common.file.utils.FileUploadUtils;
import com.troy.file.service.SysFileService;
import com.troy.system.api.domain.VO.SysFileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 10:10:36
 * @Description: 本地文件存储
 * @Version: 1.0.0
 */
@Service(value = "localService")
public class LocalSysFileServiceImpl implements SysFileService {
    /**
     * 资源映射路径 前缀
     */
    @Value("${file.prefix}")
    public String localFilePrefix;

    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    public String domain;

    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public SysFileVO uploadFile(MultipartFile file) throws Exception {
        String name = FileUploadUtils.upload(localFilePath, file);
        SysFileVO vo = new SysFileVO();
        vo.setName(FileUtils.getName(domain + localFilePrefix + name));
        vo.setFilePath(localFilePrefix + name);
        vo.setSize(FileUploadUtils.readableFileSize(file.getSize()));
        vo.setSuffix(FileTypeUtils.getExtension(file));
        vo.setUrl(domain + localFilePrefix + name);
        return vo;
    }

    @Override
    public byte[] getFile(String name) throws Exception {
        return null;
    }

    @Override
    public ResultVO deleteFile(String name) throws Exception {
        return ResultVO.success();
    }
}
