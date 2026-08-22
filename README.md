# PTIDSS 电力交易智能辅助决策系统

依据《开发过程规划文档_电力交易智能辅助决策系统_开发基线V1.6.md》与《PRD_电力交易智能辅助决策系统_v1.1.md》开发。

**当前阶段**：P0 业务模块交付（市场行情/交易申报/辅助决策/复盘结算/数据管理/情报中心/政策解析/消息中心/数据底座/预测中心/联合优化/模型平台/审批流已交付，见开发基线 V1.6.6 修订记录）

## 目录结构

| 目录 | 说明 |
|---|---|
| `ptidss-server/` | 后端单体服务（Spring Boot 2.7 + JDK8 + MyBatis-Plus + PostgreSQL 18） |
| `low-code-cms-v2-dev/` | 前端（Vue3 + Vite5 + Element Plus + Pinia，由 low-code-cms-v2-dev 基线改造） |
| `low-code-dev/` | 后端组织架构与权限管理参考基线（微服务模式：JWT + LoginUser + 切面鉴权 + 审计） |
| `docs/` | 设计文档：DDL 基线 v1.0.3（01/03/06/09/07 执行序）、OpenAPI 契约 V1.1、评审记录、多省方案等 |
| `开发过程规划文档_...V1.6.md` | 开发基线 V1.6（M0-M6 里程碑实施契约 C1-C6） |
| `PRD_...v1.1.md` | 产品需求文档 V1.1 |
| `logs/` | 运行日志（backend.log / vite.log） |

## 环境与启动

### 1. 数据库（PostgreSQL 18，端口 5432）

```bash
# 启动 PG18（开发机：/tmp/pg18，数据目录 /tmp/pg18/data）
/tmp/pg18/bin/pg_ctl -D /tmp/pg18/data -l /tmp/pg18/pg.log start
# 建库建表（执行序：01 → 03 → 09 → 07，v1.0.2/v1.0.3 落库修订见 DDL 文件头）
psql -h localhost -p 5432 -U ptidss -d ptidss -f docs/ddl/01_postgres_schema.sql
```

### 2. 后端（端口 9080，context-path /ptidss）

```bash
cd ptidss-server
/tools/apache-maven-3.8.8/bin/mvn spring-boot:run
# 健康检查：curl http://localhost:9080/ptidss/auth/captcha
```

关键配置 `src/main/resources/application.yml`：

- `ptidss.captcha.enabled`：登录验证码开关（默认 true）
- `ptidss.init.default-password`：种子账号占位密码初始化值（Ptidss@2026）
- JWT：jjwt 0.9.1，有效期 120 分钟，临近过期自动续期；登录态缓存 Caffeine

### 3. 前端（端口 30001，base /low-code-cms/）

```bash
cd low-code-cms-v2-dev
npm install --legacy-peer-deps
npx vite        # 访问 http://localhost:30001/low-code-cms/
```

- API 网关：`src/env/index.ts` 的 `baseUrl`（默认 http://localhost:9080/ptidss）
- 契约：code=0 成功 / 14001 未认证 / 14003 无权限；Bearer token + `X-Region-Code` 请求头
- 注意：开发机 inotify 配额被 IDE 占用，vite.config.ts 已禁用文件监听（改代码需重启 dev server）

## 种子账号（密码初始化：Ptidss@2026）

| 账号 | 角色 | 说明 |
|---|---|---|
| admin | admin | 系统管理员（全部权限） |
| trader01 | trader + mobile | 交易员（交易向权限） |
| settle01 | settlement | 结算员 |
| manager01 | manager | 经理 |

固定 7 类角色（DDL CHECK 约束）：trader / analyst / settlement / admin / manager / compliance / mobile。

## 已交付功能（M0 组织架构与用户权限管理 + P0 业务模块）

### M0 组织架构与用户权限管理

- 认证：图形验证码、登录/登出、当前用户（角色/权限/区域组装）、JWT 自动续期
- 区域管理：多省配置化（支持市场/交易通道/结算周期/接入顺序），评审决议⑤
- 用户管理：CRUD、角色×区域双重授权、密码重置、状态管理
- 角色管理：固定 7 类角色、权限分配（menu/api/data 三级权限树全量覆盖）
- 权限管理：三级权限（菜单/接口/数据）CRUD
- 审计日志：等保三级审计（操作人/IP/UA/入参快照/结果/区域），按操作人/动作/区域/结果检索
- 数据权限：`X-Region-Code` 会话区域 + 13 表 RLS（开发环境超级用户绕过，应用层按 region 过滤）

