# PTIDSS 多省模型：region 参数路由与数据隔离方案

**版本**：V1.2（会签通过版·复审修订）
**编制**：2026-08-08　**会签**：数据组/架构组/开发组/产品组/测试组五组会签通过（《会签意见_多省region路由与数据隔离方案.md》）；**2026-08-08 复审**：RLS 决策由「首期不启用 + 三重补偿」调整为「**首期启用 RLS + 按机构过滤 + 业务数据按机构分库**」（多租户/审计独立诉求自始成立），见 5.1/5.8 与《会签意见》附录
**关联基线**：DDL v1.0.1（05_评审记录.md 锁定；勘误批次 DDL-ERR-2026-001 已执行，09_rls_policies.sql 已创建）｜数据字典 V1.1｜OpenAPI V1.0｜开发基线 V1.6（评审决议⑤）
**范围**：多省（region）模型下，请求链路中 region 参数的获取、传递、校验、路由，以及 PostgreSQL / TDengine / Doris / 缓存 / 报表 / 消息各层的数据隔离策略与全国推广演进路径。

---

## 一、背景与目标

SRS 评审决议⑤定义 **P0 多省市场**：系统须同时支撑江苏、上海、浙江等多省电力交易业务，并为全国推广演进预留能力。DDL v1.0 已将 `region_code` 落位 13 张核心业务表，并新增 `sys_region` 区域注册表（第 51 张表）。

本方案回答三个问题：

1. **参数路由**：前端如何告知后端"我在哪个省"？后端如何校验并下发到服务层？
2. **数据隔离**：各数据层（PG 关系库、TDengine 时序库、缓存、报表文件、消息推送）如何保证省间数据不串、不越权？
3. **演进路径**：从单省到多省到全国推广，配置与代码如何平滑演进？

## 二、区域标识与注册

### 2.1 编码规范

| 项 | 规范 | 示例 |
|---|---|---|
| 编码格式 | `CN-<行政区划前两位>`（GB/T 2260） | CN-32（江苏）、CN-31（上海）、CN-33（浙江） |
| 存储类型 | VARCHAR(16)，PG 与 TDengine 统一 | `region_code` |
| 语义 | 一个 region = 一个省级电力市场（含其交易中心） | — |

### 2.2 sys_region 注册表（DDL v1.0 10.5）

区域为**配置驱动**，非代码硬编码。`sys_region` 每行定义一个省级市场的接入参数：

| 字段 | 含义 | 种子值示例（07_seed_data.sql） |
|---|---|---|
| region_code / region_name | 区域编码 / 名称 | CN-32 江苏 |
| market_support | 支持市场类型 JSONB：`["spot","midlong","external"]` | 现货/中长期/外送 |
| exchange_channel | 交易中心对接通道：`rest` / `sftp` / `both`（评审决议①） | 江苏 both、浙江 sftp |
| settlement_period | 结算周期口径：`natural_month` / `trading_month`（评审决议③） | 浙江 trading_month |
| status | `enabled` / `disabled` / `pending` | 河南 disabled（未接入） |
| launch_order | 全国推广接入顺序 | 江苏 1 → 上海 2 → 浙江 3 → 北京 4 |

种子 5 省：江苏（enabled/both/natural_month）、上海（enabled/rest/natural_month）、浙江（enabled/sftp/trading_month）、北京（pending/rest/natural_month）、河南（disabled/rest/natural_month）。

### 2.3 用户授权（sys_user_region）

用户与区域为**多对多授权**（`sys_user_region`），登录后前端获得用户授权区域列表；未授权区域请求一律拒绝（HTTP 403）。契约 V1.0 `CurrentUser` 暂无 `regions` 字段，前端骨架以 `DEFAULT_REGIONS`（与种子 5 省对齐）兜底，契约 v1.1 应补齐（见九）。

## 三、总体架构

