import { request } from './http'
import type { PageResult, PricePoint, SupplyDemandPoint } from './types'

/** GET /market/price/spot 现货价格时序（96 点/区间） */
export function getSpotPrice(params: {
  marketType: 'intra_province' | 'inter_province'
  stage: 'day_ahead' | 'real_time'
  startAt: string
  endAt: string
}) {
  return request<PricePoint[]>({ url: '/market/price/spot', method: 'get', params })
}

/** GET /market/price/midlong 中长期成交价格 */
export function getMidlongPrice(params: { variety?: string; startAt?: string; endAt?: string }) {
  return request<PricePoint[]>({ url: '/market/price/midlong', method: 'get', params })
}

/** GET /market/supply-demand 供需时序 */
export function getSupplyDemand(params: { startAt: string; endAt: string }) {
  return request<SupplyDemandPoint[]>({ url: '/market/supply-demand', method: 'get', params })
}

/** GET /market/heatmap 区域行情热力图（startDate/endDate 必填） */
export function getHeatmap(params: { startDate: string; endDate: string }) {
  return request<{ dates: string[]; points: number[] }>({ url: '/market/heatmap', method: 'get', params })
}

/** GET /policy/list 政策列表 */
export function getPolicyList(params: { page?: number; pageSize?: number }) {
  return request<PageResult>({ url: '/policy/list', method: 'get', params })
}
