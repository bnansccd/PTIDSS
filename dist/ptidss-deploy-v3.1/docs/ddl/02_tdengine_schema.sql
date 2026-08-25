-- ============================================================
-- 电力交易智能辅助决策系统（PTIDSS）DDL 基线 v1.0.1（勘误版）
-- TDengine 时序表（超级表 + 子表划分规范）
-- 配套：《数据字典_全量_V1.0》（V1.1）第一/二/六域｜开发基线 V1.6 3.4 数据架构｜05_评审记录.md
-- 数据库：TDengine 3.x（2026-08-08 数据组评审通过，正式锁定；v1.0.1 勘误批次 DDL-ERR-2026-001 已执行）
-- 约定：主键列为第一列 ts；子表划分见各表注释；保留策略各域自定；
--       region_code 为标签列（多省配置化，评审决议⑤；业务数据按机构分库定案，见方案 V1.2 5.2/5.8）。
-- 勘误（v1.0.1，条目 1/2）：st_spot_price / st_midlong_price 的 region_code 由普通列提升为 TAG
--       （子表划分加入 region 维度，实现按省子表物理分片）；TAG 定义不支持 NOT NULL 约束语法，
--       但子表创建时必须为每个 TAG 指定值（TDengine 机制保证，天然非空），
--       入库非空再由质量规则种子（07_seed_data.sql，条目 4）与采集层双重门禁。
-- ============================================================

-- 1.1 现货价格时序
-- 子表：按 market_type_stage_region_code 划分（region 维度随 v1.0.1 勘误加入，按省子表物理分片）
CREATE STABLE st_spot_price (
  ts                TIMESTAMP,
  price             DOUBLE      NOT NULL,        -- 出清价格（元/MWh）≥0
  volume            DOUBLE      NOT NULL,        -- 成交电量（MWh）≥0
  settlement_type   NCHAR(8),
  source            NCHAR(16)   NOT NULL,        -- marketing_platform/exchange
  sync_version      NCHAR(32)                   -- 采集批次版本（对账用）
) TAGS (
  market_type       NCHAR(16),
  stage             NCHAR(8),
  region_code       NCHAR(16)                   -- v1.0.1 勘误提升：子表必填（天然非空），按省物理分片
);

-- 1.2 中长期价格时序
-- 子表：按 variety_region_code 划分（region 维度随 v1.0.1 勘误加入，按省子表物理分片）
CREATE STABLE st_midlong_price (
  ts                TIMESTAMP,
  price             DOUBLE      NOT NULL,        -- 成交均价（元/MWh）≥0
  volume            DOUBLE      NOT NULL,        -- 成交量（MWh）≥0
  market_center     NCHAR(16),                  -- 交易中心（如省交易中心），保留普通列
  source            NCHAR(16)   NOT NULL
) TAGS (
  variety           NCHAR(16),
  contract_no       NCHAR(64),                  -- 关联 contract.contract_no
  region_code       NCHAR(16)                   -- v1.0.1 勘误提升：子表必填（天然非空），按省物理分片
);

-- 1.3 供需时序
-- 子表：按 region_code 划分
CREATE STABLE st_supply_demand (
  ts                TIMESTAMP,
  load_value        DOUBLE      NOT NULL,        -- 负荷（MW）≥0
  available_capacity DOUBLE     NOT NULL,        -- 可用能力（MW）≥0
  renewable_output  DOUBLE      NOT NULL,        -- 新能源出力（MW）≥0
  reserve           DOUBLE      NOT NULL,        -- 备用容量（MW），可为负
  supply_demand_ratio DOUBLE   NOT NULL,        -- 供需比 ≥0
  region_code       NCHAR(16)   NOT NULL
) TAGS (
  forecast_flag     BOOL                        -- true 预测 / false 实际
);

