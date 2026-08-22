package com.troy.gen.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.DTO.PageDTO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.gen.domain.DTO.DbTableDTO;
import com.troy.gen.domain.DTO.GenTableDTO;
import com.troy.gen.domain.VO.DbTableVO;
import com.troy.gen.domain.VO.GenTableVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代码生成业务表 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
public interface GenTableService {

    /**
     * 分页查询代码生成列表
     *
     * @param dto
     * @return
     */
    PageVO<GenTableVO> genTableList(PageDTO dto);

    /**
     * 修改代码生成业务（回显）
     *
     * @param tableId
     * @return
     */
    GenTableVO findById(Long id);

    /**
     * 导入表结构（保存）
     *
     * @param tableNames
     * @return
     */
    ResultVO importTableSave(List<String> tableNames);


    /**
     * 查询数据库列表
     *
     * @param dto
     * @return
     */
    PageVO<DbTableVO> dataList(DbTableDTO dto);

    /**
     * 修改保存代码生成业务
     *
     * @param dto
     * @return
     */
    ResultVO editGenTable(Long id, GenTableDTO dto);

    /**
     * 批量删除代码生成
     *
     * @param ids
     * @return
     */
    ResultVO deleteByIds(List<Long> ids);

    /**
     * 预览代码
     *
     * @param id
     * @return
     */
    Map<String, String> previewCode(Long id);

    /**
     * 生成代码（下载方式）
     *
     * @param id
     */
    byte[] downloadCode(Long id);

    /**
     * 生成代码（自定义路径）
     *
     * @param id
     */
    ResultVO generatorCode(Long id);

    /**
     * 同步数据库
     *
     * @param id
     * @return
     */
    ResultVO synchDb(Long id);

    /**
     * 批量生成代码
     *
     * @param ids
     * @return
     */
    byte[] batchGenCode(List<Long> ids);
}
