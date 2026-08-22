package com.troy.sync.service;

import com.mybatisflex.core.row.Row;
import com.troy.sync.domain.DTO.SyncDTO;
import com.troy.sync.domain.DTO.SyncScriptDTO;
import com.troy.sync.entity.DatasourceEntity;

import java.util.List;

public interface SyncService {

    /**
     * 同步
     */
    void sync(SyncDTO syncDTO);


    /**
     * 更新
     * @param syncDTO
     */
    void syncScript(SyncScriptDTO syncDTO);

    /**
     * 处理全量
     * @param syncDTO
     */
    void dealTotal(SyncDTO syncDTO, List<DatasourceEntity> list);

    /**
     * 处理增量
     * @param syncDTO
     */
    void dealIncrement(SyncDTO syncDTO, List<DatasourceEntity> list);

    /**
     * 远端同步
     * @param syncDTO
     */
    void syncRpc(SyncDTO syncDTO);


    /**
     * 处理全量
     * @param syncDTO
     */
    void dealTotalRpc(SyncDTO syncDTO, List<DatasourceEntity> list);


    /**
     * 处理全量
     * @param syncDTO
     */
    void dealIncrementRpc(SyncDTO syncDTO, List<DatasourceEntity> list);


    void syncScriptRpc(SyncScriptDTO syncDTO);

    /**
     * 获取
     * @param syncDTO
     * @return
     */
    List<Row> getSyncScript(SyncScriptDTO syncDTO);

}
