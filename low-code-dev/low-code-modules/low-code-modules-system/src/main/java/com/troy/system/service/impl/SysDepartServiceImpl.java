package com.troy.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.IterateUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.system.api.domain.VO.SysDepartDetailsVO;
import com.troy.system.api.domain.VO.SysDepartVO;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.dao.SysDepartDao;
import com.troy.system.dao.SysDepartRoleDao;
import com.troy.system.domain.DTO.SysDepartDTO;
import com.troy.system.domain.DTO.SysDepartQueryDTO;
import com.troy.system.domain.DTO.SysDepartSearchDTO;
import com.troy.system.entity.SysDepartEntity;
import com.troy.system.service.SysDepartService;
import com.troy.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门管理 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysDepartServiceImpl implements SysDepartService {

    @Autowired
    private SysDepartDao sysDepartDao;

    @Autowired
    private SysDepartRoleDao sysDepartRoleDao;

    @Autowired
    @Lazy
    private SysUserService sysUserService;

    @Override
    public PageVO<SysDepartVO> findPage(SysDepartSearchDTO dto) {
        Page<SysDepartEntity> page = sysDepartDao.findPage(dto);
        return PageUtils.convertPageVo(page, SysDepartVO.class);
    }

    @Override
    public SysDepartVO findById(Long departId) {
        SysDepartVO vo = null;
        if (StringUtils.isNotNull(departId)) {
            SysDepartEntity sysDepartEntity = this.sysDepartDao.getById(departId);
            vo = this.sysDepartVOCopy(sysDepartEntity);
        }

        return vo;
    }

    @Override
    public List<SysDepartVO> findById(List<Long> departIds) {
        List<SysDepartVO> sysDepartVOS = new ArrayList<>();
        if (StringUtils.isNotEmpty(departIds)) {
            List<SysDepartEntity> sysDepartEntities = this.sysDepartDao.listByIds(departIds);
            sysDepartVOS = this.sysDepartVOBatchCopy(sysDepartEntities);
        }
        return sysDepartVOS;
    }

    @Override
    public List<Long> findDepartAndChildById(Long departId) {
        List<Long> departIds = new ArrayList<>();
        if (StringUtils.isNotNull(departId)) {
            List<SysDepartEntity> sysDepartEntities = this.sysDepartDao.findDepartAndChildById(departId);
            if (StringUtils.isNotEmpty(sysDepartEntities)) {
                departIds = sysDepartEntities.stream().map(SysDepartEntity::getId).collect(Collectors.toList());
            }
        }
        return departIds;
    }

    @Override
    public List<SysDepartVO> getSysDepartList(SysDepartQueryDTO dto) {
        List<SysDepartEntity> sysDepartEntities = this.sysDepartDao.listAll(dto);
        List<SysDepartVO> vos = this.sysDepartVOBatchCopy(sysDepartEntities);
        List<Long> userIds = vos.stream().filter(d -> StringUtils.isNotNull(d.getUserId())).map(SysDepartVO::getUserId).distinct().collect(Collectors.toList());
        if (StringUtils.isNotEmpty(userIds)) {
            List<SysUserVO> userVOS = this.sysUserService.findByIdIn(userIds);
            if (StringUtils.isNotEmpty(userVOS)) {
                Map<Long, SysUserVO> userMap = userVOS.stream()
                        .collect(Collectors.toMap(SysUserVO::getId, user -> user, (left, right) -> left));
                vos.forEach(vo -> vo.setSysUserVO(userMap.get(vo.getUserId())));
            }
        }
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO insertSysDepart(SysDepartDTO dto) {
        SysDepartEntity sysDepartEntity = new SysDepartEntity();
        BeanUtils.copyProperties(dto, sysDepartEntity);
        this.setAncestorsAndSort(sysDepartEntity);
        this.sysDepartDao.save(sysDepartEntity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysDepartById(Long id, SysDepartDTO dto) {

        SysDepartEntity sysDepartEntity = this.sysDepartDao.getById(id);
        if (StringUtils.isNull(sysDepartEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.DEPART));
        }
        //子级列表
        if (StringUtils.isNotNull(sysDepartEntity.getParentId())) {
            List<SysDepartEntity> childrenDeparts = this.sysDepartDao.findChildrenByParentId(id);
            if (!sysDepartEntity.getParentId().equals(dto.getParentId()) && StringUtils.isNotEmpty(childrenDeparts)) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST_CHILD, ResultConstants.DEPART));
            }
        }
        BeanUtils.copyProperties(dto, sysDepartEntity);
        this.setAncestorsAndSort(sysDepartEntity);
        this.sysDepartDao.updateById(sysDepartEntity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteSysDepartById(List<Long> ids) {
        if (StringUtils.isNotEmpty(this.sysDepartDao.findChildrenByParentId(ids))) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST_CHILD, ResultConstants.DEPART));
        }
        this.sysDepartDao.removeByIds(ids);
        this.sysDepartRoleDao.deleteSysDepartRoleByDepartId(ids);
        return ResultVO.success();
    }

    @Override
    public Integer getCurrentSort(Long parentId) {
        SysDepartEntity sysDepartEntity = this.sysDepartDao.maxSort(parentId);
        if (StringUtils.isNotNull(sysDepartEntity) && StringUtils.isNotNull(sysDepartEntity.getSort())) {
            return sysDepartEntity.getSort() + Constants.TEN;
        } else {
            return Constants.ZERO;
        }
    }

    @Override
    public List<SysDepartVO> getSysDepartTree(String enable) {
        List<SysDepartEntity> sysDepartEntities = this.sysDepartDao.findAll(enable);
        List<SysDepartVO> vos = this.sysDepartVOBatchCopy(sysDepartEntities);
        vos = IterateUtils.getList(vos);
        return vos;
    }

    @Override
    public void updateEnable(List<Long> ids, String status) {
        if (StringUtils.isNotEmpty(ids)) {
            List<SysDepartEntity> entities = sysDepartDao.listByIds(ids);
            if (StringUtils.isNotEmpty(entities)) {
                for (SysDepartEntity entity : entities) {
                    entity.setSfqy(status);
                }
                sysDepartDao.updateBatch(entities);
            }
        }
    }

    @Override
    public List<SysDepartVO> findAll() {
        return sysDepartVOBatchCopy(sysDepartDao.list());
    }

    @Override
    public List<SysDepartVO> findByDeptNameLike(String deptName) {
        return sysDepartVOBatchCopy(sysDepartDao.findByName(deptName));
    }

    /**
     * 一批部门列表详情copy
     *
     * @param sysDepartEntities
     * @return
     */
    private List<SysDepartDetailsVO> sysDepartDetailsBatchCopy(List<SysDepartEntity> sysDepartEntities) {
        List<SysDepartDetailsVO> vos = new ArrayList<>();
        if (StringUtils.isNotEmpty(sysDepartEntities)) {
            List<Long> userIds = sysDepartEntities.stream().filter(d -> StringUtils.isNotNull(d.getUserId())).map(SysDepartEntity::getUserId).distinct().collect(Collectors.toList());
            List<SysUserVO> sysUserVOS = this.sysUserService.findByIdIn(userIds);
            SysDepartDetailsVO vo = null;
            SysDepartVO sysDepartVO = null;
            for (SysDepartEntity sysDepartEntity : sysDepartEntities) {
                vo = new SysDepartDetailsVO();
                sysDepartVO = new SysDepartVO();
                BeanUtils.copyProperties(sysDepartEntity, sysDepartVO);
                if (StringUtils.isNotEmpty(sysUserVOS)) {
                    vo.setSysUserVO(
                            sysUserVOS.stream().filter(u -> u.getId().equals(sysDepartEntity.getUserId())).findFirst().orElse(null)
                    );
                }
                vo.setSysDepartVO(sysDepartVO);
                vos.add(vo);
            }
        }
        return vos;
    }

    /**
     * 单个部门列表详情copy
     *
     * @param sysDepartEntity
     * @return
     */
    private SysDepartDetailsVO sysDepartDetailsCopy(SysDepartEntity sysDepartEntity) {
        SysDepartDetailsVO vo = null;
        if (StringUtils.isNotNull(sysDepartEntity)) {
            SysUserVO sysUserVO = this.sysUserService.findById(sysDepartEntity.getUserId());
            vo = new SysDepartDetailsVO();
            SysDepartVO sysDepartVO = new SysDepartVO();
            BeanUtils.copyProperties(sysDepartEntity, sysDepartVO);
            vo.setSysDepartVO(sysDepartVO);
            vo.setSysUserVO(sysUserVO);
        }
        return vo;
    }


    /**
     * 一批部门列表基础copy
     *
     * @param sysDepartEntities
     * @return
     */
    private List<SysDepartVO> sysDepartVOBatchCopy(List<SysDepartEntity> sysDepartEntities) {
        if (StringUtils.isEmpty(sysDepartEntities)) {
            return new ArrayList<>();
        }
        return sysDepartEntities.stream().map(this::copyDepartVO).collect(Collectors.toList());
    }

    /**
     * 部门列表基础copy
     *
     * @param sysDepartEntity
     * @return
     */
    private SysDepartVO sysDepartVOCopy(SysDepartEntity sysDepartEntity) {
        SysDepartVO vo = copyDepartVO(sysDepartEntity);
        if (StringUtils.isNotNull(sysDepartEntity) && StringUtils.isNotNull(vo)) {
            vo.setAncestorsDepartName(vo.getDepartName());
            if (StringUtils.isNotBlank(sysDepartEntity.getAncestors())) {
                String[] split = StringUtils.split(sysDepartEntity.getAncestors(), ",");
                List<Long> ids = new ArrayList<>();
                for (String s : split) {
                    ids.add(Long.valueOf(s));
                }
                Collections.reverse(ids);
                List<SysDepartEntity> sysDepartEntities = this.sysDepartDao.findByIdIn(ids);
                if (StringUtils.isNotEmpty(sysDepartEntities)) {
                    String departName = vo.getDepartName() + "/";
                    for (int i = 0; i < ids.size(); i++) {
                        Long id = ids.get(i);
                        String s = sysDepartEntities.stream().filter(e -> e.getId().equals(id))
                                .map(SysDepartEntity::getDepartName)
                                .findFirst().orElse("");
                        departName += s;
                        if (i < ids.size() - 1) {
                            departName += "/";
                        }
                    }
                    vo.setAncestorsDepartName(departName);
                }
            }
        }
        return vo;
    }

    /**
     * 设置组级与排序
     *
     * @param sysDepartEntity
     */
    private void setAncestorsAndSort(SysDepartEntity sysDepartEntity) {
        //配置祖级
        if (StringUtils.isNotNull(sysDepartEntity.getParentId())) {
            SysDepartEntity parentDepart = this.sysDepartDao.getById(sysDepartEntity.getParentId());
            if (StringUtils.isNull(parentDepart)) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.PARENT_NOT_FOUND, ResultConstants.DEPART));
            }
            String ancestors = null;
            if (StringUtils.isNotBlank(parentDepart.getAncestors())) {
                ancestors = parentDepart.getAncestors() + "," + sysDepartEntity.getParentId();
            } else {
                ancestors = String.valueOf(sysDepartEntity.getParentId());
            }
            sysDepartEntity.setAncestors(ancestors);
        }
        //配置排序
        if (StringUtils.isNull(sysDepartEntity.getSort())) {
            SysDepartEntity departEntity = this.sysDepartDao.maxSort(sysDepartEntity.getParentId());
            if (StringUtils.isNotNull(departEntity) && StringUtils.isNotNull(departEntity.getSort())) {
                sysDepartEntity.setSort(departEntity.getSort() + Constants.TEN);
            } else {
                sysDepartEntity.setSort(Constants.ZERO);
            }
        }
    }

    private SysDepartVO copyDepartVO(SysDepartEntity departEntity) {
        if (StringUtils.isNull(departEntity)) {
            return null;
        }
        SysDepartVO vo = new SysDepartVO();
        BeanUtils.copyProperties(departEntity, vo);
        return vo;
    }
}
