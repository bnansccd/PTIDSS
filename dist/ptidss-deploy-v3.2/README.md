# PTIDSS 部署包 v3.2

> 一键部署包：保持仓库相对布局（ptidss-server/target、frontend/dist、docs/ddl、deploy、tests/longrun），
> 部署脚本路径假设全部成立，解包即用。

## 前置要求

- JDK 21+（jar 由 Java 21 编译；start.sh 自动探测 JAVA_HOME/常见路径，找不到会明确报错）
- PostgreSQL 18+（模式 A 需本机实例；模式 B 用云 RDS）
- API 契约：docs/openapi/openapi.yaml（115 路径）

## 快速开始（进程级）

### 模式 A：本系统自带数据库（单机自带 PostgreSQL）

```bash
sudo ./deploy/scripts/init_db.sh                      # 本机 5432 建库建用户 + 01~18 DDL 幂等
./deploy/scripts/start.sh                             # 启动后端（prod，9080；DB 默认本机 127.0.0.1）
./deploy/scripts/status.sh                            # 状态检查
```

### 模式 B：云环境已有数据服务（云 RDS / 自建云实例）

```bash
# ① 在能连通云库的机器上执行初始化（传云库地址/端口/超级用户，密码经 PGPASSWORD ）
PGPASSWORD='<云超级用户密码>' ./deploy/scripts/init_db.sh <rds-endpoint> 5432 postgres
# ② 应用启动指向云库（环境变量全部可覆盖）
export DB_HOST=<rds-endpoint>
export DB_PORT=5432
# export DB_SSLMODE=require      # 云 RDS 强制 SSL 时必填
./deploy/scripts/start.sh
```

> 两种模式共用同一份部署包：`init_db.sh` 参数化主机地址，`start.sh` 经 `DB_*`
> 环境变量接管连接（缺省即本机自带库 127.0.0.1:5432）。

## 容器化（可选）

```bash
cd deploy/docker && docker compose up -d --build
```

## 上线后动作

1. 切换真实行情：/intel/fetch-status 台账中 conn_config.mock 置 false 并配置各省交易中心真实 endpoint；
2. 90 天长期验证：tests/longrun/ 按 README 配置 crontab（日/周/月/季）；
3. 算法包规范：jar/zip 内置 ptidss-algorithm.json 即全自动适配；
4. 移动端（M7）：mini-program/ 导入微信开发者工具，修改 utils/request.js 的 BASE_URL 为生产 https 域名，并在公众平台配置 request 合法域名；移动审批账号需授予 menu:flow 权限。

详见 docs/部署文档.md。