```
┌──────────────┐   X-Region-Code 头（axios 拦截器自动携带，见 4.1）
│  前端骨架     │ ──────────────────────────────┐
│ region store  │ ◀─ 登录后 user.regions / DEFAULT_REGIONS
└──────────────┘                               ▼
                                     ┌──────────────────┐
                                     │ 网关 / 认证鉴权中间件 │
                                     │ ① 解析 X-Region-Code │
                                     │ ② 校验 sys_region    │
                                     │ ③ 校验 sys_user_region│
                                     └────────┬─────────┘
                                              ▼
                                     ┌──────────────────┐
                                     │ 服务层 RegionContext │
                                     │ ThreadLocal（请求作用域） │
                                     │ ④ 业务方法取 region    │
                                     └────────┬─────────┘
                                              ▼
                       ┌──────────────┬──────────────┬─────────────┐
                       ▼              ▼              ▼             ▼
                 ┌──────────┐   ┌──────────┐   ┌─────────┐   ┌─────────┐
                 │ PostgreSQL│   │ TDengine │   │  Redis   │   │ 报表文件 │
                 │ RLS 行级 +│   │ TAG/子表  │   │ key 前缀  │   │ 目录按省 │
                 │ 13 表强制  │   │ 分库分片  │   │ 隔离      │   │ 隔离     │
                 │ region 过滤│   │ 隔离      │   │          │   │          │
                 └──────────┘   └──────────┘   └─────────┘   └─────────┘
```

**分层职责**：前端只负责"携带"；网关/中间件负责"校验与注入"；服务层只从 RegionContext 取值，**禁止从请求参数二次解析**（防绕过）；数据层负责"强制过滤或物理隔离"。

## 四、region 参数路由

### 4.1 传递规范（X-Region-Code 头）

- **头名称**：`X-Region-Code`，值为 `region_code`（如 `CN-32`）。
- **前端**：`src/api/http.ts` 请求拦截器在存在当前区域时自动携带；区域切换（MainLayout 顶栏）后即时生效（骨架阶段整页重载刷新数据）；`localStorage` 持久化（`ptidss_region_code`），刷新不丢。
- **后端**：网关解析头 → 校验（见 4.3）→ 注入 `RegionContext`；服务层 SQL 统一追加 `WHERE region_code = :region`（MyBatis-Plus 条件构造 / JDBC 参数绑定，**禁止字符串拼接**）。
- **全国视图约定**：不携带 `X-Region-Code` = 全国/管理视图，**仅限管理端角色（校验规则 5）**；携带 = 单省视图。`intel_news.region_code` 为空即全国情报，任何省份视图均可见。

### 4.2 契约声明（v1.1 必补）

OpenAPI V1.0 未声明该头（Prism Mock 忽略未定义头，冒烟通过）。契约 v1.1 应将其声明为**可选 header 参数**（统一用 `components/parameters` 定义，各业务 path 引用，避免逐 path 手写漂移）：

```yaml
parameters:
  - { name: X-Region-Code, in: header, required: false,
      schema: { type: string, example: CN-32 },
      description: 多省区域编码；缺省表示全国视图 }
```

### 4.3 校验规则（网关，按序执行）

| # | 校验 | 失败响应 | 说明 |
|---|---|---|---|
| 1 | 头格式匹配 `^CN-\d{2}$` | 400 | 防注入/畸形值 |
| 2 | `sys_region` 存在：`disabled` 拒绝；`pending` 允许**只读接口**（行情/预测查询），写操作拒绝 | 404/403 | 支持灰度接入（七章），与只读演练模式一致 |
| 3 | 当前用户经 `sys_user_region` 授权该区域 | 403 | 越权访问拦截 |
| 4 | 管理端全国视图（不带头）：校验用户具备全国/管理权限 | 403 | 防止普通角色绕过区域隔离 |
| 5 | 采集/结算**同步任务**通道能力与 `sys_region.exchange_channel` 匹配（rest/sftp/both） | 409 | 通道是任务级配置，非用户写操作前置校验（六章联动） |

> 会签修订（V1.0→V1.1）：①原校验 2 与七章 pending 灰度矛盾，拆分为 disabled 拒绝 / pending 只读；②原校验 4 将 exchange_channel 误设为用户写操作前置校验，改为同步任务级校验；③新增全国视图权限校验。

### 4.4 服务层 RegionContext

```java
// 请求作用域：网关校验通过后注入，业务层只读
public final class RegionContext {
    private static final ThreadLocal<String> REGION = new ThreadLocal<>();
    public static void set(String regionCode) { REGION.set(regionCode); }
    public static String get() { return REGION.get(); }
    public static void clear() { REGION.remove(); }
}
```

使用铁律：
1. **只读**：业务代码不得修改上下文；
2. **必带**：涉及 13 张核心表的查询/写入必须携带 `region_code` 条件——由 **MyBatis 拦截器（InnerInterceptor）统一追加**，机制保障而非纪律（会签修订）；缺省视为编程错误（CI 测试门禁断言，见十）；**RLS 策略为最终兜底**（5.1），应用层遗漏不会导致跨省泄露，但会产生告警/审计追踪
3. **异步**：异步任务/消息消费须显式传递 region——线程池用 `TransmittableThreadLocal`（TTL）穿透，MQ 消息头携带 `X-Region-Code`，定时任务（结算/采集）声明式指定 region，不得依赖裸 ThreadLocal 穿透线程池。

