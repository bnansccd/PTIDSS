import { request } from './http'
import type { EvaluateResult, InferenceResult, ModelInfo } from './types'

/** GET /model/registry 模型注册表（版本/指标/状态；与 MLflow 同步） */
export function getModelRegistry() {
  return request<ModelInfo[]>({ url: '/model/registry', method: 'get' })
}

/** POST /model/inference 在线推理（LLM 问答/预测增强） */
export function postModelInference(payload: { modelCode: string; input: Record<string, unknown>; temperature?: number }) {
  return request<InferenceResult>({ url: '/model/inference', method: 'post', data: payload })
}

/** POST /model/evaluate 离线评估（MAPE/方向准确率，双指标判定） */
export function postModelEvaluate(payload: { modelVersion: string; testSetVersion: string }) {
  return request<EvaluateResult>({ url: '/model/evaluate', method: 'post', data: payload })
}

/** LLM 模型条目（GET /llm/models；V2.2 智能体可关联的生成式模型） */
export interface LlmModelItem {
  id: string
  modelCode: string
  modelName: string
  provider: string
  endpoint?: string | null
  baseModel?: string | null
  temperature: number
  maxTokens: number
  apiKeyRef?: string | null
  status: string
}

/** GET /llm/models LLM 模型列表（模型平台-LLM 管理） */
export function getLlmModels() {
  return request<LlmModelItem[]>({ url: '/llm/models', method: 'get' })
}

/** POST /llm/models 新增 LLM 模型（仅 admin） */
export function createLlmModel(payload: {
  modelCode: string
  modelName: string
  provider: string
  endpoint?: string
  baseModel?: string
  temperature?: number
  maxTokens?: number
  apiKeyRef?: string
  status?: string
}) {
  return request<LlmModelItem>({ url: '/llm/models', method: 'post', data: payload })
}

/** PUT /llm/models/{id} 更新 LLM 模型配置（连接参数/温度/限额/启停；客户部署适配；仅 admin） */
export function updateLlmModel(
  id: string,
  payload: {
    modelName?: string
    provider?: string
    endpoint?: string
    baseModel?: string
    temperature?: number
    maxTokens?: number
    apiKeyRef?: string
    status?: string
  },
) {
  return request<void>({ url: `/llm/models/${id}`, method: 'put', data: payload })
}

/** SPI 执行器条目（GET /algorithm/spis；P3 插件化执行：算法绑定执行器，空=按类目默认） */
export interface SpiItem {
  spiKey: string
  label: string
  category: string
}

/** GET /algorithm/spis SPI 执行器清单（算法注册/编辑下拉） */
export function getAlgorithmSpis() {
  return request<SpiItem[]>({ url: '/algorithm/spis', method: 'get' })
}

/** 算法注册条目（GET /algorithm/registry；V2.2 专业算法注册/替换/匹配 + P3 SPI 绑定） */
export interface AlgorithmItem {
  id: string
  algCode: string
  algName: string
  category: string
  description?: string | null
  paramsSchema: Record<string, unknown> | string
  version: string
  spiKey?: string | null
  status: string
}

/** GET /algorithm/registry 算法注册表（按类目/状态筛选） */
export function getAlgorithms(params?: { category?: string; status?: string }) {
  return request<AlgorithmItem[]>({ url: '/algorithm/registry', method: 'get', params })
}

/** POST /algorithm/registry 新增算法（编码+版本唯一；spiKey 须为已注册执行器或空=按类目默认；仅 admin） */
export function createAlgorithm(payload: {
  algCode: string
  algName: string
  category: string
  description?: string
  paramsSchema?: string
  version?: string
  spiKey?: string
  status?: string
}) {
  return request<AlgorithmItem>({ url: '/algorithm/registry', method: 'post', data: payload })
}

/** PUT /algorithm/registry/{id} 更新算法（参数模板/说明/版本/SPI 执行器/启停；替换算法=新版本启用+旧版停用；仅 admin） */
export function updateAlgorithm(
  id: string,
  payload: { algName?: string; description?: string; paramsSchema?: string; version?: string; spiKey?: string; status?: string },
) {
  return request<void>({ url: `/algorithm/registry/${id}`, method: 'put', data: payload })
}

/** 算法文件自动解析结果（POST /algorithm/parse-file；客户上传专业算法文件自动解析注册要素并回填表单） */
export interface AlgorithmParseResult {
  algCode: string
  algName: string
  category: string
  description: string
  paramsSchema: string
  version: string
  fileName: string
  fileSize: number
  extension: string
  /** V2.4 打包算法（jar/zip）深度解析：入口类/清单/文件清单/包内参数模板 */
  archiveInfo?: {
    mainClass?: string
    manifestDesc?: string
    version?: string
    fileCount?: number
    summary?: string
  } | null
}

/** POST /algorithm/parse-file 算法文件自动解析（multipart：file + 可选 category；仅 admin） */
export async function uploadAlgorithmFile(file: File, category?: string): Promise<AlgorithmParseResult> {
  const fd = new FormData()
  fd.append('file', file)
  if (category) fd.append('category', category)
  return request<AlgorithmParseResult>({ url: '/algorithm/parse-file', method: 'post', data: fd })
}

/** 模型任务条目（GET /model/tasks；V2.4 训练触发/离线评估/在线推理报告列表） */
export interface ModelTaskItem {
  id: string
  taskType: string
  taskName: string
  modelCode: string
  modelVersion?: string | null
  status: string
  latencyMs?: number | null
  createdAt?: string | null
  finishedAt?: string | null
  createdBy?: string | null
}

/** 模型任务详情（GET /model/tasks/{id}；完整报告：输入/过程步骤/结果/与前面对标） */
export interface ModelTaskDetail {
  id: string
  taskType: string
  taskName: string
  modelCode: string
  modelVersion?: string | null
  status: string
  latencyMs?: number | null
  createdAt?: string | null
  finishedAt?: string | null
  createdBy?: string | null
  input: Record<string, unknown>
  processSteps: Array<{ step: string; detail: string; timeMs?: number }>
  result: Record<string, unknown>
  compare: {
    baselineTaskId?: string
    baselineCreatedAt?: string | null
    baselineMetrics?: Record<string, unknown>
    delta?: Record<string, unknown>
    summary?: string
  }
}

/** POST /model/tasks/{taskType} 执行模型任务（train/evaluate/inference）并生成详细报告（过程/结果/与前面对标） */
export function postModelTask(
  taskType: string,
  payload: {
    modelCode?: string
    modelVersion?: string
    testSetVersion?: string
    mode?: string
    input?: Record<string, unknown>
  },
) {
  return request<ModelTaskDetail>({ url: `/model/tasks/${taskType}`, method: 'post', data: payload })
}

/** GET /model/tasks 模型任务列表（按类型过滤；倒序） */
export function getModelTasks(params?: { taskType?: string; limit?: number }) {
  return request<ModelTaskItem[]>({ url: '/model/tasks', method: 'get', params })
}

/** GET /model/tasks/{id} 模型任务详情（完整报告） */
export function getModelTaskDetail(id: string) {
  return request<ModelTaskDetail>({ url: `/model/tasks/${id}`, method: 'get' })
}
