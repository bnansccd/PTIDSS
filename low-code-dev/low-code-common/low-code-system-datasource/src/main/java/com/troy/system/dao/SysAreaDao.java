package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysAreaEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/16 20:20:13
 * @Description: SysAreaDao
 * @Version: 1.0.0
 */
public interface SysAreaDao extends BaseService<SysAreaEntity> {

    /**
     * 通过编码查询区域
     *
     * @param adcode
     * @return
     */
    SysAreaEntity findByAdcode(String adcode);

    /**
     * 通过父级id查询区域
     *
     * @param parentCode
     * @return
     */
    List<SysAreaEntity> findByParentCode(String parentCode);
}
