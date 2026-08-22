package com.troy.system.service.impl;


import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.system.api.domain.VO.SysPostVO;
import com.troy.system.dao.SysPostDao;
import com.troy.system.dao.SysPostUserDao;
import com.troy.system.domain.DTO.SysPostDTO;
import com.troy.system.domain.DTO.SysPostQueryDTO;
import com.troy.system.entity.SysPostEntity;
import com.troy.system.entity.SysPostUserEntity;
import com.troy.system.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 岗位管理 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysPostServiceImpl implements SysPostService {

    @Autowired
    private SysPostUserDao sysPostUserDao;

    @Autowired
    private SysPostDao sysPostDao;

    @Override
    public PageVO<SysPostVO> getSysPostList(SysPostQueryDTO dto) {
        Page<SysPostEntity> page = this.sysPostDao.getSysPostPage(dto);
        return PageUtils.convertPageVo(page, SysPostVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO insertSysPost(SysPostDTO dto) {
        this.verifyPostCodeIsRepeat(null, dto);
        SysPostEntity sysPostEntity = new SysPostEntity();
        BeanUtils.copyProperties(dto, sysPostEntity);
        this.setSort(sysPostEntity);
        this.sysPostDao.save(sysPostEntity);
        return ResultVO.success();
    }

    @Override
    public SysPostVO getSysPostById(Long id) {
        SysPostEntity sysPostEntity = this.sysPostDao.getById(id);
        return copyPostVO(sysPostEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysPostById(Long id, SysPostDTO dto) {

        SysPostEntity sysPostEntity = this.sysPostDao.getById(id);
        if (StringUtils.isNull(sysPostEntity)) {
            return ResultVO.fail(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.POST));
        }
        this.verifyPostCodeIsRepeat(id, dto);

        BeanUtils.copyProperties(dto, sysPostEntity);
        this.setSort(sysPostEntity);
        this.sysPostDao.updateById(sysPostEntity);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteSysPostById(List<Long> ids) {
        this.sysPostDao.removeByIds(ids);
        this.sysPostUserDao.deleteByPostId(ids);
        return ResultVO.success();
    }

    @Override
    public List<SysPostVO> findByUserId(Long userId) {
        List<SysPostUserEntity> sysPostUserEntities = this.sysPostUserDao.findByUserId(userId);
        List<SysPostEntity> sysPostEntities = new ArrayList<>();

        if (StringUtils.isNotEmpty(sysPostUserEntities)) {
            List<Long> postIds = sysPostUserEntities.stream().map(SysPostUserEntity::getPostId).distinct().collect(Collectors.toList());
            sysPostEntities = this.sysPostDao.listByIds(postIds);
        }
        return getPostVos(sysPostEntities);
    }

    @Override
    public List<SysPostVO> findByUserIds(List<Long> userIds) {
        List<SysPostUserEntity> sysPostUserEntities = this.sysPostUserDao.findByUserIdIn(userIds);
        if (StringUtils.isEmpty(sysPostUserEntities)) {
            return new ArrayList<>();
        }

        Map<Long, List<Long>> postUserMap = sysPostUserEntities.stream()
                .collect(Collectors.groupingBy(SysPostUserEntity::getPostId,
                        Collectors.mapping(SysPostUserEntity::getUserId, Collectors.toList())));
        List<Long> postIds = new ArrayList<>(postUserMap.keySet());
        List<SysPostVO> vos = this.getPostVos(this.sysPostDao.listByIds(postIds));
        vos.forEach(vo -> vo.setUserIds(postUserMap.get(vo.getId())));
        return vos;
    }

    @Override
    public Integer getCurrentSort() {
        SysPostEntity sysPostEntity = this.sysPostDao.maxSort();
        if (StringUtils.isNotNull(sysPostEntity) && StringUtils.isNotNull(sysPostEntity.getSort())) {
            return sysPostEntity.getSort() + Constants.TEN;
        } else {
            return Constants.ZERO;
        }
    }

    @Override
    public void updateEnable(List<Long> ids, String status) {
        if (StringUtils.isNotEmpty(ids)){
            List<SysPostEntity> entities = sysPostDao.listByIds(ids);
            if (StringUtils.isNotEmpty(entities)){
                for (SysPostEntity entity : entities) {
                    entity.setSfqy(status);
                }
                sysPostDao.updateBatch(entities);
            }
        }
    }

    /**
     * 拼接
     */

    private List<SysPostVO> getPostVos(List<SysPostEntity> sysPostEntities) {
        if (StringUtils.isEmpty(sysPostEntities)) {
            return new ArrayList<>();
        }
        return sysPostEntities.stream().map(this::copyPostVO).collect(Collectors.toList());
    }

    /**
     * 设置排序
     *
     * @param sysPostEntity
     */
    private void setSort(SysPostEntity sysPostEntity) {
        if (StringUtils.isNull(sysPostEntity.getSort())) {
            SysPostEntity postEntity = this.sysPostDao.maxSort();
            if (StringUtils.isNotNull(postEntity)) {
                sysPostEntity.setSort(postEntity.getSort() + Constants.TEN);
            } else {
                sysPostEntity.setSort(Constants.ZERO);
            }
        }
    }

    /**
     * 验证编码是否重复
     *
     * @param dto
     */
    private void verifyPostCodeIsRepeat(Long id, SysPostDTO dto) {
        if (StringUtils.isNotBlank(dto.getPostCode())) {
            SysPostEntity sysPostEntity = this.sysPostDao.verifyPostCodeIsRepeat(id, dto.getPostCode());
            if (StringUtils.isNotNull(sysPostEntity)) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.EXIST,ResultConstants.POST_CODE));
            }
        }
    }

    private SysPostVO copyPostVO(SysPostEntity postEntity) {
        if (StringUtils.isNull(postEntity)) {
            return null;
        }
        SysPostVO vo = new SysPostVO();
        BeanUtils.copyProperties(postEntity, vo);
        return vo;
    }
}
