package com.troy.system.api.factory;

import com.troy.system.api.RemoteSysPostService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteSysPostFallbackFactory implements FallbackFactory<RemoteSysPostService> {
    @Override
    public RemoteSysPostService create(Throwable cause) {
        return new RemoteSysPostService() {
        };
    }
}
