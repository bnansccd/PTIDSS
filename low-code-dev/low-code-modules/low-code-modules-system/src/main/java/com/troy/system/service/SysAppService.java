package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysAppVO;
import com.troy.system.domain.DTO.SysAppDTO;
import com.troy.system.domain.DTO.SysAppQueryDTO;
import com.troy.system.entity.SysAppEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @author chenxl
 * @date 2023/6/19
 */
public interface SysAppService {

    /**
     * 新增文化
     *
     * @param dto
     * @return
     */
    ResultVO addSysApp(SysAppDTO dto);

    /**
     * 删除文化
     *
     * @param id
     * @return
     */
    ResultVO deleteSysApp(List<Long> id);


    /**
     * 更新
     *
     * @param dto
     * @param id
     * @return
     */
    ResultVO updateSysApp(Long id, SysAppDTO dto);

    /**
     * 获取分页数据
     *
     * @param dto
     * @return
     */
    PageVO getSysAppPage(SysAppQueryDTO dto);

    /**
     * 获取文化详情
     *
     * @param id
     * @return
     */
    SysAppVO getSysApp(Long id);


    /**
     * 更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Boolean updateStatus(List<Long> ids, String status);

    /**
     * 重置密码
     *
     * @param appId
     * @return
     */
    String reset(Long appId);

    /**
     * 处理关联菜单
     *
     * @param sysAppEntity
     * @return
     */
    void handleAssociationMenu(SysAppEntity sysAppEntity);

    /**
     * 获取密钥信息
     *
     * @param appId
     * @return
     */
    HashMap<String, String> getKey(Long appId);

    /**
     * 得到当前登录人所拥有的应用
     *
     * @return
     */
    List<SysAppVO> currentApp();


    /**
     * 获取
     * @param id
     * @return
     */
    List<SysAppVO> getSysAppInfo(Long id);
}
