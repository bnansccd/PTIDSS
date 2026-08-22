package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.PageButtonMapper;
import com.troy.form.service.PageButtonService;
import com.troy.form.entity.PageButtonEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Service
public class PageButtonServiceImpl extends ServiceImpl<PageButtonMapper, PageButtonEntity> implements PageButtonService {

}
