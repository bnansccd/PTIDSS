package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysDictVO;
import com.troy.system.domain.DTO.MenuPageDTO;
import com.troy.system.domain.DTO.SysDictDTO;

import java.util.List;

/**
 * <p>
 * 字典类型 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysDictService {

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 新增
     * @date 2022/9/11
     * @version
     */
    ResultVO insertSysDict(SysDictDTO dto);

    /**
     * @param id
     * @return
     * @author yzy
     * @description 查看字典详情
     * @date 2022/9/11
     * @version
     */
    SysDictVO getSysDictById(Long id);

    /**
     * @auther:yzy
     * @date:14:26 2022/10/10
     * @description:编辑字典
     * @version:
     **/
    ResultVO updateById(Long id, SysDictDTO dto);

    /**
     * @param ids
     * @return
     * @author yzy
     * @description 批量删除字典
     * @date 2022/9/11
     * @version
     */
    ResultVO deleteSysDictById(List<Long> ids);

    /**
     * @param parentType
     * @return
     * @author yzy
     * @description 根据父编码查询
     * @date 2022/9/11
     * @version
     */
    List<SysDictVO> getSysDictByParentType(String parentType);

    /**
     * 字典分页
     *
     * @param dto
     * @return
     */
    PageVO<SysDictVO> getDictPage(MenuPageDTO dto);

    /**
     * 获取当前排序
     *
     * @param parentId
     * @return
     */
    Integer getCurrentSort(Long parentId);

    /**
     * 字典树形列表
     *
     * @return
     */
    List<SysDictVO> getSysDictTree();

    /**
     * List<SysDictVO>
     *
     * @param parentTypes
     * @return
     */
    List<SysDictVO> getSysDictByParentTypeIn(List<String> parentTypes);

    void syncCarBusiness();
}
