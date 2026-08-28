-- =====================================================================
-- 16. 血缘全景全量图谱（V3.0）：节点中文说明 + 业务/数据双视角全量展示
-- 内容：
--   1) data_lineage 增加 node_name（中文名）/description（中文说明）/
--      domain（业务域）/layer（数据分层）四列；
--   2) node_type 扩展 business（业务应用节点，业务视角根节点）；
--   3) 全量血缘种子重建（36 节点、约 50 条上下游边）：外部源 → 采集 →
--      明细 → 指标 → 模型/报表 → 业务应用 + 系统支撑，覆盖营销/交易中心/
--      气象/政策/情报/交易/结算/预测/决策/优化/评估/报表/系统支撑全链路，
--      业务与数据血缘关系不遗漏。
-- 执行方式：psql -h 127.0.0.1 -p 5432 -U ptidss -d ptidss -f 16_lineage_full_graph_v3_0.sql
-- 幂等：ADD COLUMN IF NOT EXISTS / DROP CONSTRAINT IF EXISTS / 先清空后重建，重复执行安全。
-- 应用侧：DataService.ensureLineage 启动懒重建（检测旧 7 节点种子自动升级），与本节一致。
-- =====================================================================

-- ── 1. 节点属性列（幂等） ──────────────────────────────────────────
ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS node_name VARCHAR(64);    -- 节点中文名
ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS description VARCHAR(255); -- 中文说明
ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS domain VARCHAR(32);       -- 业务域（marketing/exchange/weather/common/trade/.../system）
ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS layer VARCHAR(16);        -- 数据分层（source/collect/detail/indicator/model/report/business）

-- ── 2. node_type 扩展 business（业务应用节点；幂等重建 CHECK） ────────
ALTER TABLE data_lineage DROP CONSTRAINT IF EXISTS data_lineage_node_type_check;
ALTER TABLE data_lineage ADD CONSTRAINT data_lineage_node_type_check
  CHECK (node_type IN ('table','task','report','model','business'));

-- ── 3. 全量血缘种子重建（先物理清空，避免旧节点与部分唯一索引冲突） ────
DELETE FROM data_lineage;

INSERT INTO data_lineage
  (id, node_id, node_type, node_name, description, domain, layer, upstream, downstream, field_mapping) VALUES
