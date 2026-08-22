package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteCaptchaService;
import com.troy.system.api.domain.DTO.OverrunMsgDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteCaptchaFactory implements FallbackFactory<RemoteCaptchaService> {
    @Override
    public RemoteCaptchaService create(Throwable cause) {
        return new RemoteCaptchaService() {
            @Override
            public ResultVO sendMsg(String phone, Long key, String source) {
                return null;
            }

            @Override
            public ResultVO sendOverrunMsg(List<OverrunMsgDTO> dtos, String source) {
                return null;
            }


        };

    }
}
