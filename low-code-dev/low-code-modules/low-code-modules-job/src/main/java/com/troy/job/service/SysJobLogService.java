package com.troy.job.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.job.domain.DTO.SysJobLogDTO;
import com.troy.job.domain.DTO.SysJobLogSearchDTO;
import com.troy.job.domain.VO.SysJobLogVO;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 14:14:45
 * @Description: 定时任务日志 service层
 * @Version: 1.0.0
 */
public interface SysJobLogService {

    /**
     * 查询定时任务调度日志列表
     *
     * @param dto
     * @return
     */
    PageVO<SysJobLogVO> selectJobLogPage(SysJobLogSearchDTO dto);

    /**
     * 导出定时任务调度日志列表
     *
     * @param dto
     * @return
     */
    List<SysJobLogVO> selectJobLogList(SysJobLogSearchDTO dto);


    /**
     * 根据调度编号获取详细信息
     *
     * @param id
     * @return
     */
    SysJobLogVO selectJobLogById(Long id);

    /**
     * 删除定时任务调度日志
     *
     * @param ids
     * @return
     */
    ResultVO deleteJobLogByIds(List<Long> ids);

    /**
     * 清空定时任务调度日志
     *
     * @return
     */
    ResultVO cleanJobLog();

    /**
     * 新增任务日志
     *
     * @param dto
     */
    void addJobLog(SysJobLogDTO dto);
}
