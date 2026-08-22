package com.troy.common.datasource.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.troy.common.core.web.DTO.PageDTO;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:34
 * @Description: BaseService
 * @Version: 1.0.0
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 数据权限无分页
     *
     * @param queryWrapper
     * @return
     */
    List<T> dataAuthorityList(QueryWrapper queryWrapper, QueryColumn user, QueryColumn depart);


    /**
     * 数据权限无分页 可自己追加查询条件到数据权限中
     *
     * @param queryWrapper
     * @return
     */
    List<T> dataAuthorityList(QueryWrapper queryWrapper, QueryCondition queryCondition, QueryColumn user, QueryColumn depart);
    /**
     * 数据权限无分页
     *
     * @return
     */
    List<T> dataAuthorityList(QueryColumn user, QueryColumn depart);

    /**
     * 数据权限无分页(默认字段)
     *
     * @param queryWrapper
     * @return
     */
    List<T> dataAuthorityList(QueryWrapper queryWrapper);

    /**
     * 数据权限无分页(默认字段)
     *
     * @return
     */
    List<T> dataAuthorityList();

    /**
     * 数据权限有分页
     *
     * @param pageDTO
     * @param queryWrapper
     * @param user
     * @param depart
     * @return
     */
    Page<T> dataAuthorityPage(PageDTO pageDTO, QueryWrapper queryWrapper, QueryColumn user, QueryColumn depart);

    /**
     * 数据权限有分页 可自己追加查询条件到数据权限中
     * @param pageDTO
     * @param queryWrapper
     * @param queryCondition
     * @param user
     * @param depart
     * @return
     */
    Page<T> dataAuthorityPage(PageDTO pageDTO, QueryWrapper queryWrapper,QueryCondition queryCondition, QueryColumn user, QueryColumn depart);

    /**
     * 默认字段
     *
     * @param pageDTO
     * @param queryWrapper
     * @return
     */
    Page<T> dataAuthorityPage(PageDTO pageDTO, QueryWrapper queryWrapper);

    /**
     * 自定义分页
     *
     * @param pageDTO
     * @param queryWrapper
     * @return
     */
    Page<T> page(PageDTO pageDTO, QueryWrapper queryWrapper);

    /**
     * 自定义查询所有数据
     *
     * @param pageSize
     * @return
     */
    List<T> selfList(Integer pageSize);

    /**
     * 数据权限统计
     *
     * @param queryWrapper
     * @return
     */
    Long dataAuthorityCount(QueryWrapper queryWrapper, QueryColumn user, QueryColumn depart);

    /**
     * 数据权限统计 可自己追加查询条件到数据权限中
     * @param queryWrapper
     * @param queryCondition
     * @param user
     * @param depart
     * @return
     */
    Long dataAuthorityCount(QueryWrapper queryWrapper,QueryCondition queryCondition, QueryColumn user, QueryColumn depart);

    /**
     * 数据权限统计
     *
     * @return
     */
    Long dataAuthorityCount(QueryColumn user, QueryColumn depart);

    /**
     * 数据权限统计(默认字段)
     *
     * @param queryWrapper
     * @return
     */
    Long dataAuthorityCount(QueryWrapper queryWrapper);

    /**
     * 数据权限统计(默认字段)
     *
     * @return
     */
    Long dataAuthorityCount();

}
