package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteSysUserService;
import com.troy.system.api.domain.DTO.AuditDTO;
import com.troy.system.api.domain.DTO.RegisterDTO;
import com.troy.system.api.domain.VO.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author y
 * @Auther: zhuqing
 * @Date: 2022/8/9 14:14:28
 * @Description: RemoteSysUserFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteSysUserFallbackFactory implements FallbackFactory<RemoteSysUserService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteSysUserFallbackFactory.class);

    @Override
    public RemoteSysUserService create(Throwable throwable) {
        LOGGER.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteSysUserService() {

            @Override
            public ResultVO<SysUserDetailsVO> sysUserByUsernameAndTenantId(String username, Long tenantId, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<SysUserDetailsVO> sysUserByPhoneAndTenantId(String phone, Long tenantId, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<SysUserVO> findById(Long id) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO sysUserRegister(RegisterDTO dto, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<AuditVO> findAuditInfo(AuditDTO dto, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysUserVO>> sysUserByRealNameAndTenantId(String username, Long tenantId, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysUserVO>> sysUserByIds(List<Long> ids, Long tenantId, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysUserVO>> byDepartIdsAndUsername(List<Long> ids, String name, Long tenantId, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysSpecialGroupVO>> group(List<Long> list, Long tenantId, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysUserVO>> byDepartIdsAndRealName(List<Long> ids, String name, Long tenantId, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysUserVO>> getAll(String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<List<SysUserVO>> getByRealNameIn(List<String> names, String source) {
                return ResultVO.fail();
            }

        };
    }
}
