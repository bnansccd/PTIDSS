<template>
  <div>
    <!-- ── 统计卡（FR-AS-01 考核看板） ── -->
    <div class="stat-grid">
      <div class="card stat-card"><div class="stat-value">{{ indicators.length }}</div><div class="stat-label">考核指标（权重合计 {{ totalWeight }}）</div></div>
      <div class="card stat-card"><div class="stat-value">{{ results.length }}</div><div class="stat-label">考核结果（{{ period }}）</div></div>
      <div class="card stat-card"><div class="stat-value">{{ confirmedCount }}</div><div class="stat-label">已确认</div></div>
      <div class="card stat-card"><div class="stat-value">{{ topRank }}</div><div class="stat-label">最优排名</div></div>
    </div>

    <!-- ── 考核指标体系（FR-AS-01，V2.4：可新增/编辑/自定义） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">考核指标体系（GET /assessment/indicators · 收益完成率/预测准确率/偏差率/合规执行率/复盘完成率 · V2.4 支持新增/编辑/自定义）</h3>
        <button class="btn btn-primary" style="margin-left: auto" @click="openIndicatorCreate">新增指标</button>
      </div>
      <table>
        <thead>
          <tr><th>编码</th><th>指标</th><th>权重</th><th>目标值</th><th>数据来源</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="indicators.length === 0"><td colspan="7" class="muted">暂无指标</td></tr>
          <tr v-for="i in indicators" :key="i.id">
            <td class="mono">{{ i.code }}</td>
            <td>{{ i.name }}</td>
            <td class="mono">{{ i.weight }}</td>
            <td class="mono">{{ targetText(i.targetValue) }}</td>
            <td class="mono muted">{{ i.formula }}</td>
            <td><span class="badge" :class="i.status === 'active' ? 'badge-green' : 'badge-gray'">{{ i.status === 'active' ? '启用' : '停用' }}</span></td>
            <td><button class="btn btn-sm" @click="openIndicatorEdit(i)">编辑</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 指标新增/编辑弹窗（V2.4：考核体系自定义） ── -->
    <div v-if="indDialog" class="modal-mask" @click.self="indDialog = false">
      <div class="modal" style="width: 560px">
        <h3>{{ indEditing ? '编辑指标：' + indEditing.code : '新增考核指标' }}</h3>
        <div class="form-row">
          <input v-model="indForm.code" placeholder="指标编码（如 revenue_rate）" :disabled="!!indEditing" style="flex: 1" />
          <input v-model="indForm.name" placeholder="指标名称（如 收益完成率）" style="flex: 1" />
        </div>
        <div class="form-row">
          <input v-model.number="indForm.weight" type="number" step="0.01" min="0" max="1" placeholder="权重（0-1，如 0.3）" style="width: 140px" />
          <input v-model="indForm.targetValue" placeholder="目标值（如 90%）" style="flex: 1" />
          <select v-model="indForm.status">
            <option value="active">启用</option>
            <option value="disabled">停用</option>
          </select>
        </div>
        <div class="form-row">
          <input v-model="indForm.dataSource" placeholder="数据来源（如 trade_result/forecast_accuracy）" style="flex: 1" />
        </div>
        <textarea v-model="indForm.scoringRule" rows="2" placeholder="评分规则（如 完成率≥目标得 100 分，每低 1% 扣 2 分）" style="width: 100%" />
        <textarea v-model="indForm.formula" rows="2" class="mono" placeholder='计算公式 JSON，如 {"base":"settlement_income","rate":"actual/target"}' style="width: 100%" />
        <div class="muted">权重合计须 ≤ 1；修改后考核结果按新指标体系重新计算</div>
        <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
          <button class="btn" @click="indDialog = false">取消</button>
          <button class="btn btn-primary" :disabled="!indForm.code || !indForm.name || indForm.weight <= 0 || indForm.weight > 1" @click="onSaveIndicator">保存</button>
        </div>
      </div>
    </div>

    <!-- ── 考核结果（FR-AS-01 自动评分） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">考核结果（GET /assessment/results · 周期必填）</h3>
        <input v-model="period" type="month" style="width: 160px" />
        <select v-model="scope">
          <option value="">全部范围</option>
          <option value="personal">个人</option>
          <option value="team">团队</option>
        </select>
        <button class="btn" @click="loadResults">查询</button>
      </div>
      <table>
        <thead>
          <tr><th>周期</th><th>范围</th><th>总分</th><th>分项得分</th><th>排名</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="results.length === 0"><td colspan="7" class="muted">暂无考核结果（选择周期后查询）</td></tr>
          <tr v-for="r in results" :key="r.id">
            <td class="mono">{{ r.period }}</td>
            <td><span class="badge badge-blue">{{ scopeLabel(r.scope) }}</span></td>
            <td class="mono"><strong>{{ r.totalScore }}</strong></td>
            <td class="muted mono">{{ scoresText(r.scores) }}</td>
            <td class="mono">{{ r.rank ?? '-' }}</td>
            <td><span class="badge" :class="statusClass(r.status)">{{ statusLabel(r.status) }}</span></td>
            <td>
              <button class="btn" @click="openAppeal(r)" :disabled="!canAppeal(r.status)">申诉</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 申诉弹窗（FR-AS-02） ── -->
    <div v-if="appealTarget" class="modal-mask" @click.self="appealTarget = null">
      <div class="modal" style="width: 480px">
        <h3>考核申诉（POST /assessment/appeals · 结果发布后 5 个工作日内）</h3>
        <div class="muted" style="margin-bottom: 8px">对象：{{ appealTarget.period }} · {{ scopeLabel(appealTarget.scope) }} · 总分 {{ appealTarget.totalScore }}</div>
        <textarea v-model="appealReason" rows="4" placeholder="申诉理由（必填）" style="width: 100%"></textarea>
        <div class="form-row" style="margin-top: 12px">
          <button class="btn" @click="appealTarget = null">取消</button>
          <button class="btn btn-primary" :disabled="submitting || !appealReason.trim()" @click="onAppeal">{{ submitting ? '提交中…' : '提交申诉' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createAssessAppeal, createAssessIndicator, getAllAssessIndicators, getAssessIndicators, getAssessResults,
  updateAssessIndicator,
  type AssessIndicator, type AssessResult,
} from '@/api/review'

const indicators = ref<AssessIndicator[]>([])
const results = ref<AssessResult[]>([])
const period = ref(currentPeriod())
const scope = ref('')
const appealTarget = ref<AssessResult | null>(null)
const appealReason = ref('')
const submitting = ref(false)

// ── 指标新增/编辑（V2.4：考核体系自定义） ──
const indDialog = ref(false)
const indEditing = ref<AssessIndicator | null>(null)
const indForm = reactive({
  code: '', name: '', weight: 0.3, targetValue: '', scoringRule: '',
  formula: '{}', dataSource: '', status: 'active',
})
const indSaving = ref(false)

function openIndicatorCreate() {
  indEditing.value = null
  Object.assign(indForm, { code: '', name: '', weight: 0.3, targetValue: '', scoringRule: '', formula: '{}', dataSource: '', status: 'active' })
  indDialog.value = true
}

function openIndicatorEdit(i: AssessIndicator) {
  indEditing.value = i
  Object.assign(indForm, {
    code: i.code, name: i.name, weight: Number(i.weight) || 0,
    targetValue: i.targetValue ?? '', scoringRule: i.scoringRule ?? '',
    formula: i.formula && i.formula !== '{}' ? i.formula : '{}', dataSource: '', status: i.status,
  })
  indDialog.value = true
}

async function onSaveIndicator() {
  indSaving.value = true
  try {
    if (indEditing.value) {
      await updateAssessIndicator(indEditing.value.id, {
        name: indForm.name, weight: indForm.weight,
        formula: indForm.formula || undefined, targetValue: indForm.targetValue || undefined,
        scoringRule: indForm.scoringRule || undefined, dataSource: indForm.dataSource || undefined,
        status: indForm.status,
      })
    } else {
      await createAssessIndicator({
        code: indForm.code, name: indForm.name, weight: indForm.weight,
        formula: indForm.formula || undefined, targetValue: indForm.targetValue || undefined,
        scoringRule: indForm.scoringRule || undefined, dataSource: indForm.dataSource || undefined,
        status: indForm.status,
      })
    }
    indDialog.value = false
    await loadIndicators()
  } catch (e) {
    alert((e as Error).message || '指标保存失败（编码唯一）')
  } finally {
    indSaving.value = false
  }
}

const totalWeight = computed(() => indicators.value.reduce((sum, i) => sum + (Number(i.weight) || 0), 0).toFixed(4))
const confirmedCount = computed(() => results.value.filter((r) => r.status === 'confirmed' || r.status === 'corrected').length)
const topRank = computed(() => {
  const ranks = results.value.map((r) => r.rank).filter((v): v is number => v != null)
  return ranks.length > 0 ? Math.min(...ranks) : '-'
})

function currentPeriod(): string {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

function scopeLabel(s: string): string {
  return s === 'personal' ? '个人' : '团队'
}
function statusLabel(s: string): string {
  return { pending: '待确认', confirmed: '已确认', appealing: '申诉中', corrected: '已更正' }[s] ?? s
}
function statusClass(s: string): string {
  return s === 'confirmed' || s === 'corrected' ? 'badge-green' : s === 'appealing' ? 'badge-orange' : 'badge-gray'
}
function targetText(v: string): string {
  if (!v) return '-'
  try {
    return JSON.stringify(JSON.parse(v))
  } catch {
    return String(v)
  }
}
function scoresText(scores: Record<string, number>): string {
  return Object.entries(scores ?? {}).map(([k, v]) => `${k}:${v}`).join('；')
}
function canAppeal(status: string): boolean {
  return status === 'pending' || status === 'confirmed'
}

async function loadIndicators() {
  // 管理端加载全量（含停用）以支持编辑维护；普通用户回退启用列表
  try {
    indicators.value = (await getAllAssessIndicators()) ?? []
  } catch {
    indicators.value = (await getAssessIndicators()) ?? []
  }
}

async function loadResults() {
  results.value = (await getAssessResults({ period: period.value, ...(scope.value ? { scope: scope.value } : {}), pageNo: 1, pageSize: 50 })) ?? []
}

function openAppeal(r: AssessResult) {
  appealTarget.value = r
  appealReason.value = ''
}

async function onAppeal() {
  if (!appealTarget.value) return
  submitting.value = true
  try {
    await createAssessAppeal({ resultId: appealTarget.value.id, appealReason: appealReason.value.trim() })
    appealTarget.value = null
    alert('申诉已提交，进入审核流程')
    await loadResults()
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadIndicators()
  await loadResults()
})
</script>
