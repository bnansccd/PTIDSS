import { http, request } from './http'
import type { PageParam, PageResult, PolicyDetail, PolicyDocument, PolicyParseResult } from './types'

/** GET /policy/list 政策文件分页列表（分类/关键词/状态筛选） */
export function getPolicyList(params: { category?: string; keyword?: string; status?: string } & PageParam) {
  return request<PageResult<PolicyDocument>>({ url: '/policy/list', method: 'get', params })
}

/** GET /policy/{id} 政策详情（含解析条款/影响研判/沉淀规则） */
export function getPolicyDetail(id: string) {
  return request<PolicyDetail>({ url: `/policy/${id}`, method: 'get' })
}

/** POST /policy/parse 解析政策文档（LLM 抽取条款→规则候选；幂等，reparse 强制重解析） */
export function postPolicyParse(payload: { policyId: string; reparse?: boolean }) {
  return request<PolicyParseResult>({ url: '/policy/parse', method: 'post', data: payload })
}

/** POST /policy/upload 上传/新建政策文档（登记入政策库；返回新增文档） */
export function postPolicyUpload(payload: {
  title: string
  issuingBody?: string
  category: string
  tags?: string[]
  publishDate?: string
  effectiveDate?: string
  status?: string
  fileUrl?: string
}) {
  return request<PolicyDocument>({ url: '/policy/upload', method: 'post', data: payload })
}

/** POST /policy/upload-file 上传新政策（multipart：政策原文文件 + 登记信息；文件落盘本地存储，列表可下载） */
export async function uploadPolicyFile(payload: {
  file: File
  title: string
  issuingBody?: string
  category: string
  tags?: string[]
  publishDate?: string
  effectiveDate?: string
  status?: string
}) {
  const fd = new FormData()
  fd.append('file', payload.file)
  fd.append('title', payload.title)
  if (payload.issuingBody) fd.append('issuingBody', payload.issuingBody)
  fd.append('category', payload.category)
  if (payload.tags && payload.tags.length > 0) fd.append('tags', payload.tags.join(','))
  if (payload.publishDate) fd.append('publishDate', payload.publishDate)
  if (payload.effectiveDate) fd.append('effectiveDate', payload.effectiveDate)
  if (payload.status) fd.append('status', payload.status)
  return request<PolicyDocument>({ url: '/policy/upload-file', method: 'post', data: fd })
}

/** GET /policy/{id}/file 政策原文文件下载（local:// 本地落盘文件；下载名取服务端原始文件名） */
export async function downloadPolicyFile(id: string, fallbackTitle: string) {
  const res = await http.get(`/policy/${id}/file`, { responseType: 'blob' })
  const blob = res.data as Blob
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  const cd = res.headers['content-disposition'] as string | undefined
  const m = cd ? /filename\*=UTF-8''([^;]+)/.exec(cd) : null
  a.href = url
  a.download = m ? decodeURIComponent(m[1]) : `${fallbackTitle}.pdf`
  a.click()
  URL.revokeObjectURL(url)
}

/** GET /policy/{id}/brief 政策研判简报导出（CSV） */
export async function downloadPolicyBrief(id: string) {
  const res = await http.get(`/policy/${id}/brief`, { responseType: 'blob' })
  const blob = res.data as Blob
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `policy_brief_${id}.csv`
  a.click()
  URL.revokeObjectURL(url)
}
