package com.troy.auth.api.factory;

import com.troy.auth.api.RemoteApiTokenService;
import com.troy.auth.api.doamin.DTO.InnerLoginDTO;
import com.troy.auth.api.doamin.VO.LoginInfoVO;
import com.troy.common.core.domain.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2026/3/31 11:37
 * @Version: 1.0
 **/
@Slf4j
@Component
public class RemoteApiTokenFallbackFactory  implements FallbackFactory<RemoteApiTokenService> {
    @Override
    public RemoteApiTokenService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteApiTokenService() {
            @Override
            public ResultVO<LoginInfoVO> login(InnerLoginDTO dto, String source) {
                return ResultVO.fail();
            }
        };
    }
}

