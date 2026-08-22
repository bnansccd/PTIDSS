package com.troy.file.service.impl;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.file.FileTypeUtils;
import com.troy.common.core.utils.file.FileUtils;
import com.troy.common.file.utils.FileUploadUtils;
import com.troy.common.file.utils.MinioUtils;
import com.troy.file.service.SysFileService;
import com.troy.file.utils.ExecutableFileUtil;
import com.troy.system.api.domain.VO.SysFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 10:10:37
 * @Description: Minio 文件存储
 * @Version: 1.0.0
 */
@Service(value = "minioService")
public class MinioSysFileServiceImpl implements SysFileService {

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public SysFileVO uploadFile(MultipartFile file) throws Exception {
        // 获取文件的 MIME 类型
        String mimeType = file.getContentType();
        // 获取文件名
        String fileName = file.getOriginalFilename();

        // 检查 MIME 类型和扩展名
        if (ExecutableFileUtil.isExecutableFile(mimeType, fileName)) {
            throw new ServiceException(ResultEnum.BE_CURRENT, "该类型文件不允许上传！");
        }


        String path = this.minioUtils.uploadFile(file);
        SysFileVO vo = new SysFileVO();
        vo.setName(FileUtils.getName(path));
        vo.setFilePath(path);
        vo.setSize(FileUploadUtils.readableFileSize(file.getSize()));
        vo.setSuffix(FileTypeUtils.getExtension(file));
        vo.setUrl(this.minioUtils.getMinioConfig().getUrl() + "/" + this.minioUtils.getMinioConfig().getBucketName() + path);
        return vo;
    }

    @Override
    public byte[] getFile(String name) throws Exception {
        InputStream inputStream = minioUtils.getFile(name);
        if (inputStream == null) {
            return null;
        }
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] byteArray = null;
        try {
            byte[] buff = new byte[100];
            int rc;
            while ((rc = inputStream.read(buff, 0, buff.length)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byteArray = swapStream.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (StringUtils.isNotNull(swapStream)){
                swapStream.close();
            }
            if (StringUtils.isNotNull(inputStream)){
                inputStream.close();
            }
        }
        return byteArray;
    }

    @Override
    public ResultVO deleteFile(String name) throws Exception {
        minioUtils.removeObject(name);
        return ResultVO.success();
    }


}