### P0 业务模块（开发基线 V1.6.2 ~ V1.6.6）

- 市场行情（menu:market）：现货 96 点量价曲线、中长期 24 点、供需平衡 96 点、量价热力图（dates×96），数据源 mock 可切换 TDengine
- 交易申报（menu:trade）：申报单创建+分段电量/合规预检（限价/段数/总量）、提交回执、成交结果、持仓曲线、日滚动方案
- 辅助决策（menu:decision）：6 智能体编排模拟、冲突仲裁、人机确认（修改依据 FR-DM-05 必填）、双人复核、依据链回溯
- 结算管理（menu:settlement，V1.6.3）：结算记录按周期懒生成 system/exchange 双份、核对引擎逐科目 1% 容差比对（passRate≥0.95 一致，FR-RS-02）、差异工单自动生成与状态机流转（assign/process/review/close 留痕）、结算单 OCR 识别（置信度≥0.95 直通，低置信人工复核）
- 复盘考核（menu:review，V1.6.3）：复盘报告三层归因（预测/决策/执行，FR-RS-01）、策略回流（reviewId 可选自动关联最近报告）、考核指标/结果按权重加权评分、申诉批准重算（×1.05，FR-DM-07）
- 数据管理（menu:report + menu:settlement，V1.6.4）：报表中心（模板懒生成 5 类含口径说明/按周期生成实例数据快照落库口径可追溯/CSV 导出表头+口径说明+指标行，FR-DM-02）+ OCR 复核工作台（任务列表分页/低置信人工复核确认或修正字段全留痕，FR-DM-03 补录闭环）
- 情报中心（menu:intel，V1.6.4）：情报源台账（10 源代表子集覆盖 6 类）、情报流（归一化标签+重要度分级，当前省+全国可见）、推送规则（标签×重要度→角色/渠道，high 级自动 +sms/+miniapp 实时推送 ≤30s，FR-INT-04 RE-01 P0）
- 政策中心（menu:policy，V1.6.5）：政策文件台账（6 种子懒生成覆盖国家/区域/省内×现货/中长期/结算/考核标签）、政策解析（确定性模拟 LLM 抽取条款/影响研判/规则沉淀 RULE-POLICY-* 版本化留痕，幂等 + reparse 强制重建版本递增，FR-PD-01）、CSV 简报导出（条款/研判/规则三表）
- 消息中心（menu:message，V1.6.5）：消息 5 类种子按 receiverId 个人维度懒生成、类型筛选/未读过滤分页、标记已读幂等 + 越权操作他人消息拒绝
- 数据底座（menu:data，V1.6.6，WBS 3.0）：数据源台账（营销/交易中心/气象双通道建模，关联采集最近运行）、手动采集留痕（5 类任务 force 强制重跑）、数据质量报告（完整率/准确率/及时率按规则类型聚合，FR-PD-04 P0 + FR-PD-05 P1）、数据血缘（七节点链路懒种子含上游/下游，表/任务/模型/报表四类节点）
- 预测中心（menu:forecast，V1.6.6，WBS 4.0）：预测任务异步状态机（queued→running→success 确定性可复现）、96 点结果含 90% 置信区间（FR-TR-01~03 P0 + FR-TR-06）、模型注册（与 model 域共用 model_registry）、模型训练触发（daily_increment/weekly_full 写 training_task）
- 联合优化（menu:optimize，V1.6.6，WBS 5.0）：MILP 联合优化任务（HiGHS/SCIP/Gurobi 求解抽象，1-7 天 × 10-500 场景，expectedRevenue/CVaR 确定性模拟，FR-TR-06 P0）、策略回测（收益增量验收核心）、策略库（回测/复盘/人工三源懒种子）
- 模型平台（menu:model，V1.6.6）：模型注册表（MLflow 同步 3 模型在线）、在线推理（确定性 latency）、离线评估（MAPE+方向准确率双指标判定，FR-PD-03）
- 审批流（menu:flow，V1.6.6）：流程实例发起（轻量状态机 apply→review→approve→archive，不引入 Flowable，幂等同单据）、实例详情（状态/当前节点/待办任务，M7 移动端审批依赖）

