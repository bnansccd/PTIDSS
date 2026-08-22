package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteSsoService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/15 15:15:28
 * @Description: RemoteDictFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteSsoFallbackFactory implements FallbackFactory<RemoteSsoService> {

    @Override
    public RemoteSsoService create(Throwable cause) {
        return new RemoteSsoService() {

            @Override
            public ResultVO<String> code(String code, String source, String domain) {
                return new ResultVO<>();
            }
        };
    }
}
