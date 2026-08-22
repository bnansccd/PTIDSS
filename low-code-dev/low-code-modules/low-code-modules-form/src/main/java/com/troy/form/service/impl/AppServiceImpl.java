package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.form.entity.AppEntity;
import com.troy.form.mapper.AppMapper;
import com.troy.form.service.AppService;
import com.troy.form.dao.AppDao;
import com.troy.form.domain.DTO.AppDTO;
import com.troy.form.domain.DTO.AppSearchDTO;
import com.troy.form.domain.VO.AppVO;
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
public class AppServiceImpl extends ServiceImpl<AppMapper, AppEntity> implements AppService {

    @Autowired
    private AppDao appDao;

    @Override
    public void saveAppEntity(AppDTO appDTO) {
        if (judgeAppEntity(null, appDTO.getName())){
            throw new ServiceException(ResultEnum.EXIST, "应用名称已存在！");
        }

        AppEntity app = new AppEntity();
        BeanUtils.copyProperties(appDTO, app);
        appDao.save(app);
    }

    @Override
    public void updateAppEntity(Long id, AppDTO appDTO) {
        if (judgeAppEntity(id, appDTO.getName())){
            throw new ServiceException(ResultEnum.EXIST, "应用名称");
        }

        AppEntity app = new AppEntity();
        BeanUtils.copyProperties(appDTO, app);
        app.setId(id);
        appDao.updateById(app);
    }

    @Override
    public void deleteAppEntity(List<Long> ids) {
        List<AppEntity> appEntityList = appDao.listByIds(ids);
        if (StringUtils.isEmpty(appEntityList) || appEntityList.size() != ids.size()){
            throw new ServiceException(ResultEnum.NOT_FOUND, "应用");
        }

        appDao.removeByIds(ids);
    }

    @Override
    public List<AppVO> findAllApps(AppSearchDTO appSearchDTO) {
        List<AppEntity> appEntityList = appDao.findPageList(appSearchDTO);
        return appEntityList.stream().map(e->{
            AppVO appVO = new AppVO();
            BeanUtils.copyProperties(e, appVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean judgeAppEntity(Long id, String appName) {
        AppEntity byName = appDao.findFirstByName(appName);
        if (byName == null){
            return false;
        }
        return byName.getId().equals(id);
    }


}