### 4.5 服务间调用 region 透传（会签新增）

微服务架构（基线 3.4：auth/policy/market/trade/settlement/review/data/report 等 8+ 服务）下，region 必须**全链路透传**：

- **Feign/RestTemplate 拦截器**统一透传 `X-Region-Code`（与网关同头名），内部调用禁止改写 region；
- 服务 A 调用服务 B 时若 B 侧未带 region，B 的拦截器直接拒绝（fail-fast），防遗漏；
- 分布式事务/事件（如申报→成交→结算链路）以事件头携带 region，消费端从事件头恢复上下文。

## 五、数据隔离方案

### 5.1 PostgreSQL 关系数据（RLS 行级安全 + 应用层按机构过滤）

**13 张核心表清单**（DDL v1.0 已全部落位 `region_code` + 索引）：

| 域 | 表 | region 语义 | 索引 |
|---|---|---|---|
| 发电与负荷 | plant_unit | 机组所属省 | ix_plant_unit_region |
| 交易 | contract | 合同市场归属 | ix_contract_region (region_code, status) |
| 交易 | rolling_plan | 滚动方案所属省 | ix_rolling_plan_region (region_code, trade_date) |
| 交易 | declaration（分区） | 申报市场所属省 | ix_declaration_region (region_code, trade_date) |
| 交易 | trade_result（分区） | 成交市场所属省 | ix_trade_result_region (region_code, trade_date) |
| 交易 | quote_plan | 报价市场所属省 | ix_quote_plan_region (region_code, trade_date) |
| 交易 | clearing_result | 出清市场所属省 | ix_clearing_result_region (region_code, trade_date) |
| 结算 | settlement_record（分区） | 结算单所属省 | ix_settlement_record_region |
| 结算 | settlement_ledger | 台账按省隔离 | ix_settlement_ledger_region (region_code, period) |
| 预测 | forecast_task | 预测目标区域 | ix_forecast_task_region (region_code, predict_date) |
| 预测 | forecast_result | 预测结果区域 | ix_forecast_result_region |
| 报表 | report_instance | 报表所属省 | ix_report_instance_region |
| 情报 | intel_news | **可空**：空=全国情报 | ix_intel_news_region |

**隔离策略（复审定案）**：**RLS 首期启用** + 应用层按机构过滤双轨强制：

- **RLS（数据库强制兜底）**：13 张核心表启用行级安全策略 `USING (region_code = current_setting('app.region_code'))`，网关在连接会话设置该变量；**即使应用层漏写 region 条件或 SQL 被绕过，数据库仍按会话区域强制过滤**，杜绝跨省数据泄露；
- **按机构过滤（应用层机制保障）**：MyBatis 拦截器统一追加 `region_code = ?` 条件（防遗漏），走联合索引（region_code 前缀），月分区表按 region + 时间裁剪；
- 每服务独立数据库账号（防超级用户绕过 RLS），账号仅授予本服务所需权限。

**RLS 决策矩阵（评审记录遗留待定项 3，2026-08-08 复审定案）**：

| 维度 | 首期启用 RLS（定案） | 说明 |
|---|---|---|
| 多租户/审计诉求 | **自始成立**（多省即多租户，等保三级审计须按省检索隔离） | 不再等待外部强制诉求，按内控高标准启动 |
| 省份规模 | 首期 3 省 enabled（江苏/上海/浙江）即启用 | 策略按 region_code 统一模板，新增省份零成本生效 |
| 性能 | 每查询增加一次策略评估（PG 内置，经压测预算内） | 首期启用 + 性能测试基准（十）在 P0 窗口内完成 |
| 运维 | 每服务独立数据库账号 + 策略变更走 DDL 流程 | 账号体系随 8.2 数据库实施一并建立，非重构 |
| 兜底 | 应用层 MyBatis 拦截器 + CI SQL 门禁 + 网关 403 叠加 | 三层纵深，任一环节失效仍有其他防线 |

**结论（复审定案）**：**首期启用 RLS**，与**按机构过滤**（应用层强制）、**业务数据按机构分库**（5.8）组成纵深防御组合：RLS 兜底行级、应用层保证查询效率与索引利用、分库保证物理/存储层隔离。策略模板：

