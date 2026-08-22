package com.troy.common.datasource.service.impl;


import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DbTypeUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryOrderBy;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.DataScopeConstants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.DTO.OrderByDTO;
import com.troy.common.core.web.DTO.PageDTO;
import com.troy.common.datasource.entity.BaseEntity;
import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.common.datasource.service.BaseService;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.domain.VO.DataPermissionsVO;
import com.troy.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:35
 * @Description: BaseServiceImpl
 * @Version: 1.0.0
 */
public class BaseServiceImpl<M extends MyBaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    private DataSource dataSource;


    @Override
    public List<T> dataAuthorityList(QueryWrapper queryWrapper, QueryColumn user, QueryColumn depart) {
        setDataAuthority(queryWrapper, null,user, depart);
        return super.list(queryWrapper);
    }

    @Override
    public List<T> dataAuthorityList(QueryWrapper queryWrapper, QueryCondition queryCondition, QueryColumn user, QueryColumn depart) {
        setDataAuthority(queryWrapper, queryCondition,user, depart);
        return super.list(queryWrapper);
    }

    @Override
    public List<T> dataAuthorityList(QueryColumn user, QueryColumn depart) {
        return this.dataAuthorityList(QueryWrapper.create(), user, depart);
    }

    @Override
    public List<T> dataAuthorityList(QueryWrapper queryWrapper) {
        return this.dataAuthorityList(queryWrapper, new QueryColumn(DataScopeConstants.CREATE_ID), new QueryColumn(DataScopeConstants.CREATE_DEPART_ID));
    }

    @Override
    public List<T> dataAuthorityList() {
        return this.dataAuthorityList(QueryWrapper.create(), new QueryColumn(DataScopeConstants.CREATE_ID), new QueryColumn(DataScopeConstants.CREATE_DEPART_ID));
    }

    @Override
    public Page<T> dataAuthorityPage(PageDTO pageDTO, QueryWrapper queryWrapper, QueryColumn user, QueryColumn depart) {
        setDataAuthority(queryWrapper, null,user, depart);
        orderBy(pageDTO, queryWrapper);
        return page(new Page<>(pageDTO.getCurrent(), pageDTO.getSize()), queryWrapper);
    }

    @Override
    public Page<T> dataAuthorityPage(PageDTO pageDTO, QueryWrapper queryWrapper, QueryCondition queryCondition, QueryColumn user, QueryColumn depart) {
        setDataAuthority(queryWrapper, queryCondition,user, depart);
        orderBy(pageDTO, queryWrapper);
        return page(new Page<>(pageDTO.getCurrent(), pageDTO.getSize()), queryWrapper);
    }

    @Override
    public Page<T> dataAuthorityPage(PageDTO pageDTO, QueryWrapper queryWrapper) {
        return this.dataAuthorityPage(pageDTO, queryWrapper, new QueryColumn(DataScopeConstants.CREATE_ID), new QueryColumn(DataScopeConstants.CREATE_DEPART_ID));
    }

    @Override
    public Page<T> page(PageDTO pageDTO, QueryWrapper queryWrapper) {
        orderBy(pageDTO, queryWrapper);
        return super.page(new Page<>(pageDTO.getCurrent(), pageDTO.getSize()), queryWrapper);
    }

    @Override
    public List<T> selfList(Integer pageSize) {
        Integer totalRow = null;
        if (StringUtils.isNull(pageSize)) {
            pageSize = Constants.FIVE_THOUSAND;
        }
        Integer totalPage = null;
        Integer pageNum = Constants.ONE;

        List<T> list = new ArrayList<>();
        Page<T> page = null;
        while (true) {
            if (StringUtils.isNotNull(totalRow)) {
                page = super.page(new Page<T>(pageNum, pageSize, totalRow), QueryWrapper.create());
            } else {
                page = super.page(new Page<T>(pageNum, pageSize), QueryWrapper.create());
                totalRow = Math.toIntExact(page.getTotalRow());
            }
            list.addAll(page.getRecords());
            if (StringUtils.isNull(totalPage)) {
                totalPage = Math.toIntExact(page.getTotalPage());
            }
            if (pageNum >= totalPage) {
                break;
            }
            pageNum++;
        }
        return list;
    }

    @Override
    public Long dataAuthorityCount(QueryWrapper queryWrapper, QueryColumn user, QueryColumn depart) {
        setDataAuthority(queryWrapper, null,user, depart);
        return super.count(queryWrapper);
    }

    @Override
    public Long dataAuthorityCount(QueryWrapper queryWrapper, QueryCondition queryCondition, QueryColumn user, QueryColumn depart) {
        setDataAuthority(queryWrapper, queryCondition,user, depart);
        return super.count(queryWrapper);
    }

    @Override
    public Long dataAuthorityCount(QueryColumn user, QueryColumn depart) {
        return this.dataAuthorityCount(QueryWrapper.create(), user, depart);
    }

    @Override
    public Long dataAuthorityCount(QueryWrapper queryWrapper) {
        return this.dataAuthorityCount(queryWrapper, new QueryColumn(DataScopeConstants.CREATE_ID), new QueryColumn(DataScopeConstants.CREATE_DEPART_ID));
    }

    @Override
    public Long dataAuthorityCount() {
        return this.dataAuthorityCount(QueryWrapper.create(), new QueryColumn(DataScopeConstants.CREATE_ID), new QueryColumn(DataScopeConstants.CREATE_DEPART_ID));
    }

    /**
     * 设置排序规则
     *
     * @param pageDTO
     * @param queryWrapper
     */
    protected void orderBy(PageDTO pageDTO, QueryWrapper queryWrapper) {
        DbType dbType = DbTypeUtil.getDbType(dataSource);
        if (dbType.equals(DbType.ORACLE) || dbType.equals(DbType.ORACLE_12C)) {
            QueryOrderBy orderBy = null;
            String asc = null;
            if (StringUtils.isNotEmpty(pageDTO.getOrderByDTOS())) {
                for (OrderByDTO orderByDTO : pageDTO.getOrderByDTOS()) {
                    if (orderByDTO.isAsc()) {
                        asc = SqlConsts.ASC;
                    } else {
                        asc = SqlConsts.DESC;
                    }
                    orderBy = new QueryOrderBy(new QueryColumn(orderByDTO.getColumn()), asc).nullsLast();
                    queryWrapper.orderBy(orderBy);
                }
            } else {
                if (StringUtils.isBlank(asc)){
                    asc = SqlConsts.DESC;
                }
                OrderByDTO orderByDTO = new OrderByDTO();
                orderBy = new QueryOrderBy(new QueryColumn(orderByDTO.getColumn()), asc).nullsLast();
                queryWrapper.orderBy(orderBy);
            }
        } else {
            if (StringUtils.isNotEmpty(pageDTO.getOrderByDTOS())) {
                for (OrderByDTO orderByDTO : pageDTO.getOrderByDTOS()) {
                    queryWrapper.orderBy(
                            new QueryColumn(orderByDTO.getColumn()),
                            orderByDTO.isAsc()
                    ).orderBy(new QueryColumn(orderByDTO.getColumn()), null);
                }
            } else {
                OrderByDTO orderByDTO = new OrderByDTO();
                queryWrapper.orderBy(
                        new QueryColumn(orderByDTO.getColumn()),
                        orderByDTO.isAsc()
                ).orderBy(new QueryColumn(orderByDTO.getColumn()), null);
            }
        }
    }

    /**
     * 配置数据权限
     *
     * @param queryWrapper
     * @param user
     * @param depart
     */
    protected void setDataAuthority(QueryWrapper queryWrapper, QueryCondition queryCondition, QueryColumn user, QueryColumn depart) {
        if (StringUtils.isNull(queryWrapper)){
            queryWrapper= QueryWrapper.create();
        }
        if (StringUtils.isNull(queryCondition)){
            queryCondition= QueryCondition.createEmpty();
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)) {
            DataPermissionsVO dataPermissionsVO = loginUser.getDataPermissionsVO();
            if (StringUtils.isNotNull(dataPermissionsVO)) {
                queryWrapper.and(
                        user.like(dataPermissionsVO.getUserId(), StringUtils.isNotNull(dataPermissionsVO.getUserId()))
                                .or(queryCondition)
                                .or(depart.in(dataPermissionsVO.getDepartIds(), StringUtils.isNotEmpty(dataPermissionsVO.getDepartIds())))
                                .or(QueryCondition.createEmpty().or(dataPermissionsVO.getSqlStr()).when(StringUtils.isNotBlank(dataPermissionsVO.getSqlStr())))
                );
            }
        }
    }
}
