package com.troy.form.service;


import com.troy.common.core.web.VO.PageVO;
import com.troy.form.domain.DTO.DatasourceDTO;
import com.troy.form.domain.DTO.DatasourceSearchDTO;
import com.troy.form.domain.VO.DatasourceVO;

import java.io.Serializable;

/**
 * @author chenxl
 * @date 2023/6/19
 */
public interface DatasourceService {

    /**
     * 新增数数据源
     * @param DatasourceDTO
     */
    void addDatasource(DatasourceDTO DatasourceDTO);

    /**
     * 删除
     * @param id
     */
    void deleteDatasource(Long id);

    /**
     * 判断url是否合法
     * @param id
     * @param DatasourceDTO
     * @return
     */
    boolean judgeDatasource(Long id, DatasourceDTO DatasourceDTO);

    /**
     * 更新table
     * @param id
     * @return
     */
    boolean updateTable(Long id);

    /**
     * 获取Id
     * @param id
     * @return
     */
    DatasourceVO getById(Serializable id);

    /**
     * 获取数据源分页
     * @param dto
     * @return
     */
    PageVO<DatasourceVO> findPage(DatasourceSearchDTO dto);
}
