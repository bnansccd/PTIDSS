<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">审批流（POST/GET /flow/** · M7 移动端审批依赖）</h3>
        <span class="muted">V2.2 定义驱动：客户自定义流程与审批环节（角色/用户/时限），实例按定义流转留痕</span>
      </div>
      <div class="form-row">
        <span class="badge" :class="tab === 'instances' ? 'badge-blue' : ''" style="cursor: pointer" @click="tab = 'instances'">流程实例</span>
        <span class="badge" :class="tab === 'definitions' ? 'badge-blue' : ''" style="cursor: pointer" @click="tab = 'definitions'">流程定义</span>
      </div>
    </div>

    <!-- ── 流程定义管理（V2.2：审批环节/角色/用户/时限可定义，适配客户流程） ── -->
    <template v-if="tab === 'definitions'">
      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">流程定义（GET /flow/definitions · 环节/审批角色/用户/时限可定义）</h3>
          <button class="btn btn-primary" style="margin-left: auto" @click="openCreate">新增定义</button>
        </div>
        <table>
          <thead>
            <tr><th>流程编码</th><th>流程名称</th><th>业务类型</th><th>审批环节</th><th>状态</th><th>更新时间</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="definitions.length === 0"><td colspan="7" class="muted">暂无定义</td></tr>
            <tr v-for="d in definitions" :key="d.id">
              <td class="mono">{{ d.processKey }}</td>
              <td>{{ d.processName }}</td>
              <td class="mono">{{ d.bizType }}</td>
              <td class="muted" style="max-width: 340px">{{ stepSummary(d.steps) }}</td>
              <td><span class="badge" :class="d.status === 'enabled' ? 'badge-green' : 'badge-gray'">{{ d.status === 'enabled' ? '启用' : '停用' }}</span></td>
              <td class="mono muted">{{ d.updatedAt ?? '-' }}</td>
              <td><button class="btn btn-sm" @click="openEdit(d)">编辑</button></td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 新增/编辑定义弹窗 -->
      <div v-if="defDialog" class="modal-mask" @click.self="defDialog = false">
        <div class="modal" style="width: 700px">
          <h3>{{ editingDef ? '编辑流程定义：' + editingDef.processKey : '新增流程定义' }}</h3>
          <div class="form-row">
            <input v-model="defForm.processKey" placeholder="流程编码（如 settlement_confirm）" :disabled="!!editingDef" style="flex: 1" />
            <input v-model="defForm.processName" placeholder="流程名称（如 结算确认）" style="flex: 1" />
          </div>
          <div class="form-row">
            <select v-model="defForm.bizType" :disabled="!!editingDef">
              <option value="decision">decision 决策确认</option>
              <option value="declaration">declaration 申报审批</option>
              <option value="ticket">ticket 差异工单</option>
              <option value="appeal">appeal 考核申诉</option>
              <option value="settlement_ticket">settlement_ticket 结算差异工单</option>
              <option value="custom">custom 客户自定义</option>
            </select>
            <span class="muted">P3-1 可视化拖拽编排：拖拽卡片排序或使用 ↑↓；环节角色/用户/时限行内编辑；JSON 高级模式可双向同步</span>
          </div>
          <!-- 可视化步骤卡片（HTML5 拖拽排序） -->
          <div class="flow-steps">
            <div
              v-for="(s, i) in defSteps"
              :key="i"
              class="flow-step-card"
              draggable="true"
              :style="dragIndex === i ? 'opacity: 0.45' : ''"
              @dragstart="onDragStart(i, $event)"
              @dragover.prevent="onDragOver(i)"
              @drop.prevent="onDrop()"
              @dragend="onDrop()"
            >
              <div class="form-row" style="gap: 6px">
                <span class="badge badge-blue" style="cursor: grab" title="拖拽排序">≡ {{ i + 1 }}</span>
                <input v-model="s.stepNo" placeholder="环节编码（如 apply）" style="width: 130px" />
                <input v-model="s.stepName" placeholder="环节名称（如 申请）" style="flex: 1" />
                <select v-model="s.approveMode" style="width: 120px">
                  <option value="any">any 任一通过</option>
                  <option value="all">all 全部通过</option>
                </select>
                <input v-model.number="s.timeoutHours" type="number" min="1" placeholder="时限h" style="width: 76px" title="审批超时小时数" />
                <button class="btn btn-sm" title="上移" :disabled="i === 0" @click="moveStep(i, -1)">↑</button>
                <button class="btn btn-sm" title="下移" :disabled="i === defSteps.length - 1" @click="moveStep(i, 1)">↓</button>
                <button class="btn btn-sm" title="删除环节" @click="removeStep(i)">×</button>
              </div>
              <div class="form-row" style="gap: 6px; margin-top: 4px">
                <span class="muted" style="width: 44px">角色</span>
                <span v-for="r in s.roleCodes ?? []" :key="r" class="badge badge-orange" style="cursor: pointer" :title="'移除 ' + roleLabel(r)" @click="toggleRole(i, r)">{{ roleLabel(r) }}</span>
                <select
                  v-if="roles.length > 0"
                  class="role-select"
                  :value="''"
                  @change="addRole(i, ($event.target as HTMLSelectElement).value); ($event.target as HTMLSelectElement).value = ''"
                >
                  <option value="">+ 添加角色</option>
                  <option v-for="r in roles.filter((x) => !(s.roleCodes ?? []).includes(x.roleCode))" :key="r.id" :value="r.roleCode">{{ r.roleName }}（{{ r.roleCode }}）</option>
                </select>
                <span class="muted" style="width: 44px">用户</span>
                <span v-for="u in s.userIds ?? []" :key="u" class="badge badge-green" style="cursor: pointer" :title="'移除 ' + userLabel(u)" @click="toggleUser(i, u)">{{ userLabel(u) }}</span>
                <select
                  v-if="users.length > 0"
                  class="role-select"
                  :value="''"
                  @change="addUser(i, ($event.target as HTMLSelectElement).value); ($event.target as HTMLSelectElement).value = ''"
                >
                  <option value="">+ 添加用户</option>
                  <option v-for="u in users.filter((x) => !(s.userIds ?? []).includes(String(x.id)))" :key="u.id" :value="String(u.id)">{{ u.displayName || u.username }}（{{ u.username }}）</option>
                </select>
              </div>
            </div>
            <div class="form-row" style="gap: 6px; margin-top: 6px">
              <button class="btn btn-sm" @click="addStep">+ 添加环节</button>
              <button class="btn btn-sm" @click="showJson = !showJson">{{ showJson ? '收起 JSON' : 'JSON 高级模式' }}</button>
            </div>
            <div v-if="showJson" class="form-row" style="gap: 6px; margin-top: 4px">
              <textarea v-model="jsonText" rows="7" class="mono" style="width: 100%" placeholder="步骤 JSON（与可视化双向同步，修改后点「应用 JSON」）" />
              <button class="btn btn-sm" @click="applyJson">应用 JSON</button>
            </div>
          </div>
          <div class="form-row">
            <select v-model="defForm.status">
              <option value="enabled">启用</option>
              <option value="disabled">停用</option>
            </select>
            <span class="muted">首环节 apply 视为发起自动完成；approveMode=any 任一角色通过 / all 全部通过；停用后新实例不可发起</span>
          </div>
          <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
            <button class="btn" @click="defDialog = false">取消</button>
            <button class="btn btn-primary" :disabled="!defForm.processKey || !defForm.processName || defSteps.length === 0" @click="onSaveDefinition">保存定义</button>
          </div>
        </div>
      </div>
    </template>

    <!-- ── 流程实例 ── -->
    <template v-else>
      <div class="card">
        <div class="form-row">
          <select v-model="startForm.processKey" @change="onProcessKeyChange" style="flex: 1">
            <option v-for="d in definitions" :key="d.id" :value="d.processKey">{{ d.processName }}（{{ d.processKey }}）</option>
            <option v-if="definitions.length === 0" value="decision_confirm">决策确认</option>
            <option v-if="definitions.length === 0" value="declaration_approve">申报审批</option>
            <option v-if="definitions.length === 0" value="ticket_handle">差异工单</option>
            <option v-if="definitions.length === 0" value="appeal_review">考核申诉</option>
            <option v-if="definitions.length === 0" value="settlement_ticket_review">结算差异工单</option>
          </select>
          <span v-if="currentBiz" class="badge badge-blue">业务类型：{{ currentBiz.bizName }}（{{ currentBiz.bizType }}）</span>
        </div>
        <!-- V2.4：选业务类型后引入该类型已有单号（搜索可选）或自动生成，规则按业务类型匹配 -->
        <div class="form-row" style="margin-top: 8px">
          <input v-model="startForm.bizId" list="biz-options" placeholder="业务单号（可从已有单号中选择，或点击自动生成；留空发起时按业务类型自动生成）" style="flex: 1" />
          <datalist id="biz-options">
            <option v-for="o in bizOptions" :key="o.value" :value="o.value">{{ o.label }}</option>
          </datalist>
          <button class="btn" :disabled="!currentBiz" @click="onAutoBizId">自动生成单号</button>
          <button class="btn btn-primary" :disabled="starting" @click="onStart">{{ starting ? '发起中…' : '发起流程' }}</button>
          <span v-if="startResp" class="badge badge-blue">instanceId：{{ startResp.instanceId }}</span>
          <button v-if="startResp" class="btn" @click="detail.instanceId = startResp.instanceId; onQueryDetail()">查看详情</button>
        </div>
        <div class="muted" v-if="currentBiz">单号规则：{{ currentBiz.autoPrefix }} + 时间戳 + 4 位随机；已有单号来自{{ currentBiz.bizName }}模块（最多 50 条），可直接选择</div>
      </div>

      <div class="card">
        <div class="form-row">
          <h3 style="margin: 0">流程实例详情（GET /flow/instances/{instanceId}）</h3>
          <input v-model="detail.instanceId" placeholder="instanceId" />
          <button class="btn" @click="onQueryDetail">查询</button>
        </div>
        <div v-if="instance" class="form-row" style="flex-wrap: wrap; margin-top: 8px">
          <span class="badge badge-blue">{{ instance.instanceNo }}</span>
          <span class="badge" :class="instance.status === 'running' ? 'badge-green' : 'badge-gray'">{{ statusLabel(instance.status) }}</span>
          <span class="muted">流程：{{ instance.definitionName ?? instance.processKey }} · 业务：{{ instance.bizType }} {{ instance.bizId }}</span>
          <span class="muted">当前节点：{{ nodeLabel(instance.currentNode ?? '') }} · 处理人：{{ instance.currentAssignee }}</span>
          <span class="muted">发起人：{{ instance.startBy }} · {{ instance.startTime }}</span>
        </div>

        <!-- 环节进度（按流程定义） -->
        <div v-if="instance && stepList(instance).length" class="form-row" style="flex-wrap: wrap; margin-top: 8px">
          <template v-for="(s, i) in stepList(instance)" :key="s.stepNo">
            <span class="badge" :class="stepClass(instance, i)">{{ i + 1 }}.{{ s.stepName }}<span v-if="s.roleCodes?.length" class="muted">（{{ s.roleCodes.join('/') }}）</span></span>
            <span v-if="i < stepList(instance).length - 1" class="muted" style="margin: 0 6px">→</span>
          </template>
        </div>

        <!-- 审批推进（V2.2：approve → 下一环节/完成；reject → 终止） -->
        <div v-if="instance && instance.status === 'running'" class="form-row" style="margin-top: 8px">
          <input v-model="advanceComment" placeholder="审批意见（留痕）" style="flex: 1" />
          <button class="btn btn-primary" :disabled="advancing" @click="onAdvance('approve')">通过 → 下一环节</button>
          <button class="btn" :disabled="advancing" @click="onAdvance('reject')">驳回终止</button>
        </div>

        <!-- 审批留痕 -->
        <div v-if="instance && actionsList(instance).length" class="form-row" style="flex-wrap: wrap; margin-top: 8px">
          <span class="muted">审批留痕：</span>
          <span v-for="(a, i) in actionsList(instance)" :key="i" class="badge" :class="a.action === 'approve' ? 'badge-green' : 'badge-red'">{{ nodeLabel(a.node) }} · {{ a.action }} · {{ a.operator }} · {{ a.comment || '—' }} · {{ a.time }}</span>
        </div>

        <table v-if="instance">
          <thead>
            <tr><th>任务 ID</th><th>节点</th><th>处理人</th><th>状态</th></tr>
          </thead>
          <tbody>
            <tr v-if="instance.currentTasks.length === 0"><td colspan="4" class="muted">无待办任务（流程已结束）</td></tr>
            <tr v-for="t in instance.currentTasks" :key="t.taskId">
              <td class="mono">{{ t.taskId }}</td>
              <td>{{ nodeLabel(t.node) }}</td>
              <td>{{ t.assignee ?? '-' }}</td>
              <td><span class="badge badge-orange">{{ taskStatusLabel(t.status) }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getFlowBizOptions, getFlowBizTypes, getFlowDefinitions, getFlowInstance, postFlowAdvance, postFlowDefinition, postFlowStart, putFlowDefinition } from '@/api/flow'
