package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteApiSecretService;
import com.troy.system.api.domain.DTO.ApiSecretEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/8 16:16:34
 * @Description: RemoteSysDomainNameFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteApiSecretFallbackFactory implements FallbackFactory<RemoteApiSecretService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteApiSecretFallbackFactory.class);

    @Override
    public RemoteApiSecretService create(Throwable throwable) {
        LOGGER.error("Oauth2 服务调用失败:{}", throwable.getMessage());
        return new RemoteApiSecretService() {
            @Override
            public ResultVO<ApiSecretEntity> code(String orgId, String header) {
                return null;
            }
        };
    }

}
