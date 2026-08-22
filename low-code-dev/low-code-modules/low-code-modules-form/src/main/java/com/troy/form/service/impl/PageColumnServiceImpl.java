package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.PageColumnMapper;
import com.troy.form.service.PageColumnService;
import com.troy.form.entity.PageColumnEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Service
public class PageColumnServiceImpl extends ServiceImpl<PageColumnMapper, PageColumnEntity> implements PageColumnService {

}