import type { FlowDefinition, FlowInstanceDetail, FlowStartResp, FlowStep } from '@/api/types'
import { getAdminRoles, getAdminUsers, type AdminRole, type AdminUser } from '@/api/admin'

const tab = ref<'instances' | 'definitions'>('instances')

// ── 流程定义管理（V2.2：客户自定义流程/环节/角色/用户；P3-1 可视化拖拽编排） ──
const definitions = ref<FlowDefinition[]>([])
const defDialog = ref(false)
const editingDef = ref<FlowDefinition | null>(null)
const defForm = reactive({ processKey: '', processName: '', bizType: 'decision', status: 'enabled' })
const defSteps = ref<FlowStep[]>([])
const showJson = ref(false)
const jsonText = ref('')
const dragIndex = ref(-1)
const roles = ref<AdminRole[]>([])
const users = ref<AdminUser[]>([])

/** 角色兜底（GET /admin/roles 失败/空时使用，与流程种子/静态审批兼容） */
const FALLBACK_ROLES: AdminRole[] = [
  { id: 1, roleCode: 'trader', roleName: '交易员', status: 'enabled' },
  { id: 2, roleCode: 'manager', roleName: '经理', status: 'enabled' },
  { id: 3, roleCode: 'analyst', roleName: '分析师', status: 'enabled' },
  { id: 4, roleCode: 'settlement', roleName: '结算员', status: 'enabled' },
  { id: 5, roleCode: 'compliance', roleName: '合规员', status: 'enabled' },
  { id: 6, roleCode: 'admin', roleName: '管理员', status: 'enabled' },
]

