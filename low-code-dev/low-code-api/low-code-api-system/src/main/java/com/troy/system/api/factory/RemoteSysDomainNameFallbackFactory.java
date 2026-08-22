package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteSysDomainNameService;
import com.troy.system.api.domain.VO.SysDomainNameVO;
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
public class RemoteSysDomainNameFallbackFactory implements FallbackFactory<RemoteSysDomainNameService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteSysDomainNameFallbackFactory.class);

    @Override
    public RemoteSysDomainNameService create(Throwable throwable) {
        LOGGER.error("域名服务调用失败:{}", throwable.getMessage());
        return new RemoteSysDomainNameService() {

            @Override
            public ResultVO<SysDomainNameVO> findByDomainNameOrUniversalDomainName(String domainName, String source) {
                return ResultVO.fail();
            }
        };
    }
}
