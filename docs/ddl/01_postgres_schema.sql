-- ============================================================
-- 电力交易智能辅助决策系统（PTIDSS）DDL 基线 v1.0.1（勘误版，Part 1/2）
-- 配套：《数据字典_全量_V1.0》（V1.1）｜开发基线 V1.6 5.6/5.7｜SRS V1.1｜05_评审记录.md
-- 数据库：PostgreSQL 16（2026-08-08 数据组评审通过，正式锁定；v1.0.1 勘误批次 DDL-ERR-2026-001 已执行）
-- 勘误（v1.0.1，条目 3）：audit_log 补 region_code（可空）+ ix_audit_log_region 索引（等保三级按省检索）；
--       RLS 策略脚本独立文件见 09_rls_policies.sql（条目 5）。
-- 开发环境落库修订（v1.0.2，条目 6）：declaration 为按月分区表，唯一索引必须包含分区键 trade_date
--       （PostgreSQL 对分区表唯一约束的硬性要求，PG16/18 均适用），uk_declaration_no 由
--       (declaration_no) 调整为 (declaration_no, trade_date)——业务上申报号按月生成，等价唯一。
-- 开发环境落库修订（v1.0.2，条目 7）：settlement_record 为按月分区表（主键含 created_at），
--       PG 不允许外键引用分区表主键的子集列，settlement_reconcile.record_id 移除 REFERENCES 约束，
--       保留字段与索引，外键完整性由应用层校验保证（分区表外键业界惯例）。
-- 开发环境落库修订（v1.0.2，条目 8）：intel_source.source_code 唯一约束为部分索引
--       （WHERE NOT deleted），PG 不允许外键引用部分索引，intel_news.source_code 移除
--       REFERENCES 约束，保留字段与索引，外键完整性由应用层校验保证。
-- 开发环境落库修订（v1.0.2，条目 9）：audit_log 补 username（可空）——等保三级审计按操作人检索，
--       未认证操作（如 login）由应用层从入参提取用户名写入，已认证操作冗余落库 （快照语义，用户改名不影响审计追溯）。
-- 开发环境落库修订（v1.0.2，条目 10）：declaration.stage / quote_plan.stage 由 VARCHAR(8) 调整为
--       VARCHAR(16)——枚举值 day_ahead（9 字符）超出原长度，交易申报模块联调发现并修正（PG 分区表
--       ALTER 自动传播到各分区）。
-- 开发环境落库修订（v1.0.2，条目 11）：strategy_feedback.review_id 移除 NOT NULL——OpenAPI V1.1
--       契约 required 仅 [strategyCode, feedback]，reviewId 可选；服务端缺省自动关联最近一份复盘
--       报告，无报告时允许 NULL 落库（契约优先，联调发现 DB 约束与契约不一致后修正）。
-- 开发环境落库修订（v1.0.3，条目 12）：新增 flow_instance 流程实例表（11.6 平台服务数据域）——
--       OpenAPI V1.1 /flow/start、/flow/instances/{instanceId} 契约落地（审批流：决策确认/申报审批/
--       差异工单/考核申诉）；settlement_ticket.flow_instance_id 既有预留字段（VARCHAR 业务号）与
--       本表 id 雪花 ID 通过 biz_id 关联；流程引擎为轻量状态机实现（不引入 Flowable，M7 移动端
--       审批依赖本表）。
-- 开发环境落库修订（v1.0.3，条目 13）：model_registry 业务版本列 version 改名 model_version，并补
--       乐观锁列 version INT——业务版本 VARCHAR(32) 与 BaseEntity 乐观锁 version 字段同名列冲突导致
--       编译失败（子类 getVersion() 返回类型不兼容），改名后新增乐观锁列保证实体继承体系一致。
-- 通用约定（与数据字典一致）：
--   id BIGINT 应用层雪花生成；created_at/updated_at TIMESTAMP；
--   version INT 乐观锁默认 1；deleted BOOLEAN 软删除默认 false；
--   金额 NUMERIC(18,4) 元；电量 NUMERIC(18,4) MWh；价格 NUMERIC(12,4) 元/MWh；
--   功率 NUMERIC(12,4) MW；时间 TIMESTAMP 北京时间；曲线类 JSONB；
--   region_code 多省配置化（评审决议⑤：P0 多省，支撑全国推广）；
--   落位 13 张核心表（12 张交易链路 + plant_unit）+ sys_user_region 授权 + sys_region 注册；
--   结算周期口径 settlement.periodMode 配置（评审决议③）；
--   交易中心双通道 exchange.channel 配置（评审决议①）；
--   敏感字段（合同价格/结算金额/联系方式）AES-GCM 加密存储（应用层）。
-- ============================================================

-- ==================== 二、发电与负荷域 ====================

