package com.troy.common.core.exception.file;

import com.troy.common.core.exception.base.BaseException;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:25
 * 文件信息异常类
 */
public class FileException extends BaseException {

    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

    public FileException(String message) {
        super(message);
    }

}