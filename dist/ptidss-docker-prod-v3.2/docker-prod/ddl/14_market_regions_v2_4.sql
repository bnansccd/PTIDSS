-- =====================================================================
-- 14. 市场化交易重点省份接入（V2.4 需求11）：地区省份调研 → 区域注册 + 数据源/情报源接入
-- 依据（2025 年底调研）：全国正式运行的省级电力现货市场 7 家
--   = 山西、广东、山东、甘肃（先期转正）+ 蒙西、湖北、浙江（2025 年转正），另有 19 省试运行；
-- 现有区域（CN-31 上海/CN-32 江苏/CN-33 浙江/CN-11 北京/CN-41 河南）基础上，
-- 补充 2025 年已正式运行现货市场的省份区域并登记对应省份数据源/情报源。
-- 执行方式：psql -h 127.0.0.1 -p 5432 -U ptidss -d ptidss -f 14_market_regions_v2_4.sql
-- 幂等：ON CONFLICT/WHERE NOT EXISTS；重复执行安全。
-- =====================================================================

-- ── 1. 区域注册：2025 年现货市场正式运行省份 ────────────────────
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
SELECT 6,  'CN-37', '山东', '["spot","midlong","external"]', 'both', 'natural_month', 'enabled', 6
WHERE NOT EXISTS (SELECT 1 FROM sys_region WHERE id = 6 OR region_code = 'CN-37');
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
SELECT 7,  'CN-14', '山西', '["spot","midlong","external"]', 'both', 'trading_month', 'enabled', 7
WHERE NOT EXISTS (SELECT 1 FROM sys_region WHERE id = 7 OR region_code = 'CN-14');
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
SELECT 8,  'CN-44', '广东', '["spot","midlong","external"]', 'both', 'natural_month', 'enabled', 8
WHERE NOT EXISTS (SELECT 1 FROM sys_region WHERE id = 8 OR region_code = 'CN-44');
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
SELECT 9,  'CN-62', '甘肃', '["spot","midlong"]',            'rest', 'natural_month', 'enabled', 9
WHERE NOT EXISTS (SELECT 1 FROM sys_region WHERE id = 9 OR region_code = 'CN-62');
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
SELECT 10, 'CN-15', '内蒙古（蒙西）', '["spot","midlong"]',   'rest', 'trading_month', 'enabled', 10
WHERE NOT EXISTS (SELECT 1 FROM sys_region WHERE id = 10 OR region_code = 'CN-15');
INSERT INTO sys_region (id, region_code, region_name, market_support, exchange_channel, settlement_period, status, launch_order)
SELECT 11, 'CN-42', '湖北', '["spot","midlong"]',            'rest', 'natural_month', 'enabled', 11
WHERE NOT EXISTS (SELECT 1 FROM sys_region WHERE id = 11 OR region_code = 'CN-42');

-- ── 2. 数据源/情报源接入：对应省份行情与情报（幂等） ──────────────
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 11, 'INTL-PRICE-SD',  '山东电力交易中心-现货出清价格', 'price',        'api',   '15 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 11 OR source_code = 'INTL-PRICE-SD');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 12, 'INTL-PRICE-SX',  '山西电力交易中心-现货出清价格', 'price',        'api',   '15 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 12 OR source_code = 'INTL-PRICE-SX');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 13, 'INTL-PRICE-GD',  '广东电力交易中心-现货出清价格', 'price',        'api',   '15 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 13 OR source_code = 'INTL-PRICE-GD');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 14, 'INTL-PRICE-GS',  '甘肃电力交易中心-现货出清价格', 'price',        'api',   '15 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 14 OR source_code = 'INTL-PRICE-GS');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 15, 'INTL-PRICE-MX',  '蒙西电力交易中心-现货出清价格', 'price',        'api',   '15 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 15 OR source_code = 'INTL-PRICE-MX');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 16, 'INTL-ANN-SD',    '山东电网-运行公告',           'announcement', 'api',   '1 小时',  'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 16 OR source_code = 'INTL-ANN-SD');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 17, 'INTL-SD-SD',     '山东电网-供需披露',           'supply_demand','api',   '30 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 17 OR source_code = 'INTL-SD-SD');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 18, 'INTL-SD-MX',     '蒙西电网-供需披露',           'supply_demand','api',   '30 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 18 OR source_code = 'INTL-SD-MX');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 19, 'INTL-POL-PROV',  '各省能源局-现货交易规则公告',  'policy',       'crawl', '每日',   'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 19 OR source_code = 'INTL-POL-PROV');
INSERT INTO intel_source (id, source_code, source_name, intel_type, fetch_mode, frequency, status)
SELECT 20, 'INTL-PRICE-SC',  '南方区域电力市场-跨区出清价格', 'price',       'api',   '30 分钟', 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM intel_source WHERE id = 20 OR source_code = 'INTL-PRICE-SC');
