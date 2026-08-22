package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.DTO.SysLogininforDTO;
import com.troy.system.api.domain.VO.SysLogininforVO;
import com.troy.system.domain.DTO.DateRangeDTO;
import com.troy.system.domain.DTO.SysLogininfoQueryDTO;
import com.troy.system.domain.VO.LoginTimesAndChainVO;

/**
 * <p>
 * 系统访问记录 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysLogininforService {

    /**
     * 保存系统访问记录
     *
     * @param dto
     * @return
     */
    ResultVO insertLogininfor(SysLogininforDTO dto);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 访问记录列表
     * @date 2022/9/19
     * @version
     */
    PageVO<SysLogininforVO> getSysLogininforList(SysLogininfoQueryDTO dto);

    /**
     * @param id
     * @return
     * @author yzy
     * @description 查看访问记录
     * @date 2022/9/19
     * @version
     */
    SysLogininforVO getSysLogininforById(Long id);

    /**
     * 根据时间范围查询登录次数和环比
     * @param dto
     * @return
     */
    LoginTimesAndChainVO getTimesAndChain(DateRangeDTO dto);
}