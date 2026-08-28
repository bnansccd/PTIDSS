# PRD / 基线差异检查报告（M7 移动端里程碑）

- 版本：V1.0（M7 启动基线检查，对应交付基线 V2.5）
- 日期：2026-08-22
- 检查依据：《PRD v1.1》《SRS V1.0》《开发基线 V1.6（8.1/8.2/8.2.1/8.5）》《OpenAPI 契约 V1.0/V1.1》
- 检查对象：现有后端接口（25 Controller）、Web 前端（20 视图）、移动端（无）

---

## 1. 检查范围与方法

| 维度 | 基线要求 | 检查方式 |
|---|---|---|
| 需求覆盖 | SRS 37 项功能需求 + PRD 23 项 FR + RE-01~09 | 逐项对照接口清单与页面清单 |
| M7 移动端 | 基线 8.1 里程碑、WBS 9.1、PRD FR-PL-05、SRS 9.2 决议④ | 专项核对（第 3 节） |
| 接口契约 | C3 62 path（OpenAPI V1.0/V1.1） | 25 Controller 逐 controller 核对 |
| 数据契约 | C2 51 表（DDL 01~15） | 沿用 V2.5 数据库核对结论 |
| 页面契约 | 原型 V1.1 + NFR-26 响应式（Web 1920/1366、小程序 750rpx） | 20 视图清单 + 移动端目录检查 |

---

## 2. 需求—接口—页面覆盖矩阵（SRS 37 项功能需求）

