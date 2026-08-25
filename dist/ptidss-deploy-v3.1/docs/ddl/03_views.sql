-- ============================================================
-- 电力交易智能辅助决策系统（PTIDSS）DDL 基线 v1.0
-- 视图定义（PostgreSQL）
-- 配套：《数据字典_全量_V1.0》（V1.1）十二.5（position_view 表级定义）｜05_评审记录.md
-- 评审修正：v1.0 重写 position_view，消除合同×申报×成交 JOIN 放大导致的聚合重复
-- ============================================================

-- 持仓视图：合同 + 成交结果聚合（按品种/方向/交易日汇总净持仓）
-- 用途：决策会话持仓约束（≤30% 敞口）、申报合规预检、结算核对基准
-- 实现：LATERAL 子查询先按（合同, 交易日）聚合成交，再关联合同，保证合同电量不重复计
CREATE VIEW position_view AS
SELECT
  c.region_code,
  c.variety,
  c.direction,
  t.trade_date,
  c.total_volume                                 AS contract_volume,   -- 合同电量（MWh）
  COALESCE(t.matched_volume, 0)                  AS matched_volume,    -- 成交电量（MWh）
  c.total_volume - COALESCE(t.matched_volume, 0) AS open_volume,       -- 未平仓量（MWh）
  CASE
    WHEN c.total_volume = 0 THEN 0
    ELSE COALESCE(t.matched_volume, 0) / c.total_volume
  END                                            AS match_ratio,       -- 成交比例 0-1
  t.avg_match_price                                                 -- 加权成交均价
FROM contract c
LEFT JOIN LATERAL (
  SELECT
    tr.trade_date,
    SUM(tr.matched_volume) AS matched_volume,
    SUM(tr.matched_volume * tr.matched_price) /
      NULLIF(SUM(tr.matched_volume), 0)          AS avg_match_price
  FROM declaration d
  JOIN trade_result tr ON tr.declaration_id = d.id
  WHERE d.contract_id = c.id AND NOT d.deleted AND NOT tr.deleted
  GROUP BY tr.trade_date
) t ON true
WHERE NOT c.deleted AND (c.status IN ('active', 'executing'));

COMMENT ON VIEW position_view IS '持仓视图：合同×成交聚合（LATERAL 防放大），供决策/申报/结算引用（数据字典十二.5）';

-- 结算核对通过率视图：周期内核对一致率（FR-ST-03 验收 ≥95%）
-- 口径：核对状态 consistent 记录数 / 同期全部结算记录数
CREATE VIEW settlement_pass_rate_view AS
SELECT
  r.settlement_period,
  r.region_code,
  COUNT(*) FILTER (WHERE c.status = 'consistent') AS consistent_cnt,
  COUNT(*)                                        AS total_cnt,
  COUNT(*) FILTER (WHERE c.status = 'consistent')::NUMERIC / NULLIF(COUNT(*), 0) AS pass_rate
FROM settlement_record r
LEFT JOIN settlement_reconcile c ON c.record_id = r.id
WHERE NOT r.deleted
GROUP BY r.settlement_period, r.region_code;

COMMENT ON VIEW settlement_pass_rate_view IS '结算核对通过率视图（验收：pass_rate ≥ 0.95）';
