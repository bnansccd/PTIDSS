#!/usr/bin/env bash
# ============================================================
# PTIDSS 部署包打包脚本（V2.5）
# 功能：将 jar + 前端 dist + deploy 脚本 + DDL + 关键文档 + 长期验证脚本
#       组装为保持仓库相对布局的一键部署包（tar.gz），供公有云/迁移部署
# 用法：./package.sh [版本]        # 默认版本 v2.5，产物 dist/ptidss-deploy-v2.5.tar.gz
# 前置：先执行 ./build.sh 产出 ptidss-server/target/*.jar 与 frontend/dist
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
VERSION="${1:-v2.5}"
STAGE="$ROOT_DIR/dist/stage-$VERSION"
PKG_NAME="ptidss-deploy-$VERSION"
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
mkdir -p "$STAGE/ptidss-server/target" "$STAGE/frontend" "$STAGE/docs" "$STAGE/tests"

cp "$JAR" "$STAGE/ptidss-server/target/"
cp -r "$ROOT_DIR/frontend/dist" "$STAGE/frontend/dist"
cp -r "$ROOT_DIR/deploy" "$STAGE/deploy"
cp -r "$ROOT_DIR/docs/ddl" "$STAGE/docs/ddl"
cp -r "$ROOT_DIR/tests/longrun" "$STAGE/tests/longrun"

echo "==> [3/5] 收录关键文档"
for f in 部署文档.md 数据对接文档.md 操作手册.md 算法包规范.md \
         V2.4市场化省份接入与长期验证方案.md; do
  if [ -f "$ROOT_DIR/docs/$f" ]; then
    cp "$ROOT_DIR/docs/$f" "$STAGE/docs/"
  fi
done

echo "==> [4/5] 生成部署导读 README"
cat > "$STAGE/README.md" <<EOF
# PTIDSS 部署包 $VERSION

> 一键部署包：保持仓库相对布局（ptidss-server/target、frontend/dist、docs/ddl、deploy、tests/longrun），
> 部署脚本路径假设全部成立，解包即用。

## 快速开始（进程级）

\`\`\`bash
sudo ./deploy/scripts/init_db.sh                          # 初始化数据库（01~15 DDL 幂等）
export TOKEN_SECRET=\$(openssl rand -hex 32)              # 生产必改
export DB_PASSWORD='<生产数据库密码>'
./deploy/scripts/start.sh                                 # 启动后端（prod，9080）
./deploy/scripts/status.sh                                # 状态检查
\`\`\`

## 容器化（可选）

\`\`\`bash
cd deploy/docker && docker compose up -d --build
\`\`\`

## 上线后动作

1. 切换真实行情：/intel/fetch-status 台账中 conn_config.mock 置 false 并配置各省交易中心真实 endpoint；
2. 90 天长期验证：tests/longrun/ 按 README 配置 crontab（日/周/月/季）；
3. 算法包规范：jar/zip 内置 ptidss-algorithm.json 即全自动适配。

详见 docs/部署文档.md。
EOF

echo "==> [5/5] 打包（$OUT）"
cd "$ROOT_DIR/dist"
tar czf "$OUT" "$PKG_NAME"
rm -rf "$STAGE"
ls -lh "$OUT"
echo "==> 部署包完成：dist/$PKG_NAME.tar.gz"
