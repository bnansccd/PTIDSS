#!/bin/sh
# ============================================================
# PTIDSS PostgreSQL 容器初始化包装
# 功能：执行 docs/ddl 下 01~18 基线 DDL（全部幂等），
#       排除 02_tdengine_schema.sql（TDengine 时序库脚本，由 TDengine 侧单独执行）
# 挂载：docs/ddl → /docker-entrypoint-initdb.d-src（只读源目录）
#       本脚本 → /docker-entrypoint-initdb.d/（PG 首次启动自动执行）
# ============================================================
set -e

DDL_DIR=/docker-entrypoint-initdb.d-src

for f in \
  01_postgres_schema.sql \
  03_views.sql \
  06_enum_dict.sql \
  07_seed_data.sql \
  09_rls_policies.sql \
  10_platform_config.sql \
  11_platform_spi.sql \
  12_trade_gateway_config.sql \
  13_model_task.sql \
  14_market_regions_v2_4.sql \
  15_intel_fetch_config_v2_5.sql \
  16_lineage_full_graph_v3_0.sql \
  17_sys_config_v3_0.sql \
  18_perf_indexes_v3_1.sql; do
  if [ -f "$DDL_DIR/$f" ]; then
    echo "==> 执行 $f"
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$DDL_DIR/$f" >/dev/null
  else
    echo "!! 跳过（文件不存在）：$f"
  fi
done

echo "==> PTIDSS DDL 初始化完成（02_tdengine_schema.sql 已排除，由 TDengine 侧执行）"
