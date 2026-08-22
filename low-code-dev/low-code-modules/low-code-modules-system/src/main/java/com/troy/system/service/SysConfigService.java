package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysConfigVO;
import com.troy.system.domain.DTO.SysConfigDTO;
import com.troy.system.domain.DTO.SysConfigQueryDTO;

import java.util.List;

/**
 * <p>
 * 参数配置表 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysConfigService {

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 参数配置分页列表
     * @date 2022/9/12
     * @version
     */
    PageVO<SysConfigVO> getSysConfigPage(SysConfigQueryDTO dto);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 新增参数配置
     * @date 2022/9/12
     * @version
     */
    ResultVO insertSysConfig(SysConfigDTO dto);

    /**
     * @param id
     * @return
     * @author yzy
     * @description 参数配置详情
     * @date 2022/9/12
     * @version
     */
    SysConfigVO getSysConfigById(Long id);

    /**
     * @param id
     * @param dto
     * @return
     * @author yzy
     * @description 编辑参数配置
     * @date 2022/9/12
     * @version
     */
    ResultVO editSysConfig(Long id, SysConfigDTO dto);

    /**
     * @param ids
     * @return
     * @author yzy
     * @description 批量删除参数配置
     * @date 2022/9/12
     * @version
     */
    ResultVO deleteSysConfigById(List<Long> ids);

    /**
     * 获取系统参数
     *
     * @return
     */
    List<SysConfigVO> getList();

    /**
     * 通过配置key查询配置（不传参数查所有）
     *
     * @param configKeys
     * @return
     */
    List<SysConfigVO> findBySysConfigByConfigKeyIn(List<String> configKeys);

    /**
     * 通过租户ID查询系统参数
     * @param tenantId
     * @return
     */
    List<SysConfigVO>  findByTenantId(Long tenantId);
}
