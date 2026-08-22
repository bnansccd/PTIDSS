package com.troy.form.service;

import com.troy.form.domain.DTO.FormDTO;

/**
 * @author chenxl
 * @date 2023/11/14
 */
public interface RequestService {

    /**
     * 数据信息
     * @param formDTO
     */
    void addRequest(FormDTO formDTO);
}
