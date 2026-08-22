import { request } from './http'
import type { IntelNews, IntelPushRule, IntelSource, PageParam, PageResult } from './types'

/** GET /intel/news 情报流（high 级 30s 推送；按 region 过滤） */
export function getIntelNews(params: { importance?: string; intelType?: string } & PageParam) {
  return request<PageResult<IntelNews>>({ url: '/intel/news', method: 'get', params })
}

/** GET /intel/sources 情报源台账（60+ 源，种子代表性子集） */
export function getIntelSources() {
  return request<IntelSource[]>({ url: '/intel/sources', method: 'get' })
}

/** POST /intel/sources 新增情报源（台账登记；编码唯一，类型/采集方式/对接方式/状态枚举校验） */
export function createIntelSource(payload: {
  sourceCode: string
  sourceName: string
  intelType: string
  fetchMode?: string
  connType?: string
  connConfig?: string
  frequency?: string
  status?: string
}) {
  return request<IntelSource>({ url: '/intel/sources', method: 'post', data: payload })
}

/** PUT /intel/sources/{id} 更新情报源对接配置（连接方式/连接参数/频率/启停；客户部署适配） */
export function updateIntelSource(
  id: string,
  payload: { fetchMode?: string; connType?: string; connConfig?: string; frequency?: string; status?: string },
) {
  return request<IntelSource>({ url: `/intel/sources/${id}`, method: 'put', data: payload })
}

/** GET /intel/push-rules 推送规则列表 */
export function getIntelPushRules() {
  return request<IntelPushRule[]>({ url: '/intel/push-rules', method: 'get' })
}

/** POST /intel/push-rules 配置推送规则（标签×重要度→角色/渠道；high 级自动 +sms/+miniapp） */
export function createIntelPushRule(payload: { ruleName: string; matchTags: string[]; importance: string; targets?: string[] }) {
  return request<{ ruleId: string; channels: string[] }>({ url: '/intel/push-rules', method: 'post', data: payload })
}

/** 消息中心见 ./message.ts（GET /message/list 等） */