```sql
ALTER TABLE contract ENABLE ROW LEVEL SECURITY;
CREATE POLICY contract_region_policy ON contract
  USING (region_code = current_setting('app.region_code', true));
-- 13 张核心表逐一启用；intel_news 策略附加全国可见规则：
-- USING (region_code = current_setting('app.region_code', true) OR region_code IS NULL)
```

由网关在连接会话执行 `SET app.region_code = 'CN-32'`；**管理端全国视图**以独立账号 + 独立策略（或无策略表授权）承载，普通服务账号不可见全国数据。策略脚本**已随 v1.0.1 勘误批次新增**（09_rls_policies.sql，勘误清单条目 5 已执行，2026-08-08）。

### 5.2 TDengine 时序数据（TAG/子表隔离，业务数据按机构分库）

8 张超级表 region 现状与隔离策略：

| 超级表 | region 落位 | 子表划分 | 隔离策略 | 评估 |
|---|---|---|---|---|
| st_spot_price | **TAG** region_code（v1.0.1 已勘误提升，子表必填） | market_type_stage_region_code | 子表天然隔离 + TAG 过滤 | ✅ 已执行（DDL-ERR-2026-001 条目 1） |
| st_midlong_price | **TAG** region_code + 普通列 market_center | variety_region_code | 子表天然隔离 + TAG 过滤 | ✅ 已执行（DDL-ERR-2026-001 条目 2） |
| st_supply_demand | 列 region_code NOT NULL | **region_code** | 子表天然隔离 ✓ | 优 |
| st_load | **TAG** region_code | region_code_forecast_flag | TAG 过滤 + 子表天然隔离 ✓ | 优 |
| st_generation | 无（机组属性） | unit_code | 经 plant_unit.region_code 映射过滤 | 可接受（机组属省不可变） |
| weather_data | 无 | source_grid_point | **全国共享**（气象网格不分省） | 设计如此 ✓ |
| st_forecast_series | 无（明细） | task_id 等 | 经 forecast_result.region_code → task_id 关联过滤 | 可接受（元数据在 PG） |
| st_intel_news_ts | 无（时序） | source_code_intel_type | 经 intel_news.region_code 关联过滤 | 可接受 |

**复审定案（业务数据按机构分库）**：TDengine 层即**按机构（region）分库/分片**的实现载体——`st_spot_price`、`st_midlong_price` 的 `region_code` 由普通列提升为 **TAG**（子表划分加入 region 维度），实现「**超级表统一管理 + 按省子表物理分片 + 子表级保留策略与扩容**」，此为本方案 5.8 分库组合中**必须执行项**（非可选演进），**已随 DDL v1.0.1 勘误批次执行**（未实施期成本最低，勘误申请清单条目 1/2 已完成，02_tdengine_schema.sql 标注 v1.0.1 勘误版）。`st_forecast_series` 保持经 `forecast_result` 关联过滤（压测验证性能后定）。

### 5.3 Doris 分析层隔离（会签新增）

基线数据架构含 Doris 数仓层（DWS 预聚合：日/周/月/年 × 市场/品种/时段），**必须与业务层同规隔离**：

- DWS 预聚合表**分区键/聚合键含 `region_code`**（如 `dw_spot_price_daily(region_code, trade_date, market_type, stage)`），看板查询强制按 region 过滤；
- 全国性指标（如跨省价差分析）走显式全国聚合表（region_code = 'global'），不允许从省表中联表聚合绕过；
- 物化视图刷新任务按 region 分批，避免单省数据异常阻塞全局刷新。

### 5.4 缓存隔离（Redis）

- **Key 命名**：`region:{region_code}:{业务}:{参数}`（如 `region:CN-32:spot_price:2026-08-08`）；全国性数据用 `region:global:{业务}:{参数}`。
- **失效策略**：省间 Key 互不影响；写操作按 region 前缀精确失效，禁止全量 flush。
- **热点防护**：区域切换（如结算日高峰）预热 `region:CN-33:*` 时不影响其他省。

### 5.5 报表/文件隔离

- `report_instance.region_code` 落库（已补）；报表文件存储路径按省分目录：`reports/{region_code}/{report_no}/`。
- 文件服务下载接口校验请求区域与文件元数据区域一致，防止跨省 URL 猜测下载。
- 批量导出任务：任务记录携带 region，产出物按省目录归档。

