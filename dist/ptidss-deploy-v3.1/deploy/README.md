# PTIDSS 部署目录（V3.0）

本目录承载生产部署配套，主推**进程级部署**，另附容器化可选方案与**一键部署包**。

## 目录结构

```
deploy/
├── scripts/                  # 进程级部署脚本（主推）
│   ├── init_db.sh            # 数据库初始化：建库建用户 + 执行 01~15 基线 DDL（幂等）
│   ├── build.sh              # 后端 mvn package + 前端 npm build
│   ├── package.sh            # 打包部署包（jar+dist+ddl+脚本+验证脚本 → dist/*.tar.gz）
│   ├── start.sh              # 以 prod profile 启动后端（环境变量可覆盖配置）
│   ├── stop.sh               # 优雅停止后端
│   └── status.sh             # 进程/端口/数据库/前端产物状态检查
├── nginx/
│   └── ptidss.conf.example   # Nginx 配置模板（静态托管 + /ptidss 反代）
└── docker/                   # 容器化可选方案
    ├── docker-compose.yml    # postgres18 + ptidss-server + nginx 三服务编排
    ├── Dockerfile.server     # 后端镜像（eclipse-temurin:21-jre）
    └── ptidss.conf.container # 容器内 Nginx 配置
```

## 快速开始（进程级）

```bash
# 1. 初始化数据库（本机 PG 超级用户；自动执行 01~15，含市场化区域/行情采集配置）
sudo ./scripts/init_db.sh

# 2. 构建前后端
./scripts/build.sh

# 3. 生产启动（验证码开启，默认密码 Ptidss@2026 仅首启初始化占位账号）
export TOKEN_SECRET=$(openssl rand -hex 32)   # 生产必改
./scripts/start.sh

# 4. 状态检查 / 停止
./scripts/status.sh
./scripts/stop.sh
```

## 一键部署包（公有云/迁移场景）

```bash
# 构建部署包（需先 ./scripts/build.sh 产出 jar 与 dist）
./scripts/package.sh            # 产出 dist/ptidss-deploy-v3.0.tar.gz

# 目标机器解包即用（目录结构保持仓库相对布局，脚本路径假设全部成立）
tar xzf ptidss-deploy-v3.0.tar.gz && cd ptidss-deploy-v3.0
sudo ./deploy/scripts/init_db.sh
export TOKEN_SECRET=$(openssl rand -hex 32)
./deploy/scripts/start.sh
```

部署包内含：后端 jar、前端 dist、deploy 全套脚本/编排、DDL 01~15、关键文档（部署/数据对接/操作手册/算法包规范/长期验证方案）、90 天验证脚本（tests/longrun）。

## 关键配置

| 项 | 默认 | 环境变量覆盖 |
|---|---|---|
| 数据库 | 127.0.0.1:5432/ptidss | DB_HOST / DB_PORT / DB_NAME / DB_USER / DB_PASSWORD |
| 数据库 SSL | disable | DB_SSLMODE（云 RDS 用 require / verify-ca） |
| 连接池 | max 20 / min 4 | DB_POOL_MAX / DB_POOL_MIN |
| 令牌密钥 | 占位（生产必改） | TOKEN_SECRET |
| 字段加密种子 | 占位（生产必改） | CONFIG_SECRET_KEY |
| 验证码 | true（prod） | CAPTCHA_ENABLED |
| 初始密码 | Ptidss@2026 | INIT_DEFAULT_PASSWORD |
| 存储目录 | ./data/ptidss | STORAGE_PATH |
| 跨域白名单 | *（生产必配） | CORS_ALLOWED_ORIGINS（逗号分隔前端域名） |
| 登录失败锁定 | 5 次/10 分钟 | LOGIN_FAIL_MAX / LOGIN_FAIL_LOCK_MINUTES |
| LLM 网关 | true | LLM_GATEWAY_ENABLED / LLM_API_KEY_* |

## 云环境接入已有 PostgreSQL（RDS/自建云实例）

