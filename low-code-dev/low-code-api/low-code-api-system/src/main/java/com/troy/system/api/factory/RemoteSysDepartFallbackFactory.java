package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteSysDepartService;
import com.troy.system.api.domain.DTO.SysDepartDTO;
import com.troy.system.api.domain.VO.SysDepartVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteSysDepartFallbackFactory implements FallbackFactory<RemoteSysDepartService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteSysDepartFallbackFactory.class);

    @Override
    public RemoteSysDepartService create(Throwable cause) {

        LOGGER.error("远程调用部门失败：", cause);
        return new RemoteSysDepartService() {

            @Override
            public ResultVO<List<SysDepartVO>> findByIdIn(List<Long> ids, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<SysDepartVO> findById(Long id, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysDepartVO>> findByDeptNameLike(String deptName) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<SysDepartVO> findSysDepartById(Long id) {
                return null;
            }

            @Override
            public ResultVO<List<SysDepartVO>> findAll(String source) {
                return null;
            }

            @Override
            public ResultVO insertSysDepart(SysDepartDTO dto, String source) {
                return null;
            }

        };
    }
}
