-- ============================================================
-- 电力交易智能辅助决策系统（PTIDSS）DDL 基线 v1.0.1（勘误版）
-- 基线种子数据（开发/测试环境）
-- 配套：01_postgres_schema.sql｜06_enum_dict.sql｜数据字典 V1.1
-- 约定：
--   1) 全部语句幂等（ON CONFLICT DO NOTHING），可重复执行；
--   2) id 使用固定小整数便于识别，生产环境由应用层雪花 ID 生成；
--   3) 不落任何明文凭据：password_hash 为占位符，首次启动由应用初始化；
--   4) connect_config 中密钥类字段以 <由运维配置> 占位，不落明文。
-- 勘误（v1.0.1，条目 4）：补充行情时序 region 非空质量规则种子（data_quality_rule），
--       与 02_tdengine_schema.sql TAG 提升（条目 1/2）联动，入库即受 region 非空门禁。
-- 勘误（v1.0.2，V1.7）：补充情报推送规则种子（intel_push_rule）与情报源采集示意，
--       与 intel-service 推送执行器联动：标签交集 × 重要度匹配未推送情报 → 按目标角色派发个人消息。
-- 执行：psql -h <host> -U ptidss -d ptidss -f 07_seed_data.sql
-- ============================================================

-- ── 1. 区域注册（评审决议⑤：多省配置化，全国推广演进） ──────
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
VALUES
  (1, 'CN-32', '江苏', '["spot","midlong","external"]', 'both',  'natural_month', 'enabled',  1),
  (2, 'CN-31', '上海', '["spot","midlong"]',            'rest',  'natural_month', 'enabled',  2),
  (3, 'CN-33', '浙江', '["spot","midlong","external"]', 'sftp',  'trading_month', 'enabled',  3),
  (4, 'CN-11', '北京', '["midlong"]',                   'rest',  'natural_month', 'pending',  4)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
SELECT 5, 'CN-41', '河南', '["spot"]', 'rest', 'natural_month', 'disabled', 5
WHERE NOT EXISTS (SELECT 1 FROM sys_region WHERE id = 5);

-- ── 2. 角色（角色编码固定 7 类） ─────────────────────────────
INSERT INTO sys_role (id, role_code, role_name, description, status)
VALUES
  (1, 'trader',     '交易员',     '申报/报价/方案确认', 'active'),
  (2, 'analyst',    '分析师',     '预测/复盘/策略',    'active'),
  (3, 'settlement', '结算员',     '结算核对/工单/台账', 'active'),
  (4, 'admin',      '管理员',     '系统管理/用户/配置', 'active'),
  (5, 'manager',    '经理',       '审批/报表/考核',    'active'),
  (6, 'compliance', '合规专员',   '规则/合规校验',     'active'),
  (7, 'mobile',     '移动审批',   '小程序行情/消息/审批', 'active')
ON CONFLICT (id) DO NOTHING;

-- ── 3. 权限（菜单/接口/数据三级示例） ────────────────────────
INSERT INTO sys_permission (id, perm_code, perm_name, resource_type, resource_pattern, status)
VALUES
  (101, 'menu:market',      '市场行情',   'menu', '/market/**',           'active'),
  (102, 'menu:trade',       '交易申报',   'menu', '/trade/**',            'active'),
  (103, 'menu:decision',    '智能决策',   'menu', '/decision/**,/optimize/**', 'active'),
  (104, 'menu:settlement',  '结算管理',   'menu', '/settlement/**,/ocr/**',    'active'),
  (105, 'menu:review',      '复盘考核',   'menu', '/review/**,/assessment/**', 'active'),
  (106, 'menu:intel',       '情报中心',   'menu', '/intel/**',            'active'),
  (107, 'menu:admin',       '系统管理',   'menu', '/admin/**,/data/**,/message/**', 'active'),
  (108, 'menu:report',      '报表中心',   'menu', '/report/**',           'active'),
  (109, 'menu:policy',      '政策中心',   'menu', '/policy/**',           'active'),
  (110, 'menu:message',     '消息中心',   'menu', '/message/**',          'active'),
  (111, 'menu:data',        '数据底座',   'menu', '/data/**',             'active'),
  (112, 'menu:forecast',    '预测中心',   'menu', '/forecast/**',         'active'),
  (113, 'menu:optimize',    '联合优化',   'menu', '/optimize/**',         'active'),
  (114, 'menu:model',       '模型平台',   'menu', '/model/**',            'active'),
  (115, 'menu:flow',        '审批流',     'menu', '/flow/**',             'active'),
  (201, 'api:declaration',  '申报接口',   'api',  'POST /trade/declarations/**',   'active'),
  (202, 'api:settlement',   '结算接口',   'api',  '/settlement/**',       'active'),
  (301, 'data:region',      '区域数据',   'data', 'region_code:*',         'active')