async function loadRoles() {
  try {
    roles.value = (await getAdminRoles()) ?? []
  } catch {
    roles.value = []
  }
  if (roles.value.length === 0) roles.value = FALLBACK_ROLES
}

async function loadUsers() {
  try {
    const page = await getAdminUsers({ pageNum: 1, pageSize: 200, status: 'enabled' })
    users.value = page?.records ?? []
  } catch {
    users.value = []
  }
}

function roleLabel(code: string): string {
  return roles.value.find((r) => r.roleCode === code)?.roleName ?? code
}

function userLabel(id: string): string {
  const hit = users.value.find((u) => String(u.id) === id)
  return hit ? hit.displayName || hit.username : id
}

function addStep() {
  defSteps.value.push({
    stepNo: 'step' + (defSteps.value.length + 1),
    stepName: '环节' + (defSteps.value.length + 1),
    approveMode: 'any',
    roleCodes: [],
    userIds: [],
    timeoutHours: 24,
  })
}

function removeStep(i: number) {
  defSteps.value.splice(i, 1)
}

function moveStep(i: number, delta: number) {
  const j = i + delta
  if (j < 0 || j >= defSteps.value.length) return
  const arr = defSteps.value
  const tmp = arr[i]
  arr[i] = arr[j]
  arr[j] = tmp
}

