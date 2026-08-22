-- =====================================================================
-- 15. 行情采集状态与各省接口配置（V2.5 遗留建议①）：重试/降级/状态监测
-- 内容：
--   1) intel_source 增加采集状态列：last_success_at / last_error / consecutive_failures；
--   2) 为 10 个市场化省份数据源/情报源（DDL 14 注册）配置 conn_config：
--      endpoint（生产环境各省电力交易中心公开地址）、fallbackUrl（降级兜底）、
--      timeoutMs（超时毫秒）、retries（重试次数）、frequencyMinutes（采集周期分钟）、
--      regionCode（区域路由）、mock（测试环境模拟开关，生产置 false 走真实拉取）。
-- 执行方式：psql -h 127.0.0.1 -p 5432 -U ptidss -d ptidss -f 15_intel_fetch_config_v2_5.sql
-- 幂等：ADD COLUMN IF NOT EXISTS / UPDATE 按编码定位；重复执行安全。
-- =====================================================================

-- ── 1. 采集状态列（幂等） ─────────────────────────────────────────
ALTER TABLE intel_source
  ADD COLUMN IF NOT EXISTS last_success_at TIMESTAMP;          -- 最近成功采集时间
ALTER TABLE intel_source
  ADD COLUMN IF NOT EXISTS last_error VARCHAR(512);            -- 最近失败原因（重试/降级均失败后留痕）
ALTER TABLE intel_source
  ADD COLUMN IF NOT EXISTS consecutive_failures INT DEFAULT 0; -- 连续失败次数（≥10 自动停用）

-- ── 2. 各省交易中心行情/情报源连接配置（幂等：按编码更新） ─────────
-- 说明：endpoint 为各省电力交易中心公开门户/数据页地址，生产环境可按实际开放数据接口替换；
--       mock=true 时由系统按确定性规则生成模拟行情（测试/演示），生产置 false 走真实 HTTP 拉取。
UPDATE intel_source SET conn_config = '{"endpoint":"https://pmos.sd.sgcc.com.cn","fallbackUrl":"https://pmos.sd.sgcc.com.cn/website/market/announcement","timeoutMs":5000,"retries":2,"frequencyMinutes":15,"regionCode":"CN-37","mock":true}'
WHERE source_code = 'INTL-PRICE-SD' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://pmos.sx.sgcc.com.cn","fallbackUrl":"https://pmos.sx.sgcc.com.cn/website/market/announcement","timeoutMs":5000,"retries":2,"frequencyMinutes":15,"regionCode":"CN-14","mock":true}'
WHERE source_code = 'INTL-PRICE-SX' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://www.gdpx.com.cn","fallbackUrl":"https://www.gdpx.com.cn/portal/market/spot","timeoutMs":5000,"retries":2,"frequencyMinutes":15,"regionCode":"CN-44","mock":true}'
WHERE source_code = 'INTL-PRICE-GD' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://pmos.gs.sgcc.com.cn","fallbackUrl":"https://pmos.gs.sgcc.com.cn/website/market/announcement","timeoutMs":5000,"retries":2,"frequencyMinutes":15,"regionCode":"CN-62","mock":true}'
WHERE source_code = 'INTL-PRICE-GS' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://www.nmgpower.com.cn","fallbackUrl":"https://www.nmgpower.com.cn/sdsc","timeoutMs":5000,"retries":2,"frequencyMinutes":15,"regionCode":"CN-15","mock":true}'
WHERE source_code = 'INTL-PRICE-MX' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://pmos.sd.sgcc.com.cn/website/notice","fallbackUrl":"https://pmos.sd.sgcc.com.cn/website/market/announcement","timeoutMs":5000,"retries":2,"frequencyMinutes":60,"regionCode":"CN-37","mock":true}'
WHERE source_code = 'INTL-ANN-SD' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://pmos.sd.sgcc.com.cn/website/supply","fallbackUrl":"https://pmos.sd.sgcc.com.cn/website/market/announcement","timeoutMs":5000,"retries":2,"frequencyMinutes":30,"regionCode":"CN-37","mock":true}'
WHERE source_code = 'INTL-SD-SD' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://www.nmgpower.com.cn/sdsc/supply","fallbackUrl":"https://www.nmgpower.com.cn/sdsc","timeoutMs":5000,"retries":2,"frequencyMinutes":30,"regionCode":"CN-15","mock":true}'
WHERE source_code = 'INTL-SD-MX' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://www.nea.gov.cn","fallbackUrl":"https://www.nea.gov.cn/ztzl","timeoutMs":8000,"retries":2,"frequencyMinutes":1440,"regionCode":"CN-32","mock":true}'
WHERE source_code = 'INTL-POL-PROV' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
UPDATE intel_source SET conn_config = '{"endpoint":"https://www.gzpx.cn","fallbackUrl":"https://www.gzpx.cn/website/market/price","timeoutMs":5000,"retries":2,"frequencyMinutes":30,"regionCode":"CN-44","mock":true}'
WHERE source_code = 'INTL-PRICE-SC' AND (conn_config IS NULL OR conn_config = '{}'::jsonb);
