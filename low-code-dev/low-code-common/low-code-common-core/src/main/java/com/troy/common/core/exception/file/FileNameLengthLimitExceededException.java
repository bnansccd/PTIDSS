package com.troy.common.core.exception.file;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:26
 * 文件名称超长限制异常类
 */
public class FileNameLengthLimitExceededException extends FileException {

    private static final long serialVersionUID = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super("upload.filename.exceed.length", new Object[]{defaultFileNameLength});
    }
}
