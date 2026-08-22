<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">智能体管理（GET/POST /agent/**）</h3>
        <span class="muted">PRD FR-TR-05 智能体管理 · SRS FR-DM-02 七大智能体契约化 · 运行监控/效果评估/参数调优</span>
      </div>
      <div class="form-row">
        <span
          class="badge"
          :class="tab === 'registry' ? 'badge-blue' : ''"
          style="cursor: pointer"
          @click="tab = 'registry'"
        >注册表</span>
        <span
          class="badge"
          :class="tab === 'runs' ? 'badge-blue' : ''"
          style="cursor: pointer"
          @click="tab = 'runs'"
        >运行监控</span>
        <span
          class="badge"
          :class="tab === 'metrics' ? 'badge-blue' : ''"
          style="cursor: pointer"
          @click="tab = 'metrics'"
        >效果评估</span>
        <button class="btn" style="margin-left: auto" @click="onRefresh">刷新</button>
      </div>
    </div>

    <!-- ── 注册表：七大智能体（版本/职责/输入输出契约/状态） ── -->
    <div v-if="tab === 'registry'" class="card">
      <h3>智能体注册表（{{ agents.length }} / 7）</h3>
      <table>
        <thead>
          <tr><th>编码</th><th>名称</th><th>版本</th><th>职责</th><th>输出契约</th><th>模型</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="agents.length === 0"><td colspan="8" class="muted">暂无数据</td></tr>
          <tr v-for="a in agents" :key="a.id">
            <td class="mono">{{ a.agentCode }}</td>
            <td>{{ a.agentName }}</td>
            <td class="mono">{{ a.version }}</td>
            <td class="muted" style="max-width: 320px">{{ a.role }}</td>
            <td class="mono muted" style="max-width: 220px">{{ schemaText(a.outputSchema) }}</td>
            <td class="mono muted">{{ modelText(a.modelConfig) }}
              <button class="btn" style="margin-left: 6px; padding: 0 6px" @click="bindOpen(a)">绑定</button>
            </td>
            <td>
              <span class="badge" :class="statusClass(a.status)">{{ statusLabel(a.status) }}</span>
            </td>
            <td>
              <select :value="a.status" @change="onStatusChange(a, $event)">
                <option value="active">active</option>
                <option value="disabled">disabled</option>
                <option value="maintenance">maintenance</option>
              </select>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 运行监控：agent_run 记录（输入快照/输出/置信度/耗时） ── -->
    <div v-if="tab === 'runs'" class="card">
      <h3>运行监控（agent_run · 最近 {{ runs.length }} 条）</h3>
      <div class="form-row">
        <select v-model="filterCode" @change="onLoadRuns">
          <option value="">全部智能体</option>
          <option v-for="a in agents" :key="a.agentCode" :value="a.agentCode">{{ a.agentName }}</option>
        </select>
        <input v-model="filterSession" placeholder="会话编号（如 DS20260820XXXX）" style="width: 220px" @change="onLoadRuns" />
        <span class="muted">置信度 &lt; 0.7 高亮（SRS FR-DM-02 R3 强制人工关注）</span>
      </div>
      <div class="table-scroll">
        <table>
          <thead>
            <tr><th>运行ID</th><th>智能体</th><th>会话</th><th>输入快照</th><th>输出</th><th>置信度</th><th>耗时</th><th>状态</th><th>时间</th></tr>
          </thead>
          <tbody>
            <tr v-if="runs.length === 0"><td colspan="9" class="muted">暂无运行记录</td></tr>
            <tr v-for="r in runs" :key="r.runId" :class="{ 'row-warn': Number(r.confidence) < 0.7 }">
              <td class="mono">{{ shortId(r.runId) }}</td>
              <td><span class="badge badge-blue">{{ r.agentCode }}</span></td>
              <td class="mono">{{ r.sessionId }}</td>
              <td class="muted" style="max-width: 200px">{{ text(r.inputSnapshot) }}</td>
              <td class="muted" style="max-width: 260px">
                <span v-if="typeof r.output === 'object' && r.output && r.output.mode" class="badge" :class="r.output.mode === 'model' ? 'badge-green' : 'badge-gray'" style="margin-right: 4px">{{ r.output.mode === 'model' ? '模型推理' : '算法' }}</span>
                {{ text(r.output) }}
              </td>
              <td>
                <span class="badge" :class="Number(r.confidence) < 0.7 ? 'badge-red' : 'badge-green'">{{ r.confidence }}</span>
              </td>
              <td class="mono">{{ r.elapsedMs }}ms</td>
              <td>
                <span class="badge" :class="r.status === 'success' ? 'badge-green' : 'badge-orange'">{{ runStatusLabel(r.status) }}（{{ r.status }}）</span>
              </td>
              <td class="mono muted">{{ r.createdAt }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ── 效果评估：按智能体聚合成功率/平均置信度/平均耗时 ── -->
    <div v-if="tab === 'metrics'" class="card">
      <h3>效果评估（成功率 / 平均置信度 / 平均耗时）</h3>
      <table>
        <thead>
          <tr><th>智能体</th><th>名称</th><th>运行次数</th><th>成功率</th><th>平均置信度</th><th>平均耗时</th><th>健康度</th></tr>
        </thead>
        <tbody>
          <tr v-if="metrics.length === 0"><td colspan="7" class="muted">暂无运行数据（创建决策会话后产生）</td></tr>
          <tr v-for="m in metrics" :key="m.agentCode">
            <td class="mono">{{ m.agentCode }}</td>
            <td>{{ nameOf(m.agentCode) }}</td>
            <td class="mono">{{ m.runCount }}</td>
            <td class="mono">{{ m.successRate === null ? '-' : m.successRate + '%' }}</td>
            <td class="mono">{{ m.avgConfidence === null ? '-' : m.avgConfidence }}</td>
            <td class="mono">{{ m.avgElapsedMs === null ? '-' : m.avgElapsedMs + 'ms' }}</td>
            <td>
              <span class="badge" :class="healthClass(m)">{{ healthText(m) }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 模型绑定弹窗（PRD FR-TR-05：智能体绑定 model_registry 在线模型 → 编排走模型推理） ── -->
    <div v-if="bindTarget" class="modal-mask" @click.self="bindTarget = null">
      <div class="modal" style="width: 500px">
        <h3>模型绑定（{{ bindTarget.agentName }} · {{ bindTarget.agentCode }}）</h3>
        <div class="muted" style="margin-bottom: 8px">数值模型（model_registry）：绑定后编排时该智能体调用模型推理；不选则回退确定性算法</div>
        <select v-model="bindModelCode" style="width: 100%">
          <option value="">（不绑定 · 确定性算法）</option>
          <option value="price">price - 现货价格预测模型</option>
          <option value="generation">generation - 新能源出力预测模型</option>
          <option value="load">load - 负荷预测模型</option>
        </select>
        <div class="muted" style="margin: 8px 0">LLM 生成式模型（llm_model · V2.2 可配置关联）：决策输出叠加 LLM 解读，失败可回退</div>
        <select v-model="bindLlmCode" style="width: 100%">
          <option value="">（不关联 LLM）</option>
          <option v-for="m in llms" :key="m.id" :value="m.modelCode" :disabled="m.status !== 'enabled'">{{ m.modelName }}（{{ m.modelCode }} · {{ m.provider }}）</option>
        </select>
        <div class="form-row" style="margin-top: 12px">
          <button class="btn" @click="bindTarget = null">取消</button>
          <button class="btn btn-primary" :disabled="submitting" @click="onBindModel">{{ submitting ? '提交中…' : '保存绑定' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAgentMetrics, getAgentRegistry, getAgentRuns, postAgentModelConfig, postAgentStatus } from '@/api/agent'
import { getLlmModels } from '@/api/model'
import type { LlmModelItem } from '@/api/model'
import type { AgentMetricItem, AgentRegistryItem, AgentRunItem } from '@/api/agent'

const tab = ref('registry')
const agents = ref<AgentRegistryItem[]>([])
const runs = ref<AgentRunItem[]>([])
const metrics = ref<AgentMetricItem[]>([])
const filterCode = ref('')
const filterSession = ref('')
const bindTarget = ref<AgentRegistryItem | null>(null)
const bindModelCode = ref('')
const bindLlmCode = ref('')
const llms = ref<LlmModelItem[]>([])
const submitting = ref(false)

const AGENT_NAMES: Record<string, string> = {
  forecast: '预测智能体',
  market: '行情智能体',
  quote: '报价智能体',
  risk: '风险智能体',
  compliance: '合规智能体',
  settlement: '结算智能体',
  review: '复盘智能体',
}

onMounted(() => {
  onRefresh()
})

function onRefresh() {
  getAgentRegistry().then((d) => (agents.value = d))
  getAgentMetrics().then((d) => (metrics.value = d))
  getLlmModels().then((d) => (llms.value = d)).catch(() => (llms.value = []))
  onLoadRuns()
}

async function onLoadRuns() {
  try {
    const params: { agentCode?: string; sessionId?: string; limit?: number } = { limit: 50 }
    if (filterCode.value) params.agentCode = filterCode.value
    if (filterSession.value) params.sessionId = filterSession.value
    runs.value = await getAgentRuns(params)
  } catch {
    runs.value = []
  }
}

async function onStatusChange(a: AgentRegistryItem, e: Event) {
  const status = (e.target as HTMLSelectElement).value
  if (status === a.status) return
  try {
    await postAgentStatus(a.id, status)
    a.status = status
  } catch {
    // 后端 500 时回滚下拉
    ;(e.target as HTMLSelectElement).value = a.status
  }
}

function bindOpen(a: AgentRegistryItem) {
  bindTarget.value = a
  const cfg = a.modelConfig as Record<string, unknown> | undefined
  bindModelCode.value = String(cfg?.modelCode ?? '')
  bindLlmCode.value = String(cfg?.llmCode ?? '')
}

async function onBindModel() {
  if (!bindTarget.value) return
  submitting.value = true
  try {
    await postAgentModelConfig(bindTarget.value.id, bindModelCode.value, bindLlmCode.value || undefined)
    const cfg: Record<string, unknown> = {}
    if (bindModelCode.value) cfg.modelCode = bindModelCode.value
    if (bindLlmCode.value) cfg.llmCode = bindLlmCode.value
    bindTarget.value.modelConfig = cfg
    bindTarget.value = null
    await getAgentRegistry().then((d) => (agents.value = d))
  } catch {
    alert('绑定失败（模型编码须存在于 model_registry / llm_model）')
  } finally {
    submitting.value = false
  }
}

function nameOf(code: string): string {
  return AGENT_NAMES[code] ?? code
}

function statusLabel(s: string): string {
  return { active: '运行中', disabled: '停用', maintenance: '维护中' }[s] ?? s
}

/** V2.4 编码+名称：运行记录状态中文标签 */
function runStatusLabel(s: string): string {
  return { success: '成功', running: '执行中', failed: '失败', queued: '排队中' }[s] ?? s
}

function statusClass(s: string): string {
  return { active: 'badge-green', disabled: 'badge-orange', maintenance: 'badge-orange' }[s] ?? ''
}

function schemaText(v: Record<string, unknown> | undefined): string {
  if (!v) return '-'
  return Object.keys(v).join(', ')
}

function modelText(v: Record<string, unknown> | undefined): string {
  if (!v) return '-'
  const parts: string[] = []
  if (v.modelCode) parts.push(String(v.modelCode))
  if (v.llmCode) parts.push('LLM:' + String(v.llmCode))
  return parts.length ? parts.join(' + ') : '-'
}

function healthClass(m: AgentMetricItem): string {
  if (m.runCount === 0) return ''
  if (m.successRate !== null && m.successRate >= 95 && m.avgConfidence !== null && m.avgConfidence >= 0.75) {
    return 'badge-green'
  }
  return 'badge-orange'
}

function healthText(m: AgentMetricItem): string {
  if (m.runCount === 0) return '未运行'
  if (m.successRate !== null && m.successRate >= 95 && m.avgConfidence !== null && m.avgConfidence >= 0.75) {
    return '健康'
  }
  return '关注'
}

function shortId(id: string): string {
  return id.length > 28 ? id.slice(0, 28) + '…' : id
}

function text(v: Record<string, unknown> | string | undefined): string {
  if (v == null) return '-'
  if (typeof v === 'string') return v.length > 80 ? v.slice(0, 80) + '…' : v
  const s = JSON.stringify(v)
  return s.length > 80 ? s.slice(0, 80) + '…' : s
}
</script>
