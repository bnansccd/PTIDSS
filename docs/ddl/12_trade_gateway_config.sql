-- =====================================================================
-- 12. 交易网关配置（V2.4）：申报单 → 交易系统接口配置与状态监测
-- 产品化诉求：客户图形界面只输入 URL/账户/密码即可完成对接（对齐 V2.3 配置图形化）
-- 配套代码：TradeGatewayService / TradeService（submitDeclaration 推送状态监测）
-- 执行方式：psql -h 127.0.0.1 -p 5432 -U ptidss -d ptidss -f 12_trade_gateway_config.sql
-- =====================================================================

-- ---------- 12.1 交易网关配置（按区域隔离，region_code 唯一） ----------
CREATE TABLE IF NOT EXISTS trade_gateway_config (
  id                  BIGINT PRIMARY KEY,
  region_code         VARCHAR(16) NOT NULL,                 -- 申报市场所属区域（评审决议⑤ 多省隔离）
  gateway_name        VARCHAR(128) NOT NULL DEFAULT '交易中心申报网关',
  endpoint            VARCHAR(255),                         -- 接口地址（如 https://trade.center/api/declaration）
  conn_config         JSONB NOT NULL DEFAULT '{}',          -- {appKey, appSecret,...} 敏感字段 AES 加密存储（ConfigCryptoService）
  status              VARCHAR(16) NOT NULL DEFAULT 'disabled' CHECK (status IN ('enabled','disabled')),
  last_test_at        TIMESTAMP,                            -- 最近连通性测试时间
  last_test_result    VARCHAR(512),                         -- 最近测试结果（ok/fail + 延迟）
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_trade_gateway_region ON trade_gateway_config(region_code) WHERE NOT deleted;

-- ---------- 12.2 申报单网关推送状态（提交申报 → 网关推送监测） ----------
ALTER TABLE declaration
  ADD COLUMN IF NOT EXISTS gateway_push_status VARCHAR(16);  -- pending/success/failed/skipped
ALTER TABLE declaration
  ADD COLUMN IF NOT EXISTS gateway_push_time TIMESTAMP;
ALTER TABLE declaration
  ADD COLUMN IF NOT EXISTS gateway_push_detail VARCHAR(512); -- 推送回执/错误信息
