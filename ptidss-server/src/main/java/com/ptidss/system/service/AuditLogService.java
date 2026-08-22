package com.ptidss.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.AuditLog;
import com.ptidss.system.domain.SysUser;
import com.ptidss.system.mapper.AuditLogMapper;
import com.ptidss.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审计日志查询（DDL 10.3 audit_log；等保三级：操作留痕、按省检索、按操作人检索）
 */
@Service
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;
    private final SysUserMapper sysUserMapper;

    public AuditLogService(AuditLogMapper auditLogMapper, SysUserMapper sysUserMapper) {
        this.auditLogMapper = auditLogMapper;
        this.sysUserMapper = sysUserMapper;
    }

    public Page<AuditLog> page(long pageNum, long pageSize, String action, String username,
                               String regionCode, String result) {
        LambdaQueryWrapper<AuditLog> qw = new LambdaQueryWrapper<>();
        qw.eq(StrUtils.isNotBlank(action), AuditLog::getAction, action)
                .eq(StrUtils.isNotBlank(regionCode), AuditLog::getRegionCode, regionCode)
                .eq(StrUtils.isNotBlank(result), AuditLog::getResult, result);
        if (StrUtils.isNotBlank(username)) {
            // 操作人模糊检索：username -> user_id 集合
            List<Long> userIds = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                            .like(SysUser::getUsername, username))
                    .stream().map(SysUser::getId).collect(Collectors.toList());
            qw.in(!userIds.isEmpty(), AuditLog::getUserId, userIds);
        }
        qw.orderByDesc(AuditLog::getCreatedAt);
        Page<AuditLog> page = auditLogMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        // 用户名映射（审计展示）：仅当 userId 命中用户表时覆盖；未认证操作（如 login）保留入参提取的用户名
        Map<Long, String> nameMap = sysUserMapper.selectList(null).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername, (a, b) -> a));
        page.getRecords().forEach(log -> {
            String name = nameMap.get(log.getUserId());
            if (name != null) {
                log.setUsername(name);
            }
        });
        return page;
    }

    public AuditLog getById(Long id) {
        AuditLog log = auditLogMapper.selectById(id);
        if (log == null) {
            throw new ServiceException("日志不存在");
        }
        return log;
    }
}
