package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysPostDao;
import com.troy.system.domain.DTO.SysPostQueryDTO;
import com.troy.system.entity.SysPostEntity;
import com.troy.system.mapper.SysPostMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysPostEntityTableDef.SYS_POST_ENTITY;

/**
 * @author zhuqing
 * @Date: 2022/8/15 13:13:12
 * @Description: SysPostDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysPostDaoImpl extends BaseServiceImpl<SysPostMapper, SysPostEntity> implements SysPostDao {


    @Override
    public SysPostEntity verifyPostCodeIsRepeat(Long id, String postCode) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SysPostEntity::getPostCode).eq(postCode)
                        .and(SysPostEntity::getId).ne(id, StringUtils.isNotNull(id))
        );
    }


    @Override
    public List<SysPostEntity> listAll() {
        return super.list(
                QueryWrapper.create()
                        .orderBy(SysPostEntity::getSort).asc()
        );
    }

    @Override
    public Page<SysPostEntity> getSysPostPage(SysPostQueryDTO dto) {
        return super.dataAuthorityPage(
                dto,
                QueryWrapper.create()
                        .where(SYS_POST_ENTITY.POST_NAME.like(dto.getPostName(), StringUtils.isNotBlank(dto.getPostName())))
                        .and(SYS_POST_ENTITY.POST_CODE.like(dto.getPostCode(), StringUtils.isNotBlank(dto.getPostCode())))
                        .and(SYS_POST_ENTITY.SFQY.eq(dto.getSfqy(), StringUtils::isNotBlank))
                        .orderBy(SysPostEntity::getSort).asc()
        );
    }

    @Override
    public SysPostEntity maxSort() {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_POST_ENTITY.SORT.isNotNull())
                        .orderBy(SYS_POST_ENTITY.SORT.desc())
                        .limit(Constants.ONE)
        );
    }
}
