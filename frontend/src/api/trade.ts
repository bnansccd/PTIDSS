import { request } from './http'
import type { Declaration, PageParam, PageResult, RollingPlan } from './types'

/** GET /trade/rolling-plans 日滚动方案 */
export function getRollingPlans(params: { tradeDate?: string } & PageParam) {
  return request<PageResult<RollingPlan>>({ url: '/trade/rolling-plans', method: 'get', params })
}

/** POST /trade/rolling-plans/{planId}/confirm 方案确认 */
export function confirmRollingPlan(planId: string) {
  return request<null>({ url: `/trade/rolling-plans/${planId}/confirm`, method: 'post' })
}

/** POST /trade/declarations 创建申报单（含合规预检） */
export function createDeclaration(payload: {
  tradeDate: string
  marketType: string
  stage: string
  items: unknown[]
  sourcePlanId?: string
}) {
  return request<{ declarationId: string; complianceCheck: unknown }>({
    url: '/trade/declarations',
    method: 'post',
    data: payload,
  })
}

/** GET /trade/declarations 申报单列表 */
export function getDeclarations(params: { tradeDate?: string; status?: string } & PageParam) {
  return request<PageResult<Declaration>>({ url: '/trade/declarations', method: 'get', params })
}

/** GET /trade/declarations/{id} 申报单详情（V2.4：明细/合规预检/网关推送状态） */
export function getDeclaration(id: string) {
  return request<DeclarationDetail>({ url: `/trade/declarations/${id}`, method: 'get' })
}

/** PUT /trade/declarations/{id} 编辑申报单（V2.4：draft/pending_submit 可编辑，重新合规预检） */
export function updateDeclaration(id: string, payload: {
  tradeDate: string
  marketType: string
  stage: string
  items: unknown[]
  sourcePlanId?: string
}) {
  return request<{ declarationId: string; complianceCheck: unknown }>({
    url: `/trade/declarations/${id}`,
    method: 'put',
    data: payload,
  })
}

/** POST /trade/declarations/{id}/submit 申报提交（V2.4：网关推送状态监测） */
export function submitDeclaration(id: string) {
  return request<{ receiptNo: string; gatewayPushStatus?: string; gatewayPushDetail?: string }>({
    url: `/trade/declarations/${id}/submit`,
    method: 'post',
  })
}

/** GET /trade/gateway/config 当前区域网关配置（敏感字段脱敏） */
export function getTradeGatewayConfig() {
  return request<TradeGatewayConfig | null>({ url: '/trade/gateway/config', method: 'get' })
}

/** PUT /trade/gateway/config 保存网关配置（URL/账户/密码图形化；appSecret 加密落库） */
export function saveTradeGatewayConfig(payload: {
  gatewayName?: string
  endpoint: string
  appKey?: string
  appSecret?: string
  status: string
}) {
  return request<TradeGatewayConfig>({ url: '/trade/gateway/config', method: 'put', data: payload })
}

/** POST /trade/gateway/test 网关连通性测试 */
export function testTradeGateway() {
  return request<{ ok: boolean; latencyMs?: number; message: string; testedAt?: string }>({
    url: '/trade/gateway/test',
    method: 'post',
  })
}

export interface TradeGatewayConfig {
  id: string
  regionCode: string
  gatewayName: string
  endpoint: string
  connConfig: string
  status: string
  lastTestAt?: string
  lastTestResult?: string
}

export interface DeclarationDetail {
  id: string
  declarationNo: string
  tradeDate: string
  marketType: string
  stage: string
  regionCode: string
  status: string
  receiptNo?: string
  createdBy?: string
  createdAt?: string
  gatewayPushStatus?: string
  gatewayPushTime?: string
  gatewayPushDetail?: string
  items: unknown[]
  complianceCheck: Record<string, unknown>
}

/** GET /trade/results 成交结果 */
export function getTradeResults(params: { tradeDate?: string } & PageParam) {
  return request<PageResult>({ url: '/trade/results', method: 'get', params })
}

/** GET /trade/positions 持仓曲线（tradeDate 必填，中长期+现货合成视图） */
export function getPositions(params: { tradeDate: string }) {
  return request<unknown[]>({ url: '/trade/positions', method: 'get', params })
}
