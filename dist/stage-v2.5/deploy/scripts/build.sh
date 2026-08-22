#!/usr/bin/env bash
# ============================================================
# PTIDSS 构建脚本（V1.7）
# 功能：后端 mvn package（跳过测试）+ 前端 npm build（产物 frontend/dist）
# 用法：./build.sh
# 环境：JDK8（JAVA_HOME 可覆盖）、Node 18+、Maven 3.6+
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

JAVA_HOME="${JAVA_HOME:-/home/odoo/workspace/.tools/jdk8}"
MVN="${MVN:-$(command -v mvn || echo /home/odoo/workspace/.tools/apache-maven-3.8.8/bin/mvn)}"

echo "==> [1/2] 构建后端（ptidss-server）"
cd "$ROOT_DIR/ptidss-server"
JAVA_HOME="$JAVA_HOME" "$MVN" -q clean package -DskipTests
JAR=$(ls target/ptidss-server*.jar | head -1)
echo "    产物：$JAR"

echo "==> [2/2] 构建前端（ptidss-frontend）"
cd "$ROOT_DIR/frontend"
npm run build
echo "    产物：frontend/dist（部署时由 nginx 静态托管，/ptidss 反代后端）"

echo "==> 构建完成"
