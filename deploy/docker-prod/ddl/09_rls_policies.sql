-- ============================================================
-- 电力交易智能辅助决策系统（PTIDSS）DDL 基线 v1.0.1（勘误版）
-- RLS 首期启用策略脚本（勘误批次 DDL-ERR-2026-001 条目 5）
-- 配套：《多省region路由与数据隔离方案》V1.2 §5.1（2026-08-08 复审定案）｜开发基线 V1.6 8.2.1（C4/C5）
-- 数据库：PostgreSQL 16（执行顺序：01_postgres_schema.sql → 03_views.sql → 本脚本 → 07_seed_data.sql）
-- 执行：psql -h <host> -U ptidss_dba -d ptidss -f 09_rls_policies.sql
-- 约定：
--   1) 全部语句幂等（ENABLE 可重复；DROP POLICY IF EXISTS + CREATE POLICY），可重复执行；
--   2) 13 张核心业务表逐一启用 RLS，策略统一为
--      USING (region_code = current_setting('app.region_code', true))；
--      intel_news 附加全国可见规则（region_code 为空 = 全国情报，所有区域可见）；
--   3) 会话区域由网关连接池在建立连接时执行 SET app.region_code = 'CN-32'；
--      未设置时 current_setting 返回 NULL，策略结果为 NULL = 不可见（默认拒绝）；
--   4) 每服务独立数据库账号（防超级用户绕过 RLS，账号仅授予本服务所需权限）；
--      DBA 保留 BYPASSRLS 角色（ptidss_dba），仅限运维窗口使用；
--   5) 管理端全国视图以独立账号 ptidss_admin（BYPASSRLS）+ 应用层全国权限控制承载，
--      普通服务账号不可见全国数据。
-- ============================================================

-- ────────────────────────────────────────────
-- 0. 账号体系（服务账号 + 管理端全国视图账号；幂等创建）
--    GRANT 权限矩阵由 8.2 数据库实施时按各服务权限清单下发（账号框架先落地）
-- ────────────────────────────────────────────
DO $$
BEGIN
  -- 管理端全国视图账号（BYPASSRLS：承载管理端全国数据视图，仅限该账号 + DBA）
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_admin') THEN
    CREATE ROLE ptidss_admin LOGIN BYPASSRLS;
  END IF;
  -- 各业务服务独立账号（示例：auth/policy/market/trade/settlement/review/data/report）
  -- 部署时按实际服务清单增删；命名规范 ptidss_app_<service>，统一无 SUPERUSER 权限
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_auth') THEN
    CREATE ROLE ptidss_app_auth LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_policy') THEN
    CREATE ROLE ptidss_app_policy LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_market') THEN
    CREATE ROLE ptidss_app_market LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_trade') THEN
    CREATE ROLE ptidss_app_trade LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_settlement') THEN
    CREATE ROLE ptidss_app_settlement LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_review') THEN
    CREATE ROLE ptidss_app_review LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_data') THEN
    CREATE ROLE ptidss_app_data LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ptidss_app_report') THEN
    CREATE ROLE ptidss_app_report LOGIN;
  END IF;
END $$;

-- GRANT 模板（按服务权限清单细化，8.2 数据库实施时下发）：
--   GRANT CONNECT ON DATABASE ptidss TO ptidss_app_market;
--   GRANT USAGE ON SCHEMA public TO ptidss_app_market;
--   GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE contract, quote_plan, clearing_result TO ptidss_app_market;
--   GRANT SELECT ON TABLE sys_region, sys_user_region TO ptidss_app_market;
-- 注意：RLS 策略对所有非 BYPASSRLS 角色生效，账号仅授予本服务所需表权限即达最小化。

-- ────────────────────────────────────────────
-- 1. 发电与负荷域
-- ────────────────────────────────────────────
ALTER TABLE plant_unit ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS plant_unit_region_policy ON plant_unit;
CREATE POLICY plant_unit_region_policy ON plant_unit
USING (region_code = current_setting('app.region_code', true));

-- ────────────────────────────────────────────
-- 2. 交易域（contract / rolling_plan / declaration 分区 / trade_result 分区 / quote_plan / clearing_result）
-- ────────────────────────────────────────────
ALTER TABLE contract ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS contract_region_policy ON contract;
CREATE POLICY contract_region_policy ON contract
USING (region_code = current_setting('app.region_code', true));

