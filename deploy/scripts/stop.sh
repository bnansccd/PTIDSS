#!/usr/bin/env bash
# ============================================================
# PTIDSS 停止脚本（V1.7）：优雅停止 prod 后端进程
# 用法：./stop.sh
# ============================================================
set -euo pipefail

PIDS=$(pgrep -f "ptidss-server.*spring.profiles.active=prod" || true)
if [ -z "$PIDS" ]; then
  echo "==> 未检测到 prod 后端进程，无需停止"
  exit 0
fi

echo "==> 停止后端进程：$PIDS"
kill $PIDS
for i in $(seq 1 15); do
  if ! pgrep -f "ptidss-server.*spring.profiles.active=prod" >/dev/null; then
    echo "==> 已停止"
    exit 0
  fi
  sleep 1
done

echo "!! 15s 内未退出，强制终止" >&2
kill -9 $PIDS || true
