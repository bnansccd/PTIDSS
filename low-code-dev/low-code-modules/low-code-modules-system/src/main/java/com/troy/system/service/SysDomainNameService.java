package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.VO.BasicInfoVO;
import com.troy.system.api.domain.VO.SysDomainNameVO;
import com.troy.system.domain.DTO.SysDomainNameDTO;

/**
 * 服务层。
 *
 * @author zhuqing
 * @since 2023-10-08 13:54:15
 */
public interface SysDomainNameService {

    /**
     * 通过租户id查询域名
     *
     * @return
     */
    SysDomainNameVO findByTenantId(Long tenantId);

    /**
     * 修改域名
     *
     * @param id
     * @return
     */
    ResultVO editDomain(Long id, SysDomainNameDTO dto);

    /**
     * 域名初始化
     *
     * @param tenantId
     * @param code
     * @return
     */
    ResultVO domainInit(Long tenantId, String code);

    /**
     * 通过域名查询域名信息
     *
     * @param domainName
     * @return
     */
    BasicInfoVO findByDomainNameOrUniversalDomainName(String domainName);
}
