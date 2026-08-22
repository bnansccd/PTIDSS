package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysDepartDao;
import com.troy.system.domain.DTO.SysDepartQueryDTO;
import com.troy.system.domain.DTO.SysDepartSearchDTO;
import com.troy.system.entity.SysDepartEntity;
import com.troy.system.mapper.SysDepartMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysDepartEntityTableDef.SYS_DEPART_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/11 17:17:07
 * @Description: SysDepartDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysDepartDaoImpl extends BaseServiceImpl<SysDepartMapper, SysDepartEntity> implements SysDepartDao {
    @Override
    public List<SysDepartEntity> findDepartAndChildById(Long departId) {

        return super.list(
                QueryWrapper.create()
                        .where(SysDepartEntity::getId).eq(departId)
                        .or("FIND_IN_SET ('" + departId + "',ancestors) ")
        );
    }

    @Override
    public List<SysDepartEntity> findChildrenByParentId(Long parentId) {
        return super.list(
                QueryWrapper.create()
                        .where(SysDepartEntity::getAncestors).like(parentId, StringUtils.isNotNull(parentId))
                        .orderBy(SysDepartEntity::getParentId).asc()
                        .orderBy(SysDepartEntity::getSort).asc()
        );
    }

    @Override
    public List<SysDepartEntity> findChildrenByParentId(List<Long> parentIds) {
        return super.list(
                QueryWrapper.create()
                        .where(SysDepartEntity::getParentId).in(parentIds, StringUtils.isNotEmpty(parentIds))
                        .orderBy(SysDepartEntity::getSort).asc()
        );
    }


    @Override
    public List<SysDepartEntity> listAll(SysDepartQueryDTO dto) {
        return super.dataAuthorityList(
                QueryWrapper.create()
                        .where(SysDepartEntity::getDepartName).like(StringUtils.escape(dto.getDepartName()), StringUtils.isNotBlank(dto.getDepartName()))
                        .and(SYS_DEPART_ENTITY.SFQY.eq(dto.getEnable(), StringUtils::isNotBlank))
                        .orderBy(SysDepartEntity::getParentId).asc()
                        .orderBy(SysDepartEntity::getSort).asc()
                , SYS_DEPART_ENTITY.USER_ID, SYS_DEPART_ENTITY.ID
        );
    }

    @Override
    public SysDepartEntity maxSort(Long parentId) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_DEPART_ENTITY.PARENT_ID.eq(parentId, StringUtils.isNotNull(parentId)))
                        .and(SYS_DEPART_ENTITY.PARENT_ID.isNull(StringUtils.isNull(parentId)))
                        .and(SYS_DEPART_ENTITY.SORT.isNotNull())
                        .orderBy(SYS_DEPART_ENTITY.SORT.desc()).limit(Constants.ONE)
        );
    }

    @Override
    public List<SysDepartEntity> findAll(String enable) {
        QueryWrapper where = query().where(SYS_DEPART_ENTITY.SFQY.eq(enable, StringUtils::isNotBlank));
        return super.dataAuthorityList(where, SYS_DEPART_ENTITY.USER_ID, SYS_DEPART_ENTITY.ID);
    }

    @Override
    public List<SysDepartEntity> findBySysTarget(String code) {
        return list(QueryWrapper.create().where(SYS_DEPART_ENTITY.SYS_TARGET.eq(code)));
    }

    @Override
    public Page<SysDepartEntity> findPage(SysDepartSearchDTO dto) {
        return page(
                dto,
                QueryWrapper.create()
                        .where(SYS_DEPART_ENTITY.DEPART_NAME.like(dto.getDepartName(), StringUtils::isNotBlank))
                        .and(SYS_DEPART_ENTITY.SFQY.eq(dto.getEnable(), StringUtils::isNotBlank))
        );
    }

    @Override
    public List<SysDepartEntity> findByName(String code) {
        return list(QueryWrapper.create().where(SYS_DEPART_ENTITY.DEPART_NAME.like(code, StringUtils::isNotBlank)));
    }

    @Override
    public SysDepartEntity getBySysTargetAndCode(String sysTarget, String deptCode) {
        return getOne(
                QueryWrapper.create()
                        .where(SYS_DEPART_ENTITY.SYS_TARGET.eq(sysTarget))
                        .and(SYS_DEPART_ENTITY.CODE.eq(deptCode))
        );
    }

    @Override
    public SysDepartEntity getBySysTargetAndName(String sysTarget, String name) {
        return getOne(
                QueryWrapper.create()
                        .where(SYS_DEPART_ENTITY.SYS_TARGET.eq(sysTarget))
                        .and(SYS_DEPART_ENTITY.DEPART_NAME.eq(name))
        );
    }

    @Override
    public List<SysDepartEntity> getByTenantId(Long tenantId) {
        return list(
                QueryWrapper.create()
                        .where(SYS_DEPART_ENTITY.TENANT_ID.eq(tenantId))
        );
    }

    @Override
    public List<SysDepartEntity> findByIdIn(List<Long> ids) {
        return list(
                QueryWrapper.create()
                        .where(SYS_DEPART_ENTITY.ID.in(ids))
        );
    }
}