ON CONFLICT (id) DO NOTHING;

-- ── 4. 角色-权限关联（admin 全量；trader 交易向） ────────────
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'admin'
ON CONFLICT DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'trader' AND p.perm_code IN ('menu:market','menu:trade','menu:decision','menu:policy','menu:message','menu:forecast','menu:optimize','api:declaration','data:region')
ON CONFLICT DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'mobile' AND p.perm_code IN ('menu:market','menu:intel','menu:message','menu:flow','data:region')
ON CONFLICT DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'analyst' AND p.perm_code IN ('menu:forecast','menu:model','menu:optimize','menu:intel','menu:message','data:region')
ON CONFLICT DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'manager' AND p.perm_code IN ('menu:data','menu:flow','menu:optimize','menu:review','menu:report','menu:decision','menu:message','data:region')
ON CONFLICT DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'settlement' AND p.perm_code IN ('menu:settlement','menu:flow','menu:message','data:region')
ON CONFLICT DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'compliance' AND p.perm_code IN ('menu:policy','menu:data','menu:message','data:region')
ON CONFLICT DO NOTHING;

-- ── 5. 用户（占位密码哈希，应用首次启动初始化，不落明文） ────
INSERT INTO sys_user (id, username, display_name, password_hash, role_ids, org_code, phone, email, status)
VALUES
  (1, 'admin',    '系统管理员', 'PLACEHOLDER:APP_INIT', '[4]',    'HQ',      'ENC:PLACEHOLDER', 'ENC:PLACEHOLDER', 'active'),
  (2, 'trader01', '张交易员',   'PLACEHOLDER:APP_INIT', '[1,7]',  'JS-CN32', 'ENC:PLACEHOLDER', 'ENC:PLACEHOLDER', 'active'),
  (3, 'settle01', '李结算员',   'PLACEHOLDER:APP_INIT', '[3]',    'JS-CN32', 'ENC:PLACEHOLDER', 'ENC:PLACEHOLDER', 'active'),
  (4, 'manager01','王经理',     'PLACEHOLDER:APP_INIT', '[5]',    'JS-CN32', 'ENC:PLACEHOLDER', 'ENC:PLACEHOLDER', 'active')
ON CONFLICT (id) DO NOTHING;

-- ── 6. 用户-区域授权（角色 × 区域双重授权，评审决议⑤） ───────
INSERT INTO sys_user_region (user_id, region_code)
VALUES
  (1, 'CN-32'), (1, 'CN-31'), (1, 'CN-33'), (1, 'CN-11'),
  (2, 'CN-32'), (3, 'CN-32'), (4, 'CN-32')
ON CONFLICT DO NOTHING;

-- ── 7. 数据源（exchange 双通道建模，评审决议①；密钥占位） ────
INSERT INTO data_source (id, source_code, source_type, connect_config, sync_mode, frequency, status)
VALUES
  (1, 'marketing_platform', 'marketing',
   '{"channel":"rest","base_url":"https://marketing.example.com","token":"<由运维配置>","encrypted":true}',
   'realtime', '*/15 * * * *', 'enabled'),
  (2, 'exchange_js', 'exchange',
   '{"channel":"both","rest":{"base_url":"https://js-example.com/api","cert":"<由运维配置>"},"sftp":{"host":"sftp.js-example.com","dir":"/inbox","cred":"<由运维配置>"},"encrypted":true}',
   'timed', '0 0/30 * * * *', 'enabled'),
  (3, 'weather_gfs', 'weather',
   '{"channel":"rest","base_url":"https://nomads.example.com/gfs","token":"<由运维配置>","encrypted":true}',
   'timed', '0 */6 * * *', 'enabled')
ON CONFLICT (id) DO NOTHING;

INSERT INTO collect_task (id, source_id, task_type, cron_expr, last_status)
VALUES
  (1, 2, 'market', '0 0/30 * * * *', 'success'),
  (2, 2, 'settlement', '0 30 2 * * *', 'success'),
  (3, 3, 'weather', '0 10 */6 * * *', 'success')
ON CONFLICT (id) DO NOTHING;

