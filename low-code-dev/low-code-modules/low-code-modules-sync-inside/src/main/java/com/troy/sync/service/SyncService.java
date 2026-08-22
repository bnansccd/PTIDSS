package com.troy.sync.service;

import com.mybatisflex.core.row.Row;
import com.troy.sync.api.domain.DTO.SearchDTO;

import java.util.List;

public interface SyncService {

    /**
     * 同步
     */
    List<Row> sync(String tableName, SearchDTO empty);


    /**
     * 获取
     * @param tableName
     * @param empty
     * @return
     */
    List<Row> getSyncIncrease(String tableName, SearchDTO empty);


    /**
     * 获取
     * @param script
     * @return
     */
    List<Row> findByScript(String script);


}
