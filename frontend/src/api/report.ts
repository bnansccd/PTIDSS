import { http, request } from './http'
import type { PageParam, ReportInstance, ReportTemplate } from './types'

/** GET /report/templates 报表模板列表（含口径说明） */
export function getReportTemplates() {
  return request<ReportTemplate[]>({ url: '/report/templates', method: 'get' })
}

/** GET /report/templates/all 报表模板全量（含停用，管理端维护） */
export function getAllReportTemplates() {
  return request<ReportTemplate[]>({ url: '/report/templates/all', method: 'get' })
}

/** POST /report/templates 新增报表模板（报表自定义；admin） */
export function createReportTemplate(payload: {
  code: string
  name: string
  type: string
  periodType: string
  datasourceConfig?: string
  layout?: string
  headerConfig?: string
  status?: string
}) {
  return request<ReportTemplate>({ url: '/report/templates', method: 'post', data: payload })
}

/** PUT /report/templates/{id} 更新报表模板（admin） */
export function updateReportTemplate(id: string, payload: {
  name: string
  type: string
  periodType: string
  datasourceConfig?: string
  layout?: string
  headerConfig?: string
  status?: string
}) {
  return request<null>({ url: `/report/templates/${id}`, method: 'put', data: payload })
}

/** GET /report/instances 报表实例列表（按区域隔离） */
export function getReportInstances(params: { period?: string } & PageParam) {
  return request<ReportInstance[]>({ url: '/report/instances', method: 'get', params })
}

/** POST /report/instances 生成报表实例（模板+周期+格式，数据快照落库） */
export function createReportInstance(payload: { templateCode: string; period: string; format?: string }) {
  return request<{ instanceId: string; generateStatus: string; fileUrl: string }>({
    url: '/report/instances',
    method: 'post',
    data: payload,
  })
}

/** GET /report/instances/{id}/export 下载报表文件（CSV 文本流：表头+口径说明+指标行） */
export async function downloadReport(id: string) {
  const res = await http.get(`/report/instances/${id}/export`, { responseType: 'blob' })
  const blob = res.data as Blob
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `ptidss_report_${id}.csv`
  a.click()
  URL.revokeObjectURL(url)
}
