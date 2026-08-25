#!/usr/bin/env bash
# ============================================================
# PTIDSS 部署包打包脚本（V3.2）
# 功能：将 jar + 前端 dist + 小程序 mini-program + deploy 脚本 + DDL
#       + 关键文档 + 长期验证脚本 组装为保持仓库相对布局的一键部署包（tar.gz）
# 用法：./package.sh [版本]        # 默认版本 v3.2，产物 dist/ptidss-deploy-v3.2.tar.gz
# 前置：先执行 ./build.sh 产出 ptidss-server/target/*.jar 与 frontend/dist
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
VERSION="${1:-v3.2}"
PKG_NAME="ptidss-deploy-$VERSION"
STAGE="$ROOT_DIR/dist/$PKG_NAME"
OUT="$ROOT_DIR/dist/$PKG_NAME.tar.gz"

echo "==> [1/5] 校验构建产物"
JAR=$(ls "$ROOT_DIR"/ptidss-server/target/ptidss-server*.jar 2>/dev/null | head -1 || true)
if [ -z "${JAR:-}" ]; then
  echo "!! 未找到后端 jar，请先执行 ./build.sh" >&2
  exit 1
fi
if [ ! -d "$ROOT_DIR/frontend/dist" ]; then
  echo "!! 未找到 frontend/dist，请先执行 ./build.sh" >&2
  exit 1
fi

echo "==> [2/5] 组装部署包目录（$PKG_NAME）"
rm -rf "$STAGE"
mkdir -p "$STAGE/ptidss-server/target" "$STAGE/frontend" "$STAGE/docs" "$STAGE/tests" "$STAGE/mini-program"

cp "$JAR" "$STAGE/ptidss-server/target/"
cp -r "$ROOT_DIR/frontend/dist" "$STAGE/frontend/dist"
cp -r "$ROOT_DIR/mini-program/." "$STAGE/mini-program/"
cp -r "$ROOT_DIR/deploy" "$STAGE/deploy"
cp -r "$ROOT_DIR/docs/ddl" "$STAGE/docs/ddl"
cp -r "$ROOT_DIR/tests/longrun" "$STAGE/tests/longrun"
# 排除缓存与历史报告（部署包保持干净；验证报告由目标机器按调度生成）
find "$STAGE/tests/longrun" \( -name "__pycache__" -o -name "*.pyc" \) -exec rm -rf {} + 2>/dev/null || true
rm -rf "$STAGE/tests/longrun/reports"

echo "==> [3/5] 收录关键文档"
for f in 部署文档.md 数据对接文档.md 操作手册.md 算法包规范.md \
         V2.4市场化省份接入与长期验证方案.md PRD基线差异检查报告_M7.md; do
  if [ -f "$ROOT_DIR/docs/$f" ]; then
    cp "$ROOT_DIR/docs/$f" "$STAGE/docs/"
  fi
done
# API 契约（OpenAPI 115 路径，对接/联调用）
cp -r "$ROOT_DIR/docs/openapi" "$STAGE/docs/openapi"

echo "==> [4/5] 生成部署导读 README"
cat > "$STAGE/README.md" <<EOF
# PTIDSS 部署包 $VERSION

> 一键部署包：保持仓库相对布局（ptidss-server/target、frontend/dist、docs/ddl、deploy、tests/longrun），
> 部署脚本路径假设全部成立，解包即用。

## 前置要求

- JDK 21+（jar 由 Java 21 编译；start.sh 自动探测 JAVA_HOME/常见路径，找不到会明确报错）
- PostgreSQL 18+（模式 A 需本机实例；模式 B 用云 RDS）
- API 契约：docs/openapi/openapi.yaml（115 路径）

## 快速开始（进程级）

### 模式 A：本系统自带数据库（单机自带 PostgreSQL）

\`\`\`bash
sudo ./deploy/scripts/init_db.sh                      # 本机 5432 建库建用户 + 01~18 DDL 幂等
./deploy/scripts/start.sh                             # 启动后端（prod，9080；DB 默认本机 127.0.0.1）
./deploy/scripts/status.sh                            # 状态检查
\`\`\`

### 模式 B：云环境已有数据服务（云 RDS / 自建云实例）

\`\`\`bash
# ① 在能连通云库的机器上执行初始化（传云库地址/端口/超级用户，密码经 PGPASSWORD ）
PGPASSWORD='<云超级用户密码>' ./deploy/scripts/init_db.sh <rds-endpoint> 5432 postgres
# ② 应用启动指向云库（环境变量全部可覆盖）
export DB_HOST=<rds-endpoint>
export DB_PORT=5432
# export DB_SSLMODE=require      # 云 RDS 强制 SSL 时必填
./deploy/scripts/start.sh
\`\`\`

> 两种模式共用同一份部署包：\`init_db.sh\` 参数化主机地址，\`start.sh\` 经 \`DB_*\`
> 环境变量接管连接（缺省即本机自带库 127.0.0.1:5432）。

## 容器化（可选）

\`\`\`bash
cd deploy/docker && docker compose up -d --build
\`\`\`

## 上线后动作

1. 切换真实行情：/intel/fetch-status 台账中 conn_config.mock 置 false 并配置各省交易中心真实 endpoint；
2. 90 天长期验证：tests/longrun/ 按 README 配置 crontab（日/周/月/季）；
3. 算法包规范：jar/zip 内置 ptidss-algorithm.json 即全自动适配；
4. 移动端（M7）：mini-program/ 导入微信开发者工具，修改 utils/request.js 的 BASE_URL 为生产 https 域名，并在公众平台配置 request 合法域名；移动审批账号需授予 menu:flow 权限。

详见 docs/部署文档.md。
EOF

echo "==> [5/5] 打包（$OUT）"
cd "$ROOT_DIR/dist"
tar czf "$OUT" "$PKG_NAME"
rm -rf "$STAGE"
ls -lh "$OUT"
echo "==> 部署包完成：dist/$PKG_NAME.tar.gz"
