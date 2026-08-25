<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">数据底座（WBS 3.0 · GET/POST /data/**）</h3>
        <span class="muted">FR-PD-04 数据全流程管理 P0 · FR-PD-05 数据质量血缘 P1（营销/交易中心/气象双通道建模）</span>
      </div>
      <div class="form-row">
        <select v-model="taskType">
          <option value="market">市场行情</option>
          <option value="trade">交易申报</option>
          <option value="settlement">结算数据</option>
          <option value="weather">气象数据</option>
          <option value="intel">情报数据</option>
        </select>
        <label class="form-check">
          <input v-model="force" type="checkbox" />强制重跑
        </label>
        <button class="btn btn-primary" :disabled="collecting" @click="onCollect">
          {{ collecting ? '采集中…' : '触发采集' }}
        </button>
        <button class="btn" @click="loadAll">刷新</button>
      </div>
    </div>

    <!-- ── 新增数据源（任务 2：数据对接/采集新建增补齐；V2.2 对接方式可配置） ── -->
    <div class="card">
      <h3>新增数据源（POST /data/sources · 编码唯一，类型/同步模式/对接方式/状态枚举校验）</h3>
      <div class="form-row">
        <input v-model="sourceForm.sourceCode" placeholder="编码，如 exchange_sd" style="width: 180px" />
        <select v-model="sourceForm.sourceType">
          <option value="marketing">营销</option>
          <option value="exchange">交易中心</option>
          <option value="weather">气象</option>
          <option value="file">文件</option>
          <option value="intel">情报</option>
        </select>
        <select v-model="sourceForm.syncMode">
          <option value="timed">定时</option>
          <option value="realtime">实时</option>
        </select>
        <select v-model="sourceForm.connType">
          <option v-for="c in connTypes" :key="c" :value="c">{{ c }}</option>
        </select>
        <input v-model="sourceForm.frequency" placeholder="cron 如 0 */15 * * * *" style="flex: 1" />
        <select v-model="sourceForm.status">
          <option value="enabled">启用</option>
          <option value="disabled">停用</option>
        </select>
        <button class="btn btn-primary" :disabled="!sourceForm.sourceCode || !sourceForm.sourceType" @click="onCreateSource">新增数据源</button>
      </div>
      <div class="form-row">
        <label class="f">连接参数（图形化：URL/账号/密码即可完成配置）</label>
      </div>
      <ConnConfigEditor v-model="sourceForm.connectConfig" />
      <div class="form-row">
        <span class="muted">对接方式：api/jwt/oauth2/basic/file/poll，适配不同数据源认证形态与客户场景；敏感字段加密存储</span>
      </div>
      <div v-if="sourceResult" class="muted">
        已登记：{{ sourceResult.sourceCode }}（id={{ sourceResult.id }}），可在下方「触发采集」联调。
      </div>
    </div>

    <!-- ── 数据源台账 ── -->
    <div class="card">
      <h3>数据源台账（GET /data/sources · 对接配置可调整：连接方式/参数/频率/启停）</h3>
      <table>
        <thead>
          <tr><th>编码</th><th>类型</th><th>同步模式</th><th>对接方式</th><th>频率</th><th>状态</th><th>最近运行</th><th>最近状态</th><th>记录数</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="sources.length === 0"><td colspan="10" class="muted">暂无数据</td></tr>
          <tr v-for="s in sources" :key="s.id">
            <td class="mono">{{ s.sourceCode }}</td>
            <td>{{ sourceTypeLabel(s.sourceType) }}</td>
            <td>{{ s.syncMode === 'realtime' ? '实时' : '定时' }}</td>
            <td><span class="badge" :class="s.connType && s.connType !== 'api' ? 'badge-blue' : 'badge-gray'">{{ s.connType || 'api' }}</span></td>
            <td class="mono">{{ s.frequency }}</td>
            <td><span class="badge" :class="s.status === 'enabled' ? 'badge-green' : 'badge-gray'">{{ s.status === 'enabled' ? '启用' : '停用' }}</span></td>
            <td class="mono">{{ s.lastRunTime ?? '-' }}</td>
            <td>
              <span v-if="s.lastStatus" class="badge" :class="s.lastStatus === 'success' ? 'badge-green' : 'badge-red'">{{ s.lastStatus === 'success' ? '成功' : s.lastStatus }}</span>
              <span v-else class="muted">-</span>
            </td>
            <td class="mono">{{ s.recordsCount ?? '-' }}</td>
            <td><button class="btn btn-sm" @click="startEditSource(s)">编辑对接</button></td>
          </tr>
        </tbody>
      </table>
      <div v-if="editingSource" class="modal-mask" @click.self="editingSource = null">
        <div class="modal" style="width: 640px">
          <h3>编辑对接配置：{{ editingSource.sourceCode }}（PUT /data/sources/{id}）</h3>
          <div class="form-row">
            <label class="f">同步模式</label>
            <select v-model="editForm.syncMode" style="width: 130px">
              <option value="timed">定时</option>
              <option value="realtime">实时</option>
            </select>
            <label class="f">对接方式</label>
            <select v-model="editForm.connType" style="width: 130px">
              <option v-for="c in connTypes" :key="c" :value="c">{{ c }}</option>
            </select>
            <label class="f">频率</label>
            <input v-model="editForm.frequency" placeholder="cron" style="width: 150px" />
            <label class="f">状态</label>
            <select v-model="editForm.status" style="width: 120px">
              <option value="enabled">启用</option>
              <option value="disabled">停用</option>
            </select>
          </div>
          <div class="form-row">
            <span class="muted">连接参数（图形化配置，敏感字段留空 = 保持原配置；可展开 JSON 高级模式保留完整配置）</span>
          </div>
          <ConnConfigEditor v-model="editForm.connectConfig" />
          <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
            <button class="btn" @click="editingSource = null">取消</button>
            <button class="btn btn-primary" @click="onSaveSource">保存配置</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 数据质量报告 ── -->
    <div class="card">
      <h3>数据质量报告（GET /data/quality/report）</h3>
      <div class="form-row">
        <div class="metric">
          <div class="metric-value">{{ pct(quality.completeness) }}</div>
          <div class="muted">完整率</div>
        </div>
        <div class="metric">
          <div class="metric-value">{{ pct(quality.accuracy) }}</div>
          <div class="muted">准确率</div>
        </div>
        <div class="metric">
          <div class="metric-value">{{ pct(quality.timeliness) }}</div>
          <div class="muted">及时率</div>
        </div>
      </div>
    </div>

    <!-- ── 数据血缘 ── -->
    <div class="card">
      <h3>数据血缘全景（GET /data/lineage · V3.0 全量图谱：外部源→采集→明细→指标→模型/报表→业务应用+系统支撑）</h3>
      <div class="form-row">
        <button class="btn" :class="lineageMode === 'table' ? 'btn-primary' : ''" @click="lineageMode = 'table'">表格模式</button>
        <button class="btn" :class="lineageMode === 'graph' ? 'btn-primary' : ''" @click="lineageMode = 'graph'">全量图谱模式</button>
        <template v-if="lineageMode === 'graph'">
          <span class="muted" style="margin: 0 4px">|</span>
          <button class="btn" :class="lineageView === 'data' ? 'btn-primary' : ''" @click="lineageView = 'data'">数据视角（分层）</button>
          <button class="btn" :class="lineageView === 'business' ? 'btn-primary' : ''" @click="lineageView = 'business'">业务视角（分域）</button>
        </template>
      </div>
      <template v-if="lineageMode === 'table'">
        <table>
          <thead>
            <tr><th>节点</th><th>类型</th><th>业务域</th><th>字段映射</th></tr>
          </thead>
          <tbody>
            <tr v-if="lineage.length === 0"><td colspan="4" class="muted">暂无数据</td></tr>
            <tr v-for="n in lineage" :key="n.nodeId">
              <td><span class="muted mono" style="font-size: 12px">{{ n.nodeId }}</span><br />{{ n.nodeName || n.nodeId }}</td>
              <td><span class="badge" :class="nodeTypeClass(n.nodeType)">{{ nodeTypeLabel(n.nodeType) }}</span></td>
              <td class="muted">{{ domainLabel(n.domain) }}</td>
              <td class="muted">{{ mappingText(n.fieldMapping) }}</td>
            </tr>
          </tbody>
        </table>
      </template>
      <LineageGraph v-else :nodes="lineage" :view="lineageView" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createDataSource, getDataSources, getLineage, getQualityReport, postCollectTask, updateDataSource } from '@/api/dataPlatform'