-- 2.1 机组档案
CREATE TABLE plant_unit (
  id                  BIGINT PRIMARY KEY,
  unit_code           VARCHAR(32)  NOT NULL,
  plant_code          VARCHAR(32)  NOT NULL,
  unit_name           VARCHAR(128) NOT NULL,
  fuel_type           VARCHAR(16)  NOT NULL CHECK (fuel_type IN ('thermal','hydro','wind','pv','storage')),
  installed_capacity  NUMERIC(12,4) NOT NULL CHECK (installed_capacity > 0),
  grid_node           VARCHAR(64),
  region_code         VARCHAR(16)  NOT NULL,                -- 多省编码（通用约定⑥）
  status              VARCHAR(16)  NOT NULL DEFAULT 'active' CHECK (status IN ('active','retired','under_construction')),
  data_source         VARCHAR(16)  NOT NULL CHECK (data_source IN ('marketing_platform','manual')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_plant_unit_code ON plant_unit(unit_code) WHERE NOT deleted;
CREATE INDEX ix_plant_unit_plant ON plant_unit(plant_code) WHERE NOT deleted;
CREATE INDEX ix_plant_unit_region ON plant_unit(region_code) WHERE NOT deleted;

-- 2.3 检修计划
CREATE TABLE maintenance_plan (
  id                  BIGINT PRIMARY KEY,
  unit_code           VARCHAR(32)  NOT NULL,
  start_at            TIMESTAMP NOT NULL,
  end_at              TIMESTAMP NOT NULL CHECK (end_at > start_at),
  capacity_reduction  NUMERIC(12,4) NOT NULL CHECK (capacity_reduction >= 0),
  plan_type           VARCHAR(16)  NOT NULL CHECK (plan_type IN ('planned','unplanned')),
  remark              VARCHAR(255),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_maintenance_plan_unit ON maintenance_plan(unit_code, start_at);

-- ==================== 三、交易数据域 ====================

-- 3.1 合同（价格敏感，AES-GCM 加密）
CREATE TABLE contract (
  id                  BIGINT PRIMARY KEY,
  contract_no         VARCHAR(64)  NOT NULL,
  variety             VARCHAR(16)  NOT NULL CHECK (variety IN ('annual','monthly','intra_month','rolling')),
  direction           VARCHAR(8)   NOT NULL CHECK (direction IN ('buy','sell')),
  counterparty        VARCHAR(128),
  total_volume        NUMERIC(18,4) NOT NULL CHECK (total_volume > 0),
  curve_json          JSONB NOT NULL,                       -- 96 点 × 周期
  price               NUMERIC(12,4) NOT NULL CHECK (price >= 0),  -- 加密存储
  start_date          DATE NOT NULL,
  end_date            DATE NOT NULL CHECK (end_date >= start_date),
  status              VARCHAR(16)  NOT NULL DEFAULT 'draft' CHECK (status IN ('draft','active','executing','finished','terminated')),
  source              VARCHAR(16)  NOT NULL CHECK (source IN ('marketing_platform','exchange','manual')),
  region_code         VARCHAR(16)  NOT NULL,                -- 多省市场归属（评审决议⑤）
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_contract_no ON contract(contract_no) WHERE NOT deleted;
CREATE INDEX ix_contract_dates ON contract(start_date, end_date) WHERE NOT deleted;
CREATE INDEX ix_contract_region ON contract(region_code, status) WHERE NOT deleted;

-- 3.2 日滚动方案（决策输出，三情景）
CREATE TABLE rolling_plan (
  id                  BIGINT PRIMARY KEY,
  trade_date          DATE NOT NULL,
  scenario            VARCHAR(16) NOT NULL CHECK (scenario IN ('baseline','conservative','aggressive')),
  plan_type           VARCHAR(16) NOT NULL CHECK (plan_type IN ('rolling_adjust','quote_plan')),
  adjustments         JSONB NOT NULL,                       -- 调整明细（时段/电量/价格）
  expected_revenue    NUMERIC(18,4) NOT NULL,
  risk_metrics        JSONB NOT NULL,                       -- CVaR/最大回撤/偏差风险
  evidence_chain_ref  VARCHAR(64),                          -- decision_session.session_no
  status              VARCHAR(16) NOT NULL DEFAULT 'generated' CHECK (status IN ('generated','confirmed','modified','rejected','executed')),
  created_by          VARCHAR(32) NOT NULL,
  region_code         VARCHAR(16) NOT NULL,                -- 多省市场归属（评审决议⑤）
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_rolling_plan_date ON rolling_plan(trade_date, scenario) WHERE NOT deleted;
CREATE INDEX ix_rolling_plan_region ON rolling_plan(region_code, trade_date) WHERE NOT deleted;

-- 3.3 申报单（按月分区）
CREATE TABLE declaration (
  id                  BIGINT NOT NULL,
  declaration_no      VARCHAR(64) NOT NULL,
  contract_id         BIGINT,                             -- 关联合同（position_view 用）
  trade_date          DATE NOT NULL,
  market_type         VARCHAR(16) NOT NULL CHECK (market_type IN ('intra_province','inter_province')),
  stage               VARCHAR(16) NOT NULL CHECK (stage IN ('day_ahead','real_time','rolling')),
  region_code         VARCHAR(16) NOT NULL,                -- 申报市场所属区域（评审决议⑤）
  items               JSONB NOT NULL,                       -- 申报明细（段数/量价），合规校验后锁定
  compliance_check    JSONB NOT NULL,                       -- 段数/限价/持仓比例校验结果
  file_url            VARCHAR(255),
  status              VARCHAR(16) NOT NULL DEFAULT 'draft' CHECK (status IN ('draft','pending_submit','submitted','receipted','partially_matched')),
  receipt_no          VARCHAR(64),
  created_by          VARCHAR(32) NOT NULL,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id, trade_date)
) PARTITION BY RANGE (trade_date);
CREATE UNIQUE INDEX uk_declaration_no ON declaration(declaration_no, trade_date) WHERE NOT deleted;  -- v1.0.2：分区表唯一索引须含分区键
CREATE INDEX ix_declaration_contract ON declaration(contract_id) WHERE NOT deleted;
CREATE INDEX ix_declaration_date ON declaration(trade_date);
CREATE INDEX ix_declaration_status ON declaration(status, trade_date);
CREATE INDEX ix_declaration_region ON declaration(region_code, trade_date);
-- 月度分区示例（由数据组按月预建，如 2026-08）
CREATE TABLE declaration_p202608 PARTITION OF declaration FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE declaration_p202609 PARTITION OF declaration FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');

-- 3.4 成交结果（按日分区）
CREATE TABLE trade_result (
  id                  BIGINT NOT NULL,
  declaration_id      BIGINT NOT NULL,
  trade_date          DATE NOT NULL,
  region_code         VARCHAR(16) NOT NULL,                -- 成交市场所属区域（评审决议⑤）
  matched_volume      NUMERIC(18,4) NOT NULL CHECK (matched_volume >= 0),
  matched_price       NUMERIC(12,4) NOT NULL CHECK (matched_price >= 0),
  matched_curve       JSONB NOT NULL,                       -- 96 点成交曲线
  settlement_impact   JSONB,                                -- 结算影响测算
  status              VARCHAR(16) NOT NULL CHECK (status IN ('matched','partially','unmatched')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id, trade_date)
) PARTITION BY RANGE (trade_date);
CREATE INDEX ix_trade_result_decl ON trade_result(declaration_id);
CREATE INDEX ix_trade_result_date ON trade_result(trade_date);
CREATE INDEX ix_trade_result_region ON trade_result(region_code, trade_date);
CREATE TABLE trade_result_p202608 PARTITION OF trade_result FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE trade_result_p202609 PARTITION OF trade_result FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');

-- 3.5 现货报价方案
CREATE TABLE quote_plan (
  id                  BIGINT PRIMARY KEY,
  trade_date          DATE NOT NULL,
  market_type         VARCHAR(16) NOT NULL DEFAULT 'intra_province' CHECK (market_type IN ('intra_province','inter_province')),
  stage               VARCHAR(16) NOT NULL CHECK (stage IN ('day_ahead','real_time')),
  region_code         VARCHAR(16) NOT NULL,                -- 报价市场所属区域（评审决议⑤）
  segments            JSONB NOT NULL,                       -- [{segment_no, price, volume}]
  risk_metrics        JSONB NOT NULL,                       -- CVaR/回撤
  scenario_simulation JSONB NOT NULL,                       -- 情景模拟结果
  status              VARCHAR(16) NOT NULL DEFAULT 'generated' CHECK (status IN ('generated','confirmed','submitted')),
  created_by          VARCHAR(32) NOT NULL,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_quote_plan_date ON quote_plan(trade_date, stage) WHERE NOT deleted;
CREATE INDEX ix_quote_plan_region ON quote_plan(region_code, trade_date) WHERE NOT deleted;

-- 3.6 出清结果
CREATE TABLE clearing_result (
  id                  BIGINT PRIMARY KEY,
  trade_date          DATE NOT NULL,
  market_type         VARCHAR(16) NOT NULL,
  region_code         VARCHAR(16) NOT NULL,                -- 出清市场所属区域（评审决议⑤）
  cleared_volume      NUMERIC(18,4) NOT NULL CHECK (cleared_volume >= 0),
  cleared_price       NUMERIC(12,4) NOT NULL CHECK (cleared_price >= 0),
  congestion_info     JSONB,
  analysis            JSONB,                                -- 偏差/收益测算
  source              VARCHAR(16) NOT NULL CHECK (source IN ('exchange','manual')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_clearing_result_date ON clearing_result(trade_date, market_type) WHERE NOT deleted;
CREATE INDEX ix_clearing_result_region ON clearing_result(region_code, trade_date) WHERE NOT deleted;

-- ==================== 四、结算数据域 ====================

-- 4.1 结算记录（按月分区；金额加密；周期口径由 settlement.periodMode 配置）
CREATE TABLE settlement_record (
  id                  BIGINT NOT NULL,
  settlement_period   VARCHAR(16) NOT NULL,                 -- 如 2026-08，口径随配置
  region_code         VARCHAR(16) NOT NULL,                 -- 多省隔离（评审决议⑤）
  source              VARCHAR(16) NOT NULL CHECK (source IN ('system','exchange')),
  items               JSONB NOT NULL,                       -- 电能量/偏差考核/辅助服务/输配电价
  total_amount        NUMERIC(18,4) NOT NULL,               -- 加密存储
  sync_status         VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (sync_status IN ('synced','pending','diff')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);
CREATE INDEX ix_settlement_record_period ON settlement_record(settlement_period);
CREATE INDEX ix_settlement_record_region ON settlement_record(region_code);
CREATE INDEX ix_settlement_record_sync ON settlement_record(sync_status);
CREATE TABLE settlement_record_p202608 PARTITION OF settlement_record FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE settlement_record_p202609 PARTITION OF settlement_record FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');

-- 4.2 结算核对结果
CREATE TABLE settlement_reconcile (
  id                  BIGINT PRIMARY KEY,
  record_id           BIGINT NOT NULL,                        -- 关联结算记录（v1.0.2：分区表外键移约束，应用层校验）
  check_items         JSONB NOT NULL,                       -- 逐科目核对
  diff_records        JSONB NOT NULL,
  status              VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (status IN ('consistent','diff','pending')),
  diff_amount         NUMERIC(18,4),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_settlement_reconcile_rec ON settlement_reconcile(record_id) WHERE NOT deleted;

-- 4.3 差异工单（Flowable 流程挂接）
CREATE TABLE settlement_ticket (
  id                  BIGINT PRIMARY KEY,
  reconcile_id        BIGINT NOT NULL REFERENCES settlement_reconcile(id),
  diff_type           VARCHAR(32) NOT NULL CHECK (diff_type IN ('amount_diff','missing_record','extra_record')),
  diff_amount         NUMERIC(18,4) NOT NULL,
  status              VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','processing','reviewed','closed')),
  handler             VARCHAR(32),
  history             JSONB NOT NULL,                       -- 处理留痕时间线
  flow_instance_id    VARCHAR(64),                          -- Flowable 流程实例
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_settlement_ticket_status ON settlement_ticket(status) WHERE NOT deleted;

-- 4.4 结算台账（关账后禁止修改，审批解锁）
CREATE TABLE settlement_ledger (
  id                  BIGINT PRIMARY KEY,
  period              VARCHAR(16) NOT NULL,                 -- 周期口径随 settlement.periodMode
  region_code         VARCHAR(16) NOT NULL,                -- 结算台账按省隔离（评审决议⑤）
  amount              NUMERIC(18,4) NOT NULL,
  status              VARCHAR(16) NOT NULL DEFAULT 'opened' CHECK (status IN ('opened','confirmed','closed')),
  attachments         JSONB,                                -- MinIO 附件清单
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_settlement_ledger_period ON settlement_ledger(period) WHERE NOT deleted;
CREATE INDEX ix_settlement_ledger_region ON settlement_ledger(region_code, period) WHERE NOT deleted;

-- 4.5 结算单 OCR 识别任务
CREATE TABLE ocr_task (
  id                  BIGINT PRIMARY KEY,
  file_id             VARCHAR(64) NOT NULL,                 -- MinIO
  template_id         BIGINT,
  status              VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','recognizing','success','low_confidence','failed')),
  confidence          NUMERIC(5,4) NOT NULL,                -- 0-1
  fields              JSONB NOT NULL,                       -- 电量/电价/费用/考核抽取字段
  review_status       VARCHAR(16) NOT NULL DEFAULT 'not_required' CHECK (review_status IN ('not_required','pending','reviewed')),
  reviewer            VARCHAR(32),
  reviewed_at         TIMESTAMP,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_ocr_task_status ON ocr_task(status, review_status) WHERE NOT deleted;

-- 4.6 OCR 识别模板
CREATE TABLE ocr_template (
  id                  BIGINT PRIMARY KEY,
  template_name       VARCHAR(128) NOT NULL,
  layout_schema       JSONB NOT NULL,                       -- 字段锚点定义
  sample_count        INT NOT NULL DEFAULT 0,
  accuracy            NUMERIC(5,4),                         -- 实测准确率 0-1
  status              VARCHAR(16) NOT NULL DEFAULT 'learning' CHECK (status IN ('learning','active','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);

-- ==================== 五、政策数据域 ====================

-- 5.1 政策文件
CREATE TABLE policy_document (
  id                  BIGINT PRIMARY KEY,
  title               VARCHAR(255) NOT NULL,
  issuing_body        VARCHAR(128) NOT NULL,
  category            VARCHAR(16) NOT NULL CHECK (category IN ('national','regional','provincial')),
  tags                JSONB NOT NULL,                       -- 现货/中长期/结算/考核/信息披露
  version_no          INT NOT NULL DEFAULT 1,               -- 同一政策多版本
  file_url            VARCHAR(255) NOT NULL,                -- MinIO/PDF 原文
  publish_date        DATE NOT NULL,
  effective_date      DATE NOT NULL,
  status              VARCHAR(16) NOT NULL DEFAULT 'published' CHECK (status IN ('draft','published','expired')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_policy_doc_cat ON policy_document(category, publish_date) WHERE NOT deleted;

-- 5.2 解析条款（LLM 结构化解析 + 人工确认）
CREATE TABLE policy_article (
  id                  BIGINT PRIMARY KEY,
  policy_id           BIGINT NOT NULL REFERENCES policy_document(id),
  clause_type         VARCHAR(16) NOT NULL CHECK (clause_type IN ('trade_rule','price_mechanism','assessment','settlement')),
  original_text       TEXT NOT NULL,
  parsed_structure    JSONB NOT NULL,                       -- 参数化字段
  confidence          NUMERIC(5,4) NOT NULL,                -- 0-1
  related_rule_id     BIGINT,
  review_status       VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (review_status IN ('pending','confirmed','revised')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_policy_article_policy ON policy_article(policy_id) WHERE NOT deleted;

-- 5.3 影响研判
CREATE TABLE policy_analysis (
  id                  BIGINT PRIMARY KEY,
  policy_id           BIGINT NOT NULL REFERENCES policy_document(id),
  change_point        VARCHAR(255) NOT NULL,
  affected_link       VARCHAR(64) NOT NULL CHECK (affected_link IN ('预测','决策','申报','结算','考核')),
  impact_level        VARCHAR(8) NOT NULL CHECK (impact_level IN ('high','medium','low')),
  analysis_result     JSONB NOT NULL,                       -- 研判结论（关联历史数据）
  analyst             VARCHAR(32) NOT NULL,
  brief_file_url      VARCHAR(255),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_policy_analysis_policy ON policy_analysis(policy_id) WHERE NOT deleted;

-- 5.4 规则库（版本化；version 字段为本表业务版本）
CREATE TABLE rule_config (
  id                  BIGINT PRIMARY KEY,
  rule_code           VARCHAR(64) NOT NULL,
  rule_name           VARCHAR(128) NOT NULL,
  rule_type           VARCHAR(16) NOT NULL CHECK (rule_type IN ('compliance','decision','assessment')),
  params              JSONB NOT NULL,                       -- 段数/限价/阈值等
  version             INT NOT NULL DEFAULT 1,               -- 规则版本（业务版本，非乐观锁）
  effective_date      DATE NOT NULL,
  expired_date        DATE NOT NULL CHECK (expired_date >= effective_date),
  source_policy_id    BIGINT,
  status              VARCHAR(16) NOT NULL DEFAULT 'draft' CHECK (status IN ('draft','active','expired')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_rule_config_code ON rule_config(rule_code, version) WHERE NOT deleted;

-- ==================== 六、预测数据域 ====================

-- 6.1 预测任务
CREATE TABLE forecast_task (
  id                  BIGINT PRIMARY KEY,
  task_no             VARCHAR(64) NOT NULL,
  model_code          VARCHAR(32) NOT NULL CHECK (model_code IN ('generation','price','load')),
  predict_date        DATE NOT NULL,
  input_version       VARCHAR(64) NOT NULL,                 -- 特征版本
  region_code         VARCHAR(16) NOT NULL,                -- 预测目标区域（评审决议⑤）
  status              VARCHAR(16) NOT NULL DEFAULT 'queued' CHECK (status IN ('queued','running','success','failed','degraded')),
  start_time          TIMESTAMP,
  end_time            TIMESTAMP,
  error_msg           VARCHAR(500),
  degrade_reason      VARCHAR(255),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_forecast_task_no ON forecast_task(task_no) WHERE NOT deleted;
CREATE INDEX ix_forecast_task_date ON forecast_task(predict_date, model_code) WHERE NOT deleted;
CREATE INDEX ix_forecast_task_region ON forecast_task(region_code, predict_date) WHERE NOT deleted;

-- 6.2 预测结果（元数据；96 点明细在 TDengine st_forecast_series）
CREATE TABLE forecast_result (
  id                  BIGINT PRIMARY KEY,
  task_id             BIGINT NOT NULL REFERENCES forecast_task(id),
  model_version       VARCHAR(32) NOT NULL,                 -- v{主}.{次}.{补}
  predict_type        VARCHAR(16) NOT NULL CHECK (predict_type IN ('generation','price','load')),
  market_type         VARCHAR(16),
  region_code         VARCHAR(16) NOT NULL,                -- 预测目标区域（评审决议⑤）
  trade_date          DATE NOT NULL,
  value               NUMERIC(12,4) NOT NULL,
  lower_bound         NUMERIC(12,4) NOT NULL,               -- 90% 置信区间
  upper_bound         NUMERIC(12,4) NOT NULL CHECK (upper_bound >= lower_bound),
  confidence          NUMERIC(5,4) NOT NULL,                -- 0-1
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_forecast_result_date ON forecast_result(trade_date, predict_type) WHERE NOT deleted;
CREATE INDEX ix_forecast_result_region ON forecast_result(region_code, trade_date) WHERE NOT deleted;

-- 6.3 模型注册（MLflow 同步）
CREATE TABLE model_registry (
  id                  BIGINT PRIMARY KEY,
  model_code          VARCHAR(32) NOT NULL,
  model_name          VARCHAR(128) NOT NULL,
  model_version       VARCHAR(32) NOT NULL,                 -- 版本（避开 BaseEntity 乐观锁 version 列名冲突）
  framework           VARCHAR(32) NOT NULL CHECK (framework IN ('pytorch','xgboost','lightgbm')),
  metrics             JSONB NOT NULL,                       -- MAPE/方向准确率
  status              VARCHAR(16) NOT NULL DEFAULT 'training' CHECK (status IN ('training','evaluating','online','rolled_back')),
  file_url            VARCHAR(255) NOT NULL,                -- MinIO 权重
  trained_at          TIMESTAMP,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,                -- 乐观锁（条目 13 补）
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_model_registry ON model_registry(model_code, model_version) WHERE NOT deleted;

-- 6.4 训练任务
CREATE TABLE training_task (
  id                  BIGINT PRIMARY KEY,
  model_code          VARCHAR(32) NOT NULL,
  dataset_range       JSONB NOT NULL,                       -- 数据集区间
  config              JSONB NOT NULL,                       -- 超参
  status              VARCHAR(16) NOT NULL DEFAULT 'queued' CHECK (status IN ('queued','training','success','failed')),
  metrics             JSONB,
  artifact_url        VARCHAR(255),                         -- MLflow 运行
  triggered_by        VARCHAR(16) NOT NULL CHECK (triggered_by IN ('daily_increment','weekly_full','manual')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_training_task_model ON training_task(model_code, status) WHERE NOT deleted;

-- ==================== 七、决策与优化数据域 ====================

-- 7.1 智能体注册（LangGraph 多智能体）
CREATE TABLE agent_registry (
  id                  BIGINT PRIMARY KEY,
  agent_code          VARCHAR(32) NOT NULL CHECK (agent_code IN ('forecast','market','quote','risk','compliance','settlement','review')),
  agent_name          VARCHAR(64) NOT NULL,
  role                VARCHAR(128) NOT NULL,                -- 职责描述
  input_schema        JSONB NOT NULL,
  output_schema       JSONB NOT NULL,
  version             VARCHAR(32) NOT NULL,
  model_config        JSONB NOT NULL,                       -- LLM/规则/优化
  status              VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','disabled','maintenance')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_agent_registry_code ON agent_registry(agent_code, version) WHERE NOT deleted;

-- 7.2 智能体运行记录
CREATE TABLE agent_run (
  id                  BIGINT PRIMARY KEY,
  run_id              VARCHAR(64) NOT NULL,
  agent_code          VARCHAR(32) NOT NULL,
  session_id          VARCHAR(64) NOT NULL,                 -- decision_session.session_no
  input_snapshot      JSONB NOT NULL,
  output              JSONB NOT NULL,
  confidence          NUMERIC(5,4) NOT NULL,                -- 0-1
  reasoning           JSONB NOT NULL,                       -- 推理过程
  elapsed_ms          INT NOT NULL,
  status              VARCHAR(16) NOT NULL CHECK (status IN ('success','failed','timeout')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_agent_run_id ON agent_run(run_id) WHERE NOT deleted;
CREATE INDEX ix_agent_run_session ON agent_run(session_id) WHERE NOT deleted;

-- 7.3 决策会话（人机协同：人审/修改依据/双人复核）
CREATE TABLE decision_session (
  id                  BIGINT PRIMARY KEY,
  session_no          VARCHAR(64) NOT NULL,
  session_type        VARCHAR(16) NOT NULL CHECK (session_type IN ('rolling','spot_quote','joint_optimize')),
  trade_date          DATE NOT NULL,
  orchestrator_version VARCHAR(32) NOT NULL,
  agents              JSONB NOT NULL,                       -- 参与智能体列表
  final_strategy      JSONB NOT NULL,
  evidence_chain      JSONB NOT NULL,                       -- 依据链全量快照
  conflict_records    JSONB NOT NULL,                       -- 冲突仲裁记录
  human_review_status VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (human_review_status IN ('pending','confirmed','modified','rejected')),
  reviewed_by         VARCHAR(32),
  reviewed_at         TIMESTAMP,
  modify_reason       TEXT,                                 -- 修改必须记录依据（FR-DM-05）
  reviewer2           VARCHAR(32),                          -- 双人复核
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_decision_session_no ON decision_session(session_no) WHERE NOT deleted;
CREATE INDEX ix_decision_session_date ON decision_session(trade_date, session_type) WHERE NOT deleted;

-- 7.4 联合优化任务（HiGHS/Gurobi 兜底，suboptimal 降级）
CREATE TABLE joint_optim_task (
  id                  BIGINT PRIMARY KEY,
  task_no             VARCHAR(64) NOT NULL,
  task_type           VARCHAR(16) NOT NULL CHECK (task_type IN ('daily','rolling_N','backtest')),
  horizon_days        INT NOT NULL CHECK (horizon_days BETWEEN 1 AND 7),
  scenarios           JSONB NOT NULL,                       -- 场景抽样配置
  objective_weights   JSONB NOT NULL,                       -- 目标权重
  constraints         JSONB NOT NULL,                       -- CVaR/段数/限价/持仓/爬坡/考核
  status              VARCHAR(16) NOT NULL DEFAULT 'queued' CHECK (status IN ('queued','running','success','suboptimal','failed')),
  elapsed_ms          INT,
  solver              VARCHAR(32) NOT NULL CHECK (solver IN ('HiGHS','SCIP','Gurobi')),
  created_by          VARCHAR(32) NOT NULL,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_joint_optim_task_status ON joint_optim_task(status, created_at) WHERE NOT deleted;

-- 7.5 策略回测
CREATE TABLE backtest_run (
  id                  BIGINT PRIMARY KEY,
  strategy_code       VARCHAR(64) NOT NULL,
  date_range          JSONB NOT NULL,                       -- 回测区间
  market_data_version VARCHAR(64) NOT NULL,                 -- 数据版本锁定
  base_plan           JSONB NOT NULL,                       -- 基准方案（分步决策）
  optimized_plan      JSONB NOT NULL,
  revenue_delta       NUMERIC(18,4) NOT NULL,               -- 收益增量（验收核心）
  metrics             JSONB NOT NULL,                       -- 指标集（夏普/回撤/命中率）
  status              VARCHAR(16) NOT NULL DEFAULT 'running' CHECK (status IN ('running','success','failed')),
  report_url          VARCHAR(255),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_backtest_strategy ON backtest_run(strategy_code, created_at) WHERE NOT deleted;

-- 7.6 策略库
CREATE TABLE strategy_library (
  id                  BIGINT PRIMARY KEY,
  strategy_code       VARCHAR(64) NOT NULL,
  strategy_name       VARCHAR(128) NOT NULL,
  params              JSONB NOT NULL,
  performance         JSONB NOT NULL,                       -- 收益/胜率/回撤
  status              VARCHAR(16) NOT NULL DEFAULT 'effective' CHECK (status IN ('effective','invalid','evaluating')),
  source              VARCHAR(16) NOT NULL CHECK (source IN ('backtest','review','manual')),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_strategy_code ON strategy_library(strategy_code) WHERE NOT deleted;


-- ==================== 八、复盘与成效考核数据域 ====================

-- 8.1 复盘报告（三层归因：预测/决策/执行）
CREATE TABLE review_report (
  id                  BIGINT PRIMARY KEY,
  report_type         VARCHAR(16) NOT NULL CHECK (report_type IN ('weekly','monthly','special')),
  period_start        DATE NOT NULL,
  period_end          DATE NOT NULL CHECK (period_end >= period_start),
  summary             JSONB NOT NULL,                       -- 收益/成交/偏差
  deviation_analysis  JSONB NOT NULL,                       -- 三层归因
  strategy_eval       JSONB NOT NULL,                       -- 策略评估
  suggestions         JSONB,
  status              VARCHAR(16) NOT NULL DEFAULT 'generating' CHECK (status IN ('generating','completed','failed')),
  file_url            VARCHAR(255),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_review_report_period ON review_report(report_type, period_start) WHERE NOT deleted;

-- 8.2 偏差归因记录
CREATE TABLE deviation_record (
  id                  BIGINT PRIMARY KEY,
  report_id           BIGINT NOT NULL REFERENCES review_report(id),
  layer               VARCHAR(16) NOT NULL CHECK (layer IN ('forecast','decision','execution')),
  item                VARCHAR(64) NOT NULL,                 -- 如 电价预测偏差
  value               NUMERIC(18,4),
  impact_amount       NUMERIC(18,4) NOT NULL,               -- 收益影响（元）
  reason              VARCHAR(255) NOT NULL,
  direction           VARCHAR(8) NOT NULL CHECK (direction IN ('positive','negative')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_deviation_record_report ON deviation_record(report_id) WHERE NOT deleted;

-- 8.3 策略回流（复盘→策略库）
CREATE TABLE strategy_feedback (
  id                  BIGINT PRIMARY KEY,
  review_id           BIGINT REFERENCES review_report(id),   -- 契约可选：缺省关联最近复盘报告
  strategy_code       VARCHAR(64) NOT NULL,
  feedback            VARCHAR(16) NOT NULL CHECK (feedback IN ('effective','invalid','adjust')),
  updated_params      JSONB,
  status              VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','confirmed','rejected')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_strategy_feedback_review ON strategy_feedback(review_id) WHERE NOT deleted;

-- 8.4 考核指标
CREATE TABLE assess_indicator (
  id                  BIGINT PRIMARY KEY,
  code                VARCHAR(64) NOT NULL,
  name                VARCHAR(128) NOT NULL,                -- 收益完成率/预测准确率/偏差率/合规执行率/复盘完成率
  formula             JSONB NOT NULL,
  weight              NUMERIC(5,4) NOT NULL CHECK (weight BETWEEN 0 AND 1),
  target_value        JSONB NOT NULL,
  scoring_rule        JSONB NOT NULL,
  data_source         VARCHAR(64) NOT NULL,
  status              VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_assess_indicator_code ON assess_indicator(code) WHERE NOT deleted;

-- 8.5 考核结果
CREATE TABLE assess_result (
  id                  BIGINT PRIMARY KEY,
  period              VARCHAR(16) NOT NULL,                 -- 考核周期
  scope               VARCHAR(8) NOT NULL CHECK (scope IN ('personal','team')),
  user_id             BIGINT,
  scores              JSONB NOT NULL,                       -- 分项得分
  total_score         NUMERIC(8,2) NOT NULL,
  rank                INT,
  status              VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','confirmed','appealing','corrected')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_assess_result_period ON assess_result(period, scope) WHERE NOT deleted;

-- 8.6 考核申诉
CREATE TABLE assess_appeal (
  id                  BIGINT PRIMARY KEY,
  result_id           BIGINT NOT NULL REFERENCES assess_result(id),
  appeal_reason       TEXT NOT NULL,
  evidence            JSONB,
  status              VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','processing','approved','rejected')),
  handler             VARCHAR(32),
  decision            TEXT,
  history             JSONB NOT NULL,                       -- 处理留痕
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_assess_appeal_status ON assess_appeal(status) WHERE NOT deleted;

-- ==================== 九、报表数据域 ====================

-- 9.1 报表模板
CREATE TABLE report_template (
  id                  BIGINT PRIMARY KEY,
  code                VARCHAR(64) NOT NULL,
  name                VARCHAR(128) NOT NULL,
  type                VARCHAR(32) NOT NULL CHECK (type IN ('trade','settlement','forecast','assessment','business')),
  period_type         VARCHAR(8) NOT NULL CHECK (period_type IN ('daily','weekly','monthly','yearly')),
  datasource_config   JSONB NOT NULL,                       -- 数据源/指标定义
  layout              JSONB NOT NULL,
  header_config       JSONB NOT NULL,                       -- 表头/口径说明（报送格式）
  version             INT NOT NULL DEFAULT 1,               -- 模板版本
  status              VARCHAR(16) NOT NULL DEFAULT 'draft' CHECK (status IN ('draft','active','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_report_template_code ON report_template(code, version) WHERE NOT deleted;

-- 9.2 报表实例
CREATE TABLE report_instance (
  id                  BIGINT PRIMARY KEY,
  template_id         BIGINT NOT NULL REFERENCES report_template(id),
  period              VARCHAR(16) NOT NULL,
  region_code         VARCHAR(16) NOT NULL,                -- 报表按省隔离（通用约定⑥）
  data_snapshot       JSONB NOT NULL,                       -- 生成时数据快照（口径可追溯）
  file_url            VARCHAR(255),                         -- Excel/PDF/Word
  generate_status     VARCHAR(16) NOT NULL DEFAULT 'pending' CHECK (generate_status IN ('pending','generating','success','failed')),
  push_status         VARCHAR(16) NOT NULL DEFAULT 'none' CHECK (push_status IN ('none','pushed')),
  generated_at        TIMESTAMP,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_report_instance_period ON report_instance(template_id, period) WHERE NOT deleted;
CREATE INDEX ix_report_instance_region ON report_instance(region_code, period) WHERE NOT deleted;

-- ==================== 十、平台数据域 ====================

-- 10.1 用户（联系方式脱敏/加密存储）
CREATE TABLE sys_user (
  id                  BIGINT PRIMARY KEY,
  username            VARCHAR(64) NOT NULL,
  display_name        VARCHAR(64) NOT NULL,
  password_hash       VARCHAR(255),                         -- BCrypt（本地账号）
  role_ids            JSONB NOT NULL,                       -- 角色 ID 列表
  org_code            VARCHAR(32),
  phone               VARCHAR(32),                          -- AES-GCM 加密
  email               VARCHAR(128),                         -- AES-GCM 加密
  status              VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','locked','disabled')),
  last_login_at       TIMESTAMP,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_sys_user_username ON sys_user(username) WHERE NOT deleted;

-- 10.2 角色 / 权限 / 关联（三级权限：菜单/接口/数据）
CREATE TABLE sys_role (
  id                  BIGINT PRIMARY KEY,
  role_code           VARCHAR(32) NOT NULL CHECK (role_code IN ('trader','analyst','settlement','admin','manager','compliance','mobile')),
  role_name           VARCHAR(64) NOT NULL,
  description         VARCHAR(255),
  status              VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_sys_role_code ON sys_role(role_code) WHERE NOT deleted;

CREATE TABLE sys_permission (
  id                  BIGINT PRIMARY KEY,
  perm_code           VARCHAR(64) NOT NULL,
  perm_name           VARCHAR(128) NOT NULL,
  resource_type       VARCHAR(8) NOT NULL CHECK (resource_type IN ('menu','api','data')),
  resource_pattern    VARCHAR(255) NOT NULL,
  status              VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_sys_permission_code ON sys_permission(perm_code) WHERE NOT deleted;

CREATE TABLE sys_role_permission (
  role_id             BIGINT NOT NULL REFERENCES sys_role(id),
  permission_id       BIGINT NOT NULL REFERENCES sys_permission(id),
  PRIMARY KEY (role_id, permission_id)
);

-- 用户-区域授权（评审决议⑤：多省配置化，角色 × region 双重授权）
CREATE TABLE sys_user_region (
  user_id             BIGINT NOT NULL REFERENCES sys_user(id),
  region_code         VARCHAR(16) NOT NULL,                 -- 多省编码
  PRIMARY KEY (user_id, region_code)
);

-- 角色-区域授权（评审决议⑤：有效区域 = 用户授权区域 ∩ 角色授权区域，
-- 角色未配置区域时以用户授权区域为准，向后兼容存量数据）
CREATE TABLE sys_role_region (
  role_id             BIGINT NOT NULL REFERENCES sys_role(id),
  region_code         VARCHAR(16) NOT NULL,                 -- 多省编码
  PRIMARY KEY (role_id, region_code)
);

-- 10.3 审计日志（按月分区；关键操作前后快照）
CREATE TABLE audit_log (
  id                  BIGINT NOT NULL,
  trace_id            VARCHAR(64) NOT NULL,
  user_id             BIGINT NOT NULL,
  username            VARCHAR(64),                          -- 操作人用户名冗余（v1.0.2 条目 9：未认证操作如 login 由应用层从入参提取；用户改名不影响审计追溯）
  action              VARCHAR(64) NOT NULL,                 -- confirm_plan/modify_plan/declaration_submit/settlement_review...
  target_type         VARCHAR(32) NOT NULL,
  target_id           VARCHAR(64) NOT NULL,
  before_snapshot     JSONB,
  after_snapshot      JSONB,
  ip                  VARCHAR(45) NOT NULL,
  user_agent          VARCHAR(255) NOT NULL,
  result              VARCHAR(8) NOT NULL CHECK (result IN ('success','fail')),
  region_code         VARCHAR(16),                          -- 操作归属区域（平台级操作可为空；等保三级审计按省检索，v1.0.1 勘误条目 3）
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);
CREATE INDEX ix_audit_log_trace ON audit_log(trace_id);
CREATE INDEX ix_audit_log_user ON audit_log(user_id, created_at);
CREATE INDEX ix_audit_log_region ON audit_log(region_code, created_at);   -- v1.0.1 勘误条目 3：按省检索
CREATE TABLE audit_log_p202608 PARTITION OF audit_log FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE audit_log_p202609 PARTITION OF audit_log FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');

-- 10.4 消息记录（小程序审批消息支撑紧急业务，评审决议④）

-- 10.5 区域注册表（多省配置化核心：评审决议⑤，全国推广演进）
CREATE TABLE sys_region (
  id                  BIGINT PRIMARY KEY,
  region_code         VARCHAR(16) NOT NULL,                 -- 省级编码（如 CN-32 江苏）
  region_name         VARCHAR(64) NOT NULL,
  market_support      JSONB NOT NULL,                       -- 支持市场类型（现货/中长期/外送）
  exchange_channel    VARCHAR(8)  NOT NULL DEFAULT 'rest' CHECK (exchange_channel IN ('rest','sftp','both')),
  settlement_period   VARCHAR(16) NOT NULL DEFAULT 'natural_month' CHECK (settlement_period IN ('natural_month','trading_month')),
  status              VARCHAR(16) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled','pending')),
  launch_order        INT,                                  -- 全国推广接入顺序
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_sys_region_code ON sys_region(region_code) WHERE NOT deleted;
CREATE TABLE flow_instance (
  id                  BIGINT PRIMARY KEY,
  instance_no         VARCHAR(64) NOT NULL,
  process_key         VARCHAR(64) NOT NULL,                 -- 流程定义键（settlement_ticket_review 等）
  biz_type            VARCHAR(32) NOT NULL,                 -- 业务类型：decision/declaration/ticket/appeal
  biz_id              VARCHAR(64) NOT NULL,                 -- 业务单据号（settlement_ticket.flow_instance_id 同域）
  variables           JSONB NOT NULL,                       -- 流程变量（发起人/金额/紧急度）
  status              VARCHAR(16) NOT NULL DEFAULT 'running' CHECK (status IN ('running','completed','terminated')),
  current_node        VARCHAR(64) NOT NULL,                 -- 当前节点（apply/review/approve/archive）
  current_assignee    VARCHAR(64),                          -- 当前处理人
  start_by            VARCHAR(32) NOT NULL,
  start_time          TIMESTAMP NOT NULL DEFAULT now(),
  end_time            TIMESTAMP,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_flow_instance_no ON flow_instance(instance_no) WHERE NOT deleted;
CREATE INDEX ix_flow_instance_biz ON flow_instance(biz_type, biz_id) WHERE NOT deleted;
CREATE INDEX ix_flow_instance_status ON flow_instance(status, start_by) WHERE NOT deleted;

CREATE TABLE message_record (
  id                  BIGINT PRIMARY KEY,
  msg_type            VARCHAR(32) NOT NULL CHECK (msg_type IN ('forecast_summary','market_alert','decision_todo','settlement_diff','assess_reminder','approval_task','intel_push')),
  receiver_id         BIGINT NOT NULL,
  title               VARCHAR(255) NOT NULL,
  content             TEXT NOT NULL,
  channel             JSONB NOT NULL,                       -- web/miniapp
  read_status         VARCHAR(8) NOT NULL DEFAULT 'unread' CHECK (read_status IN ('unread','read')),
  biz_ref             VARCHAR(64),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_message_receiver ON message_record(receiver_id, read_status) WHERE NOT deleted;

-- ==================== 十一、数据管理与情报数据域 ====================

-- 11.1 数据源（exchange 支持 REST/SFTP 双通道，评审决议①）
CREATE TABLE data_source (
  id                  BIGINT PRIMARY KEY,
  source_code         VARCHAR(32) NOT NULL,
  source_type         VARCHAR(16) NOT NULL CHECK (source_type IN ('marketing','exchange','weather','file','intel')),
  connect_config      JSONB NOT NULL,                       -- 脱敏；REST/SFTP 双通道建模
  sync_mode           VARCHAR(8) NOT NULL CHECK (sync_mode IN ('realtime','timed')),
  frequency           VARCHAR(32),                          -- cron 表达式
  status              VARCHAR(16) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled','error')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_data_source_code ON data_source(source_code) WHERE NOT deleted;

-- 11.2 采集任务
CREATE TABLE collect_task (
  id                  BIGINT PRIMARY KEY,
  source_id           BIGINT NOT NULL REFERENCES data_source(id),
  task_type           VARCHAR(32) NOT NULL CHECK (task_type IN ('market','trade','settlement','weather','intel')),
  cron_expr           VARCHAR(64) NOT NULL,
  last_run_time       TIMESTAMP,
  last_status         VARCHAR(16) CHECK (last_status IN ('success','failed')),
  records_count       BIGINT,
  error_log           TEXT,
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_collect_task_source ON collect_task(source_id) WHERE NOT deleted;

-- 11.3 数据质量规则
CREATE TABLE data_quality_rule (
  id                  BIGINT PRIMARY KEY,
  rule_code           VARCHAR(64) NOT NULL,
  rule_type           VARCHAR(16) NOT NULL CHECK (rule_type IN ('completeness','accuracy','timeliness')),
  target_table        VARCHAR(64) NOT NULL,
  target_field        VARCHAR(64) NOT NULL,
  condition           JSONB NOT NULL,
  threshold           NUMERIC(8,4) NOT NULL,
  severity            VARCHAR(8) NOT NULL CHECK (severity IN ('high','medium','low')),
  status              VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_quality_rule_code ON data_quality_rule(rule_code) WHERE NOT deleted;

-- 11.4 数据血缘
CREATE TABLE data_lineage (
  id                  BIGINT PRIMARY KEY,
  node_id             VARCHAR(64) NOT NULL,
  node_type           VARCHAR(16) NOT NULL CHECK (node_type IN ('table','task','report','model')),
  upstream            JSONB NOT NULL,
  downstream          JSONB NOT NULL,
  field_mapping       JSONB NOT NULL,                       -- 字段级映射
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_lineage_node ON data_lineage(node_id) WHERE NOT deleted;

-- 11.5 情报源（RE-01 P0 正式）
CREATE TABLE intel_source (
  id                  BIGINT PRIMARY KEY,
  source_code         VARCHAR(32) NOT NULL,
  source_name         VARCHAR(128) NOT NULL,
  intel_type          VARCHAR(16) NOT NULL CHECK (intel_type IN ('price','weather','supply_demand','policy','announcement','opinion')),
  fetch_mode          VARCHAR(8) NOT NULL CHECK (fetch_mode IN ('api','crawl','file')),
  frequency           VARCHAR(32) NOT NULL,
  status              VARCHAR(16) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX uk_intel_source_code ON intel_source(source_code) WHERE NOT deleted;

-- 11.6 情报条目（high 级实时推送 ≤30s）
CREATE TABLE intel_news (
  id                  BIGINT PRIMARY KEY,
  source_code         VARCHAR(32) NOT NULL,                 -- 情报源（v1.0.2：部分唯一索引不可做外键引用，应用层校验）
  title               VARCHAR(255) NOT NULL,
  content             TEXT NOT NULL,
  region_code         VARCHAR(16),                          -- 情报关联区域（全国情报可空，评审决议⑤）
  normalized_tags     JSONB NOT NULL,                       -- 市场/品种/影响
  importance          VARCHAR(8) NOT NULL CHECK (importance IN ('high','medium','low')),
  published_at        TIMESTAMP NOT NULL,
  push_status         VARCHAR(8) NOT NULL DEFAULT 'none' CHECK (push_status IN ('none','pushed')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_intel_news_importance ON intel_news(importance, published_at) WHERE NOT deleted;
CREATE INDEX ix_intel_news_tags ON intel_news USING GIN (normalized_tags);
CREATE INDEX ix_intel_news_region ON intel_news(region_code, published_at) WHERE NOT deleted;

-- 11.7 情报推送规则（标签 × 重要度 → 角色/渠道）
CREATE TABLE intel_push_rule (
  id                  BIGINT PRIMARY KEY,
  rule_name           VARCHAR(128) NOT NULL,
  tags_filter         JSONB NOT NULL,
  importance_filter   VARCHAR(8) NOT NULL CHECK (importance_filter IN ('high','medium','low')),
  target_roles        JSONB NOT NULL,                       -- trader/analyst/manager...
  channel             JSONB NOT NULL,                       -- web/miniapp/sms
  silent_period       JSONB,                                -- 静默时段
  status              VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','disabled')),
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
