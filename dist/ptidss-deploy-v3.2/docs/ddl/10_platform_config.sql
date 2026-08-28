-- =====================================================================
-- 10. 平台配置化（V2.2）：信息源对接配置 / 审批流定义 / LLM 模型 / 算法注册
-- 产品化诉求：灵活配置、适配不同客户部署场景（JWT/API/OAuth2/Basic/文件等对接方式）
-- 配套代码：IntelService/DataService/FlowService/LlmModelService/AlgorithmService
-- 执行方式：psql -h 127.0.0.1 -p 5432 -U ptidss -d ptidss -f 10_platform_config.sql
-- =====================================================================

-- ---------- 10.1 信息源对接配置（intel_source + data_source） ----------
-- 情报源：增加对接方式（认证形态）与连接参数（端点/令牌/密钥引用等，脱敏存储）
ALTER TABLE intel_source
  ADD COLUMN IF NOT EXISTS conn_type VARCHAR(16) NOT NULL DEFAULT 'api'
  CHECK (conn_type IN ('api','jwt','oauth2','basic','file','poll'));
ALTER TABLE intel_source
  ADD COLUMN IF NOT EXISTS conn_config JSONB;

-- 数据底座：已有 connect_config JSONB，补充对接方式枚举（REST/SFTP 双通道建模不变）
ALTER TABLE data_source
  ADD COLUMN IF NOT EXISTS conn_type VARCHAR(16) NOT NULL DEFAULT 'api'
  CHECK (conn_type IN ('api','jwt','oauth2','basic','file','poll'));

