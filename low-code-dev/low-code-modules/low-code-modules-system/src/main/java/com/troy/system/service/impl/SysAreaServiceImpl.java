package com.troy.system.service.impl;

import com.troy.system.domain.DTO.SysAreaQueryDTO;
import com.troy.system.domain.VO.SysAreaVO;
import com.troy.system.service.SysAreaService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/17 10:10:36
 * @Description: SysAreaServiceImpl
 * @Version: 1.0.0
 */
@Service
public class SysAreaServiceImpl implements SysAreaService {

    @Override
    public List<SysAreaVO> sysAreaList(SysAreaQueryDTO dto) {
        return Collections.emptyList();
    }
}
