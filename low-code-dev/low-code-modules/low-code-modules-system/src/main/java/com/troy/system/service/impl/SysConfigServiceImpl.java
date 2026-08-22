package com.troy.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.common.redis.constants.BaseRedisConstants;
import com.troy.common.redis.service.RedisService;
import com.troy.system.api.domain.VO.SysConfigVO;
import com.troy.system.dao.SysConfigDao;
import com.troy.system.domain.DTO.SysConfigDTO;
import com.troy.system.domain.DTO.SysConfigQueryDTO;
import com.troy.system.entity.SysConfigEntity;
import com.troy.system.service.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 参数配置表 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private RedisService redisService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<SysConfigEntity> list = sysConfigDao.list();
        if (StringUtils.isNotEmpty(list)) {
            list.forEach(config -> {
                if (StringUtils.isNotBlank(config.getConfigKey())) {
                    redisService.setCacheObject(buildConfigCacheKey(config), config.getConfigValue());
                }
            });
        }
    }

    @Override
    public PageVO<SysConfigVO> getSysConfigPage(SysConfigQueryDTO dto) {
        Page<SysConfigEntity> page = this.sysConfigDao.getSysConfigPage(dto);
        return PageUtils.convertPageVo(page, SysConfigVO.class);
    }

    @Transactional
    @Override
    public ResultVO insertSysConfig(SysConfigDTO dto) {
        SysConfigEntity sysConfigEntity = new SysConfigEntity();
        BeanUtils.copyProperties(dto, sysConfigEntity);
        this.sysConfigDao.save(sysConfigEntity);
        init();
        return ResultVO.success();
    }

    @Override
    public SysConfigVO getSysConfigById(Long id) {
        SysConfigEntity sysConfigEntity = this.sysConfigDao.getById(id);
        return this.sysConfigCopy(sysConfigEntity);
    }

    @Transactional
    @Override
    public ResultVO editSysConfig(Long id, SysConfigDTO dto) {
        SysConfigEntity sysConfigEntity = this.sysConfigDao.getById(id);
        if (StringUtils.isNull(sysConfigEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.CONFIG));
        }
        dto.setConfigKey(sysConfigEntity.getConfigKey());
        BeanUtils.copyProperties(dto, sysConfigEntity);
        this.sysConfigDao.updateById(sysConfigEntity);
        init();
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO deleteSysConfigById(List<Long> ids) {
        List<SysConfigEntity> entities = sysConfigDao.listByIds(ids);
        if (StringUtils.isNotEmpty(entities)){
            boolean hasBasic = entities.stream().anyMatch(entity -> Constants.ZERO_STR.equals(entity.getIsBasic()));
            if (!hasBasic){
                this.sysConfigDao.removeByIds(ids);
            }
        }
        return ResultVO.success();
    }

    @Override
    public List<SysConfigVO> getList() {
        return sysConfigBatchCopy(sysConfigDao.list());
    }

    @Override
    public List<SysConfigVO> findBySysConfigByConfigKeyIn(List<String> configKeys) {
        List<SysConfigEntity> sysConfigEntities = this.sysConfigDao.findBySysConfigByConfigKeyIn(configKeys);
        return this.sysConfigBatchCopy(sysConfigEntities);
    }

    @Override
    public List<SysConfigVO> findByTenantId(Long tenantId) {
        List<SysConfigEntity> sysConfigEntities = this.sysConfigDao.findByTenantId(tenantId);
        return this.sysConfigBatchCopy(sysConfigEntities);
    }

    private SysConfigVO sysConfigCopy(SysConfigEntity sysConfig) {
        SysConfigVO vo = null;
        if (StringUtils.isNotNull(sysConfig)) {
            vo = new SysConfigVO();
            BeanUtils.copyProperties(sysConfig, vo);
        }
        return vo;
    }

    private List<SysConfigVO> sysConfigBatchCopy(List<SysConfigEntity> sysConfigs) {
        if (StringUtils.isEmpty(sysConfigs)) {
            return new ArrayList<>();
        }
        return sysConfigs.stream().map(config -> {
            SysConfigVO vo = new SysConfigVO();
            BeanUtils.copyProperties(config, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private String buildConfigCacheKey(SysConfigEntity configEntity) {
        return BaseRedisConstants.CONFIG + configEntity.getTenantId() + ":" + configEntity.getConfigKey();
    }
}
