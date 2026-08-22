<template>
  <div>
    <!-- 页头（对齐原型 dashboard.html：日期 + 上下文 + 快捷操作） -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">决策驾驶舱 · {{ today }}</h3>
        <span class="muted">{{ auth.displayName }}（{{ auth.user?.username }}）</span>
        <span class="badge badge-blue">{{ (auth.user?.roles ?? []).join(', ') || '—' }}</span>
        <span class="badge badge-green">区域 {{ region.currentRegion || '—' }}</span>
        <span class="muted">已连接 {{ apiBase }}</span>
      </div>
      <div class="form-row" style="margin-bottom: 0">
        <router-link class="btn" to="/market">市场行情</router-link>
        <router-link class="btn" to="/trade">交易申报</router-link>
        <router-link class="btn" to="/forecast">预测中心</router-link>
        <router-link class="btn" to="/intel">情报中心</router-link>
      </div>
    </div>

    <!-- 统计卡（对齐原型 wf-stat：大数字 + 标签 + 环比说明） -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-label">现货均价（近 7 日）</div>
        <div class="stat-value">{{ stats.spotAvg ?? '—' }}</div>
        <div class="stat-trend">元/MWh · 日前市场 96 点</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">持仓电量</div>
        <div class="stat-value">{{ stats.positionMwh ?? '—' }}</div>
        <div class="stat-trend">MWh · 现货持仓曲线均值</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">待确认方案</div>
        <div class="stat-value" :class="stats.pendingPlans > 0 ? 'danger' : ''">{{ stats.pendingPlans }}</div>
        <div class="stat-trend">日滚动方案待确认</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">未读消息</div>
        <div class="stat-value" :class="stats.unreadMessages > 0 ? 'danger' : ''">{{ stats.unreadMessages }}</div>
        <div class="stat-trend">消息中心 · 待办/情报推送</div>
      </div>
    </div>

    <!-- 2-1 栅格：行情速览 + 预测摘要（对齐原型 dashboard.html） -->
    <div class="grid-2">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">行情速览（GET /market/price/spot · 最近时点）</h3>
          <span v-if="spotAvg !== null" class="badge badge-blue">近 7 日均价 {{ spotAvg }} 元/MWh</span>
        </div>
        <table>
          <thead>
            <tr><th>时点</th><th>出清价（元/MWh）</th><th>成交量（MWh）</th></tr>
          </thead>
          <tbody>
            <tr v-if="spotRows.length === 0"><td colspan="3" class="muted">暂无行情数据</td></tr>
            <tr v-for="p in spotRows" :key="p.ts">
              <td class="mono">{{ p.ts }}</td>
              <td class="mono">{{ p.price.toFixed(2) }}</td>
              <td class="mono">{{ p.volume }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card">
        <h3>预测模型摘要（GET /forecast/models · MAPE / 方向准确率）</h3>
        <table>
          <thead>
            <tr><th>模型</th><th>版本</th><th>MAPE</th><th>方向准确率</th><th>状态</th></tr>
          </thead>
          <tbody>
            <tr v-if="models.length === 0"><td colspan="5" class="muted">暂无模型</td></tr>
            <tr v-for="m in models" :key="m.modelCode">
              <td>{{ modelName(m.modelCode) }}</td>
              <td class="mono">{{ m.version }}</td>
              <td class="mono">{{ pct(metric(m, 'mape')) }}</td>
              <td class="mono">{{ pct(metric(m, 'directional_accuracy')) }}</td>
              <td><span class="badge" :class="m.status === 'online' ? 'badge-green' : 'badge-gray'">{{ statusLabel(m.status) }}（{{ m.status }}）</span></td>
            </tr>
          </tbody>
        </table>
        <div v-if="forecastTip" class="muted" style="margin-top: 8px">{{ forecastTip }}</div>
      </div>
    </div>

    <!-- 2-1 栅格：待办任务 + 消息流 -->
    <div class="grid-2" style="margin-top: 16px">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">待办任务（GET /trade/rolling-plans · 日滚动方案）</h3>
          <router-link class="btn" to="/trade">全部方案 →</router-link>
        </div>
        <table>
          <thead>
            <tr><th>交易日期</th><th>场景</th><th>类型</th><th>预期收益（万元）</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="plans.length === 0"><td colspan="6" class="muted">暂无待办方案（可在交易申报创建日滚动方案）</td></tr>
            <tr v-for="p in plans" :key="p.id">
              <td class="mono">{{ p.tradeDate }}</td>
              <td>{{ scenarioLabel(p.scenario) }}（{{ p.scenario }}）</td>
              <td>{{ planTypeLabel(p.planType) }}</td>
              <td class="mono">{{ p.expectedRevenue }}</td>
              <td><span class="badge" :class="p.status === 'confirmed' ? 'badge-green' : p.status === 'pending' ? 'badge-orange' : 'badge-gray'">{{ statusLabel(p.status) }}（{{ p.status }}）</span></td>
              <td>
                <button v-if="p.status === 'pending'" class="btn" @click="onConfirm(p)">确认</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">消息流（GET /message/list · 未读）</h3>
          <router-link class="btn" to="/message">消息中心 →</router-link>
        </div>
        <table>
          <thead>
            <tr><th>类型</th><th>标题</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="messages.length === 0"><td colspan="4" class="muted">暂无未读消息</td></tr>
            <tr v-for="m in messages" :key="m.id" :class="{ 'row-unread': m.readStatus === 'unread' }">
              <td>
                <span class="badge" :class="m.msgType === 'approval_task' ? 'badge-red' : m.msgType === 'intel_push' ? 'badge-purple' : m.msgType === 'settlement_diff' ? 'badge-orange' : 'badge-blue'">
                  {{ msgTypeLabel(m.msgType) }}
                </span>
              </td>
              <td>{{ m.title }}</td>
              <td><span class="badge" :class="m.readStatus === 'unread' ? 'badge-orange' : 'badge-gray'">{{ m.readStatus === 'unread' ? '未读' : '已读' }}</span></td>
              <td>
                <button v-if="m.readStatus === 'unread'" class="btn" @click="onRead(m.id)">标已读</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRegionStore } from '@/stores/region'
