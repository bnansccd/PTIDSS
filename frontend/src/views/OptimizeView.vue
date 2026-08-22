<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">联合优化（WBS 5.0 · GET/POST /optimize/**）</h3>
        <span class="muted">FR-TR-06 联合优化引擎 P0 · MILP 求解抽象（HiGHS/SCIP/Gurobi）· 回测收益增量</span>
      </div>
      <div class="form-row">
        <select v-model="taskForm.taskType">
          <option value="daily">日滚动</option>
          <option value="rolling_N">N 日滚动</option>
          <option value="backtest">回测模式</option>
        </select>
        <input v-model.number="taskForm.horizonDays" type="number" min="1" max="7" placeholder="周期天数 1-7" style="width: 110px" />
        <input v-model.number="taskForm.scenarioCount" type="number" min="10" max="500" placeholder="场景数 10-500" style="width: 120px" />
        <button class="btn btn-primary" :disabled="creating" @click="onCreateTask">{{ creating ? '求解中…' : '创建优化任务' }}</button>
      </div>
      <div class="form-row" v-if="lastTaskId">
        <span class="badge badge-blue">taskId：{{ lastTaskId }}</span>
        <button class="btn" @click="onRefreshStatus">刷新结果</button>
        <span v-if="taskStatus" class="muted">
          状态：{{ taskStatusLabel(taskStatus.status) }}（{{ taskStatus.status }}）
          <template v-if="taskStatus.status === 'success' || taskStatus.status === 'suboptimal'">
            · 预期收益：{{ taskStatus.expectedRevenue }} 元 · CVaR：{{ taskStatus.cvar }} · 求解器：{{ taskStatus.solver }} · 耗时：{{ taskStatus.elapsedMs }}ms
          </template>
        </span>
      </div>
    </div>

    <!-- ── 策略回测 ── -->
    <div class="card">
      <h3>策略回测（POST /optimize/backtests · 分步决策 + 收益增量，验收核心）</h3>
      <div class="form-row">
        <select v-model="btForm.strategyCode">
          <option v-for="s in strategies" :key="s.strategyCode" :value="s.strategyCode">{{ s.strategyName }}（{{ s.strategyCode }}）</option>
        </select>
        <input v-model="btForm.startDate" type="datetime-local" />
        <input v-model="btForm.endDate" type="datetime-local" />
        <button class="btn" :disabled="backtesting" @click="onBacktest">{{ backtesting ? '回测中…' : '发起回测' }}</button>
      </div>
    </div>

    <!-- ── 策略库 ── -->
    <div class="card">
      <h3>策略库（GET /optimize/strategies · 回测/复盘/人工三源沉淀）</h3>
      <table>
        <thead>
          <tr><th>编码</th><th>名称</th><th>来源</th><th>绩效</th><th>状态</th></tr>
        </thead>
        <tbody>
          <tr v-if="strategies.length === 0"><td colspan="5" class="muted">暂无数据</td></tr>
          <tr v-for="s in strategies" :key="s.strategyCode">
            <td class="mono">{{ s.strategyCode }}</td>
            <td>{{ s.strategyName }}</td>
            <td>{{ sourceLabel(s.source) }}</td>
            <td class="muted mono">{{ s.performance }}</td>
            <td><span class="badge" :class="s.status === 'effective' ? 'badge-green' : 'badge-orange'">{{ statusLabel(s.status) }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getJointTaskStatus, getStrategies, postBacktest, postJointTask } from '@/api/optimize'
import type { JointTaskStatus, StrategyInfo } from '@/api/types'

const taskForm = ref({ taskType: 'daily', horizonDays: 1, scenarioCount: 100 })
const creating = ref(false)
const lastTaskId = ref('')
const taskStatus = ref<JointTaskStatus | null>(null)
const btForm = ref({ strategyCode: '', startDate: dateOffset(-30), endDate: dateOffset(0) })
const backtesting = ref(false)
const strategies = ref<StrategyInfo[]>([])

function dateOffset(days: number): string {
  const d = new Date()
  d.setDate(d.getDate() + days)
  return d.toISOString().slice(0, 10)
}

function sourceLabel(s: string): string {
  const map: Record<string, string> = { backtest: '回测沉淀', review: '复盘沉淀', manual: '人工录入' }
  return map[s] ?? s
}

function statusLabel(s: string): string {
  const map: Record<string, string> = { effective: '生效', evaluating: '评估中', deprecated: '已废弃' }
  return map[s] ?? s
}

/** V2.4 编码+名称：优化任务状态中文标签 */
function taskStatusLabel(s: string): string {
  const map: Record<string, string> = { queued: '排队中', running: '求解中', success: '完成', suboptimal: '次优解', failed: '失败' }
  return map[s] ?? s
}

async function onCreateTask() {
  creating.value = true
  try {
    const res = await postJointTask({
      taskType: taskForm.value.taskType,
      horizonDays: taskForm.value.horizonDays,
      scenarioCount: taskForm.value.scenarioCount,
    })
    lastTaskId.value = res.taskId
    taskStatus.value = null
    await onRefreshStatus()
  } finally {
    creating.value = false
  }
}

async function onRefreshStatus() {
  if (!lastTaskId.value) return
  taskStatus.value = await getJointTaskStatus(lastTaskId.value)
}

async function onBacktest() {
  backtesting.value = true
  try {
    const res = await postBacktest({ ...btForm.value })
    alert(`回测完成：runId=${res.runId}`)
  } finally {
    backtesting.value = false
  }
}

onMounted(async () => {
  strategies.value = (await getStrategies()) ?? []
  if (strategies.value.length > 0) btForm.value.strategyCode = strategies.value[0].strategyCode
})
</script>
