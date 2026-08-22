package com.ptidss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptidss.system.domain.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
