package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.PageMapper;
import com.troy.form.service.PageService;
import com.troy.form.entity.PageEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, PageEntity> implements PageService {

}
