<template>
  <div>
    <!-- ── 行情查询（FR-PL-01 现货价格 P0） ── -->
    <div class="card">
      <h3>现货价格查询（GET /market/price/spot）</h3>
      <div class="form-row">
        <select v-model="query.marketType">
          <option value="intra_province">省内</option>
          <option value="inter_province">省间</option>
        </select>
        <select v-model="query.stage">
          <option value="day_ahead">日前</option>
          <option value="real_time">实时</option>
        </select>
        <input v-model="query.startAt" type="datetime-local" style="width: 220px" />
        <input v-model="query.endAt" type="datetime-local" style="width: 220px" />
        <button class="btn btn-primary" @click="load">查询</button>
      </div>
      <div class="muted" style="margin-top: 8px">请求自动携带 X-Region-Code: {{ region.currentRegion || '—' }}</div>
    </div>

    <!-- ── 价格曲线（GAP-04 修复：SVG 自绘折线，前端无 ECharts 依赖） ── -->
    <div class="card">
      <h3>价格曲线（{{ pricePoints.length }} 点时序 · SVG 自绘）</h3>
      <svg v-if="pricePoints.length >= 2" :viewBox="`0 0 ${W} ${H}`" style="width: 100%; height: 240px">
        <!-- 水平网格 + Y 轴刻度 -->
        <g v-for="i in 4" :key="'y' + i">
          <line :x1="pad.l" :y1="yAt(i / 4)" :x2="W - pad.r" :y2="yAt(i / 4)" stroke="#e5e7eb" stroke-width="1" />
          <text :x="pad.l - 6" :y="yAt(i / 4) + 4" text-anchor="end" font-size="10" fill="#9ca3af">{{ fmt(yValue(1 - i / 4)) }}</text>
        </g>
        <!-- 价格折线 -->
        <polyline :points="linePoints" fill="none" stroke="#2f6fed" stroke-width="2" />
        <!-- 起止时点标注 -->
        <text :x="pad.l" :y="H - 8" font-size="10" fill="#9ca3af">{{ shortTs(pricePoints[0].ts) }}</text>
        <text :x="W - pad.r" :y="H - 8" text-anchor="end" font-size="10" fill="#9ca3af">{{ shortTs(pricePoints[pricePoints.length - 1].ts) }}</text>
      </svg>
      <div v-else class="muted">暂无足够数据点绘制曲线（需 ≥2 点，请先查询）</div>
    </div>

    <!-- ── 供需分析（GAP-04 修复：负荷/可用容量/新能源出力/供需比） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">供需分析（GET /market/supply-demand · FR-PL-02 供需平衡）</h3>
        <button class="btn" @click="loadSupplyDemand">加载供需</button>
        <span class="muted">共 {{ sdRows.length }} 点</span>
      </div>
      <div class="stat-grid" v-if="sdLatest">
        <div class="card stat-card"><div class="stat-value">{{ sdLatest.loadValue }}</div><div class="stat-label">负荷（MW）</div></div>
        <div class="card stat-card"><div class="stat-value">{{ sdLatest.availableCapacity }}</div><div class="stat-label">可用容量（MW）</div></div>
        <div class="card stat-card"><div class="stat-value">{{ sdLatest.renewableOutput }}</div><div class="stat-label">新能源出力（MW）</div></div>
        <div class="card stat-card">
          <div class="stat-value" :class="sdLatest.supplyDemandRatio >= 1 ? 'ok' : 'danger'">{{ sdLatest.supplyDemandRatio }}</div>
          <div class="stat-label">供需比（&lt;1 供不应求）</div>
        </div>
      </div>
      <div v-else class="muted">暂无供需数据（点击「加载供需」）</div>
    </div>

    <!-- ── 量价热力图（GET /market/heatmap · 日期×时段，色块=该小时均价） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">量价热力图（GET /market/heatmap · 色块=该小时均价，高红低绿）</h3>
        <input v-model="heat.startDate" type="date" style="width: 160px" />
        <span class="muted">至</span>
        <input v-model="heat.endDate" type="date" style="width: 160px" />
        <button class="btn btn-primary" @click="loadHeatmap">加载热力图</button>
      </div>
      <div v-if="heatRows.length" class="heat-scroll">
        <table class="heat-table">
          <thead>
            <tr>
              <th>日期</th>
              <th v-for="h in 24" :key="'h' + h">{{ h - 1 }}时</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, d) in heatRows" :key="'d' + d">
              <td class="mono heat-date">{{ heatDates[d] }}</td>
              <td v-for="(v, h) in row" :key="'c' + h" class="heat-cell" :style="{ background: heatColor(v) }">
                {{ v }}
              </td>
            </tr>
          </tbody>
        </table>
        <div class="muted" style="margin-top: 6px">
          色阶：{{ heatMin }}（低）→ {{ heatMax }}（高）元/MWh · 数据源 {{ region.currentRegion || '—' }}
        </div>
      </div>
      <div v-else class="muted">暂无热力图数据（选择日期范围后点击「加载热力图」）</div>
    </div>

    <!-- ── 明细表 ── -->
    <div class="card">
      <h3>价格明细</h3>
      <table>
        <thead>
          <tr><th>时点</th><th>价格（元/MWh）</th><th>成交量（MWh）</th><th>区域</th></tr>
        </thead>
        <tbody>
          <tr v-if="rows.length === 0"><td colspan="4" class="muted">暂无数据</td></tr>
          <tr v-for="(r, i) in rows" :key="i">
            <td class="mono">{{ r.ts }}</td>
            <td>{{ r.price }}</td>
            <td>{{ r.volume }}</td>
            <td class="mono">{{ r.regionCode ?? region.currentRegion }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getSpotPrice, getSupplyDemand, getHeatmap } from '@/api/market'