| 需求编号 | 优先级 | 后端接口（已存在） | Web 页面（已存在） | 状态 |
|---|---|---|---|---|
| FR-PD-01 政策文件管理 | P0 | /policy/upload、/policy/list、/policy/{id}、/policy/{id}/file | PolicyView | ✅ |
| FR-PD-02 政策智能解析 | P0 | /policy/parse | PolicyView | ✅ |
| FR-PD-03 政策影响研判 | P1 | /policy/{id}/brief | PolicyView | ✅ |
| FR-PD-04 数据源与采集任务 | P0 | /data/sources、/data/collect-tasks | DataManageView | ✅ |
| FR-PD-05 数据质量与血缘 | P1 | /data/quality/report、/data/lineage | DataPlatformView | ✅ |
| FR-TR-01 现货电价预测 | P0 | /forecast/tasks、/forecast/results | ForecastView | ✅ |
| FR-TR-02 负荷预测 | P0 | /forecast/tasks（类型覆盖） | ForecastView | ✅ |
| FR-TR-03 新能源出力预测 | P0 | /forecast/tasks（类型覆盖） | ForecastView | ✅ |
| FR-TR-04 预测任务与调度 | P0 | /forecast/tasks/{taskId} | ForecastView | ✅ |
| FR-TR-05 模型版本与评估 | P0 | /forecast/models、/forecast/models/train、/model/evaluate、/model/registry | ModelView | ✅ |
| FR-TR-06 预测结果可视化 | P0 | /forecast/results | ForecastView | ✅ |
| FR-DM-01 决策会话与编排 | P0 | /decision/sessions、/decision/sessions/{id} | DecisionView | ✅ |
| FR-DM-02 多智能体协同仲裁 | P0 | /agent/registry、/agent/runs | AgentView | ✅ |
| FR-DM-03 联合优化 | P0 | /optimize/joint-tasks | OptimizeView | ✅ |
| FR-DM-04 现货报价方案 | P0 | /optimize/strategies | DecisionView | ✅ |
| FR-DM-05 情景模拟与风险度量 | P0 | /optimize/backtests、/decision/sessions/{id} | OptimizeView | ✅ |
| FR-DM-06 依据链可解释性 | P0 | /decision/evidence | DecisionView | ✅ |
| FR-DM-07 人工干预双人复核 | P0 | /decision/confirm、/modify、/reject、/rerun | DecisionView | ✅ |
| FR-PL-01 日滚动方案管理 | P0 | /trade/rolling-plans | TradeView | ✅ |
| FR-PL-02 申报单管理 | P0 | /trade/declarations（GET/POST/PUT/submit） | TradeView | ✅ |
| FR-PL-03 合规预检 | P0 | /trade/declarations 预检联动 | TradeView | ✅ |
| FR-PL-04 成交结果跟踪 | P0 | /trade/results | TradeView | ✅ |
| FR-PL-05 持仓管理 | P0 | /trade/positions | TradeView | ✅ |
| FR-ST-01 结算记录与台账 | P0 | /settlement/records | SettlementView | ✅ |
| FR-ST-02 结算单 OCR | P0 | /ocr/tasks、/ocr/tasks/{id}/review | SettlementView | ✅ |
| FR-ST-03 结算核对与差异工单 | P0 | /settlement/tickets、/records/{id}/reconcile | SettlementView | ✅ |
| FR-RS-01 复盘报告自动生成 | P0 | /review/reports（GET/POST）、/{id} | ReviewView | ✅ |
| FR-RS-02 偏差三层归因 | P0 | /review/reports/{id}（归因明细） | ReviewView | ✅ |
| FR-RS-03 策略回流 | P1 | /review/strategy-feedback | ReviewView | ✅ |
| FR-AS-01 考核指标体系与评分 | P1 | /assessment/indicators、/results | AssessView | ✅ |
| FR-AS-02 考核申诉 | P1 | /assessment/appeals、/{id}/process | AssessView | ✅ |
| FR-RP-01 报表中心 | P1 | /report/templates、/instances、/{id}/export | ReportView | ✅ |
| FR-RP-02 报送格式支持 | P1 | /report/templates（格式配置） | ReportView | ✅ |
| FR-INT-01 消息中心 | P0 | /message/list、/{id}/read | MessageView | ✅（小程序通道缺失，见 D1） |
| FR-INT-02 流程引擎集成 | P0 | /flow/start、/instances/{id}、/advance、/definitions | FlowView | ✅（移动端审批缺失，见 D1） |
| FR-INT-03 审计与权限 | P0 | /system/admin/*、/auth/current | AdminView | ✅ |
| FR-INT-04 情报中心 | P0 | /intel/news、/push-rules、/fetch | IntelView | ✅ |

**核对结论**：SRS 37 项功能需求在 Web 侧全部有接口与页面承载；**除移动端双通道（FR-INT-01）与移动端审批（FR-INT-02/决议④）外，无其他 Web 侧缺失项**。

### PRD 23 项 FR 对照（摘要）

PRD v1.1 全部 7.x 功能章节（FR-PD-01~03、FR-TR-01~06、FR-RS-01/02、FR-DM-01~07、FR-PL-01~05、FR-INT-01/02）已由 SRS 逐条细化并落位（见上表），PRD 层无遗漏。RE-01~09 中：RE-01/04/05/06 已落位（FR-INT-04、FR-DM-05、FR-TR-01、FR-RS-03），RE-07 移动端行情订阅（P1）→ **落位于 M7（D1）**，RE-02/03（P1）、RE-08/09（P2）属远期不在本期。

---

## 3. M7 移动端专项检查

### 3.1 基线定义

- 基线 8.1 里程碑总览：**M7 移动端小程序与正式上线（第 33-38 周）**
- WBS 9.1：小程序开发（行情/预警/审批/复盘）
- 三层映射：FR-PL-05（PRD §7.6 移动端小程序）→ WBS 9.1 → M7
- SRS 9.2 评审决议④：小程序含**审批操作**（紧急场景业务正常支撑），**不含申报操作**
- NFR-26：小程序 750rpx 适配；NFR-09：预警推送延迟 ≤30s
- SRS 5.1 外部接口：微信小程序服务端（双向 HTTPS/REST，行情/消息/审批）

### 3.2 PRD FR-PL-05 五模块 vs 现有接口覆盖

| 小程序模块 | PRD 要求 | 依赖接口（已存在） | 覆盖 |
|---|---|---|---|
| ① 行情速览 | 现货/中长期价格、供需形势 | /market/price/spot、/market/price/midlong、/market/supply-demand、/market/heatmap | ✅ |
| ② 预警推送 | 价格异动/预测偏差/结算异常/考核提醒 | /message/list、/message/{id}/read（msg_type 分类：market_alert/forecast_summary/settlement_diff/assess_reminder/decision_todo） | ✅ |
| ③ 决策审批 | 移动端完成策略确认、申报审批 | /flow/biz-types、/flow/biz-options、/flow/start、/flow/instances/{instanceId}、/flow/instances/{instanceId}/advance | ✅ |
| ④ 复盘摘要 | 查看与分享 | /review/reports、/review/reports/{id} | ✅ |
| ⑤ 个人中心 | 账户/消息/订阅设置 | /auth/login、/auth/current、/auth/logout、/message/list（未读角标） | ✅ |

**接口层结论**：后端 API 已 100% 覆盖小程序 5 模块所需数据，**小程序为纯前端增量，无需新增后端接口**（订阅消息通道除外，见 D3）。

### 3.3 移动端现状

- 仓库内 **无 mini-program / mobile / wechat 目录**，无任何移动端代码 → 从零开发（D1）。

---

## 4. 基线契约 C1~C6 检查

| 契约 | 内容 | 检查结论 |
|---|---|---|
| C1 需求基线 | 37 项功能（P0×30 + P1×7）+ 33 NFR + RE-01~09 | Web 侧全落位；移动端 FR-INT-01/FR-PL-05 待 M7 落地 |
| C2 数据库契约 | 51 表（DDL 01~15） | V2.5 已核对通过，M7 不新增表（审批流/消息表已存在） |
| C3 接口契约 | 62 path | 25 Controller 全部在册；M7 不新增接口 |
| C4 多省 RLS | region_code 数据隔离 | 已实现（X-Region-Code 头 + SecurityUtils.checkRegionAccess），小程序需携带 |
| C5 多省配置化 | 市场/区域配置化接入 | 已实现（14_market_regions_v2_4），小程序登录后取 currentRegion |
| C6 配置化决议 | 双通道/双口径/多省 P0 | 已实现；小程序登录沿用同一账号体系 |

---

## 5. 差异清单与处置

| 编号 | 严重度 | 差异项 | 基线依据 | 处置 |
|---|---|---|---|---|
| D1 | **高** | **移动端小程序完全缺失**（无任何代码） | 基线 8.1 M7、WBS 9.1、PRD FR-PL-05、SRS FR-INT-01 双通道、RE-07、NFR-26 | **M7 开发**：新建 `mini-program/` 原生微信小程序，5 个 Tab 模块，对接既有 REST API |
| D2 | 中 | 小程序登录后审批权限约束：/flow/** 需 `menu:flow` 权限，无权限用户移动端仅可查看流程实例不可办理 | SRS 9.2 决议④（含审批操作）、FR-INT-03 | 小程序按权限动态渲染审批 Tab；文档说明账号需授予 menu:flow |
| D3 | 低 | 微信订阅消息推送通道未落地（真实订阅消息需微信公众平台资质/模板 ID） | SRS FR-INT-01（双通道）、5.1 外部接口 | 小程序端以**轮询 /message/list** 实现实时提醒（≤30s 对齐 NFR-09）；真实订阅消息列为上线后动作 |
| D4 | 低 | 小程序请求须携带 `X-Region-Code` 区域头（多省路由） | C4/C5、NFR-21 | 小程序请求封装默认注入登录响应 `currentRegion`，个人中心可切换 |
| D5 | 无 | Web 前端 20 视图覆盖 SRS 37 项完整，无缺失 | — | 无需处置（核对通过项） |
| D6 | 无 | 后端接口覆盖完整；但核对中发现流程实例与决策会话**缺列表查询能力**（原仅详情/推进、无列表），移动端无法呈现待办列表 | — | M7 补充 `GET /flow/instances`（scope=todo/started/all 分页）、`GET /decision/sessions`（按 humanReviewStatus 分页） |

---

## 6. 结论

1. **唯一实质差异**为 D1：M7 移动端小程序从零开发，涉及行情速览/预警推送/决策审批/复盘摘要/个人中心 5 模块。
2. 后端接口（25 Controller、62 path）与 Web 前端（20 视图）对 SRS 37 项功能需求与 PRD 23 项 FR 的覆盖**完整**，无需补齐 Web 页面；M7 开发中发现流程实例/决策会话缺列表查询接口，补充 `GET /flow/instances` 与 `GET /decision/sessions` 两个 GET 接口（同时惠及 Web 端）。
3. D2~D4 为小程序实现期必须遵守的对接约定（权限、订阅消息降级、区域头），随 M7 开发一并落实。
4. M7 开发完成后需补充：部署包纳入 mini-program 目录、小程序使用说明文档、接口回归验证。

---

## 7. M7 开发执行计划（对应本报告处置）

| 步骤 | 内容 | 产出 |
|---|---|---|
| 1 | 小程序工程骨架：app.js/app.json/app.wxss、utils/request.js（Bearer + X-Region-Code + context-path /ptidss）、tabBar 5 Tab | mini-program/ |
| 2 | 登录页（账号密码 + 验证码开关兼容）+ 个人中心（/auth/current、未读统计、退出） | pages/login、pages/profile |
| 3 | 行情速览（/market/price/spot 96 点曲线、/market/price/midlong、/market/supply-demand、/market/heatmap） | pages/market |
| 4 | 预警推送（/message/list 分类筛选、未读角标、/message/{id}/read；decision_todo 跳转审批） | pages/message |
| 5 | 决策审批（/flow/biz-types 发起、/flow/instances 详情、/flow/instances/{id}/advance approve/reject，含 menu:flow 权限控制） | pages/flow |
| 6 | 复盘摘要（/review/reports 列表 + /review/reports/{id} 三层归因、分享） | pages/review |
| 7 | 验证：接口回归（复用 V2.5 回归脚本）+ 小程序代码静态校验 + 部署包纳入 mini-program | dist/ptidss-deploy-v2.6 |
