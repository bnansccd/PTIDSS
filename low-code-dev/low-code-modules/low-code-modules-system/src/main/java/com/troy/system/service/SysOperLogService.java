package com.troy.system.service;

import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysOperLogVO;
import com.troy.system.domain.DTO.SysOperLogDTO;
import com.troy.system.domain.DTO.SysOperLogQueryDTO;

/**
 * <p>
 * 操作日志记录 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysOperLogService {

    /**
     * @author yzy
     * @description 分页列表
     * @date  2022/9/19
     * @param dto
     * @return
     * @version
     */
    PageVO<SysOperLogVO> getSysOperLogList(SysOperLogQueryDTO dto);

    /**
     * @author yzy
     * @description 查看操作日志
     * @date  2022/9/19
     * @param id
     * @return
     * @version
     */
    SysOperLogVO getSysOperLogById(Long id);

    /**
     * 新增记录
     * @param dto
     * @return
     */
    Boolean addSysOperLog(SysOperLogDTO dto);
}
