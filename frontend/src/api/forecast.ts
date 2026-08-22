import { request } from './http'
import type { ForecastPoint, ForecastTaskResp, ForecastTaskStatus, ModelInfo, TrainTaskResp } from './types'

/** POST /forecast/tasks 创建预测任务（price 必填 marketType；load/generation 必填 regionCode） */
export function postForecastTask(payload: {
  modelCode: string
  predictDate: string
  marketType?: string
  regionCode?: string
}) {
  return request<ForecastTaskResp>({ url: '/forecast/tasks', method: 'post', data: payload })
}

/** GET /forecast/tasks/{taskId} 预测任务状态 */
export function getForecastTaskStatus(taskId: string) {
  return request<ForecastTaskStatus>({ url: `/forecast/tasks/${taskId}`, method: 'get' })
}

/** GET /forecast/results 96 点预测结果（含 90% 置信区间） */
export function getForecastResults(params: { predictType: string; tradeDate: string; modelVersion?: string }) {
  return request<ForecastPoint[]>({ url: '/forecast/results', method: 'get', params })
}

/** GET /forecast/models 模型注册列表（与 model 域共用 model_registry） */
export function getForecastModels() {
  return request<ModelInfo[]>({ url: '/forecast/models', method: 'get' })
}

/** POST /forecast/models/train 触发模型训练（daily_increment/weekly_full） */
export function postForecastTrain(payload: { modelCode: string; mode?: string }) {
  return request<TrainTaskResp>({ url: '/forecast/models/train', method: 'post', data: payload })
}
