package com.troy.uniRoad;

import com.troy.common.core.domain.ResultVO;
import com.troy.uniRoad.domain.DTO.FbDataConsistencyDTO;

import java.util.List;

/**
 * 远程数据一致性服务
 * 原定义于私有 artifact com.troy:uniRoad-api-system（中移物联网 API），
 * 私有 nexus 不可达后平替为内部接口，由本地实现（LocalFbDataConsistencyServiceImpl）提供。
 * 若后续私有 nexus 恢复或远程服务接入，可直接替换实现类为 Feign/RPC 客户端。
 *
 * @Author: zhuQing
 * @Date: 2026/4/2 10:11
 */
public interface RemoteFbDataConsistencyService {

    /**
     * 上报数据一致性记录
     *
     * @param dtos   数据一致性记录列表
     * @param source 调用来源标识（SecurityConstants.INNER）
     * @return 处理结果
     */
    ResultVO insertFbDataConsistency(List<FbDataConsistencyDTO> dtos, String source);
}
