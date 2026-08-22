package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysDictDao;
import com.troy.system.domain.DTO.MenuPageDTO;
import com.troy.system.domain.DTO.SysDictQueryDTO;
import com.troy.system.entity.SysDictEntity;
import com.troy.system.entity.table.SysDictEntityTableDef;
import com.troy.system.mapper.SysDictMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysDictEntityTableDef.SYS_DICT_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:55
 * @Description: SysDictDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysDictDaoImpl extends BaseServiceImpl<SysDictMapper, SysDictEntity> implements SysDictDao {
    @Override
    public List<SysDictEntity> listAll(SysDictQueryDTO dto) {
        return super.list(
                QueryWrapper.create()
                        .where(SysDictEntity::getDictName).like(dto.getDictName(), StringUtils.isNotNull(dto) && StringUtils.isNotBlank(dto.getDictName()))
                        .orderBy(SysDictEntity::getParentId).asc().orderBy(SysDictEntity::getSort).asc()
        );
    }

    @Override
    public SysDictEntity verifyDictTypeIsRepeat(Long id, Long parentId, String dictType) {
        return this.getOne(
                QueryWrapper.create()
                        .where(SYS_DICT_ENTITY.PARENT_ID.eq(parentId, StringUtils.isNotNull(parentId)))
                        .and(SYS_DICT_ENTITY.PARENT_ID.isNull(StringUtils.isNull(parentId)))
                        .and(SYS_DICT_ENTITY.ID.ne(id, StringUtils.isNotNull(id)))
                        .and(SysDictEntity::getDictType).eq(dictType)
        );
    }

    @Override
    public List<SysDictEntity> findByParentType(String parentType) {
        return super.list(
                QueryWrapper.create()
                        .where(SysDictEntity::getParentType).eq(parentType, StringUtils.isNotBlank(parentType))
        );
    }

    @Override
    public Page<SysDictEntity> getDictPage(MenuPageDTO dto) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SysDictEntity::getDictName).like(dto.getDictName())
                .and(SysDictEntity::getParentId).isNull(StringUtils.isNull(dto.getParentId()))
                .and(SysDictEntity::getParentId).eq(dto.getParentId(), StringUtils.isNotNull(dto.getParentId()));
        return super.dataAuthorityPage(dto, queryWrapper);
    }

    @Override
    public SysDictEntity maxSort(Long parentId) {
        return super.getOne(
                QueryWrapper.create()
                        .and(SysDictEntity::getParentId).isNull(StringUtils.isNull(parentId))
                        .and(SysDictEntity::getParentId).eq(parentId, StringUtils.isNotNull(parentId))
                        .orderBy(SysDictEntity::getSort).desc()
                        .limit(Constants.ONE)

        );
    }

    @Override
    public List<SysDictEntity> findByParentTypeIn(List<String> parentTypes) {
        return super.list(
                QueryWrapper.create()
                        .where(SysDictEntityTableDef.SYS_DICT_ENTITY.PARENT_TYPE.in(parentTypes))
        );
    }

    @Override
    public List<SysDictEntity> listOrderByParentIdAndSort() {
        return super.list(
                QueryWrapper.create()
                        .orderBy(SysDictEntity::getParentId).asc().orderBy(SysDictEntity::getSort).asc()
        );
    }

    @Override
    public List<SysDictEntity> getAllParents() {
        return super.list(
                QueryWrapper.create().where(SysDictEntityTableDef.SYS_DICT_ENTITY.PARENT_ID.isNull())
        );
    }

    @Override
    public List<SysDictEntity> getAllChild() {
        return super.list(
                QueryWrapper.create().where(SysDictEntityTableDef.SYS_DICT_ENTITY.PARENT_ID.isNotNull())
        );
    }

    @Override
    public List<SysDictEntity> getByDictTypes(List<String> dictTypes) {
        return super.list(
                QueryWrapper.create().where(SysDictEntityTableDef.SYS_DICT_ENTITY.DICT_TYPE.in(dictTypes))
        );
    }

    @Override
    public Integer getMaxSortByLevelOne() {
        return super.getObjAs(
                QueryWrapper.create().select(QueryMethods.max(SysDictEntityTableDef.SYS_DICT_ENTITY.SORT))
                        .where(SysDictEntityTableDef.SYS_DICT_ENTITY.PARENT_ID.isNull())
                        .and(SysDictEntityTableDef.SYS_DICT_ENTITY.PARENT_TYPE.isNull()),
                Integer.class
        );
    }

}
