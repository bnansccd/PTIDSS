package com.troy.common.file.utils;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.exception.file.FileException;
import com.troy.common.core.utils.DateUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.file.FileUtils;
import com.troy.common.core.utils.uuid.UUID;
import com.troy.common.file.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 11:11:11
 * @Description: MinioUtil工具类
 * @Version: 1.0.0
 */
@Slf4j
@Component
@Order(1)
public class MinioUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(MinioUtils.class);

    private final MinioClient minioClient;

    private final MinioConfig minioConfig;

    @Autowired
    public MinioUtils(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    public MinioConfig getMinioConfig() {
        return minioConfig;
    }

    /**
     * 创建桶
     *
     * @throws Exception
     */
    public void makeBucket() throws Exception {
        boolean isExist = this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.minioConfig.getBucketName()).build());
        if (!isExist) {
            this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.minioConfig.getBucketName()).build());
        }
    }

    /**
     * 文件上件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = FileUploadUtils.extractFilename(file);
        return uploadFile(file.getInputStream(), fileName, file.getSize(), file.getContentType());
    }

    /**
     * 文件上件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadFile(File file) throws Exception {
        String fileName = FileUploadUtils.extractFilename(file);
        return uploadFile(FileUtils.fileToInputStream(file), fileName, file.length(), "application/octet" + "-stream");
    }

    /**
     * 上传base64图片
     *
     * @param base64String
     * @return
     */
    public String uploadFile(String base64String) {
        try {
            String suffix = null;
            if (StringUtils.startsWith(base64String, "data:image")) {
                String[] split = StringUtils.split(base64String, ",");
                suffix = StringUtils.split(StringUtils.split(split[0], "/")[1], ";")[0];
                base64String = split[1];
            } else {
                suffix = "jpg";
            }
            byte[] bytes = Base64.getMimeDecoder().decode(base64String);
            return uploadFile(bytes, suffix);
        } catch (Exception e) {
            log.error("解析BASE64失败,{}", base64String, e);
        }
        return null;
    }

    /**
     * 文件上件
     *
     * @return
     * @throws Exception
     */
    public String uploadFile(byte[] bytes, String suffix) {
        String dateToStr = DateUtils.parseDateToStr("yyyy/MM/dd", DateUtils.getNowDate());
        String fileName = dateToStr + Constants.SLASH + UUID.fastUUID().toString() + "." + suffix;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return this.uploadFile(byteArrayInputStream, fileName, byteArrayInputStream.available(), "application/octet" + "-stream");
    }


    /**
     * 文件上件
     *
     * @param inputStream
     * @param fileName
     * @param size
     * @param contentType
     * @return
     * @throws Exception
     */
    public String uploadFile(InputStream inputStream, String fileName, long size, String contentType) {
        try {
            this.makeBucket();
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(this.minioConfig.getBucketName())
                    .object(fileName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build();
            this.minioClient.putObject(args);
            inputStream.close();
            return "/" + fileName;
        } catch (Exception e) {
            throw new FileException(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过文件名得到base64的
     *
     * @param prefix
     * @return
     */
    public String getBase64(String prefix) {
        String base64String = null;
        byte[] bytes = getBytes(prefix);
        if (null != bytes && bytes.length > 0) {
            base64String = Base64.getEncoder().encodeToString(bytes);
        } else {
            throw new FileException("下载图片失败");
        }
        return base64String;
    }

    /**
     * 通过文件名与指定桶得到base64的
     *
     * @param prefix
     * @return
     */
    public String getBase64(String prefix, String bucketName) {
        String base64String = null;
        byte[] bytes = getBytes(prefix, bucketName);
        if (null != bytes && bytes.length > 0) {
            base64String = Base64.getEncoder().encodeToString(bytes);
        } else {
            throw new FileException("下载图片失败");
        }
        return base64String;
    }

    /**
     * 通过文件名攻取字节流
     *
     * @param prefix
     * @return
     */
    public byte[] getBytes(String prefix) {
        InputStream inputStream = this.getFile(prefix);
        byte[] byteArray = null;
        if (StringUtils.isNotNull(inputStream)) {
            try {
                byteArray = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                LOGGER.error("{}文件获取失败", prefix, e);
                throw new FileException("下载文件失败");
            } finally {
                if (StringUtils.isNotNull(inputStream)) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return byteArray;
    }

    /**
     * 通过文件名与指定桶攻取字节流
     *
     * @param prefix
     * @return
     */
    public byte[] getBytes(String prefix, String bucketName) {
        InputStream inputStream = this.getFile(prefix, bucketName);
        byte[] byteArray = null;
        if (StringUtils.isNotNull(inputStream)) {
            try {
                byteArray = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                LOGGER.error("{}文件获取失败", prefix, e);
                throw new FileException("下载文件失败");
            } finally {
                if (StringUtils.isNotNull(inputStream)) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return byteArray;
    }

    /**
     * 获取文件
     *
     * @param prefix
     * @return
     */
    public InputStream getFile(String prefix) {
        InputStream inputStream = null;
        try {
            if (StringUtils.isNotBlank(prefix)) {
                this.makeBucket();
                StatObjectResponse statObjectResponse = this.statObject(prefix);
                if (StringUtils.isNotNull(statObjectResponse) && statObjectResponse.size() > 0) {
                    inputStream = this.minioClient.getObject(
                            GetObjectArgs
                                    .builder()
                                    .bucket(this.minioConfig.getBucketName())
                                    .object(prefix)
                                    .build()
                    );
                }
            }
        } catch (Exception e) {
            LOGGER.error("{}文件获取失败", prefix);
        }
        return inputStream;
    }

    /**
     * 获取指定桶的文件文件
     *
     * @param prefix
     * @return
     */
    public InputStream getFile(String prefix, String bucketName) {
        InputStream inputStream = null;
        try {
            if (StringUtils.isNotBlank(prefix)) {
                this.makeBucket();
                StatObjectResponse statObjectResponse = this.statObject(prefix, bucketName);
                if (StringUtils.isNotNull(statObjectResponse) && statObjectResponse.size() > 0) {
                    inputStream = this.minioClient.getObject(
                            GetObjectArgs
                                    .builder()
                                    .bucket(bucketName)
                                    .object(prefix)
                                    .build()
                    );
                }
            }
        } catch (Exception e) {
            LOGGER.error("{}文件获取失败", prefix);
        }
        return inputStream;
    }

    /**
     * 获取对象的元数据
     *
     * @param objectName
     * @return
     * @throws Exception
     */
    public StatObjectResponse statObject(String objectName) throws Exception {
        this.makeBucket();
        return this.minioClient.statObject(
                StatObjectArgs
                        .builder()
                        .bucket(this.minioConfig.getBucketName())
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 获取对象的元数据
     *
     * @param objectName
     * @return
     * @throws Exception
     */
    public StatObjectResponse statObject(String objectName, String bucketName) throws Exception {
        this.makeBucket();
        return this.minioClient.statObject(
                StatObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }


    /**
     * 验证文件是否存在
     *
     * @param prefix
     * @return
     * @throws Exception
     */
    public boolean isFileExit(String prefix) throws Exception {
        this.makeBucket();
        Iterable<Result<Item>> results = this.minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(this.minioConfig.getBucketName())
                        .prefix(prefix)
                        .recursive(false)
                        .build()
        );
        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.isDir()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param objectName
     * @return
     * @throws Exception
     */
    public boolean removeObject(String objectName) throws Exception {
        this.makeBucket();
        this.minioClient.removeObject(
                RemoveObjectArgs
                        .builder()
                        .bucket(this.minioConfig.getBucketName())
                        .object(objectName)
                        .build()
        );
        return true;
    }

    /**
     * 得到文件路径
     *
     * @param objectName
     * @return
     * @throws Exception
     */
    public String getObjectUrl(String objectName) {
        String url = null;
        try {
            this.makeBucket();
            StatObjectResponse statObjectResponse = this.statObject(objectName);
            if (StringUtils.isNotNull(statObjectResponse) && statObjectResponse.size() > 0) {
                url = this.minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs
                                .builder()
                                .method(Method.GET)
                                .bucket(this.minioConfig.getBucketName())
                                .object(objectName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new FileException("获取文件地址失败");
        }
        return url;
    }


}
