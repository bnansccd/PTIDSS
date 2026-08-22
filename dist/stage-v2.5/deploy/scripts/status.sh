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
ss -tlnp 2>/dev/null | grep -E ':(9080|30001)\b' || echo "9080/30001 均未监听"

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
