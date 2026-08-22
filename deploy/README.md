# PTIDSS 部署目录（V2.5）

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
    ├── Dockerfile.server     # 后端镜像（eclipse-temurin:8-jre）
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
./scripts/package.sh            # 产出 dist/ptidss-deploy-v2.5.tar.gz

# 目标机器解包即用（目录结构保持仓库相对布局，脚本路径假设全部成立）
tar xzf ptidss-deploy-v2.5.tar.gz && cd ptidss-deploy-v2.5
sudo ./deploy/scripts/init_db.sh
export TOKEN_SECRET=$(openssl rand -hex 32)
./deploy/scripts/start.sh
```

部署包内含：后端 jar、前端 dist、deploy 全套脚本/编排、DDL 01~15、关键文档（部署/数据对接/操作手册/算法包规范/长期验证方案）、90 天验证脚本（tests/longrun）。

## 关键配置

| 项 | 默认 | 环境变量覆盖 |
|---|---|---|
| 数据库 | 127.0.0.1:5432/ptidss | DB_HOST / DB_PORT / DB_NAME / DB_USER / DB_PASSWORD |
| 令牌密钥 | 占位（生产必改） | TOKEN_SECRET |
| 验证码 | true（prod） | CAPTCHA_ENABLED |
| 初始密码 | Ptidss@2026 | INIT_DEFAULT_PASSWORD |

## 上线后动作（V2.5）

1. **切换真实行情**：`/intel/fetch-status` 台账中市场化源 `conn_config.mock` 置 `false` 并配置各省交易中心真实 endpoint，系统自动走"HTTP 拉取 → 指数退避重试 → fallbackUrl 降级 → 状态留痕（连续失败 ≥10 次自动停用）"链路；
2. **90 天长期验证**：`tests/longrun/` 日/周/月/季脚本按 crontab 调度（见 tests/longrun/README.md），报告留存 `reports/`；
3. **算法包规范**：客户 jar/zip 算法包内置 `ptidss-algorithm.json` 或 MANIFEST `PTIDSS-Algorithm-*` 属性即全自动适配（见 docs/算法包规范.md）。

详细说明见 `docs/部署文档.md`、`docs/数据对接文档.md`、`docs/操作手册.md`。