## 核心 API（/ptidss 前缀，详见 docs/openapi/）

| 模块 | 接口 |
|---|---|
| 认证 | GET /auth/captcha、POST /auth/login、GET /auth/current、POST /auth/logout |
| 系统管理 | /admin/regions、/admin/users（含 /{id}/regions、/{id}/password）、/admin/roles（含 /{id}/permissions）、/admin/permissions、/admin/logs |
| 市场行情 | GET /market/price/spot、GET /market/price/midlong、GET /market/supply-demand、GET /market/heatmap（menu:market） |
| 交易申报 | GET/POST /trade/declarations、POST /trade/declarations/{id}/submit、GET /trade/results、GET /trade/positions、GET /trade/rolling-plans（menu:trade） |
| 辅助决策 | POST /decision/sessions、GET /decision/sessions/{id}、POST /decision/sessions/{id}/confirm、POST /decision/sessions/{id}/modify、GET /decision/sessions/{id}/evidence（menu:decision） |
| 结算管理 | GET /settlement/records、POST /settlement/records/{id}/reconcile、GET /settlement/tickets、POST /settlement/tickets/{id}/process（menu:settlement） |
| 结算单识别 | POST /ocr/tasks（multipart）、GET /ocr/tasks、GET /ocr/tasks/{taskId}、POST /ocr/tasks/{taskId}/review（复核确认/修正字段）（menu:settlement） |
| 复盘考核 | POST /review/reports、GET /review/reports/{id}、POST /review/strategy-feedback、GET /assessment/indicators、GET /assessment/results、POST /assessment/appeals、POST /assessment/appeals/{id}/process（menu:review） |
| 报表中心 | GET /report/templates、GET /report/instances、POST /report/instances、GET /report/instances/{id}/export（menu:report） |
| 情报中心 | GET /intel/news、GET /intel/sources、GET/POST /intel/push-rules（menu:intel） |
| 政策中心 | GET /policy/list、GET /policy/{id}、POST /policy/parse（幂等/reparse）、GET /policy/{id}/brief（CSV 导出）（menu:policy） |
| 消息中心 | GET /message/list（msgType/unreadOnly）、POST /message/{id}/read（menu:message） |
| 数据底座 | GET /data/sources、POST /data/collect-tasks（force）、GET /data/quality/report、GET /data/lineage（menu:data） |
| 预测中心 | POST /forecast/tasks、GET /forecast/tasks/{taskId}、GET /forecast/results（96 点含置信区间）、GET /forecast/models、POST /forecast/models/train（menu:forecast） |
| 联合优化 | POST /optimize/joint-tasks、GET /optimize/joint-tasks/{taskId}、POST /optimize/backtests、GET /optimize/strategies（menu:optimize） |
| 模型平台 | GET /model/registry、POST /model/inference、POST /model/evaluate（menu:model） |
| 审批流 | POST /flow/start（幂等同单据）、GET /flow/instances/{instanceId}（menu:flow） |

所有 /admin/* 接口需 `menu:admin` 权限（类级 @RequiresPermissions + 切面校验）；业务模块接口需对应 `menu:market` / `menu:trade` / `menu:decision` / `menu:settlement` / `menu:review` / `menu:report` / `menu:intel` / `menu:policy` / `menu:message` / `menu:data` / `menu:forecast` / `menu:optimize` / `menu:model` / `menu:flow` 权限。

## 文档索引

| 文档 | 路径 |
|---|---|
| 开发基线 V1.6（含 8.2.1 里程碑契约、修订记录） | `开发过程规划文档_电力交易智能辅助决策系统_开发基线V1.6.md` |
| PRD V1.1 | `PRD_电力交易智能辅助决策系统_v1.1.md` |
| DDL 基线 v1.0.3（落库修订条目 6-13 登记在文件头） | `docs/ddl/01_postgres_schema.sql` |
| 数据字典 V1.0 | `docs/数据字典_全量_V1.0.md` |
| OpenAPI 契约 V1.1（89 operationId，redocly lint 0 error） | `docs/openapi/` |
| 多省 region 路由与数据隔离方案 V1.2 | `docs/多省region路由与数据隔离方案.md` |
| 评审记录 / 勘误申请清单 | `docs/ddl/05_评审记录.md`、`docs/ddl/08_勘误申请清单_v1.0.1.md` |