-- ── 8. 情报源（RE-01 P0 正式；10 源代表子集：覆盖 6 类情报，与 intel-service 种子同源） ──
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
VALUES
  (1, 'INTL-PROV-JS',  '江苏省电力交易中心公告', 'announcement', 'api',   '5 */1 * * *', 'enabled'),
  (2, 'INTL-NEA',      '国家能源局政策发布',     'policy',       'crawl', '0 8 * * *',   'enabled'),
  (3, 'INTL-GRID-JS',  '江苏电网调度信息披露',   'supply_demand','api',   '*/10 * * * *', 'enabled'),
  (4, 'INTL-MET-CMA',  '中央气象台气象预警',     'weather',      'file',  '0 */6 * * *',  'enabled'),
  (5, 'INTL-PRICE-NEM','国家电力交易中心-出清价格','price',      'api',   '5 分钟',        'enabled'),
  (6, 'INTL-PRICE-PROV','省电力交易中心-现货出清','price',      'api',   '15 分钟',       'enabled'),
  (7, 'INTL-ANN-GRID', '电网公司-运行公告',     'announcement', 'api',   '1 小时',        'enabled'),
  (8, 'INTL-OPI-MEDIA','行业媒体-市场舆情',     'opinion',      'crawl', '4 小时',        'enabled'),
  (9, 'INTL-POL-ENERGY','能源局-交易规则公告',  'policy',       'crawl', '每日',          'enabled'),
  (10,'INTL-SD-DEMAND','负荷预测公告',         'supply_demand','api',   '1 小时',        'enabled')
ON CONFLICT (id) DO NOTHING;

-- ── 9. 合规规则库（版本化，评审决议③口径入参） ───────────────
INSERT INTO rule_config (id, rule_code, rule_name, rule_type, params, version, effective_date, expired_date, status)
VALUES
  (1, 'RULE-DECL-SEG', '申报段数上限',   'compliance', '{"max_segments":10,"per_band_volume_max":300}', 1, '2026-08-01', '2099-12-31', 'active'),
  (2, 'RULE-PRICE-LIM', '申报价格上下限', 'compliance', '{"floor":0,"ceiling":1500,"unit":"yuan/MWh"}', 1, '2026-08-01', '2099-12-31', 'active'),
  (3, 'RULE-POS-RATIO', '持仓比例约束',   'compliance', '{"max_open_ratio":0.30,"calc":"open/contract"}', 1, '2026-08-01', '2099-12-31', 'active'),
  (4, 'RULE-DEV-ASSESS','偏差考核阈值',   'assessment', '{"band_pct":0.05,"penalty_unit":"yuan/MWh"}', 1, '2026-08-01', '2099-12-31', 'active')
ON CONFLICT (id) DO NOTHING;

-- ── 10. 考核指标（5 项） ────────────────────────────────────
INSERT INTO assess_indicator (id, code, name, formula, weight, target_value, scoring_rule, data_source, status)
VALUES
  (1, 'KPI-REV',  '收益完成率', '{"type":"ratio","numerator":"actual_revenue","denominator":"target_revenue"}', 0.30, '{"target":1.0}', '{"linear":true}', 'settlement_ledger', 'active'),
  (2, 'KPI-ACC',  '预测准确率', '{"type":"mape","target":"price_forecast"}', 0.25, '{"max_mape":0.10}', '{"linear":true}', 'forecast_result', 'active'),
  (3, 'KPI-DEV',  '偏差率',     '{"type":"ratio","numerator":"deviation_volume","denominator":"matched_volume"}', 0.20, '{"max":0.05}', '{"linear":true}', 'trade_result', 'active'),
  (4, 'KPI-COMP', '合规执行率', '{"type":"ratio","numerator":"compliant_decls","denominator":"total_decls"}', 0.15, '{"target":1.0}', '{"linear":true}', 'declaration', 'active'),
  (5, 'KPI-RVW',  '复盘完成率', '{"type":"ratio","numerator":"reviewed_weeks","denominator":"total_weeks"}', 0.10, '{"target":1.0}', '{"linear":true}', 'review_report', 'active')
ON CONFLICT (id) DO NOTHING;

