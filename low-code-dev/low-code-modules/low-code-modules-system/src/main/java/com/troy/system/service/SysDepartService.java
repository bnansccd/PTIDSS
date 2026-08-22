package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysDepartVO;
import com.troy.system.domain.DTO.SysDepartDTO;
import com.troy.system.domain.DTO.SysDepartQueryDTO;
import com.troy.system.domain.DTO.SysDepartSearchDTO;

import java.util.List;

/**
 * <p>
 * 部门管理 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysDepartService {

    /**
     * 获取
     * @param dto
     * @return
     */
    PageVO<SysDepartVO> findPage(SysDepartSearchDTO dto);


    /**
     * 通过部门id查询部门
     *
     * @param departId
     * @return
     */
    SysDepartVO findById(Long departId);

    /**
     * 通过部门id查询部门
     *
     * @param departIds
     * @return
     */
    List<SysDepartVO> findById(List<Long> departIds);

    /**
     * 查询部门子父集
     *
     * @param departId
     * @return
     */
    List<Long> findDepartAndChildById(Long departId);


    /**
     * 部门列表
     *
     * @param dto
     * @return
     */
    List<SysDepartVO> getSysDepartList(SysDepartQueryDTO dto);


    /**
     * @param dto
     * @return
     * @author yzy
     * @description 新增部门
     * @date 2022/9/6
     * @version
     */
    ResultVO insertSysDepart(SysDepartDTO dto);


    /**
     * @param id
     * @param dto
     * @return
     * @author yzy
     * @description 编辑部门
     * @date 2022/9/6
     * @version
     */
    ResultVO updateSysDepartById(Long id, SysDepartDTO dto);

    /**
     * @param ids
     * @return
     * @author yzy
     * @description 批量删除部门
     * @date 2022/9/6
     * @version
     */
    ResultVO deleteSysDepartById(List<Long> ids);

    /**
     * 获取排序数据
     *
     * @param parentId
     * @return
     */
    Integer getCurrentSort(Long parentId);

    /**
     * 得到用户树形列表
     *
     * @return
     */
    List<SysDepartVO> getSysDepartTree(String enable);

    /**
     * 更新
     * @param ids
     * @param status
     * @return
     */
    void updateEnable(List<Long> ids, String status);

    /**
     * 查找所有
     * @return
     */
    List<SysDepartVO> findAll();

    /**
     * 根据部门名称查询
     * @param deptName
     * @return
     */
    List<SysDepartVO> findByDeptNameLike(String deptName);
}