function onDragStart(i: number, e: DragEvent) {
  dragIndex.value = i
  e.dataTransfer?.setData('text/plain', String(i))
}

/** 拖拽经过其他卡片时实时交换（HTML5 DnD 排序） */
function onDragOver(i: number) {
  const from = dragIndex.value
  if (from < 0 || from === i) return
  const arr = defSteps.value
  const [moved] = arr.splice(from, 1)
  arr.splice(i, 0, moved)
  dragIndex.value = i
}

function onDrop() {
  dragIndex.value = -1
}

function toggleRole(i: number, code: string) {
  const arr = (defSteps.value[i].roleCodes ??= [])
  const idx = arr.indexOf(code)
  if (idx >= 0) arr.splice(idx, 1)
  else arr.push(code)
}

function addRole(i: number, code: string) {
  if (!code) return
  const arr = (defSteps.value[i].roleCodes ??= [])
  if (!arr.includes(code)) arr.push(code)
}

function toggleUser(i: number, id: string) {
  const arr = (defSteps.value[i].userIds ??= [])
  const idx = arr.indexOf(id)
  if (idx >= 0) arr.splice(idx, 1)
  else arr.push(id)
}

function addUser(i: number, id: string) {
  if (!id) return
  const arr = (defSteps.value[i].userIds ??= [])
  if (!arr.includes(id)) arr.push(id)
}

