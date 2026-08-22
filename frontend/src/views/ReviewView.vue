<template>
  <div>
    <!-- ── 统计卡（FR-RS-01 复盘看板） ── -->
    <div class="stat-grid">
      <div class="card stat-card"><div class="stat-value">{{ reports.length }}</div><div class="stat-label">复盘报告总数</div></div>
      <div class="card stat-card"><div class="stat-value">{{ weeklyCount }}</div><div class="stat-label">周报</div></div>
      <div class="card stat-card"><div class="stat-value">{{ monthlyCount }}</div><div class="stat-label">月报</div></div>
      <div class="card stat-card"><div class="stat-value">{{ completedCount }}</div><div class="stat-label">已完成（三层归因）</div></div>
    </div>

    <!-- ── 生成复盘报告（FR-RS-01） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">生成复盘报告（POST /review/reports · 周/月/专项 + 三层归因）</h3>
      </div>
      <div class="form-row">
        <select v-model="form.reportType">
          <option value="weekly">周报</option>
          <option value="monthly">月报</option>
          <option value="special">专项</option>
        </select>
        <input v-model="form.startDate" type="datetime-local" />
        <input v-model="form.endDate" type="datetime-local" />
        <input v-model="focusTopicsText" placeholder="聚焦主题（逗号分隔，可选）" style="width: 240px" />
        <button class="btn btn-primary" :disabled="creating" @click="onCreate">{{ creating ? '生成中…' : '生成报告' }}</button>
      </div>
    </div>

    <!-- ── 报告列表 ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">复盘报告列表（GET /review/reports）</h3>
        <select v-model="filters.reportType">
          <option value="">全部类型</option>
          <option value="weekly">周报</option>
          <option value="monthly">月报</option>
          <option value="special">专项</option>
        </select>
        <button class="btn" @click="load">查询</button>
      </div>
      <table>
        <thead>
          <tr><th>类型</th><th>周期</th><th>状态</th><th>收益摘要</th><th>改进建议</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="reports.length === 0"><td colspan="6" class="muted">暂无复盘报告，请先生成</td></tr>
          <tr v-for="r in reports" :key="r.id">
            <td><span class="badge" :class="typeClass(r.reportType)">{{ typeLabel(r.reportType) }}</span></td>
            <td class="mono">{{ r.periodStart }} ~ {{ r.periodEnd }}</td>
            <td><span class="badge" :class="r.status === 'completed' ? 'badge-green' : 'badge-orange'">{{ statusLabel(r.status) }}（{{ r.status }}）</span></td>
            <td class="muted">{{ summaryText(r.summary) }}</td>
            <td class="muted">{{ summaryText(r.suggestions) }}</td>
            <td>
              <button class="btn" @click="onView(r.id)">详情/归因</button>
              <button class="btn" @click="openFeedback(r.id)">策略回流</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 报告详情弹窗（FR-RS-02 三层归因） ── -->
    <div v-if="detail" class="modal-mask" @click.self="detail = null">
      <div class="modal" style="width: 720px; max-height: 84vh; overflow: auto">
        <h3>复盘报告详情（{{ detail.reportType }} · {{ detail.periodStart }} ~ {{ detail.periodEnd }}）</h3>
        <h4>收益摘要</h4>
        <div class="grid-2" v-if="detail.summary">
          <div v-for="s in toSummaryItems(detail.summary)" :key="s.label" class="card">
            <span class="muted">{{ s.label }}</span><br /><strong>{{ s.value }}</strong>
          </div>
        </div>
        <h4>偏差三层归因（预测 / 决策 / 执行）</h4>
        <div v-for="layer in detail.deviationAnalysis?.layers ?? []" :key="layer.layer" class="card">
          <h5 style="margin: 0 0 8px">{{ layerLabel(layer.layer) }}</h5>
          <table>
            <thead><tr><th>项目</th><th>数值</th><th>收益影响（元）</th><th>方向</th><th>原因</th></tr></thead>
            <tbody>
              <tr v-if="layer.items.length === 0"><td colspan="5" class="muted">无归因记录</td></tr>
              <tr v-for="it in layer.items" :key="it.item">
                <td>{{ it.item }}</td>
                <td class="mono">{{ it.value ?? '-' }}</td>
                <td class="mono">{{ it.impactAmount }}</td>
                <td><span class="badge" :class="it.direction === 'positive' ? 'badge-green' : 'badge-red'">{{ it.direction }}</span></td>
                <td class="muted">{{ it.reason }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <h4>策略评估</h4>
        <div class="grid-2" v-if="detail.strategyEval">
          <div v-for="s in toSummaryItems(detail.strategyEval)" :key="s.label" class="card">
            <span class="muted">{{ s.label }}</span><br /><strong>{{ s.value }}</strong>
          </div>
        </div>
        <div class="form-row" style="margin-top: 12px">
          <button class="btn" @click="detail = null">关闭</button>
          <button class="btn btn-primary" @click="openFeedback(detail.id)">策略回流（FR-RS-03）</button>
        </div>
      </div>
    </div>

    <!-- ── 策略回流弹窗（FR-RS-03 复盘结论沉淀策略库） ── -->
    <div v-if="feedbackTarget" class="modal-mask" @click.self="feedbackTarget = null">
      <div class="modal" style="width: 460px">
        <h3>策略回流（POST /review/strategy-feedback）</h3>
        <div class="form-row">
          <input v-model="feedback.strategyCode" list="review-strategies" placeholder="策略编码（如 STRAT-DA-PRICE，可搜索选择）" style="width: 220px" />
          <select v-model="feedback.feedback">
            <option value="effective">有效（effective）</option>
            <option value="invalid">失效（invalid）</option>
            <option value="adjust">调整（adjust）</option>
          </select>
        </div>
        <datalist id="review-strategies">
          <option v-for="s in strategies" :key="s.strategyCode" :value="s.strategyCode">{{ s.strategyName }}</option>
        </datalist>
        <div class="form-row" v-if="feedback.feedback === 'adjust'">
          <input v-model="feedback.paramsText" placeholder="调整参数 JSON（如 { riskAversion: 0.6 }）" style="width: 100%" />
        </div>
        <div class="muted">回流建议由交易主管确认后生效（status=pending）。</div>
        <div class="form-row" style="margin-top: 12px">
          <button class="btn" @click="feedbackTarget = null">取消</button>
          <button class="btn btn-primary" :disabled="submitting" @click="onFeedback">{{ submitting ? '提交中…' : '提交回流' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createReviewReport, getReviewReportDetail, getReviewReports, postStrategyFeedback,
  type ReviewReportDetail, type ReviewReportItem,
} from '@/api/review'
import { getStrategies } from '@/api/optimize'
import type { StrategyInfo } from '@/api/types'

const reports = ref<ReviewReportItem[]>([])
const detail = ref<ReviewReportDetail | null>(null)
const feedbackTarget = ref<string | null>(null)
const creating = ref(false)
const submitting = ref(false)
const focusTopicsText = ref('')
// 策略库关联（策略回流输入即搜即选）
const strategies = ref<StrategyInfo[]>([])

const form = reactive({ reportType: 'weekly' as 'weekly' | 'monthly' | 'special', startDate: dateOffset(-7), endDate: dateOffset(0) })
const filters = reactive({ reportType: '' })
const feedback = reactive({ strategyCode: 'STRAT-DA-PRICE', feedback: 'effective' as 'effective' | 'invalid' | 'adjust', paramsText: '' })

const weeklyCount = computed(() => reports.value.filter((r) => r.reportType === 'weekly').length)
const monthlyCount = computed(() => reports.value.filter((r) => r.reportType === 'monthly').length)
const completedCount = computed(() => reports.value.filter((r) => r.status === 'completed').length)

function dateOffset(days: number): string {
  const d = new Date()
  d.setDate(d.getDate() + days)
  return d.toISOString().slice(0, 10)
}

function typeLabel(t: string): string {
  return { weekly: '周报', monthly: '月报', special: '专项' }[t] ?? t
}
function typeClass(t: string): string {
  return t === 'weekly' ? 'badge-blue' : t === 'monthly' ? 'badge-green' : 'badge-orange'
}
/** V2.4 编码+名称：复盘报告状态中文标签 */
function statusLabel(s: string): string {
  return { completed: '已完成', running: '生成中', draft: '草稿', failed: '失败' }[s] ?? s
}
function layerLabel(l: string): string {
  return { forecast: '预测偏差', decision: '决策偏差', execution: '执行偏差' }[l] ?? l
}
function summaryText(items?: Array<{ label: string; value: string }> | Record<string, unknown>): string {
  const arr = toSummaryItems(items)
  if (arr.length === 0) return '-'
  return arr.slice(0, 2).map((i) => `${i.label}:${i.value}`).join('；')
}

/** 后端 summary/suggestions/strategyEval 为扁平对象（{period,revenue,...}）或 label/value 数组，统一转展示数组 */
function toSummaryItems(v: unknown): Array<{ label: string; value: string }> {
  if (!v) return []
  if (Array.isArray(v)) {
    return (v as Array<{ label: string; value: string }>).filter((i) => i && i.label != null)
  }
  if (typeof v === 'object') {
    return Object.entries(v as Record<string, unknown>).map(([k, val]) => ({ label: k, value: fmtValue(val) }))
  }
  return []
}

/** 嵌套对象/数组值转为可读文本（避免 [object Object]） */
function fmtValue(val: unknown): string {
  if (val == null) return '-'
  if (typeof val === 'object') {
    const s = JSON.stringify(val)
    return s && s.length > 60 ? s.slice(0, 60) + '…' : (s ?? '-')
  }
  return String(val)
}

async function load() {
  reports.value = (await getReviewReports({ ...(filters.reportType ? { reportType: filters.reportType } : {}) })) ?? []
}

async function onCreate() {
  creating.value = true
  try {
    await createReviewReport({
      reportType: form.reportType,
      startDate: form.startDate,
      endDate: form.endDate,
      focusTopics: focusTopicsText.value ? focusTopicsText.value.split(/[,，]/).map((s) => s.trim()).filter(Boolean) : undefined,
    })
    await load()
  } finally {
    creating.value = false
  }
}

async function onView(id: string) {
  detail.value = await getReviewReportDetail(id)
}

function openFeedback(reviewId: string) {
  feedbackTarget.value = reviewId
  feedback.feedback = 'effective'
  feedback.paramsText = ''
}

async function onFeedback() {
  if (!feedbackTarget.value) return
  submitting.value = true
  try {
    let updatedParams: Record<string, unknown> | undefined
    if (feedback.feedback === 'adjust' && feedback.paramsText) {
      try {
        updatedParams = JSON.parse(feedback.paramsText)
      } catch {
        alert('调整参数需为合法 JSON')
        return
      }
    }
    await postStrategyFeedback({ strategyCode: feedback.strategyCode, feedback: feedback.feedback, updatedParams, reviewId: feedbackTarget.value })
    feedbackTarget.value = null
    alert('回流建议已提交（待主管确认生效）')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  load()
  try {
    strategies.value = (await getStrategies()) ?? []
  } catch {
    strategies.value = []
  }
})
</script>
