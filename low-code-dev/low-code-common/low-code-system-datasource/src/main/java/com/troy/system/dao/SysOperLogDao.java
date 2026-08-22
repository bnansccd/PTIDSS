package com.troy.system.dao;


import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysOperLogQueryDTO;
import com.troy.system.entity.SysOperLogEntity;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:10
 * @Description: SysOperLogDao
 * @Version: 1.0.0
 */
public interface SysOperLogDao extends BaseService<SysOperLogEntity> {

    /**
     * @author yzy
     * @description 分页列表
     * @date  2022/9/19
     * @param
     * @return
     * @version
     */
    Page<SysOperLogEntity> getSysOperLogPage(SysOperLogQueryDTO dto);

}

