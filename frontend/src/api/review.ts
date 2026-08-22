import { request } from './http'
import type { PageParam } from './types'

/** ============ 复盘（FR-RS-01/02/03，权限 menu:review） ============ */

export interface ReviewReportItem {
  id: string
  reportType: 'weekly' | 'monthly' | 'special'
  periodStart: string
  periodEnd: string
  status: 'generating' | 'completed' | 'failed'
  /** 后端返回扁平对象（{period,revenue,...}）或 label/value 数组，展示层统一转换 */
  summary?: Array<{ label: string; value: string }> | Record<string, unknown>
  suggestions?: Array<{ label: string; value: string }> | Record<string, unknown>
  createdAt?: string
}

export interface DeviationLayer {
  layer: 'forecast' | 'decision' | 'execution'
  items: Array<{ item: string; value?: number; impactAmount: number; reason: string; direction: string }>
}

export interface ReviewReportDetail extends ReviewReportItem {
  deviationAnalysis: { layers: DeviationLayer[] }
  strategyEval?: Array<{ label: string; value: string }> | Record<string, unknown>
}

/** POST /review/reports 生成复盘报告（周/月/专项，三层归因） */
export function createReviewReport(payload: {
  reportType: 'weekly' | 'monthly' | 'special'
  startDate: string
  endDate: string
  focusTopics?: string[]
}) {
  return request<{ reportId: string }>({ url: '/review/reports', method: 'post', data: payload })
}

/** GET /review/reports 复盘报告列表 */
export function getReviewReports(params: { reportType?: string; periodStart?: string; periodEnd?: string }) {
  return request<ReviewReportItem[]>({ url: '/review/reports', method: 'get', params })
}

/** GET /review/reports/{id} 报告详情（三层归因） */
export function getReviewReportDetail(id: string) {
  return request<ReviewReportDetail>({ url: `/review/reports/${id}`, method: 'get' })
}

/** POST /review/strategy-feedback 策略回流（有效/失效/调整 → 策略库） */
export function postStrategyFeedback(payload: {
  strategyCode: string
  feedback: 'effective' | 'invalid' | 'adjust'
  updatedParams?: Record<string, unknown>
  reviewId?: string
}) {
  return request<null>({ url: '/review/strategy-feedback', method: 'post', data: payload })
}

/** ============ 成效考核（FR-AS-01/02，权限 menu:review） ============ */

export interface AssessIndicator {
  id: string
  code: string
  name: string
  formula: string
  weight: number
  targetValue: string
  scoringRule: string
  status: 'active' | 'disabled'
}

export interface AssessResult {
  id: string
  period: string
  scope: 'personal' | 'team'
  userId?: string
  scores: Record<string, number>
  totalScore: number
  rank?: number
  status: 'pending' | 'confirmed' | 'appealing' | 'corrected'
}

export interface AssessAppeal {
  appealId: string
  status: 'pending' | 'processing' | 'approved' | 'rejected'
}

/** GET /assessment/indicators 考核指标 */
export function getAssessIndicators() {
  return request<AssessIndicator[]>({ url: '/assessment/indicators', method: 'get' })
}

/** GET /assessment/indicators/all 考核指标全量（含停用，管理端维护） */
export function getAllAssessIndicators() {
  return request<AssessIndicator[]>({ url: '/assessment/indicators/all', method: 'get' })
}

/** POST /assessment/indicators 新增考核指标（考核体系自定义；admin） */
export function createAssessIndicator(payload: {
  code: string
  name: string
  weight: number
  formula?: string
  targetValue?: string
  scoringRule?: string
  dataSource?: string
  status?: string
}) {
  return request<AssessIndicator>({ url: '/assessment/indicators', method: 'post', data: payload })
}

/** PUT /assessment/indicators/{id} 更新考核指标（admin） */
export function updateAssessIndicator(id: string, payload: {
  name: string
  weight: number
  formula?: string
  targetValue?: string
  scoringRule?: string
  dataSource?: string
  status?: string
}) {
  return request<null>({ url: `/assessment/indicators/${id}`, method: 'put', data: payload })
}

/** GET /assessment/results 考核结果（周期必填） */
export function getAssessResults(params: { period: string; scope?: string } & PageParam) {
  return request<AssessResult[]>({ url: '/assessment/results', method: 'get', params })
}

/** POST /assessment/appeals 发起申诉 */
export function createAssessAppeal(payload: { resultId: string; appealReason: string; evidenceUrls?: string[] }) {
  return request<AssessAppeal>({ url: '/assessment/appeals', method: 'post', data: payload })
}

/** POST /assessment/appeals/{id}/process 审核申诉（批准重算/驳回） */
export function processAssessAppeal(id: string, payload: { decision: 'approved' | 'rejected'; comment?: string }) {
  return request<null>({ url: `/assessment/appeals/${id}/process`, method: 'post', data: payload })
}
