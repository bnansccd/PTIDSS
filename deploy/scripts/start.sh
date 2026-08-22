#!/usr/bin/env bash
# ============================================================
# PTIDSS 生产启动脚本（V1.7）
# 功能：以 prod profile 启动后端（java -jar），支持环境变量覆盖配置
# 用法：./start.sh [PORT]
# 环境变量：DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD/TOKEN_SECRET/
#          CAPTCHA_ENABLED/INIT_DEFAULT_PASSWORD（缺省见 application-prod.yml）
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
PORT="${1:-9080}"
JAR=$(ls "$ROOT_DIR"/ptidss-server/target/ptidss-server*.jar 2>/dev/null | head -1 || true)

if [ -z "${JAR:-}" ]; then
  echo "!! 未找到后端 jar，请先执行 deploy/scripts/build.sh" >&2
  exit 1
fi

if pgrep -f "ptidss-server.*spring.profiles.active=prod" >/dev/null; then
  echo "!! 后端已在运行（prod），如需重启请先 ./stop.sh" >&2
  exit 1
fi

mkdir -p "$ROOT_DIR/logs"
nohup java -Xms512m -Xmx1024m \
  -jar "$JAR" \
  --spring.profiles.active=prod \
  --server.port="$PORT" \
  > "$ROOT_DIR/logs/backend_prod.log" 2>&1 &

echo "==> 后端已启动（port=$PORT，profile=prod，日志：logs/backend_prod.log）"
echo "    冒烟检查：curl -s http://127.0.0.1:${PORT}/ptidss/auth/captcha"
