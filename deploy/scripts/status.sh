#!/usr/bin/env bash
# ============================================================
# PTIDSS 状态检查脚本（V1.7）
# 用法：./status.sh
# ============================================================
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "── 后端进程 ─────────────────────────────"
if pgrep -f "ptidss-server" >/dev/null; then
  ps -ef | grep "ptidss-server" | grep -v grep | awk '{print "PID="$2" 启动时间="$5" 命令行="$8" "$9" "$10}'
  echo "进程数：$(pgrep -f ptidss-server | wc -l)"
else
  echo "未运行"
fi

echo "── 端口监听 ─────────────────────────────"
# 端口规范：后端 9080（唯一）、前端对外 80（nginx/容器，可按 FRONT_PORT 调整）、数据库 5432
echo "后端 9080：$(ss -tlnp 2>/dev/null | grep -q ':9080\b' && echo 监听中 || echo 未监听)"
echo "前端 80（对外入口）：$(ss -tlnp 2>/dev/null | grep -Eq ':(80|443)\b' && echo 监听中 || echo 未监听)"
echo "数据库 5432：$(ss -tlnp 2>/dev/null | grep -q ':5432\b' && echo 监听中 || echo 未监听)"

echo "── 数据库连通 ───────────────────────────"
if psql -h 127.0.0.1 -U ptidss -d ptidss -A -t -c "SELECT 'ok' FROM sys_region LIMIT 1" 2>/dev/null | grep -q ok; then
  echo "PostgreSQL(ptidss) 连通正常"
else
  echo "PostgreSQL(ptidss) 无法连接"
fi

echo "── 前端产物 ─────────────────────────────"
if [ -f "$ROOT_DIR/frontend/dist/index.html" ]; then
  echo "frontend/dist 存在（$(du -sh "$ROOT_DIR/frontend/dist" | cut -f1)）"
else
  echo "frontend/dist 缺失，请先执行 build.sh"
fi
