# PTIDSS Docker Compose 生产部署验证报告（V3.2）

> 交付形态：自包含部署目录 `deploy/docker-prod/` + 打包产物 `dist/ptidss-docker-prod-v3.2.tar.gz`（33M）
> 核心承诺：**整个目录拷贝到生产主机 → `docker compose up -d --build` 即完成部署**（生产主机无需 JDK/Node/PostgreSQL）

## 一、部署目录自包含内容

```
docker-prod/                    # 全部构建产物内置，无外部依赖
├── docker-compose.yml          # 编排：postgres(18) + ptidss-server + nginx
├── .env / .env.example         # 环境变量（已生成随机 DB_PASSWORD / TOKEN_SECRET）
├── init-db.sh                  # 数据库初始化包装（首次启动自动执行 DDL 01~18，幂等）
├── ddl/                        # DDL 基线 14 个（01~18，排除 02 TDengine）
├── server/
│   ├── Dockerfile              # temurin:21-jre + jar（离线可构建）
│   └── ptidss-server.jar       # 后端构建产物（36M）
├── frontend/dist/              # 前端构建产物（37 个资源，含验证码修复）
└── nginx/ptidss.conf           # 静态托管 + /ptidss 反代 + 等保安全头
```

## 二、部署要点（与进程级方案的差异）

| 项 | 进程级（旧） | Docker Compose（新，主推） |
|---|---|---|
| 生产主机依赖 | JDK21、PostgreSQL 18、Node | **仅 Docker** |
| 启动 | init_db.sh + start.sh 多步 | `docker compose up -d --build` 一步 |
| 数据库初始化 | 手动三态幂等检测 | 首次启动 entrypoint 自动执行 01~18 |
| 数据持久化 | 宿主目录 | named volume（pgdata/ptidss-data/ptidss-logs） |
| 端口 | 前端 80/5173 + 后端 9080 + 库 5432 | **对外仅 80**；9080/5432 仅绑 127.0.0.1 |

## 三、本机全流程验证结果（模拟生产主机，全部通过）

| # | 验证项 | 结果 |
|---|---|---|
| 1 | 干净环境 `docker compose up -d --build` 三服务启动 | ✅ postgres healthy / server healthy / nginx Up |
| 2 | 前端页面 http://127.0.0.1:80/ | ✅ 标题/表单正常 |
| 3 | 验证码经 nginx 反代（80 → 9080） | ✅ code:0，PNG base64 |
| 4 | 验证码直连 9080 | ✅ code:0 |
| 5 | 错误验证码登录被拒 | ✅ 400「验证码错误」（CAPTCHA_ENABLED=true） |
| 6 | 登录成功链路（临时关闭验证码复验后恢复 true） | ✅ accessToken/roles/permissions 完整 |
| 7 | 业务接口（auth/current、intel/sources、agent/registry） | ✅ 全部 code:0 |
| 8 | 数据库自动初始化 | ✅ 7 角色/18 权限/4 用户/11 区域/20 情报源/33 配置/18 号索引/2 分区表 |
| 9 | 重启后数据持久化 | ✅ 数据不丢（named volume） |
| 10 | 浏览器实测（Playwright） | ✅ 验证码 130×46 正常显示、点击刷新有效、控制台零错误 |
| 11 | 安全头/CSP | ✅ nginx 等保响应头生效 |

## 四、过程中发现并修复的问题

1. **postgres:18 挂载点变更**：18+ 镜像要求挂载 `/var/lib/postgresql`（非 `/var/lib/postgresql/data`），否则拒绝启动。已在 docker-prod 与旧版 deploy/docker 编排同步修复。
2. **Docker Hub 直连超时**：本机需经镜像加速器拉取 `eclipse-temurin:21-jre`（`docker pull docker.m.daocloud.io/eclipse-temurin:21-jre && docker tag ...`）。生产主机如网络受限需同样处理（或预拉镜像 `docker save/load` 离线导入）。
3. **后端健康检查**：temurin jammy 基础镜像无 wget，Dockerfile 显式安装（否则 HEALTHCHECK 永远失败，server 无法就绪）。

## 五、生产主机部署步骤（3 步）

```bash
# 1. 拷贝（tar 包或目录）
scp dist/ptidss-docker-prod-v3.2.tar.gz root@prod:/opt/ && tar xzf ptidss-docker-prod-v3.2.tar.gz -C /opt/

# 2. 配置（.env 已含随机密钥，仅按需修改）
cd /opt/docker-prod && vi .env     # TOKEN_SECRET/DB_PASSWORD 已随机；FRONT_PORT/CORS 按需

# 3. 一键启动
docker compose up -d --build && docker compose ps
```

访问 `http://<主机IP>`，默认账号 admin / trader01（初始密码 Ptidss@2026，首登后修改）。

## 六、双模式说明

- **模式 A（默认）**：编排内自带 PostgreSQL，首次启动自动建库 + DDL 01~18。
- **模式 B（云 RDS）**：`.env` 配置 DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD/DB_SSLMODE 后，移除 postgres 服务即可直连云库（云库 DDL 用 `deploy/scripts/init_db.sh` 在连通机器上执行）。

## 七、遗留事项

- 生产上线前按 README §七 安全清单核对（CORS 白名单、HTTPS 前置、初始密码修改）
- 验证码为 Caffeine 内存态：多副本横向扩容时建议改 Redis 共享（当前单实例无影响）
