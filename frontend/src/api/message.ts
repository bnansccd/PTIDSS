import { request } from './http'
import type { MessageRecord, PageParam, PageResult } from './types'

/** GET /message/list 我的消息（分类/未读筛选，个人维度） */
export function getMessageList(params: { msgType?: string; unreadOnly?: boolean } & PageParam) {
  return request<PageResult<MessageRecord>>({ url: '/message/list', method: 'get', params })
}

/** POST /message/{id}/read 标记已读 */
export function postMessageRead(id: string) {
  return request<void>({ url: `/message/${id}/read`, method: 'post' })
}

// 兼容别名（DashboardView 驾驶舱消息预览沿用旧名）
export const getMessages = getMessageList
export const markMessageRead = postMessageRead
