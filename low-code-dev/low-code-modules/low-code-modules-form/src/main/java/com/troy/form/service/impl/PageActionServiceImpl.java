package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.PageActionMapper;
import com.troy.form.service.PageActionService;
import com.troy.form.entity.PageActionEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Service
public class PageActionServiceImpl extends ServiceImpl<PageActionMapper, PageActionEntity> implements PageActionService {

}