import type { DataSourceInfo, LineageNode, QualityReport } from '@/api/types'
import ConnConfigEditor from '@/components/ConnConfigEditor.vue'
import LineageGraph from '@/components/LineageGraph.vue'

const connTypes = ['api', 'jwt', 'oauth2', 'basic', 'file', 'poll']
const sources = ref<DataSourceInfo[]>([])
const quality = ref<QualityReport>({ completeness: 0, accuracy: 0, timeliness: 0 })
const lineage = ref<LineageNode[]>([])
const lineageMode = ref<'table' | 'graph'>('graph')
const lineageView = ref<'data' | 'business'>('data')
const taskType = ref('market')
const force = ref(false)
const collecting = ref(false)

// ── 新增数据源表单 ──
const sourceForm = reactive({ sourceCode: '', sourceType: 'exchange', syncMode: 'timed', connType: 'api', connectConfig: '', frequency: '0 */30 * * * *', status: 'enabled' })
const sourceResult = ref<{ id: string; sourceCode: string } | null>(null)

async function onCreateSource() {
  try {
    sourceResult.value = await createDataSource({ ...sourceForm })
    sourceForm.sourceCode = ''
    sourceForm.connectConfig = ''
    await loadAll()
  } catch (e) {
    alert((e as Error).message || '新增失败')
  }
}

