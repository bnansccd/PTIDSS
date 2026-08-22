package com.troy.common.core.exception.file;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:27
 * 文件名大小限制异常类
 */
public class FileSizeLimitExceededException extends FileException {

    private static final long serialVersionUID = 1L;

    public FileSizeLimitExceededException(long defaultMaxSize) {
        super("upload.exceed.maxSize", new Object[]{defaultMaxSize});
    }
}