function applyJson() {
  try {
    const parsed = JSON.parse(jsonText.value) as FlowStep[]
    if (!Array.isArray(parsed) || parsed.length === 0) {
      alert('至少定义 1 个审批环节')
      return
    }
    defSteps.value = parsed
    showJson.value = false
  } catch {
    alert('审批环节 JSON 格式错误')
  }
}

function fillSteps(steps: FlowStep[]) {
  defSteps.value = (steps ?? []).map((s) => ({
    stepNo: s.stepNo,
    stepName: s.stepName,
    approveMode: s.approveMode ?? 'any',
    roleCodes: [...(s.roleCodes ?? [])],
    userIds: [...(s.userIds ?? [])],
    timeoutHours: s.timeoutHours ?? 24,
  }))
  jsonText.value = JSON.stringify(defSteps.value, null, 2)
}

async function loadDefinitions() {
  try {
    definitions.value = await getFlowDefinitions()
  } catch {
    definitions.value = []
  }
}

function stepSummary(steps: FlowStep[] | string): string {
  return stepsOf(steps).map((s) => s.stepName).join(' → ')
}

function stepsOf(steps: FlowStep[] | string): FlowStep[] {
  if (!steps) return []
  if (Array.isArray(steps)) return steps
  try {
    return JSON.parse(steps) as FlowStep[]
  } catch {
    return []
  }
}

function openCreate() {
  editingDef.value = null
  defForm.processKey = ''
  defForm.processName = ''
  defForm.bizType = 'decision'
  fillSteps([
    { stepNo: 'apply', stepName: '申请', approveMode: 'any', roleCodes: ['trader'], userIds: [], timeoutHours: 24 },
    { stepNo: 'review', stepName: '复核', approveMode: 'any', roleCodes: ['manager'], userIds: [], timeoutHours: 24 },
  ])
  defForm.status = 'enabled'
  showJson.value = false
  defDialog.value = true
}

function openEdit(d: FlowDefinition) {
  editingDef.value = d
  defForm.processKey = d.processKey
  defForm.processName = d.processName
  defForm.bizType = d.bizType
  fillSteps(stepsOf(d.steps))
  defForm.status = d.status || 'enabled'
  showJson.value = false
  defDialog.value = true
}

async function onSaveDefinition() {
  const steps = defSteps.value
  if (steps.length === 0) {
    alert('至少定义 1 个审批环节')
    return
  }
  const normalized: FlowStep[] = steps.map((s, i) => ({
    stepNo: s.stepNo?.trim() || 'step' + (i + 1),
    stepName: s.stepName?.trim() || '环节' + (i + 1),
    approveMode: s.approveMode ?? 'any',
    roleCodes: s.roleCodes ?? [],
    userIds: s.userIds ?? [],
    timeoutHours: s.timeoutHours ?? 24,
  }))
  try {
    if (editingDef.value) {
      await putFlowDefinition(editingDef.value.id, { processName: defForm.processName, steps: normalized, status: defForm.status })
    } else {
      await postFlowDefinition({ processKey: defForm.processKey, processName: defForm.processName, bizType: defForm.bizType, steps: normalized })
    }
    defDialog.value = false
    await loadDefinitions()
  } catch (e) {
    alert((e as Error).message || '保存失败')
  }
}

// ── 流程实例 ──
const startForm = ref({ processKey: 'declaration_approve', bizId: '' })
const starting = ref(false)
const startResp = ref<FlowStartResp | null>(null)
const detail = ref({ instanceId: '' })
const instance = ref<FlowInstanceDetail | null>(null)
const advanceComment = ref('')
const advancing = ref(false)

// V2.4：业务类型字典 + 已有单号选项（选流程定义 → 带出业务类型 → 引入该类型单号/自动生成）
const bizTypesMap = ref<Record<string, { bizName: string; autoPrefix: string }>>({})
const bizOptions = ref<Array<{ value: string; label: string }>>([])
const currentBiz = computed(() => {
  const d = definitions.value.find((x) => x.processKey === startForm.value.processKey)
  if (!d) return null
  const meta = bizTypesMap.value[d.bizType]
  return { bizType: d.bizType, bizName: meta?.bizName ?? d.bizType, autoPrefix: meta?.autoPrefix ?? 'BIZ' }
})

