package com.troy.form.service;

import com.mybatisflex.core.service.IService;
import com.troy.form.domain.DTO.AppTypeDTO;
import com.troy.form.domain.DTO.AppTypeSearchDTO;
import com.troy.form.domain.VO.AppTypeVO;
import com.troy.form.entity.AppTypeEntity;

import java.util.List;

/**
 *  服务层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
public interface AppTypeService extends IService<AppTypeEntity> {

    /**
     * 保存新app
     * @param appTypeDTO
     */
    void saveAppTypeEntity(AppTypeDTO appTypeDTO);

    /**
     * 更新
     * @param id
     * @param appTypeDTO
     */
    void updateAppTypeEntity(Long id, AppTypeDTO appTypeDTO);

    /**
     * 删除
     * @param ids
     */
    void deleteAppTypeEntity(List<Long> ids);

    /**
     * 获取所有应用
     * @param appTypeSearchDTO
     * @return
     */
    List<AppTypeVO> findAllApps(AppTypeSearchDTO appTypeSearchDTO);

    /**
     * 判断app
     * @param id
     * @param appName
     * @return
     */
    boolean judgeAppTypeEntity(Long id, String appName);
}
