# PTIDSS 移动端小程序（M7 里程碑）

电力交易智能辅助决策系统移动端微信小程序，对应开发基线 **M7 移动端小程序与正式上线（WBS 9.1）**，覆盖 PRD FR-PL-05 五模块：

| 模块 | Tab | 页面 | 依赖接口 |
|---|---|---|---|
| ① 行情速览 | 行情 | pages/market | /market/price/spot、/market/price/midlong、/market/supply-demand、/market/heatmap |
| ② 预警推送 | 预警 | pages/message | /message/list、/message/{id}/read（30s 轮询对齐 NFR-09） |
| ③ 决策审批 | 审批 | pages/flow、pages/flow-detail | /flow/biz-types、/flow/biz-options、/flow/start、/flow/instances、/flow/instances/{id}、/flow/instances/{id}/advance |
| ④ 复盘摘要 | 复盘 | pages/review、pages/review-detail | /review/reports、/review/reports/{id}（支持分享） |
| ⑤ 个人中心 | 我的 | pages/profile | /auth/login、/auth/current、/auth/logout、/message/list（未读角标）、区域切换、订阅设置 |

范围边界（SRS 9.2 评审决议④）：小程序含**审批操作**，**不含申报操作**。

## 运行环境

- 微信开发者工具（基础库 ≥ 2.33.0，已配置 `touristappid` 游客模式，无需注册 appid 即可预览）
- 后端：PTIDSS V2.6（Spring Boot 2.7 + PostgreSQL），context-path `/ptidss`

## 接入步骤

1. 打开微信开发者工具 → 导入项目 → 选择本目录 `mini-program/`；
2. 修改 [utils/request.js](utils/request.js) 顶部 `BASE_URL`：
   - 本地联调：`http://localhost:9080/ptidss`（需勾选"不校验合法域名"）
   - 生产环境：`https://<域名>/ptidss`，并在微信公众平台配置 request 合法域名；
3. 编译运行，使用 Web 端同一账号登录（测试环境默认 admin / Ptidss@2026）；
4. 登录成功自动进入行情速览，底部 5 个 Tab 对应五模块。

## 对接约定

- **认证**：登录后令牌存本地，请求自动携带 `Authorization: Bearer {accessToken}`；令牌失效（14001）自动跳转登录页；滑动续期响应头 `X-New-Token` 自动替换本地令牌。
- **多省路由**：请求自动携带 `X-Region-Code`（登录响应 `currentRegion` 默认值），个人中心可切换区域（C4/C5 契约）。
- **验证码**：服务端 `ptidss.captcha.enabled=false` 时直接登录；开启时登录页点击"遇到验证码？"加载验证码（GET /auth/captcha）后重试。
- **审批权限**：`/flow/**` 需 `menu:flow` 权限（FR-INT-03），无权限账号审批 Tab 仅可查看列表与详情，办理时服务端拒绝并提示。建议移动审批角色授予 `menu:flow`。
- **实时预警**：以消息中心轮询（30s）实现，对齐 NFR-09 ≤30s；真实微信订阅消息推送需微信公众平台资质与模板 ID，属上线后动作（差异报告 D3）。

## 目录结构

```
mini-program/
├── app.js / app.json / app.wxss     # 全局：登录态、tabBar、样式（750rpx）
├── project.config.json              # 开发者工具项目配置
├── utils/
│   ├── request.js                   # 请求封装（Bearer + X-Region-Code + 401 处理 + 滑动续期）
│   └── util.js                      # 日期/金额格式化 + 编码→名称映射
└── pages/
    ├── login/                       # 登录（验证码自适应）
    ├── market/                      # 行情速览（canvas 96 点曲线/供需/中长期/热力图）
    ├── message/                     # 预警推送（分类/未读/30s 轮询）
    ├── flow/                        # 决策审批（待办/我发起/全部 + 发起流程）
    ├── flow-detail/                 # 审批详情（环节进度/留痕/通过/驳回）
    ├── review/                      # 复盘摘要（类型筛选/分享）
    ├── review-detail/               # 复盘详情（三层归因/策略评估/建议）
    └── profile/                     # 个人中心（区域切换/未读/订阅设置/退出）
```

## 验证

- 接口层：小程序 5 模块依赖接口与 Web 端同源，回归脚本见 `tests/api_regression/`（V2.5）；
- M7 新增后端接口：`GET /flow/instances`（待办/我发起/全部，分页）、`GET /decision/sessions`（策略确认入口列表），已纳入 OpenAPI 契约与回归。
