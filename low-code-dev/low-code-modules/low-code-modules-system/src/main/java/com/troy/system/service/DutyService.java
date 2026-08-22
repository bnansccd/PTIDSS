package com.troy.system.service;

import com.troy.system.domain.VO.DutyInfoVO;

/**
 * @author chenxl
 * @description
 * @date 2024-05-29 14:18
 */
public interface DutyService {

    /**
     * 获取
     * @return
     */
    DutyInfoVO findToday();

}
