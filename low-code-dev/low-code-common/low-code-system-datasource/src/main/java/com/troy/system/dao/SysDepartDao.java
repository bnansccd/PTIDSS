package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysDepartQueryDTO;
import com.troy.system.domain.DTO.SysDepartSearchDTO;
import com.troy.system.entity.SysDepartEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/11 17:17:07
 * @Description: SysDepartDao
 * @Version: 1.0.0
 */
public interface SysDepartDao extends BaseService<SysDepartEntity> {

    /**
     * 设置部门子父集
     *
     * @param departId
     * @return
     */
    List<SysDepartEntity> findDepartAndChildById(Long departId);


    /**
     * 设备部门子父集
     *
     * @param parentId
     * @return
     */
    List<SysDepartEntity> findChildrenByParentId(Long parentId);

    /**
     * 设备部门子父集
     *
     * @param parentIds
     * @return
     */
    List<SysDepartEntity> findChildrenByParentId(List<Long> parentIds);


    /**
     * @param
     * @return
     * @author yzy
     * @description 部门列表（不分页）
     * @date 2022/9/7
     * @version
     */
    List<SysDepartEntity> listAll(SysDepartQueryDTO dto);

    /**
     * 得到部门最大排序
     *
     * @param parentId
     * @return
     */
    SysDepartEntity maxSort(Long parentId);

    /**
     * 查询所有部门
     * @return
     */
    List<SysDepartEntity> findAll(String enable);

    /**
     * 获取code
     * @param code
     * @return
     */
    List<SysDepartEntity> findBySysTarget(String code);

    /**
     * 获取
     * @param dto
     * @return
     */
    Page<SysDepartEntity> findPage(SysDepartSearchDTO dto);

    /**
     * 获取
     * @param code
     * @return
     */
    List<SysDepartEntity> findByName(String code);

    /**
     * 通过 code 查询
     * @param sysTarget
     * @param deptCode
     * @return
     */
    SysDepartEntity getBySysTargetAndCode(String sysTarget, String deptCode);

    /**
     * 通过 name 查询
     * @param sysTarget
     * @param name
     * @return
     */
    SysDepartEntity getBySysTargetAndName(String sysTarget, String name);

    List<SysDepartEntity> getByTenantId(Long tenantId);

    List<SysDepartEntity> findByIdIn(List<Long> ids);
}
