import { request } from './http'
import type { OcrTask, PageParam, PageResult, SettlementRecord } from './types'

/** GET /settlement/records 结算记录（period 必填 + source 可选，按 region 隔离） */
export function getSettlementRecords(params: { period: string; source?: 'system' | 'exchange' } & PageParam) {
  return request<PageResult<SettlementRecord>>({ url: '/settlement/records', method: 'get', params })
}

/** POST /settlement/records/{id}/reconcile 发起核对 */
export function reconcileRecord(id: string) {
  return request<null>({ url: `/settlement/records/${id}/reconcile`, method: 'post' })
}

/** GET /settlement/tickets 差异工单 */
export function getTickets(params: { status?: string } & PageParam) {
  return request<PageResult>({ url: '/settlement/tickets', method: 'get', params })
}

/** POST /settlement/tickets/{id}/process 工单处理 */
export function processTicket(id: string, payload: { action: string; remark?: string }) {
  return request<null>({ url: `/settlement/tickets/${id}/process`, method: 'post', data: payload })
}

/** GET /ocr/tasks OCR 任务列表（状态/复核状态筛选，分页；复核工作台） */
export function getOcrTasks(params: { status?: string; reviewStatus?: string } & PageParam) {
  return request<PageResult<OcrTask>>({ url: '/ocr/tasks', method: 'get', params })
}

/** POST /ocr/tasks/{id}/review 人工复核提交（确认通过或修正字段） */
export function reviewOcrTask(id: string, payload: { approved: boolean; fields?: Record<string, unknown>; comment?: string }) {
  return request<{ taskId: string; reviewStatus: string; reviewer: string }>({
    url: `/ocr/tasks/${id}/review`,
    method: 'post',
    data: payload,
  })
}
