package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.PageSearchMapper;
import com.troy.form.service.PageSearchService;
import com.troy.form.entity.PageSearchEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Service
public class PageSearchServiceImpl extends ServiceImpl<PageSearchMapper, PageSearchEntity> implements PageSearchService {

}
