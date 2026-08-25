-- =====================================================================
-- 17. 系统配置中心（V3.0）：系统管理--系统配置 全面实现
-- 内容：
--   1) sys_config 配置项表（分组/类型/枚举/敏感/内置/排序），对标 PRD：
--      "申报段数、限价参数可配置""规则参数化配置，快速适配各省规则变化"
--      评审决议（通道/周期口径/多省模式）与等保三级安全参数；
--   2) 30 项内置种子配置：交易规则/结算/多省/优化/预测/模型/智能体/安全/通知 9 组；
--   3) 敏感项加密存储（应用层 ConfigCryptoService），列表脱敏 ******；
--      内置项（is_builtin=true）只允许改值不允许删除。
-- 执行方式：psql -h 127.0.0.1 -p 5432 -U ptidss -d ptidss -f 17_sys_config_v3_0.sql
-- 幂等：CREATE TABLE IF NOT EXISTS / ON CONFLICT 按 key 跳过，重复执行安全。
-- 配套代码：SysConfigService（CRUD + 业务读取 getEffectiveValue）+ AdminView Tab6
-- =====================================================================

-- ── 1. 配置项表（幂等） ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sys_config (
  id                  BIGINT PRIMARY KEY,
  config_key          VARCHAR(64) NOT NULL,                  -- 配置键（业务代码读取标识，如 rule.declareSegments）
  config_name         VARCHAR(128) NOT NULL,                 -- 中文名
  description         VARCHAR(255),                          -- 说明
  config_group        VARCHAR(32) NOT NULL,                  -- 分组：trade_rule/settlement/region/optimize/forecast/model/agent/security/notification
  config_type         VARCHAR(16) NOT NULL DEFAULT 'string'  -- string/number/boolean/select/json
                      CHECK (config_type IN ('string','number','boolean','select','json')),
  enum_values         JSONB,                                 -- select 类型枚举候选（["natural_month","trading_month"]）
  value               JSONB NOT NULL,                        -- 当前值（按类型：字符串/数值/布尔/枚举值/JSON 对象）
  is_sensitive        BOOLEAN NOT NULL DEFAULT FALSE,        -- 敏感项：加密存储 + 列表脱敏 ******
  is_builtin          BOOLEAN NOT NULL DEFAULT TRUE,         -- 系统内置：禁止删除（PRD 基线项）
  status              VARCHAR(8) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled')),
  sort_order          INT NOT NULL DEFAULT 100,              -- 组内排序
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_config_key ON sys_config(config_key) WHERE NOT deleted;

-- ── 2. 内置种子（幂等：按 config_key 跳过已存在） ──────────────────
INSERT INTO sys_config
  (id, config_key, config_name, description, config_group, config_type, enum_values, value, is_sensitive, is_builtin, status, sort_order) VALUES
-- 交易规则（PRD：申报段数/限价参数可配置；规则参数化快速适配各省规则变化）
(1,  'rule.declareSegments', '申报段数', '各省申报段数差异（现货/中长期），规则参数化适配各省', 'trade_rule', 'number', NULL, '3', FALSE, TRUE, 'enabled', 10),
(2,  'rule.priceUpperLimit', '申报限价上限', '申报价格上限（元/MWh），按省规则可调', 'trade_rule', 'number', NULL, '1500', FALSE, TRUE, 'enabled', 20),
(3,  'rule.priceLowerLimit', '申报限价下限', '申报价格下限（元/MWh）', 'trade_rule', 'number', NULL, '0', FALSE, TRUE, 'enabled', 30),
(4,  'rule.bidStep', '报价最小单位', '申报价格最小变动单位（元/MWh）', 'trade_rule', 'number', NULL, '1', FALSE, TRUE, 'enabled', 40),
(5,  'rule.tradeFeeRate', '交易手续费率', '交易手续费率（如 0.001 = 千分之一）', 'trade_rule', 'number', NULL, '0.001', FALSE, TRUE, 'enabled', 50),
(6,  'rule.maxDeclareTime', '申报截止时间', '日申报截止时间（HH:mm），逾期不可申报', 'trade_rule', 'string', NULL, '"10:00"', FALSE, TRUE, 'enabled', 60),
-- 结算（评审决议③：周期口径双口径）
(7,  'settlement.periodMode', '结算周期口径', '结算周期口径：natural_month 自然月 / trading_month 交易月（浙江等省）', 'settlement', 'select', '["natural_month","trading_month"]', '"natural_month"', FALSE, TRUE, 'enabled', 10),
(8,  'settlement.reconcileThreshold', '结算核对差异阈值', '结算核对差异容忍阈值（元），超出即偏差告警', 'settlement', 'number', NULL, '0.01', FALSE, TRUE, 'enabled', 20),
(9,  'settlement.autoReconcile', '自动核对开关', '结算完成后自动执行核对（对账）', 'settlement', 'boolean', NULL, 'true', FALSE, TRUE, 'enabled', 30),
(10, 'settlement.reconcileNotify', '核对偏差通知角色', '结算偏差告警通知角色（逗号分隔角色编码）', 'settlement', 'string', NULL, '"trader,manager"', FALSE, TRUE, 'enabled', 40),
-- 多省（评审决议⑤：多省模式与区域路由）
(11, 'region.mode', '多省模式', '多省模式：single 单省 / multi 多省', 'region', 'select', '["single","multi"]', '"multi"', FALSE, TRUE, 'enabled', 10),
(12, 'region.defaultRegionCode', '默认区域编码', '默认区域（单省模式/未路由时兜底）', 'region', 'string', NULL, '"CN-33"', FALSE, TRUE, 'enabled', 20),
(13, 'region.sourcesQueryTimeout', '各省行情查询超时', '各省行情/情报源查询超时（毫秒）', 'region', 'number', NULL, '5000', FALSE, TRUE, 'enabled', 30),
-- 优化（联合优化求解器与参数）
(14, 'optimize.solver', '联合优化求解器', '优化求解器：HiGHS 默认 / SCIP / Gurobi 兜底', 'optimize', 'select', '["HiGHS","SCIP","Gurobi"]', '"HiGHS"', FALSE, TRUE, 'enabled', 10),
(15, 'optimize.timeLimit', '求解时间上限', '联合优化求解时间上限（秒）', 'optimize', 'number', NULL, '120', FALSE, TRUE, 'enabled', 20),
(16, 'optimize.gapTolerance', '求解精度', '求解最优性间隙容差（0~1）', 'optimize', 'number', NULL, '0.01', FALSE, TRUE, 'enabled', 30),
-- 预测
(17, 'forecast.horizonDays', '预测天数', '负荷/价格预测展望天数', 'forecast', 'number', NULL, '7', FALSE, TRUE, 'enabled', 10),
(18, 'forecast.retrainIntervalHours', '模型重训间隔', '预测模型自动重训间隔（小时）', 'forecast', 'number', NULL, '24', FALSE, TRUE, 'enabled', 20),
(19, 'forecast.autoRun', '定时预测开关', '开启后按任务 cron 自动执行预测', 'forecast', 'boolean', NULL, 'true', FALSE, TRUE, 'enabled', 30),
-- 模型（算法包规范）
(20, 'model.uploadMaxSizeMB', '算法包上传上限', '算法包上传大小上限（MB）', 'model', 'number', NULL, '25', FALSE, TRUE, 'enabled', 10),
(21, 'model.defaultTimeoutSec', '模型推理超时', '模型推理默认超时（秒）', 'model', 'number', NULL, '30', FALSE, TRUE, 'enabled', 20),
(22, 'model.allowAutoAdapt', '算法包全自动适配', '开启后按算法包规范（manifest/同包 params.json）全自动适配', 'model', 'boolean', NULL, 'true', FALSE, TRUE, 'enabled', 30),
-- 智能体（PRD：智能体参数调优）
(23, 'agent.defaultTemperature', 'LLM 默认温度', '智能体 LLM 推理默认温度（0~2）', 'agent', 'number', NULL, '0.70', FALSE, TRUE, 'enabled', 10),
(24, 'agent.maxTokens', 'LLM 最大令牌', '智能体 LLM 单次响应最大令牌数', 'agent', 'number', NULL, '2048', FALSE, TRUE, 'enabled', 20),
(25, 'agent.autoReview', '情报重评开关', '新情报到达自动触发决策会话情报重评', 'agent', 'boolean', NULL, 'true', FALSE, TRUE, 'enabled', 30),
-- 安全（等保三级：身份鉴别/会话/审计）
(26, 'security.loginFailMax', '登录失败锁定次数', '连续登录失败达到该次数即锁定（等保 8.1.4.1）', 'security', 'number', NULL, '5', FALSE, TRUE, 'enabled', 10),
(27, 'security.loginLockMinutes', '登录锁定分钟', '登录失败锁定持续时长（分钟），到期自动解锁', 'security', 'number', NULL, '10', FALSE, TRUE, 'enabled', 20),
(28, 'security.sessionTimeoutMin', '会话超时（分钟）', '访问令牌有效期（分钟），超时需重新登录', 'security', 'number', NULL, '120', FALSE, TRUE, 'enabled', 30),
(29, 'security.captchaEnabled', '验证码开关', '登录图形验证码（交付态开启）', 'security', 'boolean', NULL, 'true', FALSE, TRUE, 'enabled', 40),
(30, 'security.auditRetentionDays', '审计日志保留天数', '操作审计日志保留天数，超期归档清理', 'security', 'number', NULL, '365', FALSE, TRUE, 'enabled', 50),
-- 通知
(31, 'notification.intelPushEnabled', '情报推送开关', '情报中心推送总开关（消息派发）', 'notification', 'boolean', NULL, 'true', FALSE, TRUE, 'enabled', 10),
(32, 'notification.smsChannelEnabled', '短信渠道开关', '短信渠道（high 级情报/告警实时推送）', 'notification', 'boolean', NULL, 'false', FALSE, TRUE, 'enabled', 20),
(33, 'notification.highPriorityDelaySec', 'high 级推送延迟', 'high 级情报推送延迟秒数（≤30s 实时）', 'notification', 'number', NULL, '30', FALSE, TRUE, 'enabled', 30)
ON CONFLICT (config_key) DO NOTHING;
