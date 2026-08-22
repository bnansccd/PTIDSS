package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.PageParamMapper;
import com.troy.form.service.PageParamService;
import com.troy.form.entity.PageParamEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Service
public class PageParamServiceImpl extends ServiceImpl<PageParamMapper, PageParamEntity> implements PageParamService {

}