// ── 对接配置编辑（V2.2：连接方式/参数/频率/启停可调整，适配不同客户部署） ──
const editingSource = ref<DataSourceInfo | null>(null)
const editForm = reactive({ syncMode: 'timed', connType: 'api', connectConfig: '', frequency: '', status: 'enabled' })
function startEditSource(s: DataSourceInfo) {
  editingSource.value = s
  editForm.syncMode = s.syncMode || 'timed'
  editForm.connType = s.connType || 'api'
  editForm.connectConfig = typeof s.connectConfig === 'string' ? s.connectConfig : JSON.stringify(s.connectConfig || {})
  editForm.frequency = s.frequency || ''
  editForm.status = s.status || 'enabled'
}
async function onSaveSource() {
  if (!editingSource.value) return
  try {
    await updateDataSource(editingSource.value.id, { ...editForm })
    editingSource.value = null
    await loadAll()
  } catch (e) {
    alert((e as Error).message || '保存失败')
  }
}

function sourceTypeLabel(t: string): string {
  const map: Record<string, string> = { marketing: '营销', exchange: '交易中心', weather: '气象' }
  return map[t] ?? t
}

function nodeTypeLabel(t: string): string {
  const map: Record<string, string> = { table: '数据表', task: '采集/加工', model: '模型', report: '报表', business: '业务应用' }
  return map[t] ?? t
}

function nodeTypeClass(t: string): string {
  const map: Record<string, string> = { table: 'badge-blue', task: 'badge-orange', model: 'badge-purple', report: 'badge-green', business: 'badge-pink' }
  return map[t] ?? 'badge-gray'
}

function domainLabel(d?: string): string {
  const map: Record<string, string> = {
    marketing: '营销域', exchange: '交易中心域', weather: '气象域', common: '公共数据底座',
    trade: '交易域', settle: '结算域', policy: '政策域', intel: '情报域', forecast: '预测域',
    model: '模型域', decision: '决策域', optimize: '优化域', assess: '评估域', report: '报表域', system: '系统支撑',
  }
  return map[d ?? ''] ?? d ?? '-'
}

function pct(v: number | undefined): string {
  if (v === undefined || v === null) return '-'
  return `${(Number(v) * 100).toFixed(2)}%`
}

function mappingText(v: Record<string, unknown> | string | undefined): string {
  if (!v) return '-'
  if (typeof v === 'string') return v
  return Object.entries(v).map(([k, val]) => `${k}→${String(val)}`).join('，') || '-'
}

async function loadAll() {
  const [s, q, l] = await Promise.all([getDataSources(), getQualityReport(), getLineage()])
  sources.value = s ?? []
  quality.value = q ?? quality.value
  lineage.value = l?.upstream ?? []
}

async function onCollect() {
  collecting.value = true
  try {
    const res = await postCollectTask({ taskType: taskType.value, force: force.value })
    alert(`采集完成：taskId=${res.taskId}，记录数=${res.recordsCount}`)
    await loadAll()
  } finally {
    collecting.value = false
  }
}

onMounted(loadAll)
</script>
