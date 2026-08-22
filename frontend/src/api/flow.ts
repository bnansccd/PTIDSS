import { request } from './http'
import type { FlowDefinition, FlowInstanceDetail, FlowStartResp, FlowStep } from './types'

/** POST /flow/start 发起流程实例（processKey + bizId + variables → instanceId） */
export function postFlowStart(payload: {
  processKey: string
  bizId: string
  variables?: Record<string, unknown>
}) {
  return request<FlowStartResp>({ url: '/flow/start', method: 'post', data: payload })
}

/** GET /flow/instances/{instanceId} 流程实例详情（status/currentNode/currentTasks/definitionSteps/actions） */
export function getFlowInstance(instanceId: string) {
  return request<FlowInstanceDetail>({ url: `/flow/instances/${instanceId}`, method: 'get' })
}

/** GET /flow/biz-types 业务类型字典（发起流程页下拉：编码+名称+自动单号前缀） */
export function getFlowBizTypes() {
  return request<Array<{ bizType: string; bizName: string; autoPrefix: string }>>({ url: '/flow/biz-types', method: 'get' })
}

/** GET /flow/biz-options 业务单号选项（按业务类型引入已有单号或自动生成规则） */
export function getFlowBizOptions(bizType: string) {
  return request<{
    bizType: string
    bizName: string
    allowAuto: boolean
    autoPrefix: string
    options: Array<{ value: string; label: string }>
  }>({ url: `/flow/biz-options?bizType=${encodeURIComponent(bizType)}`, method: 'get' })
}

/** POST /flow/instances/{instanceId}/advance 环节推进（approve → 下一环节/完成；reject → 终止） */
export function postFlowAdvance(instanceId: string, payload: { action: 'approve' | 'reject'; comment?: string }) {
  return request<{ instanceId: string; status: string; currentNode?: string; actions: unknown[] }>({
    url: `/flow/instances/${instanceId}/advance`,
    method: 'post',
    data: payload,
  })
}

/** GET /flow/definitions 流程定义列表（环节/角色/用户/启停） */
export function getFlowDefinitions() {
  return request<FlowDefinition[]>({ url: '/flow/definitions', method: 'get' })
}

/** POST /flow/definitions 新增流程定义（客户自定义流程与环节；仅 admin） */
export function postFlowDefinition(payload: {
  processKey: string
  processName: string
  bizType: string
  steps: FlowStep[]
}) {
  return request<FlowDefinition>({ url: '/flow/definitions', method: 'post', data: payload })
}

/** PUT /flow/definitions/{id} 更新流程定义（环节/角色/用户调整、启停切换；仅 admin） */
export function putFlowDefinition(
  id: string,
  payload: { processName?: string; steps?: FlowStep[]; status?: string },
) {
  return request<FlowDefinition>({ url: `/flow/definitions/${id}`, method: 'put', data: payload })
}
