package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.form.mapper.AppTypeMapper;
import com.troy.form.service.AppTypeService;
import com.troy.form.dao.AppTypeDao;
import com.troy.form.domain.DTO.AppTypeDTO;
import com.troy.form.domain.DTO.AppTypeSearchDTO;
import com.troy.form.domain.VO.AppTypeVO;
import com.troy.form.entity.AppTypeEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Service
public class AppTypeServiceImpl extends ServiceImpl<AppTypeMapper, AppTypeEntity> implements AppTypeService {

    @Autowired
    private AppTypeDao appTypeDao;

    @Override
    public void saveAppTypeEntity(AppTypeDTO appDTO) {
        if (judgeAppTypeEntity(null, appDTO.getName())){
            throw new ServiceException(ResultEnum.EXIST, "应用名称已存在！");
        }

        AppTypeEntity app = new AppTypeEntity();
        BeanUtils.copyProperties(appDTO, app);
        appTypeDao.save(app);
    }

    @Override
    public void updateAppTypeEntity(Long id, AppTypeDTO appDTO) {
        if (judgeAppTypeEntity(id, appDTO.getName())){
            throw new ServiceException(ResultEnum.EXIST, "应用名称");
        }

        AppTypeEntity app = new AppTypeEntity();
        BeanUtils.copyProperties(appDTO, app);
        app.setId(id);
        appTypeDao.updateById(app);
    }

    @Override
    public void deleteAppTypeEntity(List<Long> ids) {
        List<AppTypeEntity> appTypeEntities = appTypeDao.listByIds(ids);
        if (StringUtils.isEmpty(appTypeEntities) || appTypeEntities.size() != ids.size()){
            throw new ServiceException(ResultEnum.NOT_FOUND, "应用");
        }

        appTypeDao.removeByIds(ids);
    }

    @Override
    public List<AppTypeVO> findAllApps(AppTypeSearchDTO appSearchDTO) {
        List<AppTypeEntity> appTypeEntities = appTypeDao.findPageList(appSearchDTO);
        return appTypeEntities.stream().map(e->{
            AppTypeVO appTypeVO = new AppTypeVO();
            BeanUtils.copyProperties(e, appTypeVO);
            return appTypeVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean judgeAppTypeEntity(Long id, String appName) {
        AppTypeEntity byName = appTypeDao.findFirstByName(appName);
        if (byName == null){
            return false;
        }
        return byName.getId().equals(id);
    }

}
