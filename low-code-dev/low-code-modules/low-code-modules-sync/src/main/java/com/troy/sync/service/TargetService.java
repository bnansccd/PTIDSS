package com.troy.sync.service;

import com.troy.sync.domain.DTO.SyncTargetDTO;

/**
 * @author chenxl
 * @description
 * @date 2024-09-06 9:53
 */
public interface TargetService {

    /**
     * 同步
     * @param dto
     */
    void syncTarget(SyncTargetDTO dto);


}
