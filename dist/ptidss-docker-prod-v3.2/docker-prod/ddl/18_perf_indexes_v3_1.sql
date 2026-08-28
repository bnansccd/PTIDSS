-- ============================================================
-- 18_perf_indexes_v3_1.sql  性能优化索引（V3.1）
-- 场景：数据量增长下的高频查询路径补索引（情报/消息/推送规则/血缘）
-- 幂等：CREATE INDEX IF NOT EXISTS，可重复执行；执行后可用 EXPLAIN 验证走索引
-- ============================================================

-- 情报按源+发布时间检索/幂等查重（IntelFetchService 采集去重、情报源维度统计）
CREATE INDEX IF NOT EXISTS ix_intel_news_source_time
  ON intel_news(source_code, published_at) WHERE NOT deleted;

-- 消息业务引用批量命中（情报推送幂等检查 IN (biz_ref)、消息去重）
CREATE INDEX IF NOT EXISTS ix_message_bizref
  ON message_record(biz_ref) WHERE NOT deleted;

-- 消息中心未读统计与列表（receiver + read_status + 时间倒序复合）
CREATE INDEX IF NOT EXISTS ix_message_status_time
  ON message_record(receiver_id, read_status, created_at DESC) WHERE NOT deleted;

-- 情报推送规则状态过滤（规则执行器每 30s 扫描，避免全表）
CREATE INDEX IF NOT EXISTS ix_intel_push_rule_status
  ON intel_push_rule(status) WHERE NOT deleted;

-- 血缘图谱按节点类型聚合（数据资产目录/血缘查询）
CREATE INDEX IF NOT EXISTS ix_lineage_node_type
  ON data_lineage(node_type) WHERE NOT deleted;

-- 采集任务状态与 cron 扫描（DataService 每分钟扫描）
CREATE INDEX IF NOT EXISTS ix_collect_task_cron
  ON collect_task(cron_expr) WHERE NOT deleted;

-- 审计日志按操作人/操作类型过滤（审计查询高频路径，分区表外补复合索引）
CREATE INDEX IF NOT EXISTS ix_audit_log_username
  ON audit_log(username, created_at DESC);