import type { PricePoint, SupplyDemandPoint } from '@/api/types'
import { useRegionStore } from '@/stores/region'

const region = useRegionStore()
const rows = ref<PricePoint[]>([])
const sdRows = ref<SupplyDemandPoint[]>([])
const query = reactive({
  marketType: 'intra_province' as 'intra_province' | 'inter_province',
  stage: 'day_ahead' as 'day_ahead' | 'real_time',
  // datetime-local 输入（时间日期可选）；提交时转换为后端契约 yyyy-MM-dd HH:mm:ss
  startAt: dtLocalOffset(-1),
  endAt: dtLocalNow(),
})

// ── 量价热力图：近 7 天日期×24 时段（每小时均值），高红低绿归一化色阶 ──
const heat = reactive({ startDate: fmtDate(-6), endDate: fmtDate(0) })
const heatRows = ref<number[][]>([])
const heatDates = ref<string[]>([])
const heatMin = ref(0)
const heatMax = ref(0)

/** yyyy-MM-dd，距今 N 天 */
function fmtDate(offsetDays: number): string {
  const d = new Date()
  d.setDate(d.getDate() + offsetDays)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

/** 值 → 背景色：低绿 → 中黄 → 高红（线性插值） */
function heatColor(v: number): string {
  const span = heatMax.value - heatMin.value
  const r = span <= 0 ? 0 : Math.min(1, Math.max(0, (v - heatMin.value) / span))
  if (r < 0.5) {
    // 绿(#10b981) → 黄(#f59e0b)
    const t = r * 2
    return `rgb(${Math.round(16 + (245 - 16) * t)}, ${Math.round(185 + (158 - 185) * t)}, ${Math.round(129 + (11 - 129) * t)})`
  }
  // 黄(#f59e0b) → 红(#dc2626)
  const t = (r - 0.5) * 2
  return `rgb(${Math.round(245 + (220 - 245) * t)}, ${Math.round(158 + (38 - 158) * t)}, ${Math.round(11 + (38 - 11) * t)})`
}

async function loadHeatmap() {
  try {
    const data = await getHeatmap({ startDate: heat.startDate, endDate: heat.endDate })
    const dates: string[] = data?.dates ?? []
    const points: number[] = data?.points ?? []
    const dayCount = Math.min(dates.length, 31)
    const rows: number[][] = []
    let lo = Infinity
    let hi = -Infinity
    for (let d = 0; d < dayCount; d++) {
      const row: number[] = []
      for (let h = 0; h < 24; h++) {
        let sum = 0
        let n = 0
        for (let j = h * 4; j < h * 4 + 4; j++) {
          const idx = d * 96 + j
          if (idx < points.length) {
            sum += points[idx]
            n++
          }
        }
        const v = n ? Math.round(sum / n) : 0
        row.push(v)
        if (v < lo) lo = v
        if (v > hi) hi = v
      }
      rows.push(row)
    }
    heatRows.value = rows
    heatDates.value = dates.slice(0, dayCount).map((s) => s.slice(5))
    heatMin.value = lo === Infinity ? 0 : lo
    heatMax.value = hi === -Infinity ? 0 : hi
  } catch {
    heatRows.value = []
  }
}

/** datetime-local 值（yyyy-MM-ddTHH:mm），距今 N 天 */
function dtLocalOffset(days: number): string {
  const d = new Date()
  d.setDate(d.getDate() + days)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}T08:00`
}
function dtLocalNow(): string {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}T${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

/** datetime-local → 后端契约 yyyy-MM-dd HH:mm:ss */
function toBackendTs(v: string): string {
  const s = v.replace('T', ' ')
  return s.length === 16 ? s + ':00' : s
}

// ── SVG 折线几何（viewBox 坐标系，宽高固定便于等比缩放） ──
const W = 720
const H = 220
const pad = { l: 56, r: 12, t: 10, b: 20 }
const pricePoints = computed(() => rows.value.filter((p) => typeof p.price === 'number' && Number.isFinite(p.price)))

const yMin = computed(() => Math.min(...pricePoints.value.map((p) => p.price)))
const yMax = computed(() => Math.max(...pricePoints.value.map((p) => p.price)))
const ySpan = computed(() => (yMax.value - yMin.value) || 1)

function yValue(ratio: number): number {
  return yMin.value + ySpan.value * ratio
}
function yAt(ratio: number): number {
  return pad.t + (H - pad.t - pad.b) * (1 - ratio)
}
function xAt(i: number): number {
  const n = pricePoints.value.length
  return n <= 1 ? pad.l : pad.l + ((W - pad.l - pad.r) * i) / (n - 1)
}
const linePoints = computed(() =>
  pricePoints.value.map((p, i) => `${xAt(i).toFixed(1)},${yAt((p.price - yMin.value) / ySpan.value).toFixed(1)}`).join(' '),
)
function fmt(v: number): string {
  return Number.isInteger(v) ? String(v) : v.toFixed(1)
}
function shortTs(ts: string): string {
  return ts?.length >= 16 ? ts.slice(5, 16) : (ts ?? '')
}

const sdLatest = computed(() => sdRows.value[sdRows.value.length - 1] ?? null)

async function load() {
  try {
    rows.value = await getSpotPrice({
      marketType: query.marketType,
      stage: query.stage,
      startAt: toBackendTs(query.startAt),
      endAt: toBackendTs(query.endAt),
    })
  } catch {
    rows.value = []
  }
}

async function loadSupplyDemand() {
  try {
    const start = new Date(query.startAt.replace(' ', 'T'))
    const range = { startAt: toBackendTs(query.startAt), endAt: toBackendTs(query.endAt) }
    sdRows.value = await getSupplyDemand(range)
    if (sdRows.value.length === 0 && !Number.isNaN(start.getTime())) {
      // 区间无数据时回退加载当日（后端时序表按日分桶，区间跨日查询可能为空）
      const day = `${start.getFullYear()}-${String(start.getMonth() + 1).padStart(2, '0')}-${String(start.getDate()).padStart(2, '0')}`
      sdRows.value = await getSupplyDemand({ startAt: `${day} 00:00:00`, endAt: `${day} 23:59:59` })
    }
  } catch {
    sdRows.value = []
  }
}

onMounted(() => {
  load()
  loadHeatmap()
})
</script>

<style scoped>
.heat-scroll {
  margin-top: 8px;
  overflow-x: auto;
}
.heat-table {
  border-collapse: collapse;
  font-size: 11px;
}
.heat-table th,
.heat-table td {
  border: 1px solid #e5e7eb;
  padding: 2px 4px;
  text-align: center;
  white-space: nowrap;
}
.heat-table th {
  background: #f9fafb;
  font-weight: 500;
}
.heat-date {
  position: sticky;
  left: 0;
  background: #f9fafb;
}
.heat-cell {
  min-width: 44px;
  color: #1f2937;
}
</style>
