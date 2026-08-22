<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">预测中心（WBS 4.0 · GET/POST /forecast/**）</h3>
        <span class="muted">FR-TR-01~03 负荷/电价/新能源预测 P0 · FR-TR-06 置信区间（96 点 + 90% 区间）</span>
      </div>
      <div class="form-row">
        <select v-model="form.modelCode">
          <option value="price">现货价格预测</option>
          <option value="load">负荷预测</option>
          <option value="generation">新能源出力预测</option>
        </select>
        <input v-model="form.predictDate" type="datetime-local" />
        <select v-if="form.modelCode === 'price'" v-model="form.marketType">
          <option value="intra_province">省内</option>
          <option value="inter_province">省间</option>
        </select>
        <input v-else v-model="form.regionCode" placeholder="区域编码（如 370000）" />
        <button class="btn btn-primary" :disabled="creating" @click="onCreate">{{ creating ? '创建中…' : '创建预测任务' }}</button>
      </div>
      <div class="form-row" v-if="lastTaskId">
        <span class="badge badge-blue">taskId：{{ lastTaskId }}</span>
        <button class="btn" @click="onRefreshStatus">刷新状态</button>
        <span v-if="taskStatus" class="muted">
          状态：{{ taskStatusLabel(taskStatus.status) }}（{{ taskStatus.status }}） · 版本：{{ taskStatus.modelVersion ?? '-' }} · 耗时：{{ taskStatus.elapsedMs ?? '-' }}ms
        </span>
      </div>
    </div>

    <!-- ── 96 点预测结果 ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">预测结果曲线（GET /forecast/results）</h3>
        <select v-model="resultQuery.predictType">
          <option value="price">电价</option>
          <option value="load">负荷</option>
          <option value="generation">新能源出力</option>
        </select>
        <input v-model="resultQuery.tradeDate" type="datetime-local" />
        <button class="btn" @click="onLoadResults">查询</button>
        <span class="muted">共 {{ points.length }} 点</span>
      </div>

      <!-- GAP-07 修复：96 点 SVG 曲线 + 90% 置信区间阴影（无 ECharts 依赖） -->
      <svg v-if="chartPoints.length >= 2" :viewBox="`0 0 ${W} ${H}`" style="width: 100%; height: 260px">
        <!-- 网格 + Y 轴刻度 -->
        <g v-for="i in 4" :key="'y' + i">
          <line :x1="pad.l" :y1="yAt(i / 4)" :x2="W - pad.r" :y2="yAt(i / 4)" stroke="#e5e7eb" stroke-width="1" />
          <text :x="pad.l - 6" :y="yAt(i / 4) + 4" text-anchor="end" font-size="10" fill="#9ca3af">{{ fmt(yVal(1 - i / 4)) }}</text>
        </g>
        <!-- 置信区间（上界/下界围合阴影） -->
        <polygon :points="bandPoints" fill="#93c5fd" opacity="0.35" />
        <!-- 预测值主曲线 -->
        <polyline :points="mainPoints" fill="none" stroke="#2f6fed" stroke-width="2" />
        <!-- 上下界虚线 -->
        <polyline :points="upperPoints" fill="none" stroke="#93c5fd" stroke-width="1" stroke-dasharray="4 3" />
        <polyline :points="lowerPoints" fill="none" stroke="#93c5fd" stroke-width="1" stroke-dasharray="4 3" />
        <text :x="pad.l" :y="H - 8" font-size="10" fill="#9ca3af">{{ chartPoints[0].pointTime }}</text>
        <text :x="W - pad.r" :y="H - 8" text-anchor="end" font-size="10" fill="#9ca3af">{{ chartPoints[chartPoints.length - 1].pointTime }}</text>
      </svg>
      <div v-else class="muted">暂无足够预测点绘制曲线（需 ≥2 点，请先创建/查询）</div>

      <table>
        <thead>
          <tr><th>时刻</th><th>预测值</th><th>下界</th><th>上界</th><th>置信度</th></tr>
        </thead>
        <tbody>
          <tr v-if="points.length === 0"><td colspan="5" class="muted">请选择预测类型与日期查询</td></tr>
          <tr v-for="p in points.slice(0, 12)" :key="p.pointTime">
            <td class="mono">{{ p.pointTime }}</td>
            <td class="mono">{{ p.value }}</td>
            <td class="muted mono">{{ p.lowerBound }}</td>
            <td class="muted mono">{{ p.upperBound }}</td>
            <td class="mono">{{ p.confidence }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 模型注册 ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">模型注册（GET /forecast/models · 与 model 域共用 model_registry）</h3>
        <select v-model="trainModel">
          <option v-for="m in models" :key="m.modelCode" :value="m.modelCode">{{ m.modelName }}</option>
        </select>
        <select v-model="trainMode">
          <option value="daily_increment">日增量训练</option>
          <option value="weekly_full">周全量训练</option>
        </select>
        <button class="btn" :disabled="training" @click="onTrain">{{ training ? '训练中…' : '触发训练' }}</button>
      </div>
      <table>
        <thead>
          <tr><th>编码</th><th>名称</th><th>版本</th><th>框架</th><th>状态</th></tr>
        </thead>
        <tbody>
          <tr v-if="models.length === 0"><td colspan="5" class="muted">暂无数据</td></tr>
          <tr v-for="m in models" :key="m.modelCode">
            <td class="mono">{{ m.modelCode }}</td>
            <td>{{ m.modelName }}</td>
            <td class="mono">{{ m.version }}</td>
            <td class="mono">{{ m.framework }}</td>
            <td><span class="badge" :class="m.status === 'online' ? 'badge-green' : 'badge-orange'">{{ statusLabel(m.status) }}（{{ m.status }}）</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  getForecastModels,
  getForecastResults,
  getForecastTaskStatus,
  postForecastTask,
  postForecastTrain,
} from '@/api/forecast'
import type { ForecastPoint, ForecastTaskStatus, ModelInfo } from '@/api/types'

