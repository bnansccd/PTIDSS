#!/usr/bin/env bash
# ============================================================
# PTIDSS 数据库初始化脚本（V3.2）
# 功能：创建数据库/用户 → 依次执行 01~18 基线 DDL（全部幂等，可重复执行）
# 用法：sudo ./init_db.sh [PG_HOST] [PG_PORT] [PG_SUPERUSER]
#   默认：127.0.0.1 5432 postgres（本机 peer/trust 认证可直接执行）
# 双模式：本机自带实例（默认参数）或云环境已有 PostgreSQL（传云库地址/端口）
# 注意：需以有建库权限的超级用户连接（典型：sudo -u postgres 或本机 postgres 用户）
# ============================================================
set -euo pipefail

PG_HOST="${1:-127.0.0.1}"
PG_PORT="${2:-5432}"
PG_SUPER="${3:-postgres}"
DB_NAME="ptidss"
DB_USER="ptidss"
DB_PASS="${DB_PASSWORD:-ptidss}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DDL_DIR="$(dirname "$SCRIPT_DIR")/docs/ddl"

# 如果脚本不在 deploy/scripts 下（直接位于 deploy/），兼容两种路径
[ -d "$DDL_DIR" ] || DDL_DIR="$(dirname "$SCRIPT_DIR")/../docs/ddl"

echo "==> [1/3] 创建数据库用户与库（幂等）"
psql -h "$PG_HOST" -p "$PG_PORT" -U "$PG_SUPER" -d postgres -v ON_ERROR_STOP=1 <<SQL
SELECT 'CREATE ROLE ${DB_USER} LOGIN PASSWORD ''${DB_PASS}'''
WHERE NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '${DB_USER}')\gexec
SELECT 'CREATE DATABASE ${DB_NAME} OWNER ${DB_USER}'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${DB_NAME}')\gexec
SQL

echo "==> [2/3] 执行基线 DDL（01~18；TDengine 侧脚本 02 由时序库单独执行，见部署文档）"
# 后续 DDL/校验均以业务库角色连接：切换 PGPASSWORD 为角色密码
# （[1/3] 以超级用户 PGPASSWORD 连接建库，两处密码不同时须切换）
export PGPASSWORD="$DB_PASS"
# 幂等检测：基线表 sys_config（17 号 DDL 最后落地）与 18 号性能索引（ix_intel_news_source_time）
# 均已存在即视为库已完整初始化，跳过 DDL 重放以保留既有数据
# （01 号 schema 为全新库一次性建表，重放会冲突；18 号索引本身 IF NOT EXISTS 可补执行）
BASE_TABLE=$(psql -h "$PG_HOST" -p "$PG_PORT" -U "$DB_USER" -d "$DB_NAME" -A -t -c \
  "SELECT to_regclass('public.sys_config')" 2>/dev/null || true)
BASE_IDX=$(psql -h "$PG_HOST" -p "$PG_PORT" -U "$DB_USER" -d "$DB_NAME" -A -t -c \
  "SELECT to_regclass('public.ix_intel_news_source_time')" 2>/dev/null || true)
if [ "$BASE_TABLE" = "sys_config" ] && [ "$BASE_IDX" = "ix_intel_news_source_time" ]; then
  echo "    -- 库已完整初始化（基线表 sys_config + 18 号性能索引齐全），跳过 DDL 重放（保留既有数据）"
elif [ "$BASE_TABLE" = "sys_config" ] && [ "$BASE_IDX" != "ix_intel_news_source_time" ]; then
  echo "    -- 检测到部分初始化（基线表存在但缺 18 号性能索引），补执行 17/18 号增量（均幂等）"
  for f in 17_sys_config_v3_0.sql 18_perf_indexes_v3_1.sql; do
    if [ -f "$DDL_DIR/$f" ]; then
      echo "    -- 执行 $f"
      psql -h "$PG_HOST" -p "$PG_PORT" -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -f "$DDL_DIR/$f"
    else
      echo "    !! 跳过（文件不存在）：$f"
    fi
  done
else
  echo "    -- 全新库：执行 01~18 基线 DDL"
  # 10~18：平台配置/SPI 执行器/交易网关/模型任务/市场化区域/行情采集配置/血缘全量图谱/系统配置/性能索引（V2.2~V3.1 增量）
  for f in 01_postgres_schema.sql 03_views.sql 06_enum_dict.sql 07_seed_data.sql \
           10_platform_config.sql 11_platform_spi.sql 12_trade_gateway_config.sql \
           13_model_task.sql 14_market_regions_v2_4.sql 15_intel_fetch_config_v2_5.sql \
           16_lineage_full_graph_v3_0.sql 17_sys_config_v3_0.sql 18_perf_indexes_v3_1.sql; do
    if [ -f "$DDL_DIR/$f" ]; then
      echo "    -- 执行 $f"
      psql -h "$PG_HOST" -p "$PG_PORT" -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -f "$DDL_DIR/$f"
    else
      echo "    !! 跳过（文件不存在）：$f"
    fi
  done

  # 可选：行级安全策略（RLS），生产多租户隔离场景启用
  if [ -f "$DDL_DIR/09_rls_policies.sql" ]; then
    echo "    -- 执行 09_rls_policies.sql（RLS 多租户隔离）"
    psql -h "$PG_HOST" -p "$PG_PORT" -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -f "$DDL_DIR/09_rls_policies.sql"
  fi
fi

echo "==> [3/3] 校验基线数据"
psql -h "$PG_HOST" -p "$PG_PORT" -U "$DB_USER" -d "$DB_NAME" -A -t <<SQL
SELECT '角色=' || count(*) FROM sys_role
UNION ALL SELECT '权限=' || count(*) FROM sys_permission
UNION ALL SELECT '用户=' || count(*) FROM sys_user
UNION ALL SELECT '区域=' || count(*) FROM sys_region
UNION ALL SELECT '情报源=' || count(*) FROM intel_source
UNION ALL SELECT '算法注册=' || count(*) FROM algorithm_registry
UNION ALL SELECT '模型任务=' || count(*) FROM model_task
UNION ALL SELECT '行情源配置=' || count(*) FROM intel_source WHERE conn_config IS NOT NULL AND conn_config <> '{}'::jsonb;
SQL

echo "==> 数据库初始化完成（库：${DB_NAME}，用户：${DB_USER}）"
echo "    后续：后端首启将以默认密码初始化 PLACEHOLDER:APP_INIT 占位账号"
