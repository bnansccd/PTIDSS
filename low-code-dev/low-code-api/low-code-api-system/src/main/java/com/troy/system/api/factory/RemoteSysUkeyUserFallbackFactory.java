package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteSysUkeyUserService;
import com.troy.system.api.domain.DTO.UkeyUserDTO;
import com.troy.system.api.domain.VO.UkeyUserVO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author sym
 * @since 2024/11/14 16:25
 */
@Component
public class RemoteSysUkeyUserFallbackFactory implements FallbackFactory<RemoteSysUkeyUserService> {
    @Override
    public RemoteSysUkeyUserService create(Throwable cause) {
        return new RemoteSysUkeyUserService() {
            @Override
            public ResultVO<List<UkeyUserVO>> getList(UkeyUserDTO dto) {
                return ResultVO.fail();
            }
        };
    }
}