ALTER TABLE rolling_plan ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS rolling_plan_region_policy ON rolling_plan;
CREATE POLICY rolling_plan_region_policy ON rolling_plan
USING (region_code = current_setting('app.region_code', true));

-- declaration 为按月分区表：策略建在父表，分区自动继承
ALTER TABLE declaration ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS declaration_region_policy ON declaration;
CREATE POLICY declaration_region_policy ON declaration
USING (region_code = current_setting('app.region_code', true));

-- trade_result 为按月分区表
ALTER TABLE trade_result ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS trade_result_region_policy ON trade_result;
CREATE POLICY trade_result_region_policy ON trade_result
USING (region_code = current_setting('app.region_code', true));

ALTER TABLE quote_plan ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS quote_plan_region_policy ON quote_plan;
CREATE POLICY quote_plan_region_policy ON quote_plan
USING (region_code = current_setting('app.region_code', true));

ALTER TABLE clearing_result ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS clearing_result_region_policy ON clearing_result;
CREATE POLICY clearing_result_region_policy ON clearing_result
USING (region_code = current_setting('app.region_code', true));

-- ────────────────────────────────────────────
-- 3. 结算域（settlement_record 分区 / settlement_ledger）
-- ────────────────────────────────────────────
ALTER TABLE settlement_record ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS settlement_record_region_policy ON settlement_record;
CREATE POLICY settlement_record_region_policy ON settlement_record
USING (region_code = current_setting('app.region_code', true));

ALTER TABLE settlement_ledger ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS settlement_ledger_region_policy ON settlement_ledger;
CREATE POLICY settlement_ledger_region_policy ON settlement_ledger
USING (region_code = current_setting('app.region_code', true));

-- ────────────────────────────────────────────
-- 4. 预测域（forecast_task / forecast_result）
-- ────────────────────────────────────────────
ALTER TABLE forecast_task ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS forecast_task_region_policy ON forecast_task;
CREATE POLICY forecast_task_region_policy ON forecast_task
USING (region_code = current_setting('app.region_code', true));

ALTER TABLE forecast_result ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS forecast_result_region_policy ON forecast_result;
CREATE POLICY forecast_result_region_policy ON forecast_result
USING (region_code = current_setting('app.region_code', true));

-- ────────────────────────────────────────────
-- 5. 报表域（report_instance）
-- ────────────────────────────────────────────
ALTER TABLE report_instance ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS report_instance_region_policy ON report_instance;
CREATE POLICY report_instance_region_policy ON report_instance
USING (region_code = current_setting('app.region_code', true));

-- ────────────────────────────────────────────
-- 6. 情报域（intel_news：region_code 可空，空 = 全国情报，所有区域可见）
-- ────────────────────────────────────────────
ALTER TABLE intel_news ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS intel_news_region_policy ON intel_news;
CREATE POLICY intel_news_region_policy ON intel_news
USING (region_code = current_setting('app.region_code', true) OR region_code IS NULL);

-- ────────────────────────────────────────────
-- 7. 验证与运维说明
-- ────────────────────────────────────────────
-- 1) 策略生效验证（回归用例，方案 V1.2 第十章 RLS 兜底测试层）：
--    SET app.region_code = 'CN-32';
--    SELECT count(*) FROM contract;                    -- 仅 CN-32 行
--    SELECT count(*) FROM intel_news;                  -- 省级情报 + 全部全国情报
--    RESET app.region_code;
--    SELECT count(*) FROM contract;                    -- 0（未设置会话区域，默认拒绝）
-- 2) 绕过验证：以 ptidss_dba（BYPASSRLS）连接查询可见全量；以服务账号直连（不带 region
--    条件）仅见本区域——应用层漏写 region 条件不会导致跨省泄露（纵深兜底）。
-- 3) FORCE ROW LEVEL SECURITY 可选加固：对表 owner 亦强制策略（防止 owner 账号绕过）。
--    当前表 owner 为部署账号，服务账号非 owner，无需 FORCE；若部署调整为服务账号为
--    owner，须评估是否执行：ALTER TABLE <t> FORCE ROW LEVEL SECURITY;
-- 4) 策略变更（新增表/调整全国规则）走开发基线 7.5 变更管理流程，禁止绕过。
-- 5) 本脚本随 v1.0.1 勘误批次在开发/测试/生产三环境同步执行（实施窗口内）。
-- ============================================================
