package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysPostQueryDTO;
import com.troy.system.entity.SysPostEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:11
 * @Description: SysPostDao
 * @Version: 1.0.0
 */
public interface SysPostDao extends BaseService<SysPostEntity> {


    /**
     * @param
     * @return
     * @author yzy
     * @description 验证编码是否重复
     * @date 2022/9/6
     * @version
     */
    SysPostEntity verifyPostCodeIsRepeat(Long id, String postCode);

    /**
     * @param
     * @return
     * @author yzy
     * @description 全部列表
     * @date 2022/9/6
     * @version
     */
    List<SysPostEntity> listAll();


    /**
     * @param
     * @return
     * @author yzy
     * @description 分页查询
     * @date 2022/9/11
     * @version
     */
    Page<SysPostEntity> getSysPostPage(SysPostQueryDTO dto);

    /**
     * 获取最大排序
     *
     * @return
     */
    SysPostEntity maxSort();
}
