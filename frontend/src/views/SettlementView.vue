<template>
  <div>
    <!-- ── Tab 导航（GAP-08 修复：结算工作台双视图） ── -->
    <div class="form-row" style="margin-bottom: 12px">
      <button v-for="t in tabs" :key="t.key" class="btn" :class="activeTab === t.key ? 'btn-primary' : ''" @click="activeTab = t.key">
        {{ t.label }}
      </button>
    </div>

    <!-- ══ Tab1 结算台账（FR-ST-01 结算记录） ══ -->
    <div v-if="activeTab === 'record'">
      <div class="card">
        <h3>结算记录（GET /settlement/records · 按 region 隔离 + periodMode 口径）</h3>
        <div class="form-row">
          <span class="badge badge-blue">当前区域 {{ region.currentRegion }}</span>
          <span class="muted">结算口径：{{ periodMode }}</span>
          <button class="btn btn-primary" @click="load">刷新</button>
        </div>
        <table>
          <thead>
            <tr><th>结算周期</th><th>区域</th><th>总金额（元）</th><th>同步状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="rows.length === 0"><td colspan="5" class="muted">暂无数据</td></tr>
            <tr v-for="r in rows" :key="r.id">
              <td class="mono">{{ r.settlementPeriod }}</td>
              <td class="mono">{{ r.regionCode ?? region.currentRegion }}</td>
              <td>{{ r.totalAmount }}</td>
              <td>
                <span class="badge" :class="r.syncStatus === 'synced' ? 'badge-green' : r.syncStatus === 'diff' ? 'badge-red' : 'badge-orange'">{{ syncStatusLabel(r.syncStatus) }}</span>
              </td>
              <td><button class="btn" @click="onReconcile(r.id)">核对</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ══ Tab2 差异工单（FR-ST-02 差异处理闭环） ══ -->
    <div v-if="activeTab === 'ticket'">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">差异工单（GET /settlement/tickets · 处理闭环）</h3>
          <select v-model="ticketStatus">
            <option value="">全部状态</option>
            <option v-for="s in ['open', 'processing', 'resolved', 'closed']" :key="s" :value="s">{{ statusLabel(s) }}（{{ s }}）</option>
          </select>
          <button class="btn btn-primary" @click="loadTickets">查询</button>
        </div>
        <table>
          <thead>
            <tr><th>工单号</th><th>结算周期</th><th>差异类型</th><th>差异金额（元）</th><th>状态</th><th>创建时间</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="tickets.length === 0"><td colspan="7" class="muted">暂无差异工单</td></tr>
            <tr v-for="t in tickets" :key="String(t.id)">
              <td class="mono">{{ t.ticketNo ?? t.id }}</td>
              <td class="mono">{{ t.settlementPeriod ?? t.period ?? '-' }}</td>
              <td>{{ diffTypeLabel(String(t.diffType ?? t.type ?? '')) }}</td>
              <td class="mono">{{ t.diffAmount ?? t.amount ?? '-' }}</td>
              <td><span class="badge" :class="statusClass(String(t.status))">{{ statusLabel(String(t.status)) }}</span></td>
              <td class="mono muted">{{ t.createdAt ?? '-' }}</td>
              <td>
                <button class="btn" :disabled="String(t.status) === 'closed'" @click="openProcess(t)">处理</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ── 工单处理弹窗（FR-ST-02 差异说明/调整/关闭） ── -->
    <div v-if="processTarget" class="modal-mask" @click.self="processTarget = null">
      <div class="modal" style="width: 460px">
        <h3>差异工单处理（POST /settlement/tickets/{id}/process）</h3>
        <div class="muted" style="margin-bottom: 8px">工单：{{ processTarget.ticketNo ?? processTarget.id }}</div>
        <div class="form-row">
          <select v-model="processForm.action">
            <option value="acknowledge">确认差异（acknowledge）</option>
            <option value="adjust">调整记录（adjust）</option>
            <option value="reject">驳回（reject）</option>
            <option value="close">关闭（close）</option>
          </select>
        </div>
        <textarea v-model="processForm.remark" rows="3" placeholder="处理说明（备注）" style="width: 100%"></textarea>
        <div class="form-row" style="margin-top: 12px">
          <button class="btn" @click="processTarget = null">取消</button>
          <button class="btn btn-primary" :disabled="processing" @click="onProcess">{{ processing ? '处理中…' : '提交处理' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getSettlementRecords, getTickets, processTicket, reconcileRecord } from '@/api/settlement'
import type { SettlementRecord } from '@/api/types'
import { useRegionStore } from '@/stores/region'

const region = useRegionStore()
const activeTab = ref('record')
const tabs = [
  { key: 'record', label: '结算台账' },
  { key: 'ticket', label: '差异工单' },
]
const rows = ref<SettlementRecord[]>([])
const periodMode = region.currentRegion === 'CN-33' ? 'trading_month' : 'natural_month'

const tickets = ref<Record<string, unknown>[]>([])
const ticketStatus = ref('')
const processTarget = ref<Record<string, unknown> | null>(null)
const processForm = reactive({ action: 'acknowledge', remark: '' })
const processing = ref(false)

function statusClass(s: string): string {
  return s === 'closed' || s === 'resolved' ? 'badge-green' : s === 'open' ? 'badge-red' : 'badge-orange'
}

/** V2.4 编码+名称：工单状态中文标签 */
function statusLabel(s: string): string {
  return { open: '待处理', processing: '处理中', resolved: '已解决', closed: '已关闭' }[s] ?? s
}

/** V2.4 编码+名称：差异类型中文标签 */
function diffTypeLabel(s: string): string {
  return { amount_diff: '金额差异', volume_diff: '电量差异', price_diff: '价格差异' }[s] ?? (s || '-')
}

/** V2.4 编码+名称：结算同步状态中文标签 */
function syncStatusLabel(s: string): string {
  return { synced: '已同步', diff: '有差异', pending: '待同步' }[s] ?? s
}

async function onReconcile(id: string) {
  try {
    await reconcileRecord(id)
    await load()
  } catch {
    // 骨架阶段静默
  }
}

async function load() {
  try {
    const res = await getSettlementRecords({ period: currentPeriod(), pageNo: 1, pageSize: 20 })
    rows.value = ((res as unknown as { list: SettlementRecord[] }).list ?? [])
  } catch {
    rows.value = []
  }
}

async function loadTickets() {
  try {
    const res = await getTickets({ ...(ticketStatus.value ? { status: ticketStatus.value } : {}), pageNo: 1, pageSize: 50 })
    tickets.value = ((res as unknown as { list: Record<string, unknown>[] }).list ?? [])
  } catch {
    tickets.value = []
  }
}

function openProcess(t: Record<string, unknown>) {
  processTarget.value = t
  processForm.action = 'acknowledge'
  processForm.remark = ''
}

async function onProcess() {
  if (!processTarget.value) return
  processing.value = true
  try {
    const id = String(processTarget.value.id)
    await processTicket(id, { action: processForm.action, remark: processForm.remark })
    processTarget.value = null
    await loadTickets()
  } finally {
    processing.value = false
  }
}

/** 当前结算周期（YYYY-MM，natural_month/trading_month 均按自然月维度展示） */
function currentPeriod(): string {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

onMounted(async () => {
  await load()
  await loadTickets()
})
</script>
