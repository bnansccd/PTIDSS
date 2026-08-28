# PTIDSS 生产一键部署（Docker Compose，V3.2）

> **自包含部署目录**：jar、前端构建产物、DDL 基线、nginx 配置、编排文件全部在本目录内，
> **整个目录拷贝到生产主机任意位置即可启动**，生产主机无需 JDK/Node/PostgreSQL。

## 一、部署步骤（生产主机）

```bash
# 1. 拷贝本目录到生产主机（任选其一）
scp -r deploy/docker-prod user@prod-host:/opt/ptidss/

# 2. 进入部署目录，配置环境变量（TOKEN_SECRET/DB_PASSWORD 必改）
cd /opt/ptidss/docker-prod
cp .env.example .env
vi .env          # 修改 TOKEN_SECRET（openssl rand -base64 48）、DB_PASSWORD

# 3. 一键启动（首次会自动构建镜像 + 初始化数据库 01~18 DDL）
docker compose up -d --build

# 4. 验证
docker compose ps                  # 三服务均 healthy/Up
curl -s http://127.0.0.1/ptidss/auth/captcha   # 返回验证码 JSON 即后端就绪
```

浏览器访问 `http://<主机IP>`（FRONT_PORT 默认 80），默认账号：

| 账号 | 初始密码 | 角色 |
|---|---|---|
| admin | Ptidss@2026 | 系统管理员 |
| trader01 | Ptidss@2026 | 交易员 |

> 首次登录后请在「系统管理-用户」修改初始密码；TOKEN_SECRET 一经使用不可更换（否则全部令牌失效）。

## 二、服务与端口

| 服务 | 容器名 | 端口 | 说明 |
|---|---|---|---|
| nginx | ptidss-nginx | **80**（FRONT_PORT 可改） | 前端对外唯一端口：静态托管 + /ptidss 反代后端 |
| ptidss-server | ptidss-server | 127.0.0.1:9080 | 后端（仅宿主机调试，不对外） |
| postgres | ptidss-postgres | 127.0.0.1:5432 | 数据库（仅宿主机可见） |

## 三、数据持久化（named volume，容器删除不丢数据）

| Volume | 挂载点 | 内容 |
|---|---|---|
| pgdata | /var/lib/postgresql/data | 数据库全量数据 |
| ptidss-data | /app/data | 上传文件（政策原文/算法包/OCR 单据） |
| ptidss-logs | /app/logs | 后端运行日志 |

备份：`docker run --rm -v ptidss_docker-prod_pgdata:/data -v $(pwd):/backup alpine tar czf /backup/pgdata-$(date +%F).tar.gz /data`

## 四、常用运维

```bash
docker compose ps                  # 状态
docker compose logs -f ptidss-server   # 后端日志
docker compose restart ptidss-server   # 重启后端（改 .env 后生效）
docker compose down                 # 停止（保留数据卷）
docker compose down -v              # 停止并清除数据（重新初始化，慎用）
docker compose pull                 # 升级基础镜像（postgres/nginx）
docker compose build --no-cache ptidss-server   # 升级后端 jar 后重建
```

## 五、升级后端/前端

1. 重新构建 jar / frontend/dist（开发机执行 build.sh）
2. 替换本目录 `server/ptidss-server.jar`、`frontend/dist/`
3. `docker compose up -d --build ptidss-server nginx`

## 六、连接云数据库（可选双模式）

默认使用编排内自带 PostgreSQL（模式 A）。如需连接云 RDS（模式 B）：

1. 删除本编排 postgres 服务（或保留但不再使用）；
2. `.env` 配置：
   ```
   DB_HOST=<rds-endpoint>      # 云 RDS 地址
   DB_PORT=5432
   DB_NAME=ptidss
   DB_USER=ptidss
   DB_PASSWORD=<云库密码>
   DB_SSLMODE=require          # 云 RDS 强制 SSL 时必填
   ```
3. 云库初始化：在能连通云库的机器执行本目录 `init_db.sh` 的宿主版
   `deploy/scripts/init_db.sh <rds-endpoint> 5432 postgres`（见部署文档 §云 RDS）。

## 七、安全清单（上线前核对）

- [ ] `.env` 的 TOKEN_SECRET / DB_PASSWORD 已改为强随机值（本目录 .env 已生成随机值）
- [ ] CORS_ALLOWED_ORIGINS 配置为实际前端域名（不保留 `*`）
- [ ] 80 端口前置 LB/防火墙强制 HTTPS（等保 8.1.8.1）
- [ ] 初始账号密码已修改
- [ ] 数据库仅内网可达（5432 未暴露公网）
- [ ] 情报源切换真实行情：登录后「情报中心-采集监控」将 mock 源置为真实 endpoint

## 八、目录结构

```
docker-prod/
├── docker-compose.yml     # 编排（postgres + server + nginx）
├── .env / .env.example    # 环境变量（密钥/端口）
├── init-db.sh             # 数据库初始化包装（首次启动自动执行 01~18）
├── ddl/                   # DDL 基线 01~18（02 TDengine 由时序库单独执行）
├── server/
│   ├── Dockerfile         # 后端镜像（temurin:21-jre + jar）
│   └── ptidss-server.jar  # 后端构建产物
├── frontend/dist/         # 前端构建产物（nginx 托管）
└── nginx/ptidss.conf      # nginx 配置（静态 + 反代 + 安全头）
```
