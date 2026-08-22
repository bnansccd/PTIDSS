<template>
  <div>
    <!-- ── 统计卡（FR-RP-01 报表中心） ── -->
    <div class="stat-grid">
      <div class="card stat-card"><div class="stat-value">{{ templates.length }}</div><div class="stat-label">报表模板</div></div>
      <div class="card stat-card"><div class="stat-value">{{ instances.length }}</div><div class="stat-label">报表实例</div></div>
      <div class="card stat-card"><div class="stat-value">{{ successCount }}</div><div class="stat-label">生成成功</div></div>
      <div class="card stat-card"><div class="stat-value">{{ typesCount }}</div><div class="stat-label">报表类型</div></div>
    </div>

    <!-- ── 模板列表（V2.4：可新增/编辑/自定义） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">报表模板（GET /report/templates · 交易/结算/预测/考核/经营 + 表头口径报送格式 · V2.4 支持新增/编辑/自定义）</h3>
        <button class="btn btn-primary" style="margin-left: auto" @click="openTemplateCreate">新增模板</button>
      </div>
      <table>
        <thead>
          <tr><th>编码</th><th>名称</th><th>类型</th><th>周期</th><th>版本</th><th>口径说明</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="templates.length === 0"><td colspan="8" class="muted">暂无模板</td></tr>
          <tr v-for="t in templates" :key="t.id">
            <td class="mono">{{ t.code }}</td>
            <td>{{ t.name }}</td>
            <td><span class="badge badge-blue">{{ typeLabel(t.type) }}</span></td>
            <td class="mono">{{ periodTypeLabel(t.periodType) }}</td>
            <td class="mono">v{{ t.version }}</td>
            <td class="muted">{{ headerText(t.headerConfig) }}</td>
            <td><span class="badge" :class="t.status === 'active' ? 'badge-green' : 'badge-gray'">{{ t.status === 'active' ? '启用' : '停用' }}</span></td>
            <td>
              <button class="btn btn-sm" @click="openTemplateEdit(t)">编辑</button>
              <button class="btn btn-sm" @click="openGenerate(t)">生成报表</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 模板新增/编辑弹窗（V2.4：报表自定义） ── -->
    <div v-if="tplDialog" class="modal-mask" @click.self="tplDialog = false">
      <div class="modal" style="width: 620px">
        <h3>{{ tplEditing ? '编辑模板：' + tplEditing.code : '新增报表模板' }}</h3>
        <div class="form-row">
          <input v-model="tplForm.code" placeholder="模板编码（如 RPT-MONTH-PROFIT）" :disabled="!!tplEditing" style="flex: 1" />
          <input v-model="tplForm.name" placeholder="模板名称（如 收益分析月报）" style="flex: 1" />
        </div>
        <div class="form-row">
          <select v-model="tplForm.type">
            <option value="trade">trade 交易</option>
            <option value="settlement">settlement 结算</option>
            <option value="forecast">forecast 预测</option>
            <option value="assessment">assessment 考核</option>
            <option value="business">business 经营</option>
          </select>
          <select v-model="tplForm.periodType">
            <option value="daily">daily 日</option>
            <option value="weekly">weekly 周</option>
            <option value="monthly">monthly 月</option>
            <option value="yearly">yearly 年</option>
          </select>
          <select v-model="tplForm.status">
            <option value="active">启用</option>
            <option value="disabled">停用</option>
            <option value="draft">草稿</option>
          </select>
        </div>
        <textarea v-model="tplForm.datasourceConfig" rows="2" class="mono" placeholder='指标配置 JSON，如 {"indicators":["现货收益","价差收益"]}' style="width: 100%" />
        <textarea v-model="tplForm.layout" rows="2" class="mono" placeholder='布局 JSON，如 {"columns":["指标","数值"]}' style="width: 100%" />
        <textarea v-model="tplForm.headerConfig" rows="2" class="mono" placeholder='表头/口径 JSON，如 {"title":"收益分析月报（报送版）","caliber":"口径：交易中心结算数据；单位：万元"}' style="width: 100%" />
        <div class="muted">指标/布局/表头均为 JSON 配置，保存后生成报表即按新配置取数</div>
        <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
          <button class="btn" @click="tplDialog = false">取消</button>
          <button class="btn btn-primary" :disabled="!tplForm.code || !tplForm.name" @click="onSaveTemplate">保存</button>
        </div>
      </div>
    </div>

    <!-- ── 生成报表（V2.4：选择报表模板 + 时间周期，日期+周期自动推导） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">生成报表实例（POST /report/instances · 选择模板 + 起始日期 + 周期 → 自动计算报表周期 · 数据快照落库，口径可追溯）</h3>
      </div>
      <div class="form-row" style="margin-top: 8px">
        <select v-model="genForm.templateCode" style="flex: 1">
          <option value="" disabled>选择报表模板…</option>
          <option v-for="t in templates" :key="t.code" :value="t.code">{{ t.name }}（{{ t.code }}）</option>
        </select>
        <input v-model="genForm.startDate" type="date" style="width: 170px" @change="syncPeriod" />
        <select v-model="genForm.periodUnit" style="width: 90px" @change="syncPeriod">
          <option v-for="u in PERIOD_UNITS" :key="u.value" :value="u.value">{{ u.label }}</option>
        </select>
        <input :value="genForm.period" readonly class="mono" style="width: 140px" title="自动计算的报表周期" />
        <button class="btn btn-primary" :disabled="creating || !genForm.templateCode || !genForm.period" @click="onGenerate">
          {{ creating ? '生成中…' : '按所选模板生成' }}
        </button>
      </div>
      <div class="muted" style="margin-top: 4px">周期类型：日（yyyy-MM-dd）/ 周（yyyy-Www）/ 月（yyyy-MM）/ 季（yyyy-Qn）/ 年（yyyy），由起始日期自动推导，无需手工输入</div>
    </div>

    <!-- ── 实例列表 ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">报表实例（GET /report/instances · 按区域隔离）</h3>
        <button class="btn" @click="loadInstances">刷新</button>
      </div>
      <table>
        <thead>
          <tr><th>模板</th><th>周期</th><th>生成状态</th><th>推送状态</th><th>生成时间</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="instances.length === 0"><td colspan="6" class="muted">暂无实例</td></tr>
          <tr v-for="r in instances" :key="r.id">
            <td>{{ templateName(r.templateCode ?? r.templateId) }}</td>
            <td class="mono">{{ r.period }}</td>
            <td><span class="badge" :class="r.generateStatus === 'success' ? 'badge-green' : r.generateStatus === 'failed' ? 'badge-red' : 'badge-orange'">{{ generateStatusLabel(r.generateStatus) }}</span></td>
            <td class="mono">{{ r.pushStatus }}</td>
            <td class="mono muted">{{ r.generatedAt ?? '-' }}</td>
            <td>
              <button class="btn" :disabled="r.generateStatus !== 'success'" @click="onExport(r.id)">导出</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createReportInstance, createReportTemplate, downloadReport, getAllReportTemplates, getReportInstances,
  getReportTemplates, updateReportTemplate,
} from '@/api/report'
import type { ReportInstance, ReportTemplate } from '@/api/types'
import { PERIOD_UNITS, currentPeriod, periodOf, todayStr, type PeriodUnit } from '@/utils/period'

