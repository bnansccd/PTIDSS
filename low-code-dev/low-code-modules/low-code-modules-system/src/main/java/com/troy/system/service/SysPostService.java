package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysPostVO;
import com.troy.system.domain.DTO.SysPostDTO;
import com.troy.system.domain.DTO.SysPostQueryDTO;

import java.util.List;

/**
 * <p>
 * 岗位管理 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysPostService {

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 岗位分页列表
     * @date 2022/9/5
     * @version
     */
    PageVO<SysPostVO> getSysPostList(SysPostQueryDTO dto);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 新增岗位
     * @date 2022/9/5
     * @version
     */
    ResultVO insertSysPost(SysPostDTO dto);

    /**
     * @param id
     * @return
     * @author yzy
     * @description 查看岗位
     * @date 2022/9/5
     * @version
     */
    SysPostVO getSysPostById(Long id);

    /**
     * @param id
     * @param dto
     * @return
     * @author yzy
     * @description 编辑岗位
     * @date 2022/9/5
     * @version
     */
    ResultVO updateSysPostById(Long id, SysPostDTO dto);

    /**
     * @param ids
     * @return
     * @author yzy
     * @description
     * @date 2022/9/5
     * @version
     */
    ResultVO deleteSysPostById(List<Long> ids);

    /**
     * 通过用户id查询岗位信息
     *
     * @param userId
     * @return
     */
    List<SysPostVO> findByUserId(Long userId);


    /**
     * 通过一批用户id查询岗位信息
     *
     * @param userIds
     * @return
     */
    List<SysPostVO> findByUserIds(List<Long> userIds);

    /**
     * 获取排序号
     *
     * @return
     */
    Integer getCurrentSort();


    /**
     * 更新
     * @param ids
     * @param status
     */
    void updateEnable(List<Long> ids, String status);

}