-- ---------- 10.2 LLM 模型配置（智能体可关联的生成式模型） ----------
CREATE TABLE IF NOT EXISTS llm_model (
  id                  BIGINT PRIMARY KEY,
  model_code          VARCHAR(32) NOT NULL,                 -- 模型编码（智能体绑定用）
  model_name          VARCHAR(128) NOT NULL,
  provider            VARCHAR(32) NOT NULL,                 -- deepseek/glm/qwen/openai-compatible/local
  endpoint            VARCHAR(255),                         -- API 地址（空=内置模拟推理网关）
  base_model          VARCHAR(64),                          -- 上游基础模型标识
  temperature         DECIMAL(4,2) NOT NULL DEFAULT 0.70,
  max_tokens          INT NOT NULL DEFAULT 2048,
  api_key_ref         VARCHAR(64),                          -- 密钥引用名（配置项，不落明文）
  status              VARCHAR(16) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_llm_model_code ON llm_model(model_code) WHERE NOT deleted;

-- ---------- 10.3 算法注册表（专业算法注册/替换，决策过程按类目匹配） ----------
CREATE TABLE IF NOT EXISTS algorithm_registry (
  id                  BIGINT PRIMARY KEY,
  alg_code            VARCHAR(64) NOT NULL,
  alg_name            VARCHAR(128) NOT NULL,
  category            VARCHAR(32) NOT NULL CHECK (category IN
                      ('forecast','market_analysis','quote_strategy','risk_measure',
                       'optimize','settlement','review','rule_engine')),
  description         VARCHAR(512),
  params_schema       JSONB NOT NULL DEFAULT '{}',          -- 算法参数模板（客户可调）
  version             VARCHAR(32) NOT NULL,                 -- 算法版本（同 agent_registry 约定）
  status              VARCHAR(16) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_algorithm_registry ON algorithm_registry(alg_code, version) WHERE NOT deleted;

-- ---------- 10.4 审批流程定义（环节/角色/用户可配置） ----------
CREATE TABLE IF NOT EXISTS flow_definition (
  id                  BIGINT PRIMARY KEY,
  process_key         VARCHAR(64) NOT NULL,
  process_name        VARCHAR(128) NOT NULL,
  biz_type            VARCHAR(32) NOT NULL,                 -- decision/declaration/ticket/appeal
  steps               JSONB NOT NULL,                       -- [{stepNo,stepName,approveMode,roleCodes,userIds,timeoutHours}]
  status              VARCHAR(16) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_flow_definition_key ON flow_definition(process_key) WHERE NOT deleted;

-- ---------- 10.5 默认种子（服务层懒种子兜底，此处保证初始化库直接可用） ----------

-- 审批流程定义（5 条默认：环节/角色可配置，与 PROCESS_BIZ_TYPE 静态映射等价收敛）
INSERT INTO flow_definition (id, process_key, process_name, biz_type, steps, status) VALUES
(90001, 'decision_confirm', '决策方案确认', 'decision', '[{"stepNo":"apply","stepName":"发起申请","approveMode":"any","roleCodes":["trader"],"userIds":[],"timeoutHours":24},{"stepNo":"review","stepName":"主管复核","approveMode":"any","roleCodes":["manager"],"userIds":[],"timeoutHours":24},{"stepNo":"approve","stepName":"决策批准","approveMode":"any","roleCodes":["manager"],"userIds":[],"timeoutHours":48}]'::jsonb, 'enabled'),
(90002, 'declaration_approve', '交易申报审批', 'declaration', '[{"stepNo":"apply","stepName":"发起申报","approveMode":"any","roleCodes":["trader"],"userIds":[],"timeoutHours":24},{"stepNo":"review","stepName":"申报审核","approveMode":"any","roleCodes":["analyst"],"userIds":[],"timeoutHours":24}]'::jsonb, 'enabled'),
(90003, 'ticket_handle', '差异工单处理', 'ticket', '[{"stepNo":"apply","stepName":"工单发起","approveMode":"any","roleCodes":["settlement"],"userIds":[],"timeoutHours":24},{"stepNo":"review","stepName":"结算复核","approveMode":"any","roleCodes":["settlement"],"userIds":[],"timeoutHours":24}]'::jsonb, 'enabled'),
(90004, 'appeal_review', '考核申诉评审', 'appeal', '[{"stepNo":"apply","stepName":"申诉发起","approveMode":"any","roleCodes":["settlement"],"userIds":[],"timeoutHours":24},{"stepNo":"review","stepName":"合规评审","approveMode":"any","roleCodes":["compliance"],"userIds":[],"timeoutHours":48}]'::jsonb, 'enabled'),
(90005, 'settlement_ticket_review', '结算单复核', 'ticket', '[{"stepNo":"apply","stepName":"结算单提交","approveMode":"any","roleCodes":["settlement"],"userIds":[],"timeoutHours":24},{"stepNo":"review","stepName":"复核确认","approveMode":"any","roleCodes":["manager"],"userIds":[],"timeoutHours":24}]'::jsonb, 'enabled')
ON CONFLICT DO NOTHING;

-- LLM 模型（3 条默认：内置模拟推理网关，endpoint 配置后可接入真实服务）
INSERT INTO llm_model (id, model_code, model_name, provider, endpoint, base_model, temperature, max_tokens, api_key_ref, status) VALUES
(91001, 'deepseek-v3', 'DeepSeek V3 对话模型', 'deepseek', 'https://api.deepseek.com/v1/chat/completions', 'deepseek-chat', 0.70, 2048, 'LLM_API_KEY_DEEPSEEK', 'enabled'),
(91002, 'glm-4', '智谱 GLM-4 对话模型', 'glm', 'https://open.bigmodel.cn/api/paas/v4/chat/completions', 'glm-4', 0.60, 2048, 'LLM_API_KEY_GLM', 'enabled'),
(91003, 'qwen-plus', '通义千问 Plus 对话模型', 'qwen', 'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions', 'qwen-plus', 0.70, 2048, 'LLM_API_KEY_QWEN', 'enabled')
ON CONFLICT DO NOTHING;

-- 算法注册（9 条默认：决策过程按 category 匹配最新 enabled 版本）
INSERT INTO algorithm_registry (id, alg_code, alg_name, category, description, params_schema, version, status) VALUES
(92001, 'LSTM-PRICE-96', 'LSTM 96 点价格预测', 'forecast', '负荷/新能源/价格历史特征序列预测，输出 96 点与置信带', '{"horizon":96,"confidence_band":90,"lookback":720}', 'v1.2.0', 'enabled'),
(92002, 'SENTI-NEWS-1', '情报舆情情感分析', 'market_analysis', '近 24h 情报流关键词情感加权（[-1,1]），修正供需判断', '{"window_hours":24,"high_weight":1.5}', 'v1.0.0', 'enabled'),
(92003, 'SEG-AGG-3PCT', '分段聚合报价（上浮 3%）', 'quote_strategy', '成本曲线分段聚合 + 基准情景报价，输出分段量价', '{"segments":8,"uplift":0.03}', 'v1.1.0', 'enabled'),
(92004, 'MC-CVAR-95', '蒙特卡洛 CVaR(95%) 风险度量', 'risk_measure', '出清波动率情景压力测试，输出 CVaR/最大回撤/限价', '{"scenarios":10000,"alpha":0.95}', 'v1.3.0', 'enabled'),
(92005, 'MILP-OPT-1', '混合整数规划联合优化', 'optimize', '申报/持仓/偏差考核约束下的收益最大化求解', '{"solver":"cbc","gap":0.01}', 'v1.0.0', 'enabled'),
(92006, 'DEV-ASSESS-1', '偏差考核结算测算', 'settlement', '结算收益与偏差考核风险预评估（规则阈值驱动）', '{"dev_threshold":0.05}', 'v1.0.0', 'enabled'),
(92007, 'KB-REVIEW-1', '复盘知识库归纳', 'review', '决策-结果-原因-改进四段式复盘，结论回流策略库', '{"template":"4step"}', 'v1.0.0', 'enabled'),
(92008, 'RULE-ENGINE-DROOLS', '规则引擎（合规校验）', 'rule_engine', 'rule_config 活动版本规则实时校验，仲裁最高优先', '{"engine":"drools"}', 'v2.1.0', 'enabled'),
(92009, 'HEDGE-STRATEGY-1', '省间价差套利策略', 'optimize', '省间通道价差监测与套利窗口识别（可选启用）', '{"min_spread":8,"window":4}', 'v1.0.0', 'disabled')
ON CONFLICT DO NOTHING;