-- 2.2 机组出力时序
-- 子表：按 unit_code 划分
CREATE STABLE st_generation (
  ts                TIMESTAMP,
  forecast_value    DOUBLE      NOT NULL,        -- 预测出力（MW）≥0
  actual_value      DOUBLE,                      -- 实际出力（MW）≥0，事后回填
  forecast_version  NCHAR(32)   NOT NULL,        -- 模型版本号
  confidence        DOUBLE,                      -- 0-1
  lower_bound       DOUBLE,                      -- 置信区间下界
  upper_bound       DOUBLE                       -- 置信区间上界（upper ≥ lower）
) TAGS (
  unit_code         NCHAR(32)
);

-- 2.4 负荷时序
-- 子表：按 region_code_forecast_flag 划分
CREATE STABLE st_load (
  ts                TIMESTAMP,
  load_value        DOUBLE      NOT NULL,        -- 负荷值（MW）≥0
  forecast_version  NCHAR(32),
  temperature       DOUBLE                      -- 区域温度（℃），特征对齐
) TAGS (
  region_code       NCHAR(16),
  forecast_flag     BOOL
);

-- 2.5 气象数据
-- 子表：按 source_grid_point 划分（如 gfs_<格点编码>）
CREATE STABLE weather_data (
  ts                TIMESTAMP,                   -- 预报起报时间
  temperature       DOUBLE      NOT NULL,        -- 温度（℃）
  wind_speed        DOUBLE      NOT NULL,        -- 风速（m/s）≥0
  wind_direction    DOUBLE,                      -- 风向（°）0-360
  radiation         DOUBLE,                      -- 辐照度（W/m²）≥0，光伏用
  cloud_cover       DOUBLE,                      -- 云量（%）0-100
  precipitation     DOUBLE,                      -- 降水量（mm）≥0
  forecast_hour     INT         NOT NULL         -- 预报时效（h）0-168
) TAGS (
  source            NCHAR(16),                   -- GFS/ECMWF/CMA
  grid_point        NCHAR(32)                    -- 经纬度编码
);

-- 6.2 预测结果 96 点明细
-- 子表：按 predict_type_market_type 划分
CREATE STABLE st_forecast_series (
  ts                TIMESTAMP,                   -- 96 点时点（15 分钟对齐）
  value             DOUBLE      NOT NULL,        -- 预测值
  lower_bound       DOUBLE      NOT NULL,        -- 90% 置信区间下界
  upper_bound       DOUBLE      NOT NULL,        -- 90% 置信区间上界
  confidence        DOUBLE      NOT NULL         -- 置信度 0-1
) TAGS (
  task_id           BIGINT,                      -- 关联 forecast_result 元数据
  predict_type      NCHAR(16),                   -- generation/price/load
  market_type       NCHAR(16),
  trade_date        NCHAR(16)                    -- 目标日（YYYY-MM-DD）
);

-- 11.6 情报条目时序标签（情报流时间线，可选启用）
-- 子表：按 importance 划分
CREATE STABLE st_intel_news_ts (
  ts                TIMESTAMP,                   -- published_at
  title             NCHAR(255)  NOT NULL,
  importance        NCHAR(8)    NOT NULL,        -- high/medium/low
  push_status       NCHAR(8)
) TAGS (
  source_code       NCHAR(32),
  intel_type        NCHAR(16)
);

-- ============================================================
-- 子表创建示例（由数据组按需执行，勿在基线脚本中固定创建；v1.0.1 起 TAGS 必须含 region_code）：
--   CREATE TABLE intra_day_ahead_cn32 USING st_spot_price
--     TAGS ('intra_province', 'day_ahead', 'CN-32');
--   CREATE TABLE monthly_cn32 USING st_midlong_price
--     TAGS ('monthly', 'C202608001', 'CN-32');
--   CREATE TABLE unit_001 USING st_generation TAGS ('U001');
-- 保留策略示例（按域配置，如行情 5 年 / 气象 30 天滚动）：
--   ALTER STABLE st_spot_price SET RETENTION 1825d;
-- ============================================================
