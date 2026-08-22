package com.troy.form.service;

import com.mybatisflex.core.service.IService;
import com.troy.form.entity.AppEntity;
import com.troy.form.domain.DTO.AppDTO;
import com.troy.form.domain.DTO.AppSearchDTO;
import com.troy.form.domain.VO.AppVO;

import java.util.List;

/**
 *  服务层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
public interface AppService extends IService<AppEntity> {

    /**
     * 保存新app
     * @param appDTO
     */
    void saveAppEntity(AppDTO appDTO);

    /**
     * 更新
     * @param id
     * @param appDTO
     */
    void updateAppEntity(Long id, AppDTO appDTO);

    /**
     * 删除
     * @param ids
     */
    void deleteAppEntity(List<Long> ids);

    /**
     * 获取所有应用
     * @param appSearchDTO
     * @return
     */
    List<AppVO> findAllApps(AppSearchDTO appSearchDTO);

    /**
     * 判断app
     * @param id
     * @param appName
     * @return
     */
    boolean judgeAppEntity(Long id, String appName);
}