已有云 pgsql 服务时**无需启动 compose 中的 postgres**，仅修改连接配置：

```bash
# 数据库初始化（在能连通云库的机器上执行；init_db.sh 已支持远程主机参数）
# 用法：./init_db.sh <云库地址> <端口> <超级用户名>，密码经 PGPASSWORD 传入
PGPASSWORD='<云超级用户密码>' ./scripts/init_db.sh <rds-endpoint> 5432 postgres

# 生产启动（进程级，云主机上）
export DB_HOST=<rds-endpoint>          # 云 RDS 内网/公网地址
export DB_PORT=5432
export DB_NAME=ptidss
export DB_USER=ptidss
export DB_PASSWORD='<强密码>'
export DB_SSLMODE=require              # 云 RDS 强制 SSL 时必填（verify-ca 需附证书）
export TOKEN_SECRET=$(openssl rand -hex 32)
export CONFIG_SECRET_KEY=$(openssl rand -hex 16)
export STORAGE_PATH=/opt/ptidss/data   # 持久卷目录
./scripts/start.sh
```

> 云 RDS 提示：① 安全组/白名单需放行应用服务器 IP 与 5432 端口；② 阿里云/腾讯云/AWS RDS 默认或强制 SSL，`DB_SSLMODE=require` 即可；③ RDS 的 `max_connections` 有限，连接池 `DB_POOL_MAX` 建议 ≤ RDS 上限的 1/3；④ 容器化场景把 compose 中 `postgres` 服务删掉，`ptidss-server.environment.DB_HOST` 直接指向云库地址。

## 生产上线安全清单（等保三级）

1. **HTTPS**：nginx 模板已含 443/TLSv1.2+1.3/HTTP 跳转与安全响应头（CSP/X-Frame-Options/HSTS 等），上线前将证书放置 `/etc/nginx/ssl/`；容器化场景由前置 LB 终止 TLS；
2. **跨域白名单**：`export CORS_ALLOWED_ORIGINS=https://<前端域名>`（逗号分隔），禁止保留默认 `*`；
3. **密钥全覆盖**：`TOKEN_SECRET`、`CONFIG_SECRET_KEY` 用 `openssl rand` 生成；`DB_PASSWORD`、`LLM_API_KEY_*` 经环境变量注入；
4. **登录防护**：验证码（prod 默认开）+ 连续失败锁定（默认 5 次/10 分钟，`LOGIN_FAIL_MAX`/`LOGIN_FAIL_LOCK_MINUTES` 可调）；
5. **数据隔离**：RLS 行级安全已启用（13 张核心表），服务账号最小权限，`ptidss_admin`/`ptidss_dba` 仅限运维窗口；
6. **审计**：业务操作审计（@Log）+ 登录审计落库，日志滚动（100MB×30）；建议审计库独立备份并留存 ≥6 个月；
7. **备份恢复**：数据库每日全备 + PITR（WAL 归档），恢复演练每季度一次；
8. **上线口令**：初始化账号（Ptidss@2026）首次登录后立即改密，密码策略 ≥12 位含大小写/数字/特殊字符，90 天定期更换；

## 上线后动作

1. **切换真实行情**：`/intel/fetch-status` 台账中市场化源 `conn_config.mock` 置 `false` 并配置各省交易中心真实 endpoint，系统自动走"HTTP 拉取 → 指数退避重试 → fallbackUrl 降级 → 状态留痕（连续失败 ≥10 次自动停用）"链路；
2. **90 天长期验证**：`tests/longrun/` 日/周/月/季脚本按 crontab 调度（见 tests/longrun/README.md），报告留存 `reports/`；
3. **算法包规范**：客户 jar/zip 算法包内置 `ptidss-algorithm.json` 或 MANIFEST `PTIDSS-Algorithm-*` 属性即全自动适配（见 docs/算法包规范.md）。

详细说明见 `docs/部署文档.md`、`docs/数据对接文档.md`、`docs/操作手册.md`。
