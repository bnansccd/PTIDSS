package com.troy.sync.api.factory;

import com.mybatisflex.core.row.Row;
import com.troy.common.core.domain.ResultVO;
import com.troy.sync.api.domain.DTO.SearchDTO;
import com.troy.sync.api.RemoteSyncInsideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 16:16:01
 * @Description: RemoteLogFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteSyncInsideFallbackFactory implements FallbackFactory<RemoteSyncInsideService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteSyncInsideFallbackFactory.class);

    @Override
    public RemoteSyncInsideService create(Throwable throwable) {
        LOGGER.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteSyncInsideService() {

            @Override
            public ResultVO<List<Row>> getSync(String tableName, SearchDTO searchDTO) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<Row>> getSyncIncrease(String tableName, SearchDTO condition) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<Row>> getSyncByScript(String script) {
                return ResultVO.fail();
            }
        };

    }
}
