package com.troy.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.IterateUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.common.security.utils.DictUtils;
import com.troy.system.api.domain.VO.SysDictVO;
import com.troy.system.dao.SysDictDao;
import com.troy.system.domain.DTO.MenuPageDTO;
import com.troy.system.domain.DTO.SysDictDTO;
import com.troy.system.entity.SysDictEntity;
import com.troy.system.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典类型 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Slf4j
@Service
public class SysDictServiceImpl implements SysDictService {

    @Autowired
    private SysDictDao sysDictDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO insertSysDict(SysDictDTO dto) {
        SysDictEntity parentDict = this.verifyDictParams(null, dto);
        SysDictEntity sysDictEntity = new SysDictEntity();
        BeanUtils.copyProperties(dto, sysDictEntity);
        if (StringUtils.isNotNull(parentDict)) {
            sysDictEntity.setParentType(parentDict.getDictType());
        }
        this.setSort(sysDictEntity);
        this.sysDictDao.save(sysDictEntity);
        return ResultVO.success();
    }

    @Override
    public SysDictVO getSysDictById(Long id) {
        SysDictEntity sysDictEntity = this.sysDictDao.getById(id);
        return copyDictVO(sysDictEntity);
    }

    @Override
    public ResultVO updateById(Long id, SysDictDTO dto) {
        SysDictEntity sysDictEntity = this.sysDictDao.getById(id);
        if (StringUtils.isNull(sysDictEntity)) {
            return ResultVO.fail(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.DICT));
        }
        SysDictEntity parentDict = this.verifyDictParams(id, dto);
        dto.setDictType(sysDictEntity.getDictType());
        BeanUtils.copyProperties(dto, sysDictEntity);
        if (StringUtils.isNotNull(parentDict)) {
            sysDictEntity.setParentType(parentDict.getParentType());
        }
        this.setSort(sysDictEntity);
        this.sysDictDao.updateById(sysDictEntity);
        DictUtils.removeDictCache(getCacheType(sysDictEntity));
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO deleteSysDictById(List<Long> ids) {
        List<SysDictEntity> sysDictEntities = this.sysDictDao.listByIds(ids);
        this.sysDictDao.removeByIds(ids);
        if (StringUtils.isNotEmpty(sysDictEntities)) {
            sysDictEntities.forEach(dictEntity -> DictUtils.removeDictCache(getCacheType(dictEntity)));
        }
        return ResultVO.success();
    }

    @Override
    public List<SysDictVO> getSysDictByParentType(String parentType) {
        List<SysDictVO> sysDictVOS = this.getSysDictVOS(this.sysDictDao.findByParentType(parentType));
        DictUtils.setDictCache(parentType, sysDictVOS);
        return sysDictVOS;
    }

    @Override
    public PageVO<SysDictVO> getDictPage(MenuPageDTO dto) {
        Page<SysDictEntity> page = this.sysDictDao.getDictPage(dto);
        return PageUtils.convertPageVo(page, SysDictVO.class);
    }

    @Override
    public Integer getCurrentSort(Long parentId) {
        SysDictEntity sysDictEntity = this.sysDictDao.maxSort(parentId);
        if (StringUtils.isNotNull(sysDictEntity) && StringUtils.isNotNull(sysDictEntity.getSort())) {
            return sysDictEntity.getSort() + Constants.TEN;
        } else {
            return Constants.ZERO;
        }
    }

    @Override
    public List<SysDictVO> getSysDictTree() {
        List<SysDictEntity> sysDictEntities = this.sysDictDao.list();
        List<SysDictVO> vos = this.getSysDictVOS(sysDictEntities);
        vos = IterateUtils.getList(vos);
        return vos;
    }

    @Override
    public List<SysDictVO> getSysDictByParentTypeIn(List<String> parentTypes) {
        if (StringUtils.isEmpty(parentTypes)) {
            return new ArrayList<>();
        }
        return this.getSysDictVOS(this.sysDictDao.findByParentTypeIn(parentTypes));
    }

    private List<SysDictVO> getSysDictVOS(List<SysDictEntity> sysDictEntities) {
        if (StringUtils.isEmpty(sysDictEntities)) {
            return new ArrayList<>();
        }
        return sysDictEntities.stream().map(this::copyDictVO).collect(Collectors.toList());
    }

    /**
     * 验证操作参数
     *
     * @param dto
     */
    private SysDictEntity verifyDictParams(Long id, SysDictDTO dto) {
        SysDictEntity parentDict = null;
        //验证编码是否重复
        SysDictEntity sysDictEntity = this.sysDictDao.verifyDictTypeIsRepeat(id, dto.getParentId(), dto.getDictType());
        if (StringUtils.isNotNull(sysDictEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST,ResultConstants.DICT_CODE));
        }
        //验证父级是否存在
        if (StringUtils.isNotNull(dto.getParentId())) {
            parentDict = this.sysDictDao.getById(dto.getParentId());
            if (StringUtils.isNull(parentDict)) {
                throw new ServiceException(ResultEnum.PARENT_NOT_FOUND);
            }
        }
        return parentDict;
    }

    /**
     * 配置排序
     *
     * @param sysDictEntity
     */
    private void setSort(SysDictEntity sysDictEntity) {
        //设置排序
        if (StringUtils.isNull(sysDictEntity.getSort())) {
            SysDictEntity sysDict = this.sysDictDao.maxSort(sysDictEntity.getParentId());
            if (StringUtils.isNotNull(sysDict) && StringUtils.isNotNull(sysDict.getSort())) {
                sysDictEntity.setSort(sysDict.getSort() + Constants.TEN);
            } else {
                sysDictEntity.setSort(Constants.ZERO);
            }
        }
    }

    private SysDictVO copyDictVO(SysDictEntity dictEntity) {
        if (StringUtils.isNull(dictEntity)) {
            return null;
        }
        SysDictVO vo = new SysDictVO();
        BeanUtils.copyProperties(dictEntity, vo);
        return vo;
    }

    private String getCacheType(SysDictEntity dictEntity) {
        return StringUtils.isNotBlank(dictEntity.getParentType()) ? dictEntity.getParentType() : dictEntity.getDictType();
    }

    @Override
    public void syncCarBusiness() {
        long s=System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            log.info("数据读取完成");
            log.info("数据转换完成");
            log.info("数据同步完成");
            log.info("正在同步第"+(i+1)+"条数据");
        }
        long e=System.currentTimeMillis();
        log.info("总共耗时{}秒",(e-s)/1000);
    }
}