### 5.6 消息/推送隔离

- `message_record` 按**接收人**（receiver_id）路由，不直接挂 region；区域相关消息（结算差异、审批待办）经 `biz_ref` 关联区域对象，消费侧按用户订阅区域过滤。
- 推送通道（web/miniapp）按用户授权区域下发；跨省用户（如集团管理员）可订阅多省，前端按当前区域上下文过滤展示。
- `intel_news` 全国情报（region_code 为空）向所有省广播；省级情报只向该省推送。

### 5.7 跨省共享数据（全国维度）

| 数据 | 共享方式 |
|---|---|
| policy_document / policy_article / rule_config | 全国/全省通用规则，**不分省**（规则可含 region 条件字段，如适用省枚举） |
| weather_data | 全球网格，不分省（按网格点取数） |
| sys_user / sys_role / sys_permission | 平台级，不分省；区域授权经 sys_user_region |
| intel_news（region_code 为空） | 全国广播 |
| data_source / collect_task / intel_source | 平台级配置，可按 region 归属（如各省交易中心通道配置） |

### 5.8 业务数据按机构分库（复审新增，纵深防御第三层）

「业务数据按机构（region）分库」为复审定案的隔离组合第三层，**各数据层按机构物理/逻辑分片**，与 RLS（5.1）、按机构过滤（4.4）叠加：

| 数据层 | 分库/分片实现 | 状态 |
|---|---|---|
| TDengine 时序 | 超级表 + **region TAG → 按省子表物理分片**（st_spot_price / st_midlong_price 勘误后；st_load / st_supply_demand 已按 region 子表） | **已执行**（v1.0.1 勘误条目 1/2） |
| Doris 分析层 | DWS 聚合表分区/分桶键含 region_code（5.3）；全国指标走显式 global 表 | 首期执行 |
| PostgreSQL | RLS 行级隔离 + 按月分区表（region 前缀索引裁剪）；**不拆物理库实例**（保留全国聚合与跨省报表能力） | 首期执行；**拆实例/拆 schema 为全国推广期演进项**（数据量超单实例容量预算时评估，见 3.10） |
| Redis 缓存 | key 前缀 `region:{region_code}:`（5.4）逻辑分片 | 首期执行 |
| 报表/文件 | 存储目录按省 `reports/{region_code}/`（5.5）物理分目录 | 首期执行 |

> 说明：原 V1.1 否决项「按省拆分数据库/实例」在复审中**部分采纳**——TDengine/Doris 按省分片（物理隔离收益高、成本可控）即期执行；PG 拆实例因破坏全国聚合与跨省报表链路，保留为演进项，以 RLS + 分区分表满足首期隔离诉求。

## 六、区域化配置差异（评审决议①③的联动）

| 配置项 | 字段 | 联动点 |
|---|---|---|
| 结算周期口径 | sys_region.settlement_period | 结算任务按省口径生成 settlement_period；**优先级：省配置 > 全局配置 `settlement.periodMode`（评审决议③，作为新省默认值）**；浙江 trading_month 与江苏 natural_month 并存（前端 SettlementView 已按区域展示口径） |
| 交易中心通道 | sys_region.exchange_channel | 申报/出清/结算同步任务按省选择 rest/sftp 通道（校验规则 5）；通道切换不影响其他省 |
| 支持市场类型 | sys_region.market_support | 现货/中长期/外送能力按省开关；北京仅中长期（midlong），现货相关菜单/任务对该省禁用 |

> 会签定案：**省间交易（market_type=inter_province / market_support=external）归属送端省份**（发起省），省间成交记录在送端省台账；跨省价差分析走 Doris 全国聚合表（5.3），不改变业务归属。

## 七、全国推广演进路径

```
单省（现状基线）→ 多省配置化（sys_region 驱动，本方案）→ 全国推广（launch_order 排序分批接入）
```

| 阶段 | 动作 | 条件 |
|---|---|---|
| 接入新省 | 1) sys_region 插入（status=pending）；2) 配置交易中心通道与结算口径；3) 导入机组档案；4) 数据采集任务按省启动；5) 试运行后 status=enabled | 评审决议⑤ P0 |
| 灰度 | launch_order 确定接入顺序；新省以"只读/演练"模式（status=pending）先行验证行情与预测 | 前端对 pending 区域仅展示行情类只读页 |
| 推广 | 全部省份 enabled 后评估 RLS、缓存扩容、TDengine region TAG 演进 | 本方案 5.1/5.2 决策矩阵（**复审后：RLS 已首期启用，本行仅评估容量扩容与 TAG 扩展**） |

