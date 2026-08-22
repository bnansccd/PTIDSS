package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysLogininfoQueryDTO;
import com.troy.system.entity.SysLogininforEntity;

import java.util.Date;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:57
 * @Description: SysLogininforDao
 * @Version: 1.0.0
 */
public interface SysLogininforDao extends BaseService<SysLogininforEntity> {

    /**
     * @author yzy
     * @description 分页查询
     * @date  2022/9/19
     * @param dto
     * @return
     * @version
     */
    Page<SysLogininforEntity> getSysLogininforPage(SysLogininfoQueryDTO dto);

    /**
     * 获取列表
     * @param startDate
     * @param endDate
     * @return
     */
    List<SysLogininforEntity> getSysLogininforList(String userName, Date startDate, Date endDate);
}
