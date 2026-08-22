package com.troy.system.api.factory;

import com.troy.system.api.RemoteSysAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * @Auther: zhuqing
 * @Date: 2022/8/9 14:14:28
 * @Description: RemoteSysAppFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteSysAppFallbackFactory implements FallbackFactory<RemoteSysAppService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteSysAppFallbackFactory.class);

    @Override
    public RemoteSysAppService create(Throwable throwable) {
        LOGGER.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteSysAppService() {

        };
    }
}
