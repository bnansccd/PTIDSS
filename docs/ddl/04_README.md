# DDL 基线 v1.0.3（电力交易智能辅助决策系统 PTIDSS）

配套《数据字典_全量_V1.0》（V1.1）｜开发基线 V1.6 5.6/5.7｜SRS V1.1（评审决议已同步）｜**05_评审记录.md（2026-08-08 数据组评审通过，正式锁定；v1.0.1 勘误批次 DDL-ERR-2026-001 已执行；v1.0.2 落库兼容修订 DDL-ERR-2026-002 开发环境已执行；v1.0.3 落库修订条目 12/13 开发环境已执行）**

## 文件说明

| 文件 | 内容 |
|---|---|
| `01_postgres_schema.sql` | PostgreSQL 16/18 业务表基线：**52 张表**（含 4 张按月/日分区父表、4 张关联表）+ 6 张分区子表（2026-08/09 示例）+ 索引/约束/注释；v1.0.2 落库兼容修订见 08 附录、v1.0.3 落库修订（条目 12/13）见文件头登记 |
| `02_tdengine_schema.sql` | TDengine 3.x 时序超级表：**8 张**（现货/中长期/供需/出力/负荷/气象/预测 96 点/情报时序）+ 子表划分规范 |
| `03_views.sql` | 视图：`position_view`（持仓聚合，LATERAL 防放大）、`settlement_pass_rate_view`（结算核对通过率） |
| `04_README.md` | 本说明 |
| `05_评审记录.md` | 数据组评审记录（v0.1→v1.0）：逐域勾稽、问题清单 9 项、变更明细、遗留待定项 |
| `06_enum_dict.sql` | 全库 CHECK 枚举值集中字典（开发/测试/验收参照） |
| `07_seed_data.sql` | 基线种子数据（幂等）：region 注册/角色权限/数据源/情报源/规则库/指标/模板/模型 |
| `08_勘误申请清单_v1.0.1.md` | **v1.0.1 勘误申请清单（另附，DDL-ERR-2026-001，5 项）**：st_spot_price / st_midlong_price region 列→TAG、audit_log 补 region_code、质量规则种子、**RLS 策略脚本**；由数据组走开发基线 7.5 变更管理流程执行（**已执行并回归，见文件七章执行记录**） |
| `09_rls_policies.sql` | **RLS 首期启用策略脚本（v1.0.1 勘误批次创建）**：13 张核心表 ENABLE RLS + 策略 USING region_code = current_setting + 每服务独立账号授权（方案 V1.2 5.1 复审定案） |

## 与基线挂接点

1. **数据字典一致性**：字段名/类型/约束与《数据字典_全量_V1.0》逐表对齐；通用约定（雪花 ID、乐观锁、软删除、NUMERIC 精度、JSONB 曲线、AES-GCM 敏感字段）全部落地；
2. **评审决议落位**（SRS 9.2）：
   - 决议① 交易中心双通道：`data_source.connect_config` 支持 REST/SFTP 双通道建模；`sys_region.exchange_channel` 按省配置；
   - 决议③ 结算双口径：`settlement_record.settlement_period` / `settlement_ledger.period` 口径随 `settlement.periodMode` 配置；`sys_region.settlement_period` 按省默认；
   - 决议⑤ 多省 P0：`region_code` 落位 **13 张核心表**（contract / rolling_plan / declaration / trade_result / quote_plan / clearing_result / settlement_record / settlement_ledger / forecast_task / forecast_result / report_instance / intel_news / plant_unit）+ `sys_user_region` 角色×区域双重授权 + `sys_region` 区域注册表；
   - 决议④ 移动端审批：`message_record.msg_type` 增加 `approval_task` 类型；
3. **验收支撑**：`settlement_pass_rate_view.pass_rate ≥ 0.95`（FR-ST-03）、`position_view` 支撑持仓约束与合规预检（FR-DM-03 R3，LATERAL 防聚合放大）；
4. **里程碑衔接**：本基线为 **8.2 里程碑（开发基线）→ 数据库实施** 的建表契约；2026-08-08 数据组评审通过转 v1.0 锁定，由数据组在开发/测试/生产三环境执行（基线 8.2、8.4）。

## 使用与评审要求

```bash
# 在 PostgreSQL 16/18 环境执行（顺序执行 01 → 03 → 09 → 07；02 在 TDengine 环境执行）
psql -h <host> -U ptidss -d ptidss -f 01_postgres_schema.sql
psql -h <host> -U ptidss -d ptidss -f 03_views.sql
psql -h <host> -U ptidss_dba -d ptidss -f 09_rls_policies.sql   # v1.0.1 新增：RLS 策略（幂等，可重复）
psql -h <host> -U ptidss -d ptidss -f 07_seed_data.sql   # 开发/测试种子（幂等）
taos -f 02_tdengine_schema.sql
```

- 09 执行后，应用侧数据库连接统一走服务账号（ptidss_app_*），网关连接池须执行 `SET app.region_code = '<区域>'`；管理端全国视图走 ptidss_admin（BYPASSRLS）；详情见 09 脚本头部约定与方案 V1.2 5.1；

- 分区表（declaration / trade_result / settlement_record / audit_log）已预建 2026-08/09 分区示例，生产由数据组按月度预建脚本统一管理；
- 敏感字段（contract.price、settlement_record.total_amount、sys_user.phone/email）由应用层 AES-GCM 加密后写入，DDL 层不落明文；种子数据不落明文凭据（占位符由应用初始化）；
- **评审门禁**：v1.0 已通过数据组评审（05_评审记录.md），字段级一致性以数据字典 V1.1 为准；后续变更走开发基线 7.5 流程。

## 版本记录

| 版本 | 日期 | 说明 |
|---|---|---|
| v0.1 | 2026-08-08 | 首版：50 PG 表 + 8 TDengine 超级表 + 2 视图；评审决议五项全部落位；待数据组评审 |
| v1.0 | 2026-08-08 | **锁定版**：数据组评审通过；修复 9 项问题（多省 13 表 region_code、position_view 防放大、TDengine 字段对齐、sys_region 新增 51 表）；新增 枚举字典 06 与种子数据 07；与数据字典 V1.1 同步发布 |
| v1.0.1 | 2026-08-08 | **勘误版（DDL-ERR-2026-001 已执行并回归）**：5 项（st_spot_price / st_midlong_price region 列→TAG 提升、audit_log 补 region_code + 索引、质量规则种子 2 条、新增 09_rls_policies.sql RLS 策略脚本）；SQL 静态校验通过；数据字典 V1.1 同步；三环境同步在实施窗口内执行（文件数 8 → 9） |
| v1.0.2 | 2026-08-17 | **落库兼容修订（DDL-ERR-2026-002，开发环境已执行并回归）**：4 项 PG 落库兼容修复（declaration 分区表唯一索引含分区键、settlement_reconcile/intel_news 移除 PG 不允许的外键引用、declaration/quote_plan.stage VARCHAR(8)→VARCHAR(16) 容错 day_ahead）；开发环境 PostgreSQL 18.4 全量落库 + RLS/种子回归通过；详见 08_勘误清单附录 |
| v1.0.3 | 2026-08-20 | **落库修订（条目 12/13，开发环境已执行）**：新增 flow_instance 流程实例表（11.6 平台服务数据域，轻量状态机不引入 Flowable，M7 移动端审批依赖；settlement_ticket.flow_instance_id 经 biz_id 关联）；model_registry 业务版本列 version 改名 model_version 并补乐观锁列（与 BaseEntity 同名冲突编译失败）；详见 01 文件头修订登记 |
