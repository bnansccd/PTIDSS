import { request } from './http'
import type { CollectTaskResult, DataSourceInfo, LineageResult, QualityReport } from './types'

/** GET /data/sources 数据源台账（营销/交易中心/气象，双通道建模） */
export function getDataSources() {
  return request<DataSourceInfo[]>({ url: '/data/sources', method: 'get' })
}

/** POST /data/sources 新增数据源（台账登记；编码唯一，类型/同步模式/对接方式/状态枚举校验） */
export function createDataSource(payload: {
  sourceCode: string
  sourceType: string
  syncMode?: string
  connType?: string
  frequency?: string
  status?: string
  connectConfig?: string
}) {
  return request<DataSourceInfo>({ url: '/data/sources', method: 'post', data: payload })
}

/** PUT /data/sources/{id} 更新数据源对接配置（连接方式/连接参数/同步模式/频率/启停；客户部署适配） */
export function updateDataSource(
  id: string,
  payload: { syncMode?: string; connType?: string; connectConfig?: string; frequency?: string; status?: string },
) {
  return request<DataSourceInfo>({ url: `/data/sources/${id}`, method: 'put', data: payload })
}

/** POST /data/collect-tasks 手动触发采集任务（force 强制重跑） */
export function postCollectTask(payload: { taskType: string; force?: boolean }) {
  return request<CollectTaskResult>({ url: '/data/collect-tasks', method: 'post', data: payload })
}

/** GET /data/quality/report 数据质量报告（完整率/准确率/及时率） */
export function getQualityReport(params?: { startDate?: string; endDate?: string }) {
  return request<QualityReport>({ url: '/data/quality/report', method: 'get', params })
}

/** GET /data/lineage 数据血缘（nodeId 缺省返回全景） */
export function getLineage(params?: { nodeId?: string }) {
  return request<LineageResult>({ url: '/data/lineage', method: 'get', params })
}
