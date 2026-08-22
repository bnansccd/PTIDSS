package com.troy.file.api.factory;

import com.troy.file.api.RemoteFileService;
import com.troy.common.core.domain.ResultVO;
import com.troy.file.api.domain.VO.SysFileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 16:16:01
 * @Description: RemoteLogFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileFallbackFactory.class);

    @Override
    public RemoteFileService create(Throwable throwable) {
        LOGGER.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteFileService() {

            @Override
            public ResultVO<SysFileVO> upload(MultipartFile file, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<byte[]> download(String name, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO delete(String name, String source) {
                return ResultVO.fail();
            }
        };

    }
}
