# PTIDSS Web 前端骨架（Vue3 + Vite + TS）

电力交易智能辅助决策系统 Web 端骨架，对接 **OpenAPI V1.0 契约**（`docs/openapi/openapi.yaml`），开发期以 **Prism Mock Server**（:4010）联调。

## 快速开始

```bash
# 1. 安装依赖（node >= 22）
npm install

# 2. 启动开发服务器（需 Mock 已在 :4010 运行）
npm run dev
# 访问 http://127.0.0.1:5173

# 3. 类型检查
npx vue-tsc --noEmit

# 4. 生产构建
npm run build
```

登录：Mock 环境下任意账号密码（骨架默认 `trader01 / demo`）。

## 环境变量

| 变量 | 开发值 | 生产值 | 说明 |
|---|---|---|---|
| `VITE_API_BASE` | `http://localhost:4010` | `https://api.ptidss.example.com/api/v1` | API 基础地址 |

> Prism mock 忽略契约 `server` 的 base path，开发期**不带** `/api/v1` 前缀。

## 目录结构

```
frontend/
├── .env.development / .env.production   # VITE_API_BASE 按环境切换
├── vite.config.ts                        # @ 别名 → src
└── src/
    ├── api/
    │   ├── http.ts        # axios 封装：Bearer + X-Region-Code、ApiResponse 解包、14001 处理
    │   ├── types.ts       # 契约类型（ApiResponse / PageResult / CurrentUser …）
    │   ├── auth.ts        # /auth/login|refresh|current|logout
    │   ├── market.ts      # /market/price/spot|midlong、supply-demand、heatmap
    │   ├── trade.ts       # /trade/rolling-plans|declarations|results|positions
    │   ├── settlement.ts  # /settlement/records|tickets
    │   ├── decision.ts    # /decision/sessions、/optimize/joint-tasks|backtests|strategies
    │   └── intel.ts       # /intel/news|sources|push-rules
    ├── stores/
    │   ├── auth.ts        # token + 用户信息（localStorage 持久化）
    │   └── region.ts      # 多省区域上下文：X-Region-Code 头 + DEFAULT_REGIONS 兜底
    ├── router/index.ts    # 7 路由 + 登录守卫
    ├── layouts/MainLayout.vue  # 侧边栏 + 顶栏区域切换
    └── views/             # Login / Dashboard / Market / Trade / Decision / Settlement / Intel / Admin
```

## 与契约的挂接点（联调要点）

1. **统一响应**：`{ code, message, data, traceId }`；`code=0` 成功、`14001` 未认证 → 自动登出跳转登录。
2. **认证**：登录后所有请求自动携带 `Authorization: Bearer <token>`。
3. **多省路由**：切换区域后所有请求自动携带 `X-Region-Code` 头（评审决议⑤，后端据此路由与隔离）。
4. **分页**：契约 `PageResult` 字段为 `list / pageNo / pageSize / total`。

## 契约 V1.0 已知缺口（骨架已适配，v1.1 待补）

| 缺口 | 影响 | 骨架处理 |
|---|---|---|
| `CurrentUser` 无 `regions` 字段 | 无法获取用户授权区域 | `DEFAULT_REGIONS` 兜底（与 DDL v1.0 `sys_region` 种子 5 省对齐） |
| `/message/list` msgType 枚举缺 `approval_task`（DDL 已有） | 审批待办消息契约不可表达 | 驾驶舱已接 /message/list，前端兼容展示 |
| `/decision/sessions`、`/optimize/joint-tasks`、`/optimize/backtests` 仅 POST 无列表 GET | 决策页无法列历史 | 决策页改为"发起型"演示（POST 创建），列表 GET 待契约 v1.1 |
| `/ocr/tasks` 仅 POST | OCR 列表不可用 | 未接页面 |
| `X-Region-Code` 头未在契约声明 | 多省路由无契约契约 | 前端已携带，契约 v1.1 以 components/parameters 声明（方案 4.2） |

## 冒烟验证记录（2026-08-08）

| 端点 | 结果 |
|---|---|
| POST /auth/login、GET /auth/current | 200 |
| GET /market/price/spot（marketType/stage/startAt/endAt） | 200 |
| GET /trade/declarations | 200 |
| GET /settlement/records（period 必填）、/settlement/tickets | 200 |
| GET /intel/news、/intel/sources、/optimize/strategies、/forecast/models、/policy/list | 200 |
| GET /market/heatmap（startDate/endDate 必填） | 200 |
| GET /message/list（驾驶舱待办，含 approval_task 兼容） | 200 |
| POST /decision/sessions、/optimize/joint-tasks、/optimize/backtests | 200 |

> 422 排查记录：参数与契约不一致（如 `marketType=spot` 不在枚举、`scenarioCount` 缺失、`date` 应为 `startDate/endDate`）已全部对齐修复。
>
> 多角色阶段评估（2026-08-08）：/message/list 此前因端点清单检索范围遗漏被误判为缺失，已恢复封装与驾驶舱待办；切区刷新缺陷（onMounted 不重跑、periodMode 非响应式）已修复（整页重载 + computed）。详见 docs/阶段成果全面评估报告.md。