import { getMessages, markMessageRead } from '@/api/message'
import { getRollingPlans, confirmRollingPlan } from '@/api/trade'
import { getForecastModels } from '@/api/forecast'
import { getSpotPrice } from '@/api/market'
import type { ModelInfo, PricePoint, RollingPlan } from '@/api/types'

const auth = useAuthStore()
const region = useRegionStore()
// 模板无法直接解析 import.meta.env，先取到 script 变量再插值
const apiBase = import.meta.env.VITE_API_BASE as string

function todayStr(): string {
  const d = new Date()
  const p = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`
}
const today = todayStr()

// ── 行情速览 ──
const spotRows = ref<PricePoint[]>([])
const spotAvg = ref<number | null>(null)

async function loadSpot() {
  try {
    // 近 7 日窗口（种子行情数据按历史日期生成，取最近一段展示）
    const end = today
    const startDate = new Date(Date.now() - 6 * 86400_000)
    const p = (n: number) => String(n).padStart(2, '0')
    const start = `${startDate.getFullYear()}-${p(startDate.getMonth() + 1)}-${p(startDate.getDate())} 00:00:00`
    const rows = await getSpotPrice({
      marketType: 'intra_province',
      stage: 'day_ahead',
      startAt: start,
      endAt: `${end} 23:59:59`,
    })
    const valid = (rows || []).filter((r) => r && typeof r.price === 'number')
    spotRows.value = valid.slice(-8)
    if (valid.length > 0) {
      spotAvg.value = Math.round((valid.reduce((s, r) => s + r.price, 0) / valid.length) * 100) / 100
    }
  } catch {
    spotRows.value = []
    spotAvg.value = null
  }
}

// ── 预测模型摘要 ──
const models = ref<ModelInfo[]>([])
const forecastTip = ref('')
function modelName(code: string): string {
  const map: Record<string, string> = { price: '现货价格预测', load: '负荷预测', generation: '新能源出力预测' }
  return map[code] ?? code
}

/** V2.4 编码+名称：模型状态中文标签 */
function statusLabel(s: string): string {
  return { online: '在线', evaluating: '评估中', training: '训练中', rolled_back: '已回滚' }[s] ?? s
}

/** V2.4 编码+名称：滚动方案场景中文标签 */
function scenarioLabel(s: string): string {
  return { base: '基准', optimistic: '乐观', pessimistic: '悲观' }[s] ?? s
}

/** V2.4 编码+名称：滚动方案类型中文标签 */
function planTypeLabel(s: string): string {
  return { daily: '日滚动', rolling_N: 'N 日滚动', backtest: '回测' }[s] ?? s
}
function metric(m: ModelInfo, key: string): number | undefined {
  const v = m.metrics
  if (!v) return undefined
  if (typeof v === 'string') {
    try {
      const parsed = JSON.parse(v) as Record<string, unknown>
      return typeof parsed[key] === 'number' ? (parsed[key] as number) : undefined
    } catch {
      return undefined
    }
  }
  const val = v[key]
  return typeof val === 'number' ? val : undefined
}
function pct(v: number | undefined): string {
  if (v === undefined || v === null) return '-'
  return `${(v * 100).toFixed(1)}%`
}
async function loadModels() {
  try {
    models.value = (await getForecastModels()) || []
    if (models.value.length > 0 && !models.value.some((m) => m.metrics)) {
      forecastTip.value = '模型指标（MAPE/方向准确率）以 model_registry.metrics 为准。'
    }
  } catch {
    models.value = []
  }
}

// ── 待办任务：日滚动方案 ──
const plans = ref<RollingPlan[]>([])
async function loadPlans() {
  try {
    const res = await getRollingPlans({ pageNo: 1, pageSize: 10 })
    plans.value = (res as unknown as { records?: RollingPlan[] }).records ?? (res as unknown as RollingPlan[]) ?? []
  } catch {
    plans.value = []
  }
}
async function onConfirm(p: RollingPlan) {
  try {
    await confirmRollingPlan(p.id)
    await loadPlans()
  } catch {
    // 确认失败静默（骨架阶段）
  }
}

// ── 消息流 ──
const messages = ref<Array<{ id: string; msgType: string; title: string; readStatus: string }>>([])
function msgTypeLabel(t: string): string {
  const map: Record<string, string> = { approval_task: '审批待办', intel_push: '情报推送', settlement_diff: '结算差异', system: '系统通知' }
  return map[t] ?? t
}
async function onRead(id: string) {
  try {
    await markMessageRead(id)
    await loadMessages()
  } catch {
    // 骨架阶段静默
  }
}
async function loadMessages() {
  try {
    const res = await getMessages({ unreadOnly: true, pageNo: 1, pageSize: 10 })
    messages.value = ((res as unknown as { list: Array<{ id: string; msgType: string; title: string; readStatus: string }> }).list ?? [])
  } catch {
    messages.value = []
  }
}

// ── 统计卡聚合 ──
const positionMwh = ref<number | null>(null)
const stats = computed(() => ({
  spotAvg: spotAvg.value,
  positionMwh: positionMwh.value,
  pendingPlans: plans.value.filter((p) => p.status === 'pending').length,
  unreadMessages: messages.value.length,
}))

async function loadPositionAvg() {
  try {
    const { getPositions } = await import('@/api/trade')
    const res = (await getPositions({ tradeDate: today })) as unknown as { spot?: number[]; longTerm?: number[] }
    const arr = res?.spot?.filter((v) => typeof v === 'number') ?? []
    if (arr.length === 0) {
      positionMwh.value = null
      return
    }
    positionMwh.value = Math.round((arr.reduce((s, v) => s + v, 0) / arr.length) * 100) / 100
  } catch {
    positionMwh.value = null
  }
}

onMounted(() => {
  loadSpot()
  loadModels()
  loadPlans()
  loadMessages()
  loadPositionAvg()
})
</script>
