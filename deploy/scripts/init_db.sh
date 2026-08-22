#!/usr/bin/env bash
# ============================================================
# PTIDSS 数据库初始化脚本（V2.5）
# 功能：创建数据库/用户 → 依次执行 01~15 基线 DDL（全部幂等，可重复执行）
# 用法：sudo ./init_db.sh [PG_HOST] [PG_PORT] [PG_SUPERUSER]
#   默认：127.0.0.1 5432 postgres（本机 peer/trust 认证可直接执行）
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

echo "==> [2/3] 执行基线 DDL（01~15 幂等；TDengine 侧脚本 02 由时序库单独执行，见部署文档）"
# 10~15：平台配置/SPI 执行器/交易网关/模型任务/市场化区域/行情采集配置（V2.2~V2.5 增量）
for f in 01_postgres_schema.sql 03_views.sql 06_enum_dict.sql 07_seed_data.sql \
         10_platform_config.sql 11_platform_spi.sql 12_trade_gateway_config.sql \
         13_model_task.sql 14_market_regions_v2_4.sql 15_intel_fetch_config_v2_5.sql; do
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
