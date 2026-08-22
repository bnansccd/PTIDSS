package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.MenuPageDTO;
import com.troy.system.domain.DTO.SysDictQueryDTO;
import com.troy.system.entity.SysDictEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:55
 * @Description: SysDictDao
 * @Version: 1.0.0
 */
public interface SysDictDao extends BaseService<SysDictEntity> {

    /**
     * @param
     * @return
     * @author yzy
     * @description 查询全部
     * @date 2022/9/11
     * @version
     */
    List<SysDictEntity> listAll(SysDictQueryDTO dto);

    /**
     * @param dictType
     * @return
     * @author yzy
     * @description 验证同组中编码是否重复
     * @date 2022/9/11
     * @version
     */
    SysDictEntity verifyDictTypeIsRepeat(Long id, Long parentId, String dictType);

    /**
     * @param parentType
     * @return
     * @author yzy
     * @description 根据父编码查询
     * @date 2022/9/11
     * @version
     */
    List<SysDictEntity> findByParentType(String parentType);

    /**
     * 分页列表
     *
     * @param dto
     * @return
     */
    Page<SysDictEntity> getDictPage(MenuPageDTO dto);

    /**
     * 获取同级目录下，最大排序号
     *
     * @param parentId
     * @return
     */
    SysDictEntity maxSort(Long parentId);

    /**
     * 通过一批父级查询子级
     *
     * @param parentTypes
     * @return
     */
    List<SysDictEntity> findByParentTypeIn(List<String> parentTypes);


    /**
     * 得到数据字典且排序
     *
     * @return
     */
    List<SysDictEntity> listOrderByParentIdAndSort();

    /**
     * 查询所有父级
     * @return
     */
    List<SysDictEntity> getAllParents();

    /**
     * 查询所有子集
     * @return
     */
    List<SysDictEntity> getAllChild();

    /**
     * 通过编码查询
     * @return
     */
    List<SysDictEntity> getByDictTypes(List<String> dictTypes);

    /**
     * 查询父类最大排序号
     */
    Integer getMaxSortByLevelOne();

}
