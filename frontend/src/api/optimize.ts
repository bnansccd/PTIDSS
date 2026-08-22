import { request } from './http'
import type { BacktestResult, JointTaskResp, JointTaskStatus, StrategyInfo } from './types'

/** POST /optimize/joint-tasks 创建联合优化任务（daily/rolling_N/backtest；1-7 天，10-500 场景） */
export function postJointTask(payload: {
  taskType: string
  horizonDays?: number
  scenarioCount?: number
  objectiveWeights?: Record<string, unknown>
  constraints?: Record<string, unknown>
}) {
  return request<JointTaskResp>({ url: '/optimize/joint-tasks', method: 'post', data: payload })
}

/** GET /optimize/joint-tasks/{taskId} 优化任务状态与结果摘要 */
export function getJointTaskStatus(taskId: string) {
  return request<JointTaskStatus>({ url: `/optimize/joint-tasks/${taskId}`, method: 'get' })
}

/** POST /optimize/backtests 发起策略回测（策略编码 + 区间 → runId） */
export function postBacktest(payload: {
  strategyCode: string
  startDate: string
  endDate: string
  marketDataVersion?: string
}) {
  return request<BacktestResult>({ url: '/optimize/backtests', method: 'post', data: payload })
}

/** GET /optimize/strategies 策略库列表（回测/复盘/人工三源沉淀） */
export function getStrategies() {
  return request<StrategyInfo[]>({ url: '/optimize/strategies', method: 'get' })
}
