package com.troy.file.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.file.FileTypeUtils;
import com.troy.common.core.utils.file.FileUtils;
import com.troy.common.file.utils.FileUploadUtils;
import com.troy.file.service.SysFileService;
import com.troy.system.api.domain.VO.SysFileVO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 10:10:34
 * @Description: FastDFS 文件存储
 * @Version: 1.0.0
 */
@Service(value = "fastDfsService")
public class FastDfsSysFileServiceImpl implements SysFileService {

    /**
     * 域名或本机访问地址
     */
    @Value("${fdfs.domain}")
    public String domain;

    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * FastDfs文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public SysFileVO uploadFile(MultipartFile file) throws Exception {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);
        SysFileVO vo = new SysFileVO();
        vo.setName(FileUtils.getName(domain + "/" + storePath.getFullPath()));
        vo.setFilePath(storePath.getFullPath());
        vo.setSize(FileUploadUtils.readableFileSize(file.getSize()));
        vo.setSuffix(FileTypeUtils.getExtension(file));
        vo.setUrl(domain + "/" + storePath.getFullPath());
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
