package com.troy.system.service;

import com.troy.system.domain.DTO.SysAreaQueryDTO;
import com.troy.system.domain.VO.SysAreaVO;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/17 10:10:36
 * @Description: SysAreaService
 * @Version: 1.0.0
 */
public interface SysAreaService {

    /**
     * 查询区域列表
     * @param dto
     * @return
     */
    List<SysAreaVO> sysAreaList(SysAreaQueryDTO dto);
}