const templates = ref<ReportTemplate[]>([])
const instances = ref<ReportInstance[]>([])
const creating = ref(false)
const genForm = reactive({
  templateCode: '', startDate: todayStr(), periodUnit: 'month' as PeriodUnit,
  period: currentPeriod('month'),
})

// ── 模板新增/编辑（V2.4：报表自定义） ──
const tplDialog = ref(false)
const tplEditing = ref<ReportTemplate | null>(null)
const tplForm = reactive({
  code: '', name: '', type: 'business', periodType: 'monthly', status: 'active',
  datasourceConfig: '{}', layout: '{}', headerConfig: '{}',
})

const successCount = computed(() => instances.value.filter((i) => i.generateStatus === 'success').length)
const typesCount = computed(() => new Set(templates.value.map((t) => t.type)).size)

function typeLabel(t: string): string {
  return { trade: '交易', settlement: '结算', forecast: '预测', assessment: '考核', business: '经营' }[t] ?? t
}

function periodTypeLabel(t: string): string {
  return { daily: '日', weekly: '周', monthly: '月', yearly: '年' }[t] ?? t
}

function generateStatusLabel(s: string): string {
  return { success: '生成成功', failed: '生成失败', pending: '生成中' }[s] ?? s
}

function headerText(h: unknown): string {
  if (!h) return '-'
  try {
    const obj = JSON.parse(String(h))
    return String(obj.remark ?? obj.description ?? obj.title ?? JSON.stringify(obj).slice(0, 60))
  } catch {
    return String(h).slice(0, 60)
  }
}

