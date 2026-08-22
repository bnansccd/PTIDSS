package com.troy.form.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.form.mapper.DbErFormMapper;
import com.troy.form.service.DbErFormService;
import com.troy.form.dao.DbErDao;
import com.troy.form.dao.DbErFormDao;
import com.troy.form.domain.DTO.DbErFormDTO;
import com.troy.form.domain.DTO.DbErFormSearchDTO;
import com.troy.form.domain.VO.DbErFormVO;
import com.troy.form.entity.DbErEntity;
import com.troy.form.entity.DbErFormEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-09 09:45:50
 */
@Service
public class DbErFormServiceImpl extends ServiceImpl<DbErFormMapper, DbErFormEntity> implements DbErFormService {

    @Autowired
    private DbErDao dbErDao;

    @Autowired
    private DbErFormDao dbErFormDao;

    @Override
    public void addForm(DbErFormDTO dbErFormDTO) {
        if (dbErFormDao.exists(null, dbErFormDTO.getName(), dbErFormDTO.getMark())){
            throw new ServiceException(ResultEnum.EXIST, dbErFormDTO.getName()+"或"+dbErFormDTO.getMark());
        }

        DbErEntity dao = dbErDao.getById(dbErFormDTO.getErId());
        if (dao == null) {
            throw new ServiceException(ResultEnum.NOT_FOUND, "E-R模型");
        }

        DbErFormEntity entity = new DbErFormEntity();
        BeanUtils.copyProperties(dbErFormDTO, entity);
        entity.setIsLock(Constants.FALSE);

        dbErFormDao.save(entity);
    }

    @Override
    public void updateForm(Long id, DbErFormDTO dbErFormDTO) {
        DbErEntity dao = dbErDao.getById(dbErFormDTO.getErId());
        if (dao == null) {
            throw new ServiceException(ResultEnum.NOT_FOUND, "E-R模型");
        }

        DbErFormEntity formEntity = dbErFormDao.getById(id);
        if (formEntity == null) {
            throw new ServiceException(ResultEnum.NOT_FOUND, "表单");
        }

        if (dbErFormDao.exists(id, dbErFormDTO.getName(), dbErFormDTO.getMark())){
            throw new ServiceException(ResultEnum.EXIST, dbErFormDTO.getName()+"或"+dbErFormDTO.getMark());
        }

        DbErFormEntity entity = new DbErFormEntity();
        BeanUtils.copyProperties(dbErFormDTO, entity);
        entity.setId(id);
        entity.setIsLock(formEntity.getIsLock());

        dbErFormDao.save(entity);
    }

    @Override
    public PageVO<DbErFormVO> findPage(DbErFormSearchDTO dto) {
        Page<DbErFormEntity> page = dbErFormDao.findPage(dto);
        PageVO<DbErFormVO> vo = PageUtils.convertPageVo(page, DbErFormVO.class);
        if (StringUtils.isNotEmpty(vo.getRecords())){
            List<Long> list = vo.getRecords().stream().map(DbErFormVO::getErId).collect(Collectors.toList());
            List<DbErEntity> entities = dbErDao.listByIds(list);
            vo.getRecords().forEach(e-> entities.forEach(x->{
                if (e.getErId().equals(x.getId())){
                    e.setErName(x.getName());
                }
            }));
        }
        return vo;
    }

    @Override
    public void lockForm(Long id, String status) {
        DbErFormEntity entity = dbErFormDao.getById(id);
        if (entity == null) {
            throw new ServiceException(ResultEnum.NOT_FOUND, "E-R模型");
        }

        String isLock = entity.getIsLock();
        if (StringUtils.isNotBlank(isLock) && Constants.TRUE.equals(isLock) && !entity.getUserId().equals(SecurityContextHolder.getUserId())){
            throw new ServiceException(ResultEnum.NOT_SUPPORT_OPERATE, "表单已被上锁");
        }

        if (Constants.FALSE.equals(status)){
            entity.setIsLock(status);
            entity.setUserId(null);
        } else {
            entity.setIsLock(status);
            entity.setUserId(SecurityContextHolder.getUserId());
        }

        dbErFormDao.updateById(entity);
    }


}
