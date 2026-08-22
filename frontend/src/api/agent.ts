import { request } from './http'

/** 智能体注册条目（GET /agent/registry） */
export interface AgentRegistryItem {
  id: string
  agentCode: string
  agentName: string
  role: string
  inputSchema?: Record<string, unknown>
  outputSchema?: Record<string, unknown>
  version: string
  modelConfig?: Record<string, unknown>
  status: string
}

/** 智能体运行记录（GET /agent/runs） */
export interface AgentRunItem {
  runId: string
  agentCode: string
  sessionId: string
  inputSnapshot: Record<string, unknown> | string
  output: Record<string, unknown> | string
  confidence: number
  reasoning: Record<string, unknown> | string
  elapsedMs: number
  status: string
  createdAt: string
}

/** 效果评估聚合（GET /agent/metrics） */
export interface AgentMetricItem {
  agentCode: string
  runCount: number
  successRate: number | null
  avgConfidence: number | null
  avgElapsedMs: number | null
}

/** GET /agent/registry 智能体注册表 */
export function getAgentRegistry() {
  return request<AgentRegistryItem[]>({ url: '/agent/registry', method: 'get' })
}

/** GET /agent/runs 运行记录（可按智能体/会话过滤） */
export function getAgentRuns(params?: { agentCode?: string; sessionId?: string; limit?: number }) {
  return request<AgentRunItem[]>({ url: '/agent/runs', method: 'get', params })
}

/** GET /agent/metrics 效果评估（成功率/平均置信度/平均耗时） */
export function getAgentMetrics() {
  return request<AgentMetricItem[]>({ url: '/agent/metrics', method: 'get' })
}

/** POST /agent/registry/{id}/status 启停维护（active/disabled/maintenance） */
export function postAgentStatus(id: string, status: string) {
  return request<void>({ url: `/agent/registry/${id}/status`, method: 'post', data: { status } })
}

/** POST /agent/registry/{id}/model-config 模型绑定（modelCode → model_registry 数值模型 / llmCode → llm_model 生成式模型；空=解绑） */
export function postAgentModelConfig(id: string, modelCode: string, llmCode?: string) {
  return request<void>({ url: `/agent/registry/${id}/model-config`, method: 'post', data: { modelCode, llmCode } })
}