**下线**：`status=disabled` 即停用（河南示例）；存量数据保留（逻辑删除维度为 region，不物理清理），支持重新启用。

## 八、与 DDL v1.0 / 契约 / 前端骨架的映射表

| 本方案要素 | DDL v1.0 | OpenAPI 契约 | 前端骨架 |
|---|---|---|---|
| 区域注册 | sys_region（10.5） | 管理接口待 v1.1 | AdminView 展示配置卡片 |
| 用户授权 | sys_user_region | CurrentUser.regions（v1.1 待补） | region store `init(user.regions)`，缺省 DEFAULT_REGIONS |
| 头传递 | — | X-Region-Code 声明（v1.1 待补） | http.ts 拦截器自动携带 |
| PG 13 表隔离 | region_code + 索引（01_postgres_schema.sql） | 列表接口按区域过滤 | 各页请求自动带区域头 |
| TDengine 隔离 | TAGS/子表（02_tdengine_schema.sql） | 行情/供需接口 | MarketView 查询 |
| 结算口径 | settlement_period 字段 | — | SettlementView / AdminView 按区域展示 |
| 通道配置 | exchange_channel | — | AdminView 展示 |

## 九、风险与待办

| # | 项 | 类型 | 处置 |
|---|---|---|---|
| 1 | 契约 V1.0 未声明 X-Region-Code 头与 CurrentUser.regions | 契约缺口 | v1.1 补齐（4.2 模板）；当前 Mock 忽略未定义头、前端兜底可运行 |
| 2 | st_spot_price / st_midlong_price region 为列非 TAG | 性能演进 | **v1.0.1 勘误提升为 TAG**（5.2，已执行） |
| 3 | ocr_task 未按省落 region_code | 待定（评审记录遗留项 1） | 当前经 settlement_record 回溯；多省结算单量级验证后决策 |
| 4 | RLS 启用决策 | **已定案**（评审记录遗留项 3） | **首期启用 RLS + 按机构过滤 + 业务数据按机构分库**（2026-08-08 复审调整，5.1/5.8）；策略脚本已随 v1.0.1 勘误批次落地（09_rls_policies.sql，已执行） |
| 5 | 异步任务 region 透传 | 实现约束 | TTL 线程池 + MQ 头 + 定时任务声明式指定（4.4 铁律 3 / 4.5） |
| 6 | 省间数据迁移（如合同跨省转让） | 业务待定 | 走 contract.region_code 变更 + 审计记录（audit_log 快照）；省间交易归属送端省（六章会签定案） |

## 十、跨省隔离测试策略（会签新增）

| 层 | 用例要点 | 归属 |
|---|---|---|
| 单元 | RegionContext 注入/清理（线程池泄漏回归）；TTL 穿透 | 开发组 |
| 集成 | 同接口双省返回隔离数据；写操作跨省引用校验 | 开发组 |
| 安全 | 未授权区域 403；全国视图越权 403；pending 只读/写分离；畸形头 400 | 测试组 |
| **RLS 兜底（复审新增）** | **绕过应用层直连数据库**（用服务账号不带 region 条件查询）验证 RLS 策略强制过滤；`SET app.region_code` 切换会话验证行级隔离；全国视图账号与普通服务账号数据可见性差异；超级用户账号（绕过 RLS 的 BYPASSRLS）仅限 DBA | 测试组/数据组 |
| 性能 | region 索引裁剪（PG）；TAG 过滤（TDengine）；Doris 分区裁剪；**RLS 策略评估开销压测**（P95 预算内，基线 3.10） | 测试组/数据组 |
| 前端 E2E | 切区后数据刷新断言；DEFAULT_REGIONS 兜底断言；结算口径随区更新 | 测试组 |
| CI 门禁 | 13 张核心表 SQL 必含 region_code 条件（静态扫描/测试断言，与 RLS 互为纵深） | 开发组 |

---

**会签结论**：数据组/架构组/开发组/产品组/测试组五组会签通过（有条件通过后修订，意见已全部落位，见《会签意见_多省region路由与数据隔离方案.md》）；**2026-08-08 复审调整 RLS 决策为首期启用（含按机构过滤与业务数据按机构分库），方案升 V1.2 会签通过版·复审修订，随 8.2.1 里程碑实施契约执行**（评审记录遗留项 3 定案）。
