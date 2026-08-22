package com.troy.sync.service;

/**
 * @author chenxl
 * @description
 * @date 2024-06-20 10:31
 */
public interface LogService {

    /**
     * 新增
     * @param time
     * @param logInfo
     * @param param
     */
    void addLog(Long time, String logInfo, String param, Boolean success);

}
