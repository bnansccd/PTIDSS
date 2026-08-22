package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.form.mapper.DbColumnMapper;
import com.troy.form.service.DbColumnService;
import com.troy.form.entity.DbColumnEntity;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Service
public class DbColumnServiceImpl extends ServiceImpl<DbColumnMapper, DbColumnEntity> implements DbColumnService {

}
