package com.troy.uniRoad;

import com.troy.common.core.domain.ResultVO;
import com.troy.uniRoad.domain.DTO.FbDataConsistencyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 远程数据一致性服务本地平替实现
 * 原实现为 uniRoad-api-system 中的 Feign 远程调用（私有 nexus artifact，已无法获取），
 * 平替版在本地记录日志并返回成功，保证数据加密与一致性上报流程可正常执行。
 * 接入远程服务时，替换本实现为 Feign/RPC 客户端即可，调用方无需改动。
 *
 * @Author: zhuQing
 * @Date: 2026/4/2 10:11
 */
@Slf4j
@Component
public class LocalFbDataConsistencyServiceImpl implements RemoteFbDataConsistencyService {

    @Override
    public ResultVO insertFbDataConsistency(List<FbDataConsistencyDTO> dtos, String source) {
        if (dtos == null || dtos.isEmpty()) {
            return ResultVO.success();
        }
        log.info("数据一致性上报（本地平替，共 {} 条，来源 {}）: {}", dtos.size(), source, dtos);
        return ResultVO.success();
    }
}
