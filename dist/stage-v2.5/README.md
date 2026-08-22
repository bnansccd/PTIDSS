# PTIDSS 部署包 v2.5

> 一键部署包：保持仓库相对布局（ptidss-server/target、frontend/dist、docs/ddl、deploy、tests/longrun），
> 部署脚本路径假设全部成立，解包即用。

## 快速开始（进程级）

```bash
sudo ./deploy/scripts/init_db.sh                          # 初始化数据库（01~15 DDL 幂等）
export TOKEN_SECRET=$(openssl rand -hex 32)              # 生产必改
export DB_PASSWORD='<生产数据库密码>'
./deploy/scripts/start.sh                                 # 启动后端（prod，9080）
./deploy/scripts/status.sh                                # 状态检查
```

## 容器化（可选）

```bash
cd deploy/docker && docker compose up -d --build
```

## 上线后动作

1. 切换真实行情：/intel/fetch-status 台账中 conn_config.mock 置 false 并配置各省交易中心真实 endpoint；
2. 90 天长期验证：tests/longrun/ 按 README 配置 crontab（日/周/月/季）；
3. 算法包规范：jar/zip 内置 ptidss-algorithm.json 即全自动适配。

详见 docs/部署文档.md。