/** 起始日期 + 周期类型 → 自动推导报表周期（V2.4 周期类时间联动） */
function syncPeriod() {
  genForm.period = periodOf(genForm.startDate, genForm.periodUnit)
}

function templateName(codeOrId: string): string {
  const hit = templates.value.find((t) => t.code === codeOrId || String(t.id) === String(codeOrId))
  return hit ? `${hit.name}（${hit.code}）` : codeOrId
}

function openTemplateCreate() {
  tplEditing.value = null
  Object.assign(tplForm, { code: '', name: '', type: 'business', periodType: 'monthly', status: 'active', datasourceConfig: '{}', layout: '{}', headerConfig: '{}' })
  tplDialog.value = true
}

function openTemplateEdit(t: ReportTemplate) {
  tplEditing.value = t
  Object.assign(tplForm, {
    code: t.code, name: t.name, type: t.type, periodType: t.periodType, status: t.status,
    datasourceConfig: jsonText(t.datasourceConfig), layout: jsonText(t.layout), headerConfig: jsonText(t.headerConfig),
  })
  tplDialog.value = true
}

function jsonText(v: unknown): string {
  if (!v) return '{}'
  if (typeof v === 'string') return v
  return JSON.stringify(v)
}

async function onSaveTemplate() {
  try {
    if (tplEditing.value) {
      await updateReportTemplate(tplEditing.value.id, {
        name: tplForm.name, type: tplForm.type, periodType: tplForm.periodType, status: tplForm.status,
        datasourceConfig: tplForm.datasourceConfig || undefined, layout: tplForm.layout || undefined,
        headerConfig: tplForm.headerConfig || undefined,
      })
    } else {
      await createReportTemplate({
        code: tplForm.code, name: tplForm.name, type: tplForm.type, periodType: tplForm.periodType, status: tplForm.status,
        datasourceConfig: tplForm.datasourceConfig || undefined, layout: tplForm.layout || undefined,
        headerConfig: tplForm.headerConfig || undefined,
      })
    }
    tplDialog.value = false
    await loadTemplates()
  } catch (e) {
    alert((e as Error).message || '模板保存失败（编码唯一）')
  }
}

function openGenerate(t: ReportTemplate) {
  genForm.templateCode = t.code
  genForm.startDate = todayStr()
  syncPeriod()
}

async function onGenerate() {
  creating.value = true
  try {
    const res = await createReportInstance({ templateCode: genForm.templateCode, period: genForm.period })
    alert(`报表生成任务已创建：${res.instanceId}（${res.generateStatus}）周期 ${genForm.period}`)
    await loadInstances()
  } finally {
    creating.value = false
  }
}

async function loadTemplates() {
  try {
    templates.value = (await getAllReportTemplates()) ?? []
  } catch {
    templates.value = (await getReportTemplates()) ?? []
  }
}

async function loadInstances() {
  instances.value = ((await getReportInstances({ pageNo: 1, pageSize: 50 })) as unknown as ReportInstance[]) ?? []
}

async function onExport(id: string) {
  await downloadReport(id)
}

onMounted(async () => {
  await loadTemplates()
  if (templates.value.length > 0 && !genForm.templateCode) genForm.templateCode = templates.value[0].code
  syncPeriod()
  await loadInstances()
})
</script>
