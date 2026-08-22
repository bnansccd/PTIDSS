package com.troy.system.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author chenxl
 * @date 2024/1/31
 */
public interface SsoService {

    /**
     * 获取用户
     * @return
     */
    List getUsers(Long timestamp);


    /**
     * 获取机构
     * @return
     */
    List getOrg(Long timestamp);


    /**
     * 同步组织
     * @param timestamp
     */
    void syncOrg(Long timestamp);

    /**
     * Long timestamp
     * @param timestamp
     */
    void syncUser(Long timestamp);


    /**
     * 同步
     * @param timestamp
     */
    void syncUserMulti(Long timestamp) throws ExecutionException, InterruptedException;
}
