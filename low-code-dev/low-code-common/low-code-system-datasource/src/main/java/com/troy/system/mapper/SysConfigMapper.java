package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 参数配置表 Mapper 接口
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Mapper
public interface SysConfigMapper extends MyBaseMapper<SysConfigEntity> {

    /**
     * 更新状态
     * @param list
     * @return
     */
    Boolean updateValue(@Param("list") List<SysConfigEntity> list);
}
