<template>
  <div>
    <!-- ── 发起决策会话（FR-DM-01 会话编排） ── -->
    <div class="card">
      <h3>发起决策会话（POST /decision/sessions · 多智能体编排 + 冲突仲裁）</h3>
      <div class="form-row">
        <select v-model="session.sessionType">
          <option value="rolling">日滚动</option>
          <option value="spot_quote">现货报价</option>
          <option value="joint_optimize">联合优化</option>
        </select>
        <input v-model="session.tradeDate" type="datetime-local" style="width: 190px" />
        <select v-model="session.scenario">
          <option value="baseline">基线</option>
          <option value="conservative">保守</option>
          <option value="aggressive">激进</option>
        </select>
        <button class="btn btn-primary" :disabled="submitting" @click="onCreateSession">{{ submitting ? '编排中…' : '发起' }}</button>
      </div>
      <div class="form-row">
        <span class="muted">会话详情查询：</span>
        <input v-model="queryId" placeholder="sessionId" style="width: 160px" />
        <button class="btn" :disabled="!queryId" @click="onLoadDetail">查询</button>
      </div>
    </div>

    <!-- ── 会话详情 + 人审操作（FR-DM-05：确认/修改/驳回） ── -->
    <div v-if="detail" class="card">
      <div class="form-row">
        <h3 style="margin: 0">会话详情（{{ detail.sessionNo ?? detail.sessionId }}）</h3>
        <span class="badge" :class="statusClass(String(detail.humanReviewStatus ?? ''))">{{ detail.humanReviewStatus }}</span>
        <span class="muted">类型：{{ detail.sessionType }} · 交易日：{{ detail.tradeDate }}</span>
      </div>

      <!-- 策略摘要 -->
      <div class="grid-2" v-if="strategy">
        <div class="card" v-if="strategy.expectedRevenue !== undefined">
          <span class="muted">预期收益</span><br /><strong>{{ strategy.expectedRevenue }} 万元</strong>
        </div>
        <div class="card" v-if="strategy.riskMetrics !== undefined">
          <span class="muted">风险度量</span><br /><strong>{{ strategy.riskMetrics }}</strong>
        </div>
        <div class="card" v-if="strategy.note">
          <span class="muted">说明</span><br /><span>{{ strategy.note }}</span>
        </div>
      </div>

      <!-- 情报评分快照（FR-INT-04 情报→决策联动 + 情报触发式重算） -->
      <div v-if="intel" class="card" style="margin-top: 8px; background: #f0f7ff; border-color: #91caff">
        <span class="badge badge-blue">情报影响</span>
        <span class="muted">评分 {{ intel.score }}（{{ intel.window }}，共 {{ intel.count }} 条）</span>
        <span class="muted">｜影响情报：{{ intel.impact }}</span>
        <button
          class="btn"
          style="margin-left: 12px"
          :disabled="!canReview || submitting || detail.humanReviewStatus !== 'pending'"
          @click="onIntelReassess"
        >{{ submitting ? '重评中…' : '情报重评' }}</button>
      </div>

      <!-- 低置信度强制人工关注（SRS FR-DM-02 R3） -->
      <div v-if="strategy && strategy.attentionRequired" class="card" style="margin-top: 8px; background: #fff1f0; border-color: #ffa39e">
        <span class="badge badge-red">强制人工关注</span>
        <span>{{ strategy.attentionNote }}</span>
      </div>
      <div v-if="strategy && strategy.degraded" class="card" style="margin-top: 8px; background: #fffbe6; border-color: #ffe58f">
        <span class="badge badge-orange">编排降级</span>
        <span class="muted">部分非关键智能体超时跳过（SRS FR-DM-01 R1），可人工补跑</span>
        <button
          class="btn btn-primary"
          style="margin-left: 12px"
          :disabled="!canReview || submitting"
          @click="onRerun"
        >{{ submitting ? '补跑中…' : '补跑缺失智能体' }}</button>
      </div>

      <!-- 报价段（spot_quote） / 调整建议（rolling） -->
      <table v-if="segments.length" style="margin-top: 8px">
        <thead><tr><th>段号</th><th>价格（元/MWh）</th><th>量（MWh）</th></tr></thead>
        <tbody>
          <tr v-for="seg in segments" :key="String(seg.segmentNo)">
            <td class="mono">{{ seg.segmentNo }}</td>
            <td class="mono">{{ seg.price }}</td>
            <td class="mono">{{ seg.volume }}</td>
          </tr>
        </tbody>
      </table>
      <table v-else-if="adjustments.length" style="margin-top: 8px">
        <thead><tr><th>时段</th><th>操作</th><th>量（MWh）</th><th>参考价（元/MWh）</th></tr></thead>
        <tbody>
          <tr v-for="a in adjustments" :key="String(a.period)">
            <td class="mono">{{ a.period }}</td>
            <td>{{ a.action }}</td>
            <td class="mono">{{ a.volume }}</td>
            <td class="mono">{{ a.price }}</td>
          </tr>
        </tbody>
      </table>

      <!-- 人审操作条 -->
      <div class="form-row" style="margin-top: 12px">
        <button class="btn btn-primary" :disabled="!canReview || submitting" @click="onConfirm">确认</button>
        <button class="btn" :disabled="!canReview || submitting" @click="modifyOpen = true">修改</button>
        <button class="btn" :disabled="!canReview || submitting" @click="rejectOpen = true">驳回</button>
        <button class="btn" @click="onEvidence">依据链</button>
      </div>
      <div v-if="detail.modifyReason" class="muted" style="margin-top: 6px">
        处理说明：{{ detail.modifyReason }}<span v-if="detail.reviewedBy">（{{ detail.reviewedBy }}）</span>
      </div>
    </div>

    <!-- ── 依据链（FR-TR-05 可回溯） ── -->
    <div v-if="evidence" class="card">
      <div class="form-row">
        <h3 style="margin: 0">依据链回溯（{{ evidence.sessionNo }} · {{ evidence.orchestratorVersion }}）</h3>
        <span class="badge badge-blue">{{ agents.length }} 个智能体</span>
      </div>
      <table>
        <thead><tr><th>智能体</th><th>输入快照</th><th>输出</th><th>置信度</th><th>耗时（ms）</th></tr></thead>
        <tbody>
          <tr v-for="a in agents" :key="String(a.agentCode)" :class="{ 'row-warn': Number(a.confidence) < 0.7 }">
            <td class="mono">{{ a.agentCode }}<span v-if="a.status === 'timeout'" class="badge badge-orange" style="margin-left: 6px">降级</span></td>
            <td class="muted">{{ a.inputSnapshot }}</td>
            <td>{{ a.output }}</td>
            <td class="mono">{{ a.confidence }}</td>
            <td class="mono">{{ a.elapsedMs }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="conflicts.length" class="card" style="margin-top: 8px; background: #fffbeb">
        <h4 style="font-size: 13px; margin-bottom: 6px">冲突仲裁（{{ conflicts.length }} 条）</h4>
        <div v-for="(c, i) in conflicts" :key="i" class="muted" style="margin-bottom: 4px">
          {{ ((c.between as string[]) ?? []).join(' vs ') }}：{{ c.reason }} → {{ c.arbitration }}（{{ c.resolvedPrice }} 元/MWh）
        </div>
      </div>
    </div>

    <!-- ── 联合优化 / 回测（保留既有入口） ── -->
    <div class="grid-2">
      <div class="card">
        <h3>创建联合优化任务（POST /optimize/joint-tasks · HiGHS/Gurobi）</h3>
        <div class="form-row">
          <select v-model="optim.taskType">
            <option value="daily">日度</option>
            <option value="rolling_N">滚动 N 日</option>
            <option value="backtest">回测</option>
          </select>
          <input v-model.number="optim.horizonDays" type="number" min="1" max="7" style="width: 90px" />
          <button class="btn btn-primary" :disabled="submitting" @click="onCreateOptim">{{ submitting ? '求解中…' : '创建' }}</button>
        </div>
        <div v-if="optimResult" class="mono">taskId={{ optimResult }}</div>
      </div>
      <div class="card">
        <h3>发起策略回测（POST /optimize/backtests · 验收核心 revenueDelta）</h3>
        <div class="form-row">
          <input v-model="bt.strategyCode" list="bt-strategies" placeholder="strategyCode（可搜索选择）" style="width: 150px" />
          <input v-model="bt.startDate" type="datetime-local" style="width: 170px" />
          <input v-model="bt.endDate" type="datetime-local" style="width: 170px" />
          <button class="btn btn-primary" :disabled="submitting" @click="onCreateBacktest">{{ submitting ? '回测中…' : '发起' }}</button>
        </div>
        <div v-if="btResult" class="mono">runId={{ btResult }}</div>
        <datalist id="bt-strategies">
          <option v-for="s in strategies" :key="s.strategyCode" :value="s.strategyCode">{{ s.strategyName }}</option>
        </datalist>
      </div>
    </div>

    <!-- ── 修改弹窗（FR-DM-05 修改须记录依据 + 超阈值双人复核） ── -->
    <div v-if="modifyOpen" class="modal-mask" @click.self="modifyOpen = false">
      <div class="modal" style="width: 520px">
        <h3>人审修改（POST /decision/sessions/{id}/modify）</h3>
        <div class="muted" style="margin-bottom: 8px">修改明细 JSON（如 [{"period":"T01-T08","action":"增持","volume":30,"price":420}]）</div>
        <textarea v-model="modifyForm.modificationsText" rows="3" placeholder="modifications JSON" style="width: 100%"></textarea>
        <div class="form-row" style="margin-top: 8px">
          <input v-model="modifyForm.reason" placeholder="修改依据（必填，FR-DM-05）" style="flex: 1" />
          <input v-model="modifyForm.secondReviewer" list="reviewer-users" placeholder="复核人（修改量超 15% 必填，可搜索）" style="flex: 1" />
        </div>
        <datalist id="reviewer-users">
          <option v-for="u in users" :key="u.id" :value="u.username">{{ u.displayName }}</option>
        </datalist>
        <div class="form-row" style="margin-top: 12px">
          <button class="btn" @click="modifyOpen = false">取消</button>
          <button class="btn btn-primary" :disabled="submitting" @click="onModify">{{ submitting ? '提交中…' : '提交修改' }}</button>
        </div>
      </div>
    </div>

    <!-- ── 驳回弹窗（FR-DM-05 驳回须记录原因） ── -->
    <div v-if="rejectOpen" class="modal-mask" @click.self="rejectOpen = false">
      <div class="modal" style="width: 440px">
        <h3>人审驳回（POST /decision/sessions/{id}/reject）</h3>
        <div class="muted" style="margin-bottom: 8px">会话：{{ detail?.sessionNo ?? queryId }}</div>
        <textarea v-model="rejectReason" rows="3" placeholder="驳回原因（必填，FR-DM-05）" style="width: 100%"></textarea>
        <div class="form-row" style="margin-top: 12px">
          <button class="btn" @click="rejectOpen = false">取消</button>
          <button class="btn btn-primary" :disabled="submitting || !rejectReason.trim()" @click="onReject">{{ submitting ? '提交中…' : '确认驳回' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  confirmSession, createBacktest, createJointOptimTask, createSession,
  getSessionDetail, getSessionEvidence, modifySession, reassessIntel, rejectSession, rerunSession,
} from '@/api/decision'
import { getStrategies } from '@/api/optimize'
import type { StrategyInfo } from '@/api/types'
import { getAdminUsers } from '@/api/admin'
import type { AdminUser } from '@/api/admin'

const submitting = ref(false)
const optimResult = ref('')
const btResult = ref('')
const queryId = ref('')
const detail = ref<Record<string, unknown> | null>(null)
const evidence = ref<Record<string, unknown> | null>(null)
const modifyOpen = ref(false)
const rejectOpen = ref(false)
const rejectReason = ref('')
const modifyForm = reactive({ modificationsText: '', reason: '', secondReviewer: '' })

const session = reactive({
  sessionType: 'rolling' as 'rolling' | 'spot_quote' | 'joint_optimize',
  tradeDate: '2026-08-10',
  scenario: 'baseline' as 'baseline' | 'conservative' | 'aggressive',
})
const optim = reactive({ taskType: 'daily' as 'daily' | 'rolling_N' | 'backtest', horizonDays: 7, scenarioCount: 100, objectiveWeights: { revenue: 1 }, constraints: {} })
const bt = reactive({ strategyCode: 'STRAT_001', startDate: '2026-06-01', endDate: '2026-07-31' })

// 关联数据搜索可选：策略库 / 用户列表（datalist 输入即搜即选）
const strategies = ref<StrategyInfo[]>([])
const users = ref<AdminUser[]>([])

onMounted(async () => {
  try {
    strategies.value = (await getStrategies()) ?? []
    if (strategies.value.length > 0) bt.strategyCode = strategies.value[0].strategyCode
  } catch {
    strategies.value = []
  }
  try {
    const res = await getAdminUsers({ pageNum: 1, pageSize: 200 })
    users.value = res.records ?? []
  } catch {
    users.value = []
  }
})

const strategy = computed(() => detail.value?.finalStrategy as Record<string, unknown> | undefined)
const intel = computed(() => strategy.value?.intel as Record<string, unknown> | undefined)
const canReview = computed(() => {
  const s = String(detail.value?.humanReviewStatus ?? '')
  return s === 'pending' || s === 'confirmed'
})

// 嵌套 JSON 结构经断言后供模板消费（后端契约：quoteSegments/adjustments/agents/conflicts）
const segments = computed<Array<Record<string, unknown>>>(() =>
  (strategy.value?.quoteSegments as Array<Record<string, unknown>> | undefined) ?? [],
)
const adjustments = computed<Array<Record<string, unknown>>>(() =>
  (strategy.value?.adjustments as Array<Record<string, unknown>> | undefined) ?? [],
)
const agents = computed<Array<Record<string, unknown>>>(() =>
  (evidence.value?.agents as Array<Record<string, unknown>> | undefined) ?? [],
)
const conflicts = computed<Array<Record<string, unknown>>>(() =>
  (evidence.value?.conflicts as Array<Record<string, unknown>> | undefined) ?? [],
)

function statusClass(s: string): string {
  return s === 'confirmed' ? 'badge-green' : s === 'rejected' ? 'badge-red' : s === 'modified' ? 'badge-orange' : 'badge-gray'
}

async function onCreateSession() {
  submitting.value = true
  try {
    // tradeDate 契约 yyyy-MM-dd（DTO @JsonFormat 日期粒度），datetime-local 取日期部分
    const res = await createSession({ ...session, tradeDate: session.tradeDate.slice(0, 10) })
    queryId.value = res.sessionId
    await onLoadDetail()
  } finally {
    submitting.value = false
  }
}

async function onLoadDetail() {
  if (!queryId.value) return
  evidence.value = null
  try {
    detail.value = await getSessionDetail(queryId.value)
  } catch {
    detail.value = null
    alert('会话不存在或无权访问')
  }
}

async function onConfirm() {
  if (!detail.value) return
  submitting.value = true
  try {
    await confirmSession(String(detail.value.sessionId ?? queryId.value))
    await onLoadDetail()
  } finally {
    submitting.value = false
  }
}

async function onModify() {
  if (!detail.value) return
  let modifications: unknown[]
  try {
    modifications = JSON.parse(modifyForm.modificationsText || '[]')
  } catch {
    alert('修改明细需为合法 JSON')
    return
  }
  submitting.value = true
  try {
    await modifySession(String(detail.value.sessionId ?? queryId.value), {
      modifications,
      reason: modifyForm.reason,
      ...(modifyForm.secondReviewer ? { secondReviewer: modifyForm.secondReviewer } : {}),
    })
    modifyOpen.value = false
    await onLoadDetail()
  } finally {
    submitting.value = false
  }
}

async function onReject() {
  if (!detail.value) return
  submitting.value = true
  try {
    await rejectSession(String(detail.value.sessionId ?? queryId.value), { reason: rejectReason.value.trim() })
    rejectOpen.value = false
    rejectReason.value = ''
    await onLoadDetail()
  } finally {
    submitting.value = false
  }
}

async function onRerun() {
  if (!detail.value) return
  submitting.value = true
  try {
    await rerunSession(String(detail.value.sessionId ?? queryId.value))
    alert('补跑完成：缺失智能体已补齐，策略已更新')
    await onLoadDetail()
  } catch {
    alert('补跑失败（会话可能已处理或无降级智能体）')
  } finally {
    submitting.value = false
  }
}

async function onIntelReassess() {
  if (!detail.value) return
  submitting.value = true
  try {
    await reassessIntel(String(detail.value.sessionId ?? queryId.value))
    alert('情报重评完成：评分快照已按最新情报流更新')
    await onLoadDetail()
  } catch {
    alert('情报重评失败（仅待审会话可重评）')
  } finally {
    submitting.value = false
  }
}

async function onEvidence() {
  if (!detail.value) return
  try {
    evidence.value = (await getSessionEvidence(String(detail.value.sessionId ?? queryId.value))) as Record<string, unknown>
  } catch {
    evidence.value = null
    alert('依据链查询失败')
  }
}

async function onCreateOptim() {
  submitting.value = true
  try {
    const res = await createJointOptimTask(optim)
    optimResult.value = res.taskId
  } catch {
    optimResult.value = '(失败)'
  } finally {
    submitting.value = false
  }
}

async function onCreateBacktest() {
  submitting.value = true
  try {
    const res = await createBacktest({ ...bt })
    btResult.value = res.runId
  } catch {
    btResult.value = '(失败)'
  } finally {
    submitting.value = false
  }
}
</script>
