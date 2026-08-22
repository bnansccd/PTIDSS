package com.ptidss.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.SysRegion;
import com.ptidss.system.mapper.SysRegionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区域管理（DDL 10.5 sys_region；多省配置化核心：评审决议⑤）
 */
@Service
public class SysRegionService {

    private final SysRegionMapper sysRegionMapper;

    public SysRegionService(SysRegionMapper sysRegionMapper) {
        this.sysRegionMapper = sysRegionMapper;
    }

    public List<SysRegion> list(String keyword, String status) {
        LambdaQueryWrapper<SysRegion> qw = new LambdaQueryWrapper<>();
        // 关键字：区域编码或名称（对齐权限列表的编码/名称双匹配）
        qw.and(StrUtils.isNotBlank(keyword), w -> w
                        .like(SysRegion::getRegionCode, keyword)
                        .or()
                        .like(SysRegion::getRegionName, keyword))
                .eq(StrUtils.isNotBlank(status), SysRegion::getStatus, status)
                .orderByAsc(SysRegion::getLaunchOrder);
        return sysRegionMapper.selectList(qw);
    }

    public SysRegion getById(Long id) {
        SysRegion region = sysRegionMapper.selectById(id);
        if (region == null) {
            throw new ServiceException("区域不存在");
        }
        return region;
    }

    public void create(SysRegion region) {
        checkUnique(region);
        // DDL 10.5：status CHECK (enabled/disabled/pending)，空值兜底启用
        if (StrUtils.isBlank(region.getStatus())) {
            region.setStatus("enabled");
        }
        sysRegionMapper.insert(region);
    }

    public void update(SysRegion region) {
        checkUnique(region);
        if (StrUtils.isBlank(region.getStatus())) {
            region.setStatus("enabled");
        }
        sysRegionMapper.updateById(region);
    }

    public void delete(Long id) {
        sysRegionMapper.deleteById(id);
    }

    private void checkUnique(SysRegion region) {
        Long count = sysRegionMapper.selectCount(new LambdaQueryWrapper<SysRegion>()
                .eq(SysRegion::getRegionCode, region.getRegionCode())
                .ne(region.getId() != null, SysRegion::getId, region.getId()));
        if (count != null && count > 0) {
            throw new ServiceException("区域编码已存在：" + region.getRegionCode());
        }
    }
}
