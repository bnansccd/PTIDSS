# OpenAPI 接口契约（电力交易智能辅助决策系统）

本目录为系统 **OpenAPI 3.0.3 接口契约基线 V1.0**，配套开发基线 5.5 API 目录总表。
（2026-08-17：V1.1 落地同步——补齐 /auth/captcha 与 /admin/** 系统管理接口（区域/用户/角色/权限/审计日志），共 89 个 operationId；通过 redocly lint 0 error 门禁）

## 文件说明

| 文件 | 内容 |
|---|---|
| `openapi.yaml` | 聚合契约（17 服务核心接口 + admin 系统管理 + 共享组件），可导入 Swagger Editor / Redoc / Apifox |
| `openapi.json` | 同内容 JSON 格式（构建期由 YAML 转换生成） |

## 契约管理规范

1. **版本**：契约与开发基线同步迭代，`V1.0` 为基线锁定版；后续变更走 7.5 变更管理流程（契约评审 + 兼容性检查）。
2. **拆分方式**：`openapi.yaml` 中每个服务一个 `x-service` 注释分区；各服务团队按 `paths` 前缀归属，拆分子文件（`openapi-auth.yaml` 等）后经 CI 合并校验（openapi-validator）。
3. **统一响应体**：所有接口响应封装为 `ApiResponse`（`{code, message, data, traceId}`），`data` 为业务对象；分页列表使用 `PageResult`。
4. **错误码**：`错误码分段表`（见基线 5.3）：
   - `10xxx` 通用 / `11xxx` 认证授权 / `12xxx` 政策 / `13xxx` 市场 / `14xxx` 预测 / `15xxx` 决策 / `16xxx` 交易 / `17xxx` 结算 / `18xxx` 复盘考核 / `19xxx` 报表数据 / `20xxx` 平台
   - 错误响应统一 `ErrorResponse`（`{code, message, traceId, detail}`）。
5. **鉴权**：网关签发 JWT；`Authorization: Bearer <token>`；管理接口加 `X-Role-Check: true` 触发数据权限校验（数据字典 10.2 三级权限）。
6. **Mock 优先**：契约合入主分支当日生成 Mock Server（prism），前端原型与后端并行开发以契约为准。

## Mock Server 快速开始（前后端并行开发）

```bash
# 启动（默认端口 4010，62 个 path 全量注册）
npx -y @stoplight/prism-cli mock openapi.yaml -p 4010 --host 0.0.0.0

# 冒烟验证
curl -X POST http://localhost:4010/auth/login -H "Content-Type: application/json" \
  -d '{"username":"trader01","password":"demo"}'
curl -H "Authorization: Bearer mock-token" http://localhost:4010/intel/news?importance=high&pageNo=1&pageSize=3
```

- 响应遵循统一封装 `ApiResponse`；未带 token 访问受保护接口返回 `401 + 14001`（与错误码分段一致）；
- 前端开发统一指向 `http://localhost:4010`，后端就绪后经网关切换（环境变量 `VITE_API_BASE`）；
- 静态原型预览：`cd prototype && python3 -m http.server 8787`。

7. **安全约定**：敏感字段（价格/金额）响应加密由网关协商（可选 mTLS），本文档不做字段级加密标注，见安全设计。

## 服务与 path 前缀对照（对应 5.5 目录）

| 服务 | 前缀 | 备注 |
|---|---|---|
| gateway | /api/v1/** | 统一网关入口，本契约路径均省略 /api/v1 |
| auth | /auth/** | |
| policy | /policy/** | 政策解析/研判 |
| market | /market/** | 行情 |
| forecast | /forecast/** | 预测 |
| decision | /decision/** | 决策编排 |
| optimize | /optimize/** | 联合优化/回测 |
| trade | /trade/** | 交易申报/持仓 |
| settlement | /settlement/** | 结算核对 |
| ocr | /ocr/** | 结算单识别（也可经 settlement） |
| review | /review/** | 复盘 |
| assessment | /assessment/** | 成效考核 |
| report | /report/** | 报表 |
| data | /data/** | 数据管理 |
| message | /message/** | 消息 |
| flow | /flow/** | 流程引擎 |
| model-platform | /model/** | 模型平台 |
| intel-service | /intel/** | 情报中心（V1.1 新增） |

## 扩展模板（新增接口时使用）

```yaml
# 新接口模板：粘贴到对应服务分区，替换 XXX
  /xxx/resource:
    post:
      summary: 简要描述
      tags: [xxx]
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/XxxRequest' }
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                allOf:
                  - { $ref: '#/components/schemas/ApiResponse' }
                  - properties: { data: { $ref: '#/components/schemas/XxxDto' } }
        '4XX': { $ref: '#/components/responses/BizError' }
        '5XX': { $ref: '#/components/responses/ServerError' }
```

> 校验命令：`npx @redocly/cli lint openapi.yaml`（CI 门禁之一，见基线 7.3）。当前状态：**0 error / 4 warning**（剩余为可选项提示），操作项：补齐 operationId（已完成）、nullable 与 type 同层写法（已修复）。
