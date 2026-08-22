package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.utils.bean.IdUtil;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.mapper.DbErMapper;
import com.troy.form.service.DbErService;
import com.troy.form.dao.DbErDao;
import com.troy.form.dao.DbErRelationDao;
import com.troy.form.dao.DbTableDao;
import com.troy.form.domain.DTO.DbErDTO;
import com.troy.form.domain.DTO.DbRelationDTO;
import com.troy.form.entity.DbErEntity;
import com.troy.form.entity.DbErRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  服务层实现。
 *
 * @author zhuqing
 * @since 2023-10-19 14:19:33
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DbErServiceImpl extends ServiceImpl<DbErMapper, DbErEntity> implements DbErService {

    @Autowired
    private DbErDao dbErDao;

    @Autowired
    private DbErRelationDao dbErRelationDao;

    @Autowired
    private DbTableDao dbTableDao;

    private final IdUtil idUtil = new IdUtil(1, 1);

    @Override
    public void addErModel(DbErDTO dbErDTO) {
        if (dbErDao.exists(null, dbErDTO.getName(), dbErDTO.getErModelMark())){
            throw new ServiceException(ResultEnum.EXIST, dbErDTO.getName()+"或"+dbErDTO.getErModelMark());
        }

        DbRelationDTO dto = dbErDTO.getDbRelationDto();
        List<DbErRelationEntity> list = new ArrayList<>();
        combineList(dto, list, null, null, null);

        boolean hasDuplicate = list.stream()
                .map(DbErRelationEntity::getTableId)
                .distinct()
                .count() != list.size();

        boolean relationHasDuplicate = list.stream()
                .map(DbErRelationEntity::getRelationTableId)
                .distinct()
                .count() != list.size();

        if (hasDuplicate || relationHasDuplicate){
            throw new ServiceException(ResultEnum.ERROR, "E-R模型结构错误");
        }

        Optional<Integer> max = list.stream().filter(e -> StringUtils.isNotBlank(e.getType())).map(e -> Integer.parseInt(e.getType())).max(Integer::compare);
        DbErEntity erEntity = new DbErEntity();
        BeanUtils.copyProperties(dbErDTO, erEntity);
        if (max.isPresent()){
            erEntity.setType(max.get().toString());
        } else {
            erEntity.setType(Constants.ONE.toString());
        }

        dbErDao.save(erEntity);

        List<Long> longs = list.stream().map(DbErRelationEntity::getTableId).collect(Collectors.toList());
        List<DbTableEntity> entities = dbTableDao.listByIds(longs);
        if (StringUtils.isEmpty(entities) || entities.size() != longs.size()){
            throw new ServiceException(ResultEnum.ERROR, "E-R模型结构错误");
        }

        list.forEach(e->e.setErId(erEntity.getId()));
        dbErRelationDao.saveBatch(list);

    }

    @Override
    public void updateErModel(Long id, DbErDTO dbErDTO) {
        if (dbErDao.exists(id, dbErDTO.getName(), dbErDTO.getErModelMark())){
            throw new ServiceException(ResultEnum.EXIST, dbErDTO.getName()+"或"+dbErDTO.getErModelMark());
        }

        DbErEntity erEntity = dbErDao.getById(id);
        DbRelationDTO dto = dbErDTO.getDbRelationDto();
        List<DbErRelationEntity> list = new ArrayList<>();
        combineList(dto, list, null, null, null);

        boolean hasDuplicate = list.stream()
                .map(DbErRelationEntity::getTableId)
                .distinct()
                .count() != list.size();

        boolean relationHasDuplicate = list.stream()
                .map(DbErRelationEntity::getRelationTableId)
                .distinct()
                .count() != list.size();

        if (hasDuplicate || relationHasDuplicate){
            throw new ServiceException(ResultEnum.ERROR, "E-R模型结构错误");
        }

        Integer integer = list.stream().filter(e->StringUtils.isNotBlank(e.getType())).map(e -> Integer.parseInt(e.getType())).max(Integer::compare).get();
        BeanUtils.copyProperties(dbErDTO, erEntity);
        erEntity.setType(integer.toString());
        erEntity.setId(id);
        dbErDao.updateById(erEntity);

        List<Long> longs = list.stream().map(DbErRelationEntity::getTableId).collect(Collectors.toList());
        List<DbTableEntity> entities = dbTableDao.listByIds(longs);
        if (StringUtils.isEmpty(entities) || entities.size() != longs.size()){
            throw new ServiceException(ResultEnum.ERROR, "E-R模型结构错误");
        }

        list.forEach(e->e.setErId(erEntity.getId()));

        dbErRelationDao.removeByErId(id);
        dbErRelationDao.saveBatch(list);
    }

    public void combineList(DbRelationDTO dto, List<DbErRelationEntity> list, Long parentId, Long tableId, Long columnId){
        DbErRelationEntity entity = new DbErRelationEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setParentId(parentId);

        list.add(entity);
        long l = idUtil.nextId();
        entity.setId(l);
        entity.setRelationTableId(tableId);
        entity.setRelationColumnId(columnId);
        List<DbRelationDTO> dtos = dto.getDbRelationList();
        if (StringUtils.isNotEmpty(dtos)){
            dtos.forEach(e-> combineList(e, list, l, e.getTableId(), e.getColumnId()));
        }
    }
}
