package com.troy.system.service.impl;

import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.redis.constants.BaseRedisConstants;
import com.troy.common.redis.service.RedisService;
import com.troy.system.api.domain.VO.BasicInfoVO;
import com.troy.system.api.domain.VO.SysConfigVO;
import com.troy.system.api.domain.VO.SysDomainNameVO;
import com.troy.system.api.domain.VO.SysTenantVO;
import com.troy.system.dao.SysDomainNameDao;
import com.troy.system.domain.DTO.SysDomainNameDTO;
import com.troy.system.entity.SysDomainNameEntity;
import com.troy.system.service.SysConfigService;
import com.troy.system.service.SysDomainNameService;
import com.troy.system.service.SysTenantService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 服务层实现。
 *
 * @author zhuqing
 * @since 2023-10-08 13:54:15
 */
@Service
public class SysDomainNameServiceImpl implements SysDomainNameService {

    @Autowired
    private SysDomainNameDao sysDomainNameDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    @Lazy
    private SysTenantService sysTenantService;

    @Autowired
    private SysConfigService sysConfigService;


    @PostConstruct
    public void init(){
        try {
            TenantManager.ignoreTenantCondition();
            refreshDomainCache(sysDomainNameDao.list());
        } finally {
            TenantManager.restoreTenantCondition();
        }
    }

    @Override
    public SysDomainNameVO findByTenantId(Long tenantId) {
        SysDomainNameEntity sysDomainNameEntity = this.sysDomainNameDao.findByTenantId(tenantId);
        return this.domainNameCopy(sysDomainNameEntity);
    }

    @Transactional
    @Override
    public ResultVO editDomain(Long id, SysDomainNameDTO dto) {
        try {
            TenantManager.ignoreTenantCondition();
            List<SysDomainNameEntity> domainNameEntities = this.sysDomainNameDao.validRepair(id, dto.getDomainName(), dto.getUniversalDomainName());
            if (StringUtils.isNotEmpty(domainNameEntities)) {
                throw new ServiceException(ResultEnum.EXIST, ResultConstants.DOMAIN_NAME);
            }
            SysDomainNameEntity domainName = this.sysDomainNameDao.getById(id);
            BeanUtils.copyProperties(dto, domainName);
            this.sysDomainNameDao.updateById(domainName);

            init();
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return ResultVO.success();
    }

    @Override
    public ResultVO domainInit(Long tenantId, String code) {
        String universalDomainName = code + Constants.DOMAIN_NAME;
        SysDomainNameEntity sysDomainNameEntity = new SysDomainNameEntity();
        sysDomainNameEntity.setUniversalDomainName(universalDomainName);
        sysDomainNameEntity.setTenantId(tenantId);
        try {
            TenantManager.ignoreTenantCondition();
            this.sysDomainNameDao.save(sysDomainNameEntity);

            init();
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return ResultVO.success();
    }

    @Override
    public BasicInfoVO findByDomainNameOrUniversalDomainName(String domainName) {
        BasicInfoVO vo = null;
        try {
            TenantManager.ignoreTenantCondition();
            vo=new BasicInfoVO();
            SysDomainNameEntity sysDomainNameEntity = this.sysDomainNameDao.findByDomainNameOrUniversalDomainName(domainName);
            SysDomainNameVO  sysDomainNameVO = this.domainNameCopy(sysDomainNameEntity);
            vo.setSysDomainNameVO(sysDomainNameVO);
            if (StringUtils.isNotNull(sysDomainNameVO)&&StringUtils.isNotNull(sysDomainNameVO.getTenantId())){
                SysTenantVO sysTenantVO = this.sysTenantService.findById(sysDomainNameVO.getTenantId());
                List<SysConfigVO> sysConfigVOS = this.sysConfigService.findByTenantId(sysDomainNameVO.getTenantId());
                vo.setSysTenantVO(sysTenantVO);
                vo.setSysConfigVOS(sysConfigVOS);
            }
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return vo;
    }

    /**
     * 数据copy
     *
     * @param sysDomainNameEntity
     * @return
     */
    private SysDomainNameVO domainNameCopy(SysDomainNameEntity sysDomainNameEntity) {
        SysDomainNameVO vo = null;
        if (StringUtils.isNotNull(sysDomainNameEntity)) {
            vo = new SysDomainNameVO();
            BeanUtils.copyProperties(sysDomainNameEntity, vo);
        }
        return vo;
    }

    private void refreshDomainCache(List<SysDomainNameEntity> domainEntities) {
        if (StringUtils.isEmpty(domainEntities)) {
            return;
        }
        domainEntities.forEach(entity -> {
            if (StringUtils.isNotBlank(entity.getUniversalDomainName())) {
                redisService.setCacheObject(BaseRedisConstants.DOMAIN + entity.getUniversalDomainName(), entity.getTenantId());
                redisService.setCacheObject(BaseRedisConstants.TENANT_DOMAIN + entity.getTenantId(), entity.getUniversalDomainName());
            }
        });
    }
}
