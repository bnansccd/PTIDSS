package com.troy.gen.service;

import com.troy.gen.domain.VO.GenTableColumnVO;

import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
public interface GenTableColumnService {

    /**
     * 通过表id查询数据表字段列表
     *
     * @return
     */
    List<GenTableColumnVO> findByTableId(Long tableId);

}
