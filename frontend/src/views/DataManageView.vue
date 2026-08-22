<template>
  <div>
    <div class="card">
      <div class="form-row">
        <button class="btn" :class="tab === 'report' ? 'btn-primary' : ''" @click="tab = 'report'">报表中心</button>
        <button class="btn" :class="tab === 'ocr' ? 'btn-primary' : ''" @click="tab = 'ocr'">OCR 复核</button>
        <span class="muted">WBS 6.4 数据管理：报表自动生成/导出（FR-DM-02）+ 低置信人工复核闭环（FR-DM-03）</span>
      </div>
    </div>

    <!-- ── 报表中心：模板 / 生成 / 实例 ── -->
    <template v-if="tab === 'report'">
      <div class="card">
        <h3>报表模板（GET /report/templates · 含口径说明）</h3>
        <table>
          <thead>
            <tr><th>编码</th><th>名称</th><th>类型</th><th>周期</th><th>口径说明</th></tr>
          </thead>
          <tbody>
            <tr v-if="templates.length === 0"><td colspan="5" class="muted">暂无数据</td></tr>
            <tr v-for="t in templates" :key="t.id">
              <td class="mono">{{ t.code }}</td>
              <td>{{ t.name }}</td>
              <td>{{ templateTypeLabel(t.type) }}</td>
              <td>{{ periodTypeLabel(t.periodType) }}（{{ t.periodType }}）</td>
              <td class="muted">{{ caliber(t.headerConfig) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card">
        <h3>生成报表实例（POST /report/instances · 数据快照落库，口径可追溯）</h3>
        <div class="form-row">
          <select v-model="gen.templateCode">
            <option value="">选择模板</option>
            <option v-for="t in templates" :key="t.id" :value="t.code">{{ t.name }}（{{ t.code }}）</option>
          </select>
          <input v-model="gen.period" placeholder="周期，如 2026-08-15 或 2026-08" />
          <button class="btn btn-primary" :disabled="!gen.templateCode || !gen.period" @click="onGenerate">生成</button>
          <span v-if="genResult" class="badge badge-green">实例 {{ genResult.instanceId }} · {{ genResult.generateStatus }}</span>
        </div>
      </div>

      <div class="card">
        <h3>报表实例（GET /report/instances · 按区域隔离）</h3>
        <div class="form-row">
          <span class="badge badge-blue">当前区域 {{ region.currentRegion }}</span>
          <button class="btn" @click="loadInstances">刷新</button>
        </div>
        <table>
          <thead>
            <tr><th>实例 ID</th><th>周期</th><th>区域</th><th>生成状态</th><th>生成时间</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="instances.length === 0"><td colspan="6" class="muted">暂无数据</td></tr>
            <tr v-for="r in instances" :key="r.id">
              <td class="mono">{{ r.id }}</td>
              <td class="mono">{{ r.period }}</td>
              <td class="mono">{{ r.regionCode }}</td>
              <td>
                <span class="badge" :class="r.generateStatus === 'success' ? 'badge-green' : 'badge-orange'">{{ r.generateStatus }}</span>
              </td>
              <td class="mono">{{ r.createdAt }}</td>
              <td><button class="btn" @click="onDownload(r.id)">导出</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <!-- ── OCR 复核工作台 ── -->
    <template v-else>
      <div class="card">
        <h3>OCR 复核工作台（GET /ocr/tasks · 低置信人工补录闭环，结果全留痕）</h3>
        <div class="form-row">
          <select v-model="reviewFilter">
            <option value="">全部复核状态</option>
            <option value="pending">待复核</option>
            <option value="reviewed">已复核</option>
            <option value="not_required">无需复核</option>
          </select>
          <button class="btn btn-primary" @click="loadTasks">查询</button>
        </div>
        <table>
          <thead>
            <tr><th>任务 ID</th><th>状态</th><th>置信度</th><th>复核状态</th><th>复核人</th><th>复核时间</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="tasks.length === 0"><td colspan="7" class="muted">暂无数据</td></tr>
            <tr v-for="t in tasks" :key="t.id">
              <td class="mono">{{ t.id }}</td>
              <td>{{ taskStatusLabel(t.status) }}（{{ t.status }}）</td>
              <td class="mono">{{ t.confidence }}</td>
              <td>
                <span class="badge" :class="t.reviewStatus === 'pending' ? 'badge-orange' : t.reviewStatus === 'reviewed' ? 'badge-green' : 'badge-gray'">{{ reviewStatusLabel(t.reviewStatus) }}（{{ t.reviewStatus }}）</span>
              </td>
              <td>{{ t.reviewer ?? '-' }}</td>
              <td class="mono">{{ t.reviewedAt ?? '-' }}</td>
              <td>
                <button class="btn" :disabled="t.reviewStatus !== 'pending'" @click="openReview(t)">复核</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="reviewTarget" class="card">
        <h3>人工复核：任务 {{ reviewTarget.id }}（置信度 {{ reviewTarget.confidence }}）</h3>
        <div class="form-row">
          <span class="muted">识别字段：</span>
          <textarea v-model="reviewFields" rows="4" style="width: 100%" class="mono" />
        </div>
        <div class="form-row">
          <input v-model="reviewComment" placeholder="复核备注（可选）" style="flex: 1" />
          <button class="btn btn-primary" @click="submitReview(true)">确认通过</button>
          <button class="btn" @click="submitReview(false)">修正保存</button>
          <button class="btn" @click="reviewTarget = null">取消</button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createReportInstance, downloadReport, getReportInstances, getReportTemplates } from '@/api/report'
import { getOcrTasks, reviewOcrTask } from '@/api/settlement'
import type { OcrTask, ReportInstance, ReportTemplate } from '@/api/types'
import { useRegionStore } from '@/stores/region'

const region = useRegionStore()
const tab = ref<'report' | 'ocr'>('report')

// ── 报表中心 ──
const templates = ref<ReportTemplate[]>([])
const instances = ref<ReportInstance[]>([])
const gen = reactive({ templateCode: '', period: '' })
const genResult = ref<{ instanceId: string; generateStatus: string } | null>(null)

/** 口径说明（header_config JSONB：caliber 字段） */
function caliber(headerConfig?: string): string {
  if (!headerConfig) return '-'
  try {
    const h = JSON.parse(headerConfig) as Record<string, unknown>
    return String(h.caliber ?? '-')
  } catch {
    return '-'
  }
}

/** V2.4 编码+名称：报表模板类型中文标签 */
function templateTypeLabel(t: string): string {
  return { settlement: '结算', forecast: '预测', assessment: '考核', trade: '交易', business: '经营' }[t] ?? t
}

/** V2.4 编码+名称：报表周期类型中文标签 */
function periodTypeLabel(t: string): string {
  return { daily: '日', weekly: '周', monthly: '月', yearly: '年' }[t] ?? t
}

/** V2.4 编码+名称：OCR 任务状态中文标签 */
function taskStatusLabel(s: string): string {
  return { success: '成功', pending: '待处理', failed: '失败' }[s] ?? s
}

/** V2.4 编码+名称：OCR 复核状态中文标签 */
function reviewStatusLabel(s: string): string {
  return { pending: '待复核', reviewed: '已复核' }[s] ?? s
}

async function loadTemplates() {
  try {
    templates.value = await getReportTemplates()
  } catch {
    templates.value = []
  }
}

async function loadInstances() {
  try {
    instances.value = await getReportInstances({ pageNo: 1, pageSize: 20 })
  } catch {
    instances.value = []
  }
}

async function onGenerate() {
  try {
    genResult.value = await createReportInstance({ templateCode: gen.templateCode, period: gen.period })
    await loadInstances()
  } catch {
    genResult.value = null
  }
}

function onDownload(id: string) {
  downloadReport(id).catch(() => undefined)
}

// ── OCR 复核工作台 ──
const tasks = ref<OcrTask[]>([])
const reviewFilter = ref('')
const reviewTarget = ref<OcrTask | null>(null)
const reviewFields = ref('')
const reviewComment = ref('')

async function loadTasks() {
  try {
    const res = await getOcrTasks({ pageNo: 1, pageSize: 20, ...(reviewFilter.value ? { reviewStatus: reviewFilter.value } : {}) })
    tasks.value = (res as unknown as { list: OcrTask[] }).list ?? []
  } catch {
    tasks.value = []
  }
}

function openReview(t: OcrTask) {
  reviewTarget.value = t
  reviewFields.value = JSON.stringify(t.fields ?? {}, null, 2)
  reviewComment.value = ''
}

async function submitReview(approved: boolean) {
  if (!reviewTarget.value) return
  let fields: Record<string, unknown> | undefined
  if (!approved) {
    try {
      fields = JSON.parse(reviewFields.value) as Record<string, unknown>
    } catch {
      return
    }
  }
  try {
    await reviewOcrTask(reviewTarget.value.id, { approved, fields, comment: reviewComment.value || undefined })
    reviewTarget.value = null
    await loadTasks()
  } catch {
    // 骨架阶段静默
  }
}

onMounted(() => {
  loadTemplates()
  loadInstances()
  loadTasks()
})
</script>
