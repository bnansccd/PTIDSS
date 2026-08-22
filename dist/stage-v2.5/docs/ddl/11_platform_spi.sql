-- =====================================================================
-- PTIDSS 平台配置 DDL 11：算法 SPI 插件化执行（P3-2）
-- 变更：algorithm_registry 增加 spi_key 列（绑定 SPI 执行器，空=按类目默认）
-- 说明：spi_key 对应 AlgorithmSpiRegistry 中注册的执行器标识，
--       执行器清单可通过 GET /api/algorithm/spis 查询。
-- =====================================================================

ALTER TABLE algorithm_registry ADD COLUMN IF NOT EXISTS spi_key VARCHAR(32);
COMMENT ON COLUMN algorithm_registry.spi_key IS 'SPI 执行器标识（P3 插件化执行；空=按类目默认匹配内置执行器）';

-- 存量种子算法：按类目回填缺省执行器（幂等，仅当 spi_key 为空时回填）
UPDATE algorithm_registry SET spi_key = CASE category
    WHEN 'forecast'        THEN 'forecast'
    WHEN 'market_analysis' THEN 'market_analysis'
    WHEN 'quote_strategy'  THEN 'quote_strategy'
    WHEN 'risk_measure'    THEN 'risk_measure'
    WHEN 'optimize'        THEN 'optimize'
    WHEN 'settlement'      THEN 'settlement'
    WHEN 'review'          THEN 'review'
    WHEN 'rule_engine'     THEN 'rule_engine'
    END
WHERE spi_key IS NULL OR spi_key = '';
