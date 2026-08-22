package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.SysOperLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 操作日志记录 Mapper 接口
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Mapper
public interface SysOperLogMapper extends MyBaseMapper<SysOperLogEntity> {

}
