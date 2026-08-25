<template>
  <div>
    <div class="card">
      <div class="form-row">
        <button class="btn" :class="tab === 'news' ? 'btn-primary' : ''" @click="tab = 'news'">情报流</button>
        <button class="btn" :class="tab === 'sources' ? 'btn-primary' : ''" @click="tab = 'sources'">情报源台账</button>
        <button class="btn" :class="tab === 'rules' ? 'btn-primary' : ''" @click="tab = 'rules'">推送规则</button>
        <button class="btn" :class="tab === 'monitor' ? 'btn-primary' : ''" @click="tab = 'monitor'">采集监控</button>
        <span class="muted">RE-01 P0 正式：采集 → 归一化标签/重要度分级 → 按规则推送（high 级 ≤30s）</span>
      </div>
    </div>

    <!-- ── 统计卡（对齐原型 intel-center：源在线/情报总量/high 级/推送规则） ── -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-label">情报源在线</div>
        <div class="stat-value">{{ stats.onlineSources }}<span class="muted" style="font-size: 14px">/{{ sources.length }}</span></div>
        <div class="stat-trend">启用 / 全部台账</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">情报流总量</div>
        <div class="stat-value">{{ stats.newsTotal }}</div>
        <div class="stat-trend">当前省 + 全国可见</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">high 级情报（本页）</div>
        <div class="stat-value danger">{{ stats.highCount }}</div>
        <div class="stat-trend">实时推送 sms/miniapp ≤30s</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">推送规则</div>
        <div class="stat-value ok">{{ stats.activeRules }}</div>
        <div class="stat-trend">active / 全部</div>
      </div>
    </div>

    <!-- ── 情报流 ── -->
    <template v-if="tab === 'news'">
      <div class="card">
        <h3>情报流（GET /intel/news · 当前省 + 全国可见，按重要度/类型筛选）</h3>
        <div class="form-row">
          <select v-model="newsFilter.importance">
            <option value="">全部重要度</option>
            <option value="high">high</option>
            <option value="medium">medium</option>
            <option value="low">low</option>
          </select>
          <select v-model="newsFilter.intelType">
            <option value="">全部类型</option>
            <option v-for="t in intelTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
          </select>
          <button class="btn btn-primary" @click="loadNews">查询</button>
        </div>
        <table>
          <thead>
            <tr><th>标题</th><th>类型</th><th>重要度</th><th>标签</th><th>区域</th><th>发布时间</th></tr>
          </thead>
          <tbody>
            <tr v-if="news.length === 0"><td colspan="6" class="muted">暂无数据</td></tr>
            <tr v-for="r in news" :key="r.id">
              <td>{{ r.title }}</td>
              <td>{{ typeLabel(r.sourceCode) }}</td>
              <td>
                <span class="badge" :class="r.importance === 'high' ? 'badge-red' : r.importance === 'medium' ? 'badge-orange' : 'badge-gray'">{{ importanceLabel(r.importance) }}</span>
              </td>
              <td class="muted">{{ tagsText(r.normalizedTags) }}</td>
              <td class="mono">{{ r.regionCode ?? '全国' }}</td>
              <td class="mono">{{ r.publishedAt }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <!-- ── 情报源台账 ── -->
    <template v-else-if="tab === 'sources'">
      <div class="card">
        <h3>新增情报源（POST /intel/sources · 编码唯一，类型/采集方式/对接方式/状态枚举校验）</h3>
        <div class="form-row">
          <input v-model="sourceForm.sourceCode" placeholder="编码，如 INTL-CUSTOM-NEWS" style="width: 190px" />
          <input v-model="sourceForm.sourceName" placeholder="源名称" style="width: 170px" />
          <select v-model="sourceForm.intelType">
            <option v-for="t in intelTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
          </select>
          <select v-model="sourceForm.fetchMode">
            <option value="api">api</option>
            <option value="crawl">crawl</option>
            <option value="file">file</option>
          </select>
          <select v-model="sourceForm.connType">
            <option v-for="c in connTypes" :key="c" :value="c">{{ c }}</option>
          </select>
          <input v-model="sourceForm.frequency" placeholder="频率，如 30 分钟" style="width: 120px" />
          <button class="btn btn-primary" :disabled="!sourceForm.sourceCode || !sourceForm.sourceName" @click="onCreateSource">新增情报源</button>
        </div>
        <div class="form-row">
          <label class="f">连接参数（图形化：URL/账号/密码即可完成配置）</label>
        </div>
        <ConnConfigEditor v-model="sourceForm.connConfig" />
        <div class="form-row">
          <span class="muted">对接方式：api/jwt/oauth2/basic/file/poll，适配不同信息源认证形态与客户场景；敏感字段加密存储</span>
        </div>
        <div v-if="sourceResult" class="muted">已登记：{{ sourceResult.sourceCode }}（id={{ sourceResult.id }}）</div>
      </div>
      <div class="card">
        <h3>情报源台账（GET /intel/sources · 对接配置可调整：连接方式/参数/频率/启停）</h3>
        <table>
          <thead>
            <tr><th>源编码</th><th>名称</th><th>类型</th><th>采集方式</th><th>对接方式</th><th>频率</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="sources.length === 0"><td colspan="8" class="muted">暂无数据</td></tr>
            <tr v-for="s in sources" :key="s.id">
              <td class="mono">{{ s.sourceCode }}</td>
              <td>{{ s.sourceName }}</td>
              <td>{{ intelTypeLabel(s.intelType) }}（{{ s.intelType }}）</td>
              <td>{{ fetchModeLabel(s.fetchMode) }}（{{ s.fetchMode }}）</td>
              <td><span class="badge" :class="s.connType && s.connType !== 'api' ? 'badge-blue' : 'badge-gray'">{{ connTypeLabel(s.connType || 'api') }}</span></td>
              <td class="mono">{{ s.frequency }}</td>
              <td><span class="badge" :class="s.status === 'enabled' ? 'badge-green' : 'badge-gray'">{{ sourceStatusLabel(s.status) }}（{{ s.status }}）</span></td>
              <td><button class="btn btn-sm" @click="startEditSource(s)">编辑对接</button> <button class="btn btn-sm" @click="onDeleteSource(s)">删除</button></td>
            </tr>
          </tbody>
        </table>
        <div v-if="editingSource" class="modal-mask" @click.self="editingSource = null">
          <div class="modal" style="width: 640px">
            <h3>编辑对接配置：{{ editingSource.sourceCode }}（PUT /intel/sources/{id}）</h3>
            <div class="form-row">
              <label class="f">采集方式</label>
              <select v-model="editForm.fetchMode" style="width: 130px">
                <option v-for="m in ['api', 'crawl', 'file']" :key="m" :value="m">{{ m }}</option>
              </select>
              <label class="f">对接方式</label>
              <select v-model="editForm.connType" style="width: 130px">
                <option v-for="c in connTypes" :key="c" :value="c">{{ c }}</option>
              </select>
              <label class="f">频率</label>
              <input v-model="editForm.frequency" placeholder="频率" style="width: 130px" />
              <label class="f">状态</label>
              <select v-model="editForm.status" style="width: 130px">
                <option value="enabled">enabled</option>
                <option value="disabled">disabled</option>
              </select>
            </div>
            <div class="form-row">
              <span class="muted">连接参数（图形化配置，敏感字段留空 = 保持原配置；可展开 JSON 高级模式保留完整配置）</span>
            </div>
            <ConnConfigEditor v-model="editForm.connConfig" />
            <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
              <button class="btn" @click="editingSource = null">取消</button>
              <button class="btn btn-primary" @click="onSaveSource">保存配置</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ── 采集监控（V3.2：GAP-06 补全：采集状态 + admin 立即抓取，后端 /intel/fetch-status /intel/fetch 就绪） ── -->
    <template v-else-if="tab === 'monitor'">
      <div class="card">
        <div class="form-row">
          <h3>行情采集状态（GET /intel/fetch-status · 各源最近成功/失败原因/连续失败/频率/端点域名）</h3>
          <button v-if="isAdmin" class="btn btn-primary" :disabled="fetching" @click="onFetchNow">
            {{ fetching ? '采集中…' : '立即抓取（force 全量）' }}
          </button>
          <button v-else class="btn" disabled title="仅 admin 可手动触发采集">立即抓取（仅 admin）</button>
          <span v-if="fetchMsg" class="muted">{{ fetchMsg }}</span>
        </div>
        <table>
          <thead>
            <tr><th>源编码</th><th>名称</th><th>类型</th><th>频率</th><th>状态</th><th>最近成功</th><th>连续失败</th><th>最近失败原因</th><th>端点</th></tr>
          </thead>
          <tbody>
            <tr v-if="fetchStatuses.length === 0"><td colspan="9" class="muted">暂无数据</td></tr>
            <tr v-for="s in fetchStatuses" :key="s.id">
              <td class="mono">{{ s.sourceCode }}</td>
              <td>{{ s.sourceName }}</td>
              <td>{{ intelTypeLabel(s.intelType) }}</td>
              <td class="mono">{{ s.frequency }}</td>
              <td>
                <span class="badge" :class="s.healthy ? 'badge-green' : 'badge-red'">
                  {{ s.healthy ? '健康' : '异常' }}（{{ sourceStatusLabel(s.status) }}）
                </span>
              </td>
              <td class="mono">{{ s.lastSuccessAt ?? '-' }}</td>
              <td><span class="badge" :class="s.consecutiveFailures > 0 ? 'badge-red' : 'badge-gray'">{{ s.consecutiveFailures }}</span></td>
              <td class="muted">{{ s.lastError ?? '-' }}</td>
              <td class="mono muted">{{ s.endpoint || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <!-- ── 推送规则 ── -->
    <template v-else>
      <div class="card">
        <h3>配置推送规则（POST /intel/push-rules · 标签×重要度→角色/渠道）</h3>
        <div class="form-row">
          <input v-model="ruleForm.ruleName" placeholder="规则名称" />
          <input v-model="ruleForm.matchTags" placeholder="匹配标签，逗号分隔，如 现货,价格" style="flex: 1" />
          <select v-model="ruleForm.importance">
            <option value="high">high（实时 +sms/+miniapp）</option>
            <option value="medium">medium</option>
            <option value="low">low</option>
          </select>
          <button class="btn btn-primary" :disabled="!ruleForm.ruleName || !ruleForm.matchTags" @click="onCreateRule">创建</button>
          <span v-if="ruleResult" class="badge badge-green">规则 {{ ruleResult.ruleId }} · 渠道 {{ ruleResult.channels.join('/') }}</span>
        </div>
      </div>
      <div class="card">
        <h3>推送规则列表（GET /intel/push-rules）</h3>
        <table>
          <thead>
            <tr><th>规则名称</th><th>标签</th><th>重要度</th><th>目标角色</th><th>渠道</th><th>状态</th></tr>
          </thead>
          <tbody>
            <tr v-if="rules.length === 0"><td colspan="6" class="muted">暂无数据</td></tr>
            <tr v-for="r in rules" :key="r.id">
              <td>{{ r.ruleName }}</td>
              <td class="muted">{{ arrayText(r.tagsFilter) }}</td>
              <td>{{ importanceLabel(r.importanceFilter) }}</td>
              <td>{{ arrayText(r.targetRoles) }}</td>
              <td>{{ arrayText(r.channel) }}</td>
              <td><span class="badge badge-green">{{ ruleStatusLabel(r.status) }}（{{ r.status }}）</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { createIntelPushRule, createIntelSource, deleteIntelSource, getIntelFetchStatus, getIntelNews, getIntelPushRules, getIntelSources, triggerIntelFetch, updateIntelSource } from '@/api/intel'
import type { IntelFetchStatus, IntelNews, IntelPushRule, IntelSource } from '@/api/types'
import ConnConfigEditor from '@/components/ConnConfigEditor.vue'
import { useAuthStore } from '@/stores/auth'

const tab = ref<'news' | 'sources' | 'rules' | 'monitor'>('news')

const auth = useAuthStore()
/** 采集手动触发仅 admin（后端 @RequiresRoles("admin") 兜底） */
const isAdmin = computed(() => (auth.user?.roles ?? []).includes('admin'))

const intelTypes = [
  { value: 'price', label: '价格' },
  { value: 'weather', label: '气象' },
  { value: 'supply_demand', label: '供需' },
  { value: 'policy', label: '政策' },
  { value: 'announcement', label: '公告' },
  { value: 'opinion', label: '舆情' },
]

// ── 情报流 ──
const news = ref<IntelNews[]>([])
const newsFilter = reactive({ importance: '', intelType: '' })
const newsTotal = ref(0)

// ── 统计卡（源在线/情报总量/high 级/推送规则） ──
const stats = computed(() => ({
  onlineSources: sources.value.filter((s) => s.status === 'enabled').length,
  newsTotal: newsTotal.value,
  highCount: news.value.filter((n) => n.importance === 'high').length,
  activeRules: rules.value.filter((r) => r.status === 'active').length,
}))

function typeLabel(sourceCode?: string): string {
  const src = sources.value.find((s) => s.sourceCode === sourceCode)
  return src ? src.intelType : (sourceCode ?? '-')
}

/** V2.4 编码+名称：情报源启用状态中文标签 */
function sourceStatusLabel(s: string): string {
  return { enabled: '启用', disabled: '停用' }[s] ?? s
}

/** V2.4 编码+名称：情报重要度中文标签 */
function importanceLabel(s: string): string {
  return { high: '高', medium: '中', low: '低' }[s] ?? s
}

/** V2.4 编码+名称：情报类型中文标签 */
function intelTypeLabel(s: string): string {
  return { opinion: '观点', price: '价格', supply_demand: '供需', policy: '政策' }[s] ?? s
}

/** V2.4 编码+名称：采集方式中文标签 */
function fetchModeLabel(s: string): string {
  return { api: '接口', crawl: '爬虫' }[s] ?? s
}

/** V2.4 编码+名称：连接认证方式中文标签 */
function connTypeLabel(s: string): string {
  return { api: 'API', oauth2: 'OAuth2', jwt: 'JWT 令牌' }[s] ?? s
}

/** V2.4 编码+名称：推送规则状态中文标签 */
function ruleStatusLabel(s: string): string {
  return { active: '启用', disabled: '停用', enabled: '启用' }[s] ?? s
}

function tagsText(tags?: string[] | string): string {
  return arrayText(normalizeArray(tags))
}

/** JSONB 字段兼容：后端经 JsonStringTypeHandler 返回 JSON 字符串，需解析为数组 */
function normalizeArray(v?: string[] | string): string[] {
  if (!v) return []
  if (Array.isArray(v)) return v
  try {
    const parsed = JSON.parse(v) as unknown
    return Array.isArray(parsed) ? parsed.map(String) : [String(parsed)]
  } catch {
    return [String(v)]
  }
}

async function loadNews() {
  try {
    const res = await getIntelNews({ pageNo: 1, pageSize: 20, ...newsFilter })
    news.value = ((res as unknown as { list: IntelNews[] }).list ?? [])
    newsTotal.value = Number((res as unknown as { total?: number }).total ?? 0)
  } catch {
    news.value = []
  }
}

// ── 情报源台账 ──
const connTypes = ['api', 'jwt', 'oauth2', 'basic', 'file', 'poll']
const sources = ref<IntelSource[]>([])
const sourceForm = reactive({ sourceCode: '', sourceName: '', intelType: 'price', fetchMode: 'api', connType: 'api', connConfig: '', frequency: '30 分钟', status: 'enabled' })
const sourceResult = ref<{ id: string; sourceCode: string } | null>(null)
async function loadSources() {
  try {
    sources.value = await getIntelSources()
  } catch {
    sources.value = []
  }
}

async function onCreateSource() {
  try {
    sourceResult.value = await createIntelSource({ ...sourceForm })
    sourceForm.sourceCode = ''
    sourceForm.sourceName = ''
    sourceForm.connConfig = ''
    await loadSources()
  } catch (e) {
    sourceResult.value = null
    alert((e as Error).message || '新增失败')
  }
}

// ── 对接配置编辑（V2.2：连接方式/参数/频率/启停可调整，适配不同客户部署） ──
const editingSource = ref<IntelSource | null>(null)
const editForm = reactive({ fetchMode: 'api', connType: 'api', connConfig: '', frequency: '', status: 'enabled' })
function startEditSource(s: IntelSource) {
  editingSource.value = s
  editForm.fetchMode = s.fetchMode || 'api'
  editForm.connType = s.connType || 'api'
  editForm.connConfig = typeof s.connConfig === 'string' ? s.connConfig : JSON.stringify(s.connConfig || {})
  editForm.frequency = s.frequency || ''
  editForm.status = s.status || 'enabled'
}
async function onSaveSource() {
  if (!editingSource.value) return
  try {
    await updateIntelSource(editingSource.value.id, { ...editForm })
    editingSource.value = null
    await loadSources()
  } catch (e) {
    alert((e as Error).message || '保存失败')
  }
}

// ── 台账删除（软删除：历史情报保留，采集任务自动跳过；同编码可重新登记） ──
async function onDeleteSource(s: IntelSource) {
  if (!window.confirm(`确定删除情报源「${s.sourceCode} ${s.sourceName}」吗？历史情报保留，同编码后续可重新登记。`)) return
  try {
    await deleteIntelSource(s.id)
    if (editingSource.value?.id === s.id) editingSource.value = null
    await loadSources()
  } catch (e) {
    alert((e as Error).message || '删除失败')
  }
}

// ── 推送规则 ──
const rules = ref<IntelPushRule[]>([])
const ruleForm = reactive({ ruleName: '', matchTags: '', importance: 'high' })
const ruleResult = ref<{ ruleId: string; channels: string[] } | null>(null)

function arrayText(arr?: string[] | string): string {
  return normalizeArray(arr)
    .map((it) => {
      // 兼容对象数组（历史种子 target_roles 形如 [{"role":"trader","channel":"sms"}]）
      if (it && typeof it === 'object') {
        const o = it as Record<string, unknown>
        return String(o.role ?? o.roleCode ?? o.channel ?? JSON.stringify(it))
      }
      return String(it)
    })
    .filter(Boolean)
    .join('、') || '-'
}

async function loadRules() {
  try {
    rules.value = await getIntelPushRules()
  } catch {
    rules.value = []
  }
}

async function onCreateRule() {
  try {
    ruleResult.value = await createIntelPushRule({
      ruleName: ruleForm.ruleName,
      matchTags: ruleForm.matchTags.split(/[,，]/).map((s) => s.trim()).filter(Boolean),
      importance: ruleForm.importance,
      targets: ['trader'],
    })
    ruleForm.ruleName = ''
    ruleForm.matchTags = ''
    await loadRules()
  } catch {
    ruleResult.value = null
  }
}

// ── 采集监控（V3.2 GAP-06：状态展示 + admin 手动抓取） ──
const fetchStatuses = ref<IntelFetchStatus[]>([])
const fetching = ref(false)
const fetchMsg = ref('')

async function loadFetchStatus() {
  try {
    fetchStatuses.value = await getIntelFetchStatus()
  } catch {
    fetchStatuses.value = []
  }
}

async function onFetchNow() {
  if (fetching.value) return
  fetching.value = true
  fetchMsg.value = ''
  try {
    const res = await triggerIntelFetch(true)
    fetchMsg.value = (res as unknown as { message?: string }).message ?? '采集触发成功'
    await loadFetchStatus()
    await loadNews()
  } catch (e) {
    fetchMsg.value = (e as Error).message || '采集触发失败'
  } finally {
    fetching.value = false
  }
}

onMounted(() => {
  loadNews()
  loadSources()
  loadRules()
  loadFetchStatus()
})
</script>
