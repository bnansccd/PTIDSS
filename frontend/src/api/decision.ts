import { request } from './http'
import type { PageParam, PageResult, StrategyItem } from './types'

/** POST /decision/sessions 发起决策会话（日滚动/现货报价/联合优化） */
export function createSession(payload: {
  sessionType: 'rolling' | 'spot_quote' | 'joint_optimize'
  tradeDate: string
  scenario?: 'baseline' | 'conservative' | 'aggressive'
  agents?: string[]
}) {
  return request<{ sessionId: string; status: string }>({ url: '/decision/sessions', method: 'post', data: payload })
}

/** POST /decision/sessions/{sessionId}/confirm 人审确认 */
export function confirmSession(sessionId: string) {
  return request<null>({ url: `/decision/sessions/${sessionId}/confirm`, method: 'post' })
}

/** POST /decision/sessions/{sessionId}/modify 人审修改（须记录依据，FR-DM-05） */
export function modifySession(sessionId: string, payload: { modifications: unknown[]; reason: string }) {
  return request<null>({ url: `/decision/sessions/${sessionId}/modify`, method: 'post', data: payload })
}

/** POST /decision/sessions/{sessionId}/reject 人审驳回（须记录原因，FR-DM-05） */
export function rejectSession(sessionId: string, payload: { reason: string }) {
  return request<null>({ url: `/decision/sessions/${sessionId}/reject`, method: 'post', data: payload })
}

/** POST /decision/sessions/{sessionId}/rerun 降级补跑（SRS FR-DM-01 R1：补齐缺失智能体） */
export function rerunSession(sessionId: string) {
  return request<Record<string, unknown>>({ url: `/decision/sessions/${sessionId}/rerun`, method: 'post' })
}

/** POST /decision/sessions/{sessionId}/intel-reassess 情报触发式重算（重评情报评分快照，仅 pending） */
export function reassessIntel(sessionId: string) {
  return request<Record<string, unknown>>({ url: `/decision/sessions/${sessionId}/intel-reassess`, method: 'post' })
}

/** GET /decision/sessions/{sessionId} 会话详情（策略/人审状态） */
export function getSessionDetail(sessionId: string) {
  return request<Record<string, unknown>>({ url: `/decision/sessions/${sessionId}`, method: 'get' })
}

/** GET /decision/sessions/{sessionId}/evidence 依据链 */
export function getSessionEvidence(sessionId: string) {
  return request<unknown>({ url: `/decision/sessions/${sessionId}/evidence`, method: 'get' })
}

/** POST /optimize/joint-tasks 创建联合优化任务（MILP） */
export function createJointOptimTask(payload: {
  taskType: 'daily' | 'rolling_N' | 'backtest'
  horizonDays?: number
  scenarioCount?: number
  objectiveWeights?: Record<string, number>
  constraints?: Record<string, unknown>
}) {
  return request<{ taskId: string }>({ url: '/optimize/joint-tasks', method: 'post', data: payload })
}

/** POST /optimize/backtests 发起策略回测（分步决策 + 收益增量） */
export function createBacktest(payload: { strategyCode: string; startDate: string; endDate: string; marketDataVersion?: string }) {
  return request<{ runId: string }>({ url: '/optimize/backtests', method: 'post', data: payload })
}

/** GET /optimize/strategies 策略库 */
export function getStrategies(params: PageParam) {
  return request<PageResult<StrategyItem>>({ url: '/optimize/strategies', method: 'get', params })
}

/** POST /forecast/tasks 创建预测任务 */
export function createForecastTask(payload: { modelCode: string; predictDate: string }) {
  return request<{ taskId: string }>({ url: '/forecast/tasks', method: 'post', data: payload })
}
