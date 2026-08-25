#!/usr/bin/env bash
# ============================================================
# PTIDSS 生产启动脚本（V1.7）
# 功能：以 prod profile 启动后端（java -jar），支持环境变量覆盖配置
# 用法：./start.sh [PORT]
# 环境变量：DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD/DB_SSLMODE/DB_POOL_MAX/DB_POOL_MIN/
#          TOKEN_SECRET/CAPTCHA_ENABLED/INIT_DEFAULT_PASSWORD/CONFIG_SECRET_KEY/STORAGE_PATH/
#          LLM_GATEWAY_ENABLED/LLM_API_KEY_*/CORS_ALLOWED_ORIGINS/LOGIN_FAIL_MAX/LOGIN_FAIL_LOCK_MINUTES
#          （缺省见 application-prod.yml；CORS_ALLOWED_ORIGINS 生产必须配置为前端域名白名单）
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

# 探测 JDK21（jar 由 Java 21 编译，class 65.0；系统默认 java 可能为 17 等旧版）
JAVA_BIN=""
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_BIN="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1 && java -version 2>&1 | grep -qE '"2[1-9]'; then
  JAVA_BIN=java
else
  for cand in "$HOME"/.tools/jdk-21*/bin/java "$HOME"/workspace/.tools/jdk-21*/bin/java \
              "$HOME"/jdk-21*/bin/java "$HOME"/.sdkman/candidates/java/*/bin/java \
              /usr/lib/jvm/java-21-openjdk*/bin/java /usr/local/jdk-21*/bin/java /opt/jdk-21*/bin/java; do
    if [ -x "$cand" ]; then JAVA_BIN="$cand"; break; fi
  done
fi
if [ -z "${JAVA_BIN:-}" ]; then
  echo "!! 未找到 JDK21：jar 由 Java 21 编译，请安装 JDK21 或设置 JAVA_HOME 指向 JDK21" >&2
  exit 1
fi

echo "==> 使用 JDK：$JAVA_BIN（$($JAVA_BIN -version 2>&1 | head -1)）"

if pgrep -f "ptidss-server.*spring.profiles.active=prod" >/dev/null; then
  echo "!! 后端已在运行（prod），如需重启请先 ./stop.sh" >&2
  exit 1
fi

mkdir -p "$ROOT_DIR/logs"
nohup "$JAVA_BIN" -Xms512m -Xmx1024m \
  -jar "$JAR" \
  --spring.profiles.active=prod \
  --server.port="$PORT" \
  > "$ROOT_DIR/logs/backend_prod.log" 2>&1 &

echo "==> 后端已启动（port=$PORT，profile=prod，日志：logs/backend_prod.log）"
echo "    冒烟检查：curl -s http://127.0.0.1:${PORT}/ptidss/auth/captcha"