-- ── 11. 报表模板（口径随 region 与 periodMode，评审决议③⑤；5 类模板与 report-service 种子同源） ──
INSERT INTO report_template (id, code, name, type, period_type, datasource_config, layout, header_config, version, status)
VALUES
  (1, 'RPT-DAILY-TRADE', '交易日报', 'trade', 'daily',
   '{"tables":["clearing_result","trade_result","quote_plan"],"region_filter":"region_code","indicators":["成交量","成交均价","峰段均价","谷段均价","申报成交率"]}',
   '{"type":"table+line"}', '{"title":"交易日报（报送版）","caliber":"口径：交易中心结算数据与系统结算数据；单位：万元"}', 1, 'active'),
  (2, 'RPT-MONTH-SETTLE', '结算月报', 'settlement', 'monthly',
   '{"tables":["settlement_record","settlement_ledger"],"region_filter":"region_code","indicators":["电能量费用","偏差考核","辅助服务","输配电价","差异工单数"]}',
   '{"type":"table+bar"}', '{"title":"结算月报（报送版）","caliber":"口径：settlement.periodMode 决定周期口径；单位：万元"}', 1, 'active'),
  (3, 'RPT-WEEK-FORECAST', '预测周报', 'forecast', 'weekly',
   '{"tables":["forecast_result"],"region_filter":"region_code","indicators":["预测负荷","实际负荷","预测电价","实际电价","准确率"]}',
   '{"type":"line"}', '{"title":"预测周报（报送版）","caliber":"口径：预测结果与实绩比对；单位：万千瓦/元每兆瓦时"}', 1, 'active'),
  (4, 'RPT-MONTH-ASSESS', '考核月报', 'assessment', 'monthly',
   '{"indicators":["收益完成率","预测准确率","偏差率","合规执行率","综合得分"]}',
   '{"columns":["收益完成率","预测准确率","偏差率","合规执行率","综合得分"]}',
   '{"title":"考核月报（报送版）","caliber":"口径：考核规则库与指标权重；单位：%"}', 1, 'active'),
  (5, 'RPT-MONTH-BIZ', '经营分析月报', 'business', 'monthly',
   '{"indicators":["现货收益","价差收益","机会成本","偏差成本","净收益"]}',
   '{"columns":["现货收益","价差收益","机会成本","偏差成本","净收益"]}',
   '{"title":"经营分析月报（报送版）","caliber":"口径：现货与中长期收益归集；单位：万元"}', 1, 'active')
ON CONFLICT (id) DO NOTHING;

-- ── 12. 模型注册（MLflow 同步示例） ─────────────────────────
INSERT INTO model_registry (id, model_code, model_name, model_version, framework, metrics, status, file_url)
VALUES
  (1, 'price',      '现货价格预测模型', 'v1.0.0', 'lightgbm', '{"mape":0.086,"directional_accuracy":0.81}', 'online', 's3://ptidss/models/price_v1.0.0.bin'),
  (2, 'generation', '新能源出力预测模型', 'v1.0.0', 'lightgbm', '{"mape":0.112,"directional_accuracy":0.78}', 'online', 's3://ptidss/models/generation_v1.0.0.bin'),
  (3, 'load',       '负荷预测模型',     'v1.0.0', 'xgboost',  '{"mape":0.032,"directional_accuracy":0.93}', 'online', 's3://ptidss/models/load_v1.0.0.bin')
ON CONFLICT (id) DO NOTHING;

-- ── 13. 质量规则种子（v1.0.1 勘误条目 4：行情时序 region 非空门禁，与 TAG 提升联动） ──
INSERT INTO data_quality_rule (id, rule_code, rule_type, target_table, target_field, condition, threshold, severity, status, version)
VALUES
  (101, 'DQ-TS-REGION-NN-SPOT',    'completeness', 'st_spot_price',     'region_code', '{"op":"not_null","note":"行情时序 region 必填：TAG 提升后由子表必填 + 质量门禁双重保证（DDL-ERR-2026-001 条目 4）"}', 1.0000, 'high', 'active', 1),
  (102, 'DQ-TS-REGION-NN-MIDLONG', 'completeness', 'st_midlong_price',  'region_code', '{"op":"not_null","note":"中长期价格时序 region 必填：TAG 提升后由子表必填 + 质量门禁双重保证（DDL-ERR-2026-001 条目 4）"}', 1.0000, 'high', 'active', 1)
ON CONFLICT DO NOTHING;

-- ── 14. 情报推送规则（v1.0.2，V1.7：与 intel-service 推送执行器联动） ──
-- 匹配口径：情报 tags 与 tags_filter 交集非空 且 重要度命中 importance_filter →
-- 按 target_roles 派发个人消息（message_record.msg_type='intel_push'，biz_ref='INTEL-{newsId}' 幂等）。
INSERT INTO intel_push_rule (id, rule_name, tags_filter, importance_filter, target_roles, channel, silent_period, status)
VALUES
  (1, '现货价格推送',   '["现货", "价格"]',      'high',   '["trader"]',              '["web", "sms", "miniapp"]', NULL, 'active'),
  (2, '结算差异提醒',   '["结算", "差异"]',      'medium', '["settlement", "manager"]', '["web"]',                    NULL, 'active'),
  (3, '政策规则变动',   '["政策", "规则"]',      'high',   '["analyst", "compliance"]', '["web", "miniapp"]',      NULL, 'active')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 种子数据完毕：region(5) / role(7) / perm(17) / user(4) /
-- data_source(3) / collect_task(3) / intel_source(10) /
-- rule_config(4) / assess_indicator(5) / report_template(5) /
-- model_registry(3) / data_quality_rule(2，v1.0.1 勘误条目 4) /
-- intel_push_rule(3，v1.0.2 勘误：V1.7 情报推送规则种子)，共 65 行基线参照数据。
-- 说明：生产环境种子由应用迁移脚本执行，凭据由配置中心注入。
-- ============================================================