async function loadBizTypes() {
  try {
    const list = await getFlowBizTypes()
    bizTypesMap.value = Object.fromEntries(list.map((x) => [x.bizType, { bizName: x.bizName, autoPrefix: x.autoPrefix }]))
  } catch {
    bizTypesMap.value = { decision: { bizName: '决策确认', autoPrefix: 'SESS' }, declaration: { bizName: '交易申报', autoPrefix: 'DECL' }, ticket: { bizName: '差异工单', autoPrefix: 'TKT' }, appeal: { bizName: '考核申诉', autoPrefix: 'APL' } }
  }
}

async function loadBizOptions() {
  bizOptions.value = []
  if (!currentBiz.value) return
  try {
    const resp = await getFlowBizOptions(currentBiz.value.bizType)
    bizOptions.value = resp.options ?? []
  } catch {
    bizOptions.value = []
  }
}

function onProcessKeyChange() {
  startForm.value.bizId = ''
  loadBizOptions()
}

/** 自动生成单号：业务前缀 + 时间戳 + 4 位随机（与后端 autoBizNo 规则一致） */
function onAutoBizId() {
  if (!currentBiz.value) return
  const d = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  const ts = `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}${pad(d.getHours())}${pad(d.getMinutes())}${pad(d.getSeconds())}`
  const rand = Math.random().toString(36).slice(2, 6).toUpperCase()
  startForm.value.bizId = `${currentBiz.value.autoPrefix}${ts}${rand}`
}

function statusLabel(s: string): string {
  const map: Record<string, string> = { running: '运行中', completed: '已完成', terminated: '已终止' }
  return map[s] ?? s
}

function nodeLabel(n: string): string {
  const map: Record<string, string> = { apply: '申请', review: '复核', approve: '审批', archive: '归档' }
  return map[n] ?? n
}

/** V2.4 编码+名称：审批任务状态中文标签 */
function taskStatusLabel(s: string): string {
  const map: Record<string, string> = { todo: '待办', doing: '处理中', done: '已完成', approved: '已通过', rejected: '已驳回' }
  return map[s] ?? s
}

function stepList(inst: FlowInstanceDetail): FlowStep[] {
  return inst.definitionSteps ?? []
}

function stepClass(inst: FlowInstanceDetail, i: number): string {
  const idx = inst.currentStepIndex ?? -1
  if (i < idx) return 'badge-green'
  if (i === idx) return 'badge-blue'
  return 'badge-gray'
}

/** 审批留痕（variables.actions，兼容 JSON 字符串） */
function actionsList(inst: FlowInstanceDetail): Array<Record<string, string>> {
  const v = inst.variables?.actions
  if (!v) return []
  if (Array.isArray(v)) return v as Array<Record<string, string>>
  try {
    const parsed = JSON.parse(String(v)) as unknown
    return Array.isArray(parsed) ? (parsed as Array<Record<string, string>>) : []
  } catch {
    return []
  }
}

async function onStart() {
  starting.value = true
  try {
    startResp.value = await postFlowStart({ processKey: startForm.value.processKey, bizId: startForm.value.bizId })
  } finally {
    starting.value = false
  }
}

async function onQueryDetail() {
  if (!detail.value.instanceId) return
  try {
    instance.value = await getFlowInstance(detail.value.instanceId)
  } catch (e) {
    alert((e as Error).message || '查询失败')
  }
}

async function onAdvance(action: 'approve' | 'reject') {
  if (!instance.value) return
  advancing.value = true
  try {
    await postFlowAdvance(instance.value.instanceId, { action, comment: advanceComment.value || undefined })
    advanceComment.value = ''
    await onQueryDetail()
  } catch (e) {
    alert((e as Error).message || '推进失败')
  } finally {
    advancing.value = false
  }
}

onMounted(() => {
  loadDefinitions()
  loadRoles()
  loadUsers()
  loadBizTypes()
  loadBizOptions()
})
</script>

<style scoped>
.flow-steps {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.flow-step-card {
  border: 1px dashed #b7c4d8;
  border-radius: 8px;
  padding: 8px 10px;
  background: #f8fafd;
}
.flow-step-card:hover {
  border-color: #4b7cf3;
  background: #f0f5ff;
}
.role-select {
  max-width: 170px;
  font-size: 12px;
}
</style>
