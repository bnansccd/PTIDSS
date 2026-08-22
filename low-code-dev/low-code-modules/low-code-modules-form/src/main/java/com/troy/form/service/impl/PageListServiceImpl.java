package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.PageListMapper;
import com.troy.form.service.PageListService;
import com.troy.form.entity.PageListEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Service
public class PageListServiceImpl extends ServiceImpl<PageListMapper, PageListEntity> implements PageListService {

}
