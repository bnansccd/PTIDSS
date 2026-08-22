package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteDictService;
import com.troy.system.api.domain.VO.SysDictVO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/15 15:15:28
 * @Description: RemoteDictFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteDictFallbackFactory implements FallbackFactory<RemoteDictService> {

    @Override
    public RemoteDictService create(Throwable cause) {
        return new RemoteDictService() {

            @Override
            public ResultVO<List<SysDictVO>> getSysDictByParentType(String parentType, String source) {
                return ResultVO.fail();
            }
        };
    }
}
