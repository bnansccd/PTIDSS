<template>
  <div>
    <!-- ── Tab 导航（GAP-06 修复：交易工作台四视图） ── -->
    <div class="form-row" style="margin-bottom: 12px">
      <button v-for="t in tabs" :key="t.key" class="btn" :class="activeTab === t.key ? 'btn-primary' : ''" @click="activeTab = t.key">
        {{ t.label }}
      </button>
    </div>

    <!-- ══ Tab1 申报单（FR-TR-01 申报管理 · V2.4 新增/编辑/网关推送监测） ══ -->
    <div v-if="activeTab === 'declaration'">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">申报单列表（GET /trade/declarations · 合规预检后提交交易中心）</h3>
          <button class="btn btn-primary" style="margin-left: auto" @click="openCreate">新增申报单</button>
        </div>
        <div class="form-row" style="margin-top: 8px">
          <input v-model="filters.tradeDate" type="date" />
          <select v-model="filters.status">
            <option value="">全部状态</option>
            <option v-for="s in ['draft', 'pending_submit', 'submitted', 'receipted', 'partially_matched']" :key="s" :value="s">{{ statusLabel(s) }}</option>
          </select>
          <button class="btn btn-primary" @click="loadDeclarations">查询</button>
        </div>
        <table>
          <thead>
            <tr><th>申报单号</th><th>交易日</th><th>市场</th><th>阶段</th><th>区域</th><th>状态</th><th>网关推送</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="rows.length === 0"><td colspan="8" class="muted">暂无数据</td></tr>
            <tr v-for="r in rows" :key="r.id">
              <td class="mono">{{ r.declarationNo || r.id }}</td>
              <td>{{ r.tradeDate }}</td>
              <td>{{ marketLabel(r.marketType) }}</td>
              <td>{{ stageLabel(r.stage) }}</td>
              <td class="mono">{{ r.regionCode ?? region.currentRegion }}</td>
              <td><span class="badge" :class="statusClass(r.status)">{{ statusLabel(r.status) }}</span></td>
              <td>
                <span v-if="r.gatewayPushStatus" class="badge" :class="r.gatewayPushStatus === 'success' ? 'badge-green' : r.gatewayPushStatus === 'failed' ? 'badge-red' : 'badge-orange'">{{ pushLabel(r.gatewayPushStatus) }}</span>
                <span v-else class="muted">—</span>
              </td>
              <td>
                <button class="btn" :disabled="submitting" @click="openEdit(r)">{{ r.status === 'draft' || r.status === 'pending_submit' ? '编辑' : '查看' }}</button>
                <button class="btn" :disabled="(r.status !== 'draft' && r.status !== 'pending_submit') || submitting" @click="onSubmit(r.id)">提交</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ── 交易网关配置（V2.4：URL/账户/密码图形化对接 + 状态监测） ── -->
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">交易网关配置（PUT /trade/gateway/config · 申报提交时推送交易系统并监测状态）</h3>
          <button class="btn" style="margin-left: auto" :disabled="testing" @click="onTestGateway">{{ testing ? '测试中…' : '测试连接' }}</button>
        </div>
        <div class="form-row" style="margin-top: 8px">
          <input v-model="gwForm.gatewayName" placeholder="网关名称（如 江苏电力交易中心申报网关）" style="flex: 1" />
          <input v-model="gwForm.endpoint" placeholder="接口地址 URL（如 https://trade.center/api/declaration）" style="flex: 2" />
        </div>
        <div class="form-row" style="margin-top: 8px">
          <input v-model="gwForm.appKey" placeholder="账户 AppKey（脱敏回显，留空不修改）" style="flex: 1" />
          <input v-model="gwForm.appSecret" type="password" placeholder="密码 AppSecret（脱敏回显，留空不修改）" style="flex: 1" />
          <select v-model="gwForm.status">
            <option value="enabled">启用</option>
            <option value="disabled">停用</option>
          </select>
          <button class="btn btn-primary" :disabled="!gwForm.endpoint" @click="onSaveGateway">保存配置</button>
        </div>
        <div v-if="gwTest" class="form-row" style="margin-top: 8px">
          <span class="badge" :class="gwTest.ok ? 'badge-green' : 'badge-red'">{{ gwTest.ok ? '连接正常' : '连接失败' }}</span>
          <span class="muted">{{ gwTest.message }}</span>
        </div>
        <div v-else-if="gwStatus.lastTestResult" class="form-row" style="margin-top: 8px">
          <span class="muted">上次测试：{{ gwStatus.lastTestResult }}</span>
        </div>
        <div class="muted" style="margin-top: 4px">仅需填写 URL/账户/密码即可完成对接；敏感字段 AES 加密落库，界面仅脱敏展示；提交申报时自动推送并记录状态（回执号/推送时间）</div>
      </div>

      <!-- ── 申报单新增/编辑弹窗（V2.4：明细行编辑 + 合规预检） ── -->
      <div v-if="decDialog" class="modal-mask" @click.self="decDialog = false">
        <div class="modal" style="width: 720px">
          <h3>{{ decEditing ? '编辑申报单：' + decEditing.declarationNo : '新增申报单' }}</h3>
          <div class="form-row">
            <input v-model="decForm.tradeDate" type="date" style="flex: 1" />
            <select v-model="decForm.marketType" style="flex: 1">
              <option value="intra_province">intra_province 省内</option>
              <option value="inter_province">inter_province 省间</option>
            </select>
            <select v-model="decForm.stage" style="flex: 1">
              <option value="day_ahead">day_ahead 日前</option>
              <option value="real_time">real_time 实时</option>
              <option value="rolling">rolling 滚动</option>
            </select>
          </div>
          <div class="form-row" style="justify-content: space-between">
            <span class="muted">申报明细（时段/量/价，合规预检：段数 ≤ 10、单价 50–1500 元/MWh、总量 &gt; 0）</span>
            <button class="btn btn-sm" @click="decItems.push({ period: `T${String(decItems.length + 1).padStart(2, '0')}`, volume: 10, price: 350 })">+ 添加明细</button>
          </div>
          <table>
            <thead>
              <tr><th>时段</th><th>电量（MWh）</th><th>价格（元/MWh）</th><th></th></tr>
            </thead>
            <tbody>
              <tr v-for="(it, i) in decItems" :key="i">
                <td><input v-model="it.period" class="mono" style="width: 90px" /></td>
                <td><input v-model.number="it.volume" type="number" style="width: 120px" /></td>
                <td><input v-model.number="it.price" type="number" style="width: 130px" /></td>
                <td><button class="btn btn-sm" @click="decItems.splice(i, 1)">删除</button></td>
              </tr>
              <tr v-if="decItems.length === 0"><td colspan="4" class="muted">暂无明细，请点击"添加明细"</td></tr>
            </tbody>
          </table>
          <div v-if="decResult" class="form-row" style="flex-wrap: wrap; margin-top: 8px">
            <span class="badge" :class="decResult.passed ? 'badge-green' : 'badge-red'">{{ decResult.passed ? '合规预检通过' : '合规预检未通过（已存为草稿）' }}</span>
            <span v-for="(v, i) in decResult.violations ?? []" :key="i" class="badge badge-orange">{{ v }}</span>
            <span v-if="decResult.totalVolume !== undefined" class="muted">总申报量：{{ decResult.totalVolume }} MWh · 段数：{{ decResult.segments }}</span>
          </div>
          <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
            <button class="btn" @click="decDialog = false">关闭</button>
            <button class="btn btn-primary" :disabled="decSaving" @click="onSaveDeclaration">{{ decSaving ? '保存中…' : '保存申报单' }}</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ══ Tab2 日滚动方案（FR-TR-05 方案确认） ══ -->
    <div v-if="activeTab === 'plan'">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">日滚动方案（GET /trade/rolling-plans · 人工确认后生成申报单）</h3>
          <button class="btn" @click="loadPlans">刷新</button>
        </div>
        <table>
          <thead>
            <tr><th>方案号</th><th>交易日</th><th>情景</th><th>类型</th><th>预期收益（元）</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="plans.length === 0"><td colspan="7" class="muted">暂无方案</td></tr>
            <tr v-for="p in plans" :key="p.id">
              <td class="mono">{{ p.id }}</td>
              <td>{{ p.tradeDate }}</td>
              <td><span class="badge badge-blue">{{ p.scenario }}</span></td>
              <td class="mono">{{ p.planType }}</td>
              <td class="mono">{{ p.expectedRevenue }}</td>
              <td><span class="badge" :class="p.status === 'confirmed' ? 'badge-green' : 'badge-orange'">{{ p.status }}</span></td>
              <td>
                <button class="btn" :disabled="p.status !== 'pending' || submitting" @click="onConfirmPlan(p.id)">确认方案</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ══ Tab3 持仓曲线（FR-PL-03 中长期+现货合成视图） ══ -->
    <div v-if="activeTab === 'position'">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">持仓曲线（GET /trade/positions · tradeDate 必填）</h3>
          <input v-model="posDate" type="datetime-local" />
          <button class="btn btn-primary" @click="loadPositions">查询</button>
          <span class="muted">共 {{ positions.length }} 点</span>
        </div>
        <svg v-if="positionPoints.length >= 2" :viewBox="`0 0 ${W} ${H}`" style="width: 100%; height: 220px">
          <g v-for="i in 4" :key="'y' + i">
            <line :x1="pad.l" :y1="yAt(i / 4)" :x2="W - pad.r" :y2="yAt(i / 4)" stroke="#e5e7eb" stroke-width="1" />
            <text :x="pad.l - 6" :y="yAt(i / 4) + 4" text-anchor="end" font-size="10" fill="#9ca3af">{{ fmtY(yVal(1 - i / 4)) }}</text>
          </g>
          <polyline :points="positionPoints" fill="none" stroke="#2f6fed" stroke-width="2" />
        </svg>
        <div v-else class="muted">暂无持仓数据（请查询）</div>
        <div style="max-height: 420px; overflow-y: auto">
          <table>
            <thead>
              <tr><th>时段</th><th>持仓量（MWh）</th><th>构成</th></tr>
            </thead>
            <tbody>
              <tr v-if="positions.length === 0"><td colspan="3" class="muted">暂无数据</td></tr>
              <tr v-for="(p, i) in positions" :key="i">
                <td class="mono">{{ p.timeRange ?? p.ts ?? '-' }}</td>
                <td class="mono">{{ p.net ?? p.position ?? p.value ?? '-' }}</td>
                <td class="muted mono">{{ compositionText(p) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- ══ Tab4 成交结果（FR-TR-06 成交回报） ══ -->
    <div v-if="activeTab === 'result'">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">成交结果（GET /trade/results）</h3>
          <input v-model="resultDate" type="datetime-local" />
          <button class="btn btn-primary" @click="loadResults">查询</button>
        </div>
        <table>
          <thead>
            <tr><th>时点</th><th>市场</th><th>阶段</th><th>价格（元/MWh）</th><th>成交量（MWh）</th></tr>
          </thead>
          <tbody>
            <tr v-if="results.length === 0"><td colspan="5" class="muted">暂无成交数据</td></tr>
            <tr v-for="(r, i) in results" :key="i">
              <td class="mono">{{ r.ts ?? r.tradeDate ?? '-' }}</td>
              <td>{{ r.marketType ?? '-' }}</td>
              <td>{{ r.stage ?? '-' }}</td>
              <td class="mono">{{ r.price ?? '-' }}</td>
              <td class="mono">{{ r.volume ?? '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  confirmRollingPlan, createDeclaration, getDeclaration, getDeclarations, getPositions, getRollingPlans,
  getTradeGatewayConfig, getTradeResults, saveTradeGatewayConfig, submitDeclaration, testTradeGateway,
  updateDeclaration, type TradeGatewayConfig,
} from '@/api/trade'
import type { Declaration, RollingPlan } from '@/api/types'
import { useRegionStore } from '@/stores/region'

const region = useRegionStore()
const activeTab = ref('declaration')
const tabs = [
  { key: 'declaration', label: '申报单' },
  { key: 'plan', label: '日滚动方案' },
  { key: 'position', label: '持仓曲线' },
  { key: 'result', label: '成交结果' },
]
const submitting = ref(false)

const rows = ref<Declaration[]>([])
const filters = ref<{ tradeDate: string; status: string }>({ tradeDate: '', status: '' })

// ── 申报单新增/编辑（V2.4） ──
const decDialog = ref(false)
const decEditing = ref<Declaration | null>(null)
const decSaving = ref(false)
const decForm = reactive({ tradeDate: '', marketType: 'intra_province', stage: 'day_ahead' })
const decItems = ref<Array<{ period: string; volume: number; price: number }>>([])
const decResult = ref<{ passed: boolean; violations?: string[]; segments?: number; totalVolume?: number } | null>(null)

// ── 交易网关配置（V2.4：URL/账户/密码图形化 + 状态监测） ──
const gwStatus = ref<Partial<TradeGatewayConfig>>({})
const gwForm = reactive({ gatewayName: '', endpoint: '', appKey: '', appSecret: '', status: 'enabled' })
const gwTest = ref<{ ok: boolean; message: string; latencyMs?: number } | null>(null)
const testing = ref(false)

const plans = ref<RollingPlan[]>([])
const positions = ref<Record<string, unknown>[]>([])
const posDate = ref(todayStr())
const results = ref<Record<string, unknown>[]>([])
const resultDate = ref(todayStr())

// ── SVG 持仓曲线几何 ──
const W = 720
const H = 200
const pad = { l: 56, r: 12, t: 10, b: 16 }
const positionPoints = computed<string>(() => {
  const pts = positions.value
    .map((p) => ({ k: String(p.timeRange ?? p.ts ?? ''), v: Number(p.net ?? p.position ?? p.value) }))
    .filter((p) => p.k && Number.isFinite(p.v))
  if (pts.length < 2) return ''
  const vals = pts.map((p) => p.v)
  const min = Math.min(...vals)
  const span = Math.max(...vals) - min || 1
  return pts
    .map((p, i) => {
      const x = pad.l + ((W - pad.l - pad.r) * i) / (pts.length - 1)
      const y = pad.t + (H - pad.t - pad.b) * (1 - (p.v - min) / span)
      return `${x.toFixed(1)},${y.toFixed(1)}`
    })
    .join(' ')
})
function yVal(ratio: number): number {
  const vals = positions.value.map((p) => Number(p.net ?? p.position ?? p.value)).filter((v) => Number.isFinite(v))
  if (vals.length === 0) return 0
  const min = Math.min(...vals)
  return min + (Math.max(...vals) - min || 1) * ratio
}
function yAt(ratio: number): number {
  return pad.t + (H - pad.t - pad.b) * (1 - ratio)
}
function fmtY(v: number): string {
  return String(Math.round(v))
}
function compositionText(p: Record<string, unknown>): string {
  const keys = ['longTerm', 'spot', 'midlong', 'shortTerm']
  const parts = keys.filter((k) => p[k] !== undefined).map((k) => `${k}:${p[k]}`)
  return parts.length > 0 ? parts.join('；') : '-'
}

function todayStr(): string {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  return d.toISOString().slice(0, 10)
}

function statusClass(s: string): string {
  return s === 'submitted' || s === 'receipted' ? 'badge-green' : s === 'draft' ? 'badge-gray' : 'badge-blue'
}

// ── 编码 + 名称显示（V2.4 需求9：业务侧友好展示） ──
function statusLabel(s: string): string {
  return { draft: '草稿', pending_submit: '待提交', submitted: '已提交', receipted: '已回执', partially_matched: '部分成交' }[s] ?? s
}

function marketLabel(m: string): string {
  return { intra_province: '省内', inter_province: '省间' }[m] ?? m
}

function stageLabel(s: string): string {
  return { day_ahead: '日前', real_time: '实时', rolling: '滚动' }[s] ?? s
}

function pushLabel(s: string): string {
  return { success: '已推送', failed: '推送失败', pending: '推送中', skipped: '未推送' }[s] ?? s
}

// ── 申报单新增/编辑（V2.4） ──
function openCreate() {
  decEditing.value = null
  decResult.value = null
  Object.assign(decForm, { tradeDate: todayStr(), marketType: 'intra_province', stage: 'day_ahead' })
  decItems.value = [{ period: 'T01', volume: 10, price: 350 }]
  decDialog.value = true
}

async function openEdit(r: Declaration) {
  decEditing.value = r
  decResult.value = null
  try {
    const d = await getDeclaration(String(r.id))
    Object.assign(decForm, { tradeDate: String(d.tradeDate ?? r.tradeDate ?? '').slice(0, 10), marketType: d.marketType, stage: d.stage })
    decItems.value = (Array.isArray(d.items) ? d.items : []).map((it) => ({
      period: String((it as Record<string, unknown>).period ?? 'T01'),
      volume: Number((it as Record<string, unknown>).volume ?? 0),
      price: Number((it as Record<string, unknown>).price ?? 0),
    }))
    if (decItems.value.length === 0) decItems.value = [{ period: 'T01', volume: 10, price: 350 }]
  } catch {
    decItems.value = [{ period: 'T01', volume: 10, price: 350 }]
  }
  decDialog.value = true
}

async function onSaveDeclaration() {
  if (!decForm.tradeDate) { alert('请选择交易日期'); return }
  if (decItems.value.length === 0) { alert('请添加申报明细'); return }
  decSaving.value = true
  try {
    const payload = {
      tradeDate: decForm.tradeDate,
      marketType: decForm.marketType,
      stage: decForm.stage,
      items: decItems.value.map((it) => ({ period: it.period, volume: it.volume, price: it.price })),
    }
    const resp = decEditing.value
      ? await updateDeclaration(String(decEditing.value.id), payload)
      : await createDeclaration(payload)
    decResult.value = resp.complianceCheck as { passed: boolean; violations?: string[]; segments?: number; totalVolume?: number }
    await loadDeclarations()
  } catch (e) {
    alert((e as Error).message || '保存失败')
  } finally {
    decSaving.value = false
  }
}

// ── 交易网关配置（V2.4） ──
async function loadGatewayConfig() {
  try {
    const cfg = await getTradeGatewayConfig()
    gwStatus.value = cfg ?? {}
    if (cfg) {
      Object.assign(gwForm, {
        gatewayName: cfg.gatewayName ?? '',
        endpoint: cfg.endpoint ?? '',
        status: cfg.status ?? 'disabled',
      })
      try {
        const conn = JSON.parse(cfg.connConfig ?? '{}') as Record<string, string>
        gwForm.appKey = conn.appKey ?? ''
        gwForm.appSecret = conn.appSecret ?? ''
      } catch {
        gwForm.appKey = ''
        gwForm.appSecret = ''
      }
    }
  } catch {
    gwStatus.value = {}
  }
}

async function onSaveGateway() {
  try {
    const saved = await saveTradeGatewayConfig({
      gatewayName: gwForm.gatewayName || undefined,
      endpoint: gwForm.endpoint,
      appKey: gwForm.appKey && gwForm.appKey !== '******' ? gwForm.appKey : undefined,
      appSecret: gwForm.appSecret && gwForm.appSecret !== '******' ? gwForm.appSecret : undefined,
      status: gwForm.status,
    })
    gwStatus.value = saved ?? {}
    alert('网关配置已保存（敏感字段已加密落库）')
  } catch (e) {
    alert((e as Error).message || '保存失败')
  }
}

async function onTestGateway() {
  testing.value = true
  try {
    gwTest.value = await testTradeGateway()
  } catch (e) {
    gwTest.value = { ok: false, message: (e as Error).message || '测试失败' }
  } finally {
    testing.value = false
  }
}

async function loadDeclarations() {
  try {
    const res = await getDeclarations({
      pageNo: 1, pageSize: 20,
      ...(filters.value.tradeDate ? { tradeDate: filters.value.tradeDate } : {}),
      ...(filters.value.status ? { status: filters.value.status } : {}),
    })
    rows.value = ((res as unknown as { list: Declaration[] }).list ?? [])
  } catch {
    rows.value = []
  }
}

async function onSubmit(id: string) {
  submitting.value = true
  try {
    const resp = await submitDeclaration(id)
    const msg = resp.gatewayPushDetail
      ? `申报已提交，回执号 ${resp.receiptNo}\n网关推送：${resp.gatewayPushDetail}`
      : `申报已提交，回执号 ${resp.receiptNo}`
    alert(msg)
    await loadDeclarations()
  } catch (e) {
    alert((e as Error).message || '提交失败')
  } finally {
    submitting.value = false
  }
}

async function loadPlans() {
  try {
    const res = await getRollingPlans({ pageNo: 1, pageSize: 20 })
    plans.value = ((res as unknown as { list: RollingPlan[] }).list ?? [])
  } catch {
    plans.value = []
  }
}

async function onConfirmPlan(id: string) {
  submitting.value = true
  try {
    await confirmRollingPlan(id)
    await loadPlans()
  } finally {
    submitting.value = false
  }
}

async function loadPositions() {
  try {
    const raw = (await getPositions({ tradeDate: posDate.value })) as unknown
    positions.value = normalizePositions(raw)
  } catch {
    positions.value = []
  }
}

/**
 * 后端 GET /trade/positions 返回 { longTerm:[96], spot:[96], net:[96] } 三个 96 点序列（中长期+现货合成视图），
 * 归一化为行数组 [{ timeRange, longTerm, spot, net }] 供表格与 SVG 曲线消费；兼容数组形态响应。
 */
function normalizePositions(raw: unknown): Record<string, unknown>[] {
  if (Array.isArray(raw)) return raw as Record<string, unknown>[]
  if (raw && typeof raw === 'object') {
    const obj = raw as Record<string, unknown[]>
    const keys = ['longTerm', 'spot', 'net']
    const seqs = keys.filter((k) => Array.isArray(obj[k])).map((k) => obj[k] as unknown[])
    if (seqs.length === 0) return []
    const n = Math.max(...seqs.map((s) => s.length))
    const out: Record<string, unknown>[] = []
    for (let i = 0; i < n; i++) {
      const row: Record<string, unknown> = { timeRange: `T${String(i + 1).padStart(2, '0')}` }
      for (const k of keys) {
        if (Array.isArray(obj[k]) && obj[k][i] !== undefined) row[k] = obj[k][i]
      }
      out.push(row)
    }
    return out
  }
  return []
}

async function loadResults() {
  try {
    const res = await getTradeResults({ ...(resultDate.value ? { tradeDate: resultDate.value } : {}), pageNo: 1, pageSize: 50 })
    results.value = ((res as unknown as { list: Record<string, unknown>[] }).list ?? [])
  } catch {
    results.value = []
  }
}

onMounted(async () => {
  await loadDeclarations()
  await loadPlans()
  await loadPositions()
  await loadResults()
  await loadGatewayConfig()
})
</script>