-- 外部源（source）
(1,  'src_marketing', 'table', '营销系统', '营销电量/用户台账等外部数据源（模拟或生产接入）', 'marketing', 'source', '[]', '["node_collect_mkt"]', '{}'),
(2,  'src_exchange', 'table', '各省电力交易中心', '现货/中长期行情、公告、供需等交易中心公开数据（山东/山西/广东/甘肃/蒙西/广州等 10 省）', 'exchange', 'source', '[]', '["node_collect_exchange","node_ocr"]', '{}'),
(3,  'src_weather', 'table', '气象数据源', '负荷预测所需气象要素（温度/湿度/风速等）', 'weather', 'source', '[]', '["node_collect_weather"]', '{}'),
-- 采集任务（collect）
(4,  'node_collect_mkt', 'task', '营销数据采集任务', '定时采集营销系统电量/用户数据，写入明细库', 'marketing', 'collect', '["src_marketing"]', '["tbl_detail"]', '{"customer_id":"biz_id"}'),
(5,  'node_collect_exchange', 'task', '交易中心行情采集任务', '采集各省现货/中长期行情、公告、供需信息，落明细库与情报库', 'exchange', 'collect', '["src_exchange"]', '["tbl_detail","tbl_quote_plan","tbl_intel_news"]', '{"trade_date":"trade_date"}'),
(6,  'node_collect_weather', 'task', '气象数据采集任务', '采集气象要素数据，写入明细库', 'weather', 'collect', '["src_weather"]', '["tbl_detail"]', '{}'),
(7,  'node_ocr', 'task', 'OCR 单据识别任务', '政策原文/结算单据扫描件识别（ocr_task/ocr_template）', 'policy', 'collect', '["src_exchange"]', '["tbl_policy","tbl_settlement"]', '{}'),
-- 明细/ODS（detail）
(8,  'tbl_detail', 'table', '业务明细库', '机组/合约/滚动计划/申报/成交等业务明细（明细库通用层）', 'common', 'detail', '["node_collect_mkt","node_collect_exchange","node_collect_weather"]', '["node_etl","data_quality"]', '{"trade_date":"trade_date"}'),
(9,  'tbl_quote_plan', 'table', '报价计划库', '报价计划数据（quote_plan）', 'trade', 'detail', '["node_collect_exchange"]', '["tbl_declaration","node_etl"]', '{}'),
(10, 'tbl_declaration', 'table', '申报记录库', '交易申报数据（declaration，按月分区）', 'trade', 'detail', '["tbl_quote_plan"]', '["tbl_settlement","node_etl"]', '{}'),
(11, 'tbl_settlement', 'table', '结算记录库', '结算记录/核对/凭证/台账（settlement_record/reconcile/ticket/ledger）', 'settle', 'detail', '["tbl_declaration","node_ocr"]', '["node_etl","rpt_settle","rpt_assess","biz_settle","data_quality"]', '{}'),
(12, 'tbl_policy', 'table', '政策原文库', '政策文档/条款/解析（policy_document/article/analysis）', 'policy', 'detail', '["node_ocr"]', '["model_policy","node_etl"]', '{}'),
(13, 'tbl_intel_news', 'table', '情报条目库', '情报中心情报条目（intel_news，按 region 过滤）', 'intel', 'detail', '["node_collect_exchange"]', '["biz_intel","biz_decision"]', '{}'),
-- 指标（indicator）
(14, 'node_etl', 'task', 'ETL 指标加工', '明细→指标宽表加工（含质量监控联动）', 'common', 'indicator', '["tbl_detail","tbl_quote_plan","tbl_declaration","tbl_settlement","tbl_policy"]', '["tbl_indicator"]', '{"price":"price"}'),
(15, 'tbl_indicator', 'table', '指标宽表', '价格/电量/结算等核心指标宽表', 'common', 'indicator', '["node_etl"]', '["model_price","model_load","model_policy","model_ops","rpt_analyze","rpt_assess","biz_decision"]', '{}'),
-- 模型（model）
(16, 'model_price', 'model', '价格预测模型', '现货/中长期价格预测（model_registry/forecast_result）', 'forecast', 'model', '["tbl_indicator"]', '["biz_forecast"]', '{}'),
(17, 'model_load', 'model', '负荷预测模型', '负荷/电量预测（model_registry/forecast_result）', 'forecast', 'model', '["tbl_indicator"]', '["biz_forecast"]', '{}'),
(18, 'model_policy', 'model', '政策解析模型', '政策条款智能解析/标签化（policy_analysis）', 'policy', 'model', '["tbl_policy","tbl_indicator"]', '["biz_policy"]', '{}'),
(19, 'model_ops', 'model', '模型训练与智能体', '训练任务/模型注册/智能体运行（training_task/model_registry/agent_registry/agent_run）', 'model', 'model', '["tbl_indicator"]', '["biz_forecast","biz_decision"]', '{}'),
-- 报表（report）
(20, 'rpt_analyze', 'report', '经营分析报表', '多维经营分析报告（report_template/report_instance）', 'report', 'report', '["tbl_indicator"]', '["biz_report"]', '{}'),
(21, 'rpt_settle', 'report', '结算核对报表', '结算核对/复盘报告（settlement_reconcile）', 'settle', 'report', '["tbl_settlement"]', '["biz_report","biz_settle"]', '{}'),
(22, 'rpt_assess', 'report', '评估考核报告', '指标评估/考核报告（assess_indicator/assess_result/review_report）', 'assess', 'report', '["tbl_indicator","tbl_settlement"]', '["biz_report","biz_assess"]', '{}'),
-- 业务应用（business）
(23, 'biz_intel', 'business', '情报中心', '情报流/推送规则/消息派发（intel_push_rule/message_record）', 'intel', 'business', '["tbl_intel_news","sys_msg"]', '["biz_decision"]', '{}'),
(24, 'biz_decision', 'business', '决策中心', '决策会话/复盘/偏差/策略反馈（decision_session/review_report/deviation_record/strategy_feedback/rule_config）', 'decision', 'business', '["tbl_indicator","tbl_intel_news","biz_intel","biz_forecast","model_ops","sys_msg","sys_flow"]', '["biz_optimize","biz_trade"]', '{}'),
(25, 'biz_trade', 'business', '交易申报', '报价/申报/成交/出清管理（quote_plan/declaration/trade_result/clearing_result）', 'trade', 'business', '["tbl_quote_plan","tbl_declaration","biz_decision","sys_flow"]', '["biz_settle"]', '{}'),
(26, 'biz_settle', 'business', '结算中心', '结算记录/核对/凭证/台账（settlement_*）', 'settle', 'business', '["tbl_settlement","rpt_settle","biz_trade","sys_flow"]', '[]', '{}'),
(27, 'biz_policy', 'business', '政策中心', '政策文档/条款/解析管理（policy_document/article/analysis）', 'policy', 'business', '["model_policy","tbl_policy"]', '[]', '{}'),
(28, 'biz_forecast', 'business', '预测中心', '预测任务/结果（forecast_task/forecast_result）', 'forecast', 'business', '["model_price","model_load","model_ops"]', '["biz_decision"]', '{}'),
(29, 'biz_optimize', 'business', '协同优化', '联合优化任务/策略库/回测（joint_optim_task/strategy_library/backtest_run）', 'optimize', 'business', '["biz_decision"]', '[]', '{}'),
(30, 'biz_report', 'business', '报表中心', '报表模板/实例管理（report_template/report_instance）', 'report', 'business', '["rpt_analyze","rpt_settle","rpt_assess"]', '[]', '{}'),
(31, 'biz_assess', 'business', '评估考核', '指标评估/申诉（assess_indicator/assess_result/assess_appeal）', 'assess', 'business', '["rpt_assess","tbl_settlement"]', '[]', '{}'),
-- 系统支撑（business/system，支撑边虚线展示）
(32, 'sys_auth', 'business', '用户权限体系', '用户/角色/权限/区域授权（sys_user/role/permission/user_region/role_region），RLS 行级隔离，支撑全部业务', 'system', 'business', '[]', '["biz_intel","biz_decision","biz_trade","biz_settle","biz_policy","biz_forecast","biz_optimize","biz_report","biz_assess"]', '{}'),
(33, 'sys_audit', 'business', '审计日志', '操作审计（audit_log，按月分区），@Log AOP 留痕，横切支撑全部业务', 'system', 'business', '[]', '[]', '{}'),
(34, 'sys_msg', 'business', '消息中心', '站内消息/推送（message_record），情报推送/通知', 'system', 'business', '[]', '["biz_intel","biz_decision"]', '{}'),
(35, 'sys_flow', 'business', '审批流', '流程实例（flow_instance），申报/结算/决策审批', 'system', 'business', '[]', '["biz_trade","biz_settle","biz_decision"]', '{}'),
(36, 'data_quality', 'business', '数据质量', '质量规则/报告（data_quality_rule/data_quality_report），监控明细与指标', 'system', 'business', '["tbl_detail","tbl_settlement"]', '["node_etl","tbl_indicator"]', '{}');
