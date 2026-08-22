package com.troy.sync.api;

import com.mybatisflex.core.row.Row;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.sync.api.domain.DTO.SearchDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/** @author chenxl */
@FeignClient(
    contextId = "remoteSyncInsideService",
    path = UrlConstants.RPC_RESTFUL,
    value = ServiceNameConstants.SYNC_INSIDE_SERVICE)
public interface RemoteSyncInsideService {


    /**
     * 获取
     * @param tableName
     * @param searchDTO
     * @return
     */
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "getSync")
    ResultVO<List<Row>> getSync(@RequestParam("tableName") String tableName, @RequestBody SearchDTO searchDTO);

    /**
     * 获取
     * @param tableName
     * @param condition
     * @return
     */
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "getSyncIncrease")
    ResultVO<List<Row>> getSyncIncrease(@RequestParam("tableName") String tableName, @RequestBody SearchDTO condition);


    /**
     * 获取
     * @param script
     * @return
     */
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "getSyncByScript")
    ResultVO<List<Row>> getSyncByScript(@RequestBody String script);

}