const form = ref({ modelCode: 'price', predictDate: today(), marketType: 'intra_province', regionCode: '370000' })
const creating = ref(false)
const lastTaskId = ref('')
const taskStatus = ref<ForecastTaskStatus | null>(null)
const resultQuery = ref({ predictType: 'price', tradeDate: today() })
const points = ref<ForecastPoint[]>([])
const models = ref<ModelInfo[]>([])
const trainModel = ref('price')
const trainMode = ref('daily_increment')
const training = ref(false)

// ── SVG 曲线几何（GAP-07：预测值 + 置信区间） ──
const W = 720
const H = 240
const pad = { l: 56, r: 12, t: 10, b: 20 }
const chartPoints = computed(() =>
  points.value.filter((p) => Number.isFinite(p.value) && Number.isFinite(p.lowerBound) && Number.isFinite(p.upperBound)),
)
const allVals = computed(() => {
  const arr: number[] = []
  for (const p of chartPoints.value) {
    arr.push(p.value, p.lowerBound, p.upperBound)
  }
  return arr
})
const yMin = computed(() => Math.min(...allVals.value))
const yMax = computed(() => Math.max(...allVals.value))
const ySpan = computed(() => (yMax.value - yMin.value) || 1)

function yVal(ratio: number): number {
  return yMin.value + ySpan.value * ratio
}
function yAt(ratio: number): number {
  return pad.t + (H - pad.t - pad.b) * (1 - ratio)
}
function xAt(i: number): number {
  const n = chartPoints.value.length
  return n <= 1 ? pad.l : pad.l + ((W - pad.l - pad.r) * i) / (n - 1)
}
function yy(v: number): number {
  return yAt((v - yMin.value) / ySpan.value)
}
const mainPoints = computed(() => chartPoints.value.map((p, i) => `${xAt(i).toFixed(1)},${yy(p.value).toFixed(1)}`).join(' '))
const upperPoints = computed(() => chartPoints.value.map((p, i) => `${xAt(i).toFixed(1)},${yy(p.upperBound).toFixed(1)}`).join(' '))
const lowerPoints = computed(() => chartPoints.value.map((p, i) => `${xAt(i).toFixed(1)},${yy(p.lowerBound).toFixed(1)}`).join(' '))
const bandPoints = computed(() =>
  [...chartPoints.value.map((p, i) => `${xAt(i).toFixed(1)},${yy(p.upperBound).toFixed(1)}`),
    ...[...chartPoints.value].reverse().map((p, i) => `${xAt(chartPoints.value.length - 1 - i).toFixed(1)},${yy(p.lowerBound).toFixed(1)}`)]
    .join(' '),
)
function fmt(v: number): string {
  return Math.abs(v) >= 100 ? String(Math.round(v)) : v.toFixed(1)
}

/** V2.4 编码+名称：预测任务状态中文标签 */
function taskStatusLabel(s: string): string {
  return { queued: '排队中', running: '执行中', success: '成功', failed: '失败' }[s] ?? s
}

/** V2.4 编码+名称：模型状态中文标签 */
function statusLabel(s: string): string {
  return { online: '在线', evaluating: '评估中', training: '训练中', rolled_back: '已回滚', offline: '离线' }[s] ?? s
}

function today(): string {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  return d.toISOString().slice(0, 10)
}

async function onCreate() {
  creating.value = true
  try {
    const res = await postForecastTask({
      modelCode: form.value.modelCode,
      predictDate: form.value.predictDate,
      marketType: form.value.modelCode === 'price' ? form.value.marketType : undefined,
      regionCode: form.value.modelCode === 'price' ? undefined : form.value.regionCode,
    })
    lastTaskId.value = res.taskId
    taskStatus.value = null
    await onRefreshStatus()
    resultQuery.value = { predictType: form.value.modelCode, tradeDate: form.value.predictDate }
    await onLoadResults()
  } finally {
    creating.value = false
  }
}

async function onRefreshStatus() {
  if (!lastTaskId.value) return
  taskStatus.value = await getForecastTaskStatus(lastTaskId.value)
}

async function onLoadResults() {
  points.value = (await getForecastResults(resultQuery.value)) ?? []
}

async function onTrain() {
  training.value = true
  try {
    const res = await postForecastTrain({ modelCode: trainModel.value, mode: trainMode.value })
    alert(`训练任务已创建：taskId=${res.taskId}`)
  } finally {
    training.value = false
  }
}

onMounted(async () => {
  models.value = (await getForecastModels()) ?? []
  if (models.value.length > 0) trainModel.value = models.value[0].modelCode
})
</script>
