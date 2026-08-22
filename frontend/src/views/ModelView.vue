<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">模型平台（GET/POST /model/**）</h3>
        <span class="muted">V2.2 LLM 模型可配置关联智能体 · 专业算法可注册替换 · 数值模型注册/训练/评估/上线</span>
      </div>
    </div>

    <!-- ── LLM 模型管理（V2.2：智能体关联的生成式模型，可配置可启停，适配客户供应商） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">LLM 模型管理（GET/POST /llm/models · 智能体关联推理，按客户供应商部署适配）</h3>
        <button class="btn btn-primary" style="margin-left: auto" @click="openLlmCreate">新增 LLM</button>
      </div>
      <table>
        <thead>
          <tr><th>编码</th><th>名称</th><th>供应商</th><th>基础模型</th><th>温度</th><th>限额</th><th>Endpoint</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="llms.length === 0"><td colspan="9" class="muted">暂无 LLM 模型（新增后可在智能体管理绑定）</td></tr>
          <tr v-for="m in llms" :key="m.id">
            <td class="mono">{{ m.modelCode }}</td>
            <td>{{ m.modelName }}</td>
            <td class="mono">{{ m.provider }}</td>
            <td class="mono">{{ m.baseModel ?? '-' }}</td>
            <td class="mono">{{ m.temperature }}</td>
            <td class="mono">{{ m.maxTokens }}</td>
            <td class="mono muted" style="max-width: 190px">{{ m.endpoint ?? '（内置模拟）' }}</td>
            <td><span class="badge" :class="m.status === 'enabled' ? 'badge-green' : 'badge-gray'">{{ m.status === 'enabled' ? '启用' : '停用' }}</span></td>
            <td><button class="btn btn-sm" @click="openLlmEdit(m)">编辑</button></td>
          </tr>
        </tbody>
      </table>
      <div class="muted" style="margin-top: 4px">P2 真实通道：Endpoint + 密钥引用（环境变量）就绪且启用时按 OpenAI 兼容协议真实调用（gateway=real）；失败自动降级内置模拟（gateway=degraded），无密钥/本地模型=模拟推理（gateway=simulate）</div>
    </div>

    <!-- ── 算法注册表（V2.2：专业算法注册/替换/决策匹配标注） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">算法注册表（GET /algorithm/registry · 决策计算/风控匹配，新版本启用+旧版停用实现替换）</h3>
        <button class="btn btn-primary" style="margin-left: auto" @click="openAlgCreate">注册算法</button>
      </div>
      <table>
        <thead>
          <tr><th>编码</th><th>名称</th><th>类目</th><th>版本</th><th>SPI 执行器</th><th>参数模板</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="algs.length === 0"><td colspan="8" class="muted">暂无算法（决策时按智能体类目自动匹配最新启用版本）</td></tr>
          <tr v-for="a in algs" :key="a.id">
            <td class="mono">{{ a.algCode }}</td>
            <td>{{ a.algName }}</td>
            <td><span class="badge badge-blue">{{ a.category }}</span></td>
            <td class="mono">{{ a.version }}</td>
            <td><span class="badge badge-orange">{{ spiLabel(a.spiKey) }}</span></td>
            <td class="mono muted" style="max-width: 220px">{{ schemaText(a.paramsSchema) }}</td>
            <td><span class="badge" :class="a.status === 'enabled' ? 'badge-green' : 'badge-gray'">{{ a.status === 'enabled' ? '启用' : '停用' }}</span></td>
            <td><button class="btn btn-sm" @click="openAlgEdit(a)">编辑</button></td>
          </tr>
        </tbody>
      </table>
      <div class="muted" style="margin-top: 4px">SPI 执行器（P3 插件化）：决策编排按绑定执行器真实计算并留痕；空=按类目默认匹配内置执行器（可查询 GET /algorithm/spis）</div>
    </div>

    <!-- ── LLM 新增/编辑弹窗 ── -->
    <div v-if="llmDialog" class="modal-mask" @click.self="llmDialog = false">
      <div class="modal" style="width: 580px">
        <h3>{{ llmEditing ? '编辑 LLM：' + llmEditing.modelCode : '新增 LLM 模型' }}</h3>
        <div class="form-row">
          <input v-model="llmForm.modelCode" placeholder="编码（如 deepseek-v3）" :disabled="!!llmEditing" style="flex: 1" />
          <input v-model="llmForm.modelName" placeholder="名称（如 DeepSeek-V3）" style="flex: 1" />
        </div>
        <div class="form-row">
          <select v-model="llmForm.provider">
            <option value="deepseek">deepseek</option>
            <option value="glm">glm（智谱）</option>
            <option value="qwen">qwen（通义）</option>
            <option value="openai-compatible">openai-compatible</option>
            <option value="local">local（内置模拟）</option>
          </select>
          <input v-model="llmForm.baseModel" placeholder="基础模型（如 deepseek-chat）" style="flex: 1" />
        </div>
        <div class="form-row">
          <input v-model="llmForm.endpoint" placeholder="Endpoint（留空=内置模拟推理）" style="flex: 1" />
          <input v-model="llmForm.apiKeyRef" placeholder="密钥引用（环境变量名，如 LLM_API_KEY）" style="flex: 1" />
        </div>
        <div class="form-row">
          <input v-model.number="llmForm.temperature" type="number" step="0.1" placeholder="温度" style="width: 100px" />
          <input v-model.number="llmForm.maxTokens" type="number" placeholder="最大输出 token" style="width: 150px" />
          <select v-model="llmForm.status">
            <option value="enabled">启用</option>
            <option value="disabled">停用</option>
          </select>
          <span class="muted">启用模型可在智能体管理绑定关联</span>
        </div>
        <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
          <button class="btn" @click="llmDialog = false">取消</button>
          <button class="btn btn-primary" :disabled="!llmForm.modelCode || !llmForm.modelName" @click="onSaveLlm">保存</button>
        </div>
      </div>
    </div>

    <!-- ── 算法新增/编辑弹窗 ── -->
    <div v-if="algDialog" class="modal-mask" @click.self="algDialog = false">
      <div class="modal" style="width: 580px">
        <h3>{{ algEditing ? '编辑算法：' + algEditing.algCode : '注册算法' }}</h3>
        <!-- 算法文件自动解析（客户上传专业算法文件，系统自动解析类目/参数/说明并回填） -->
        <div v-if="!algEditing" class="form-row">
          <input ref="algFileInput" type="file" accept=".py,.json,.jar,.zip,.txt,.md" style="flex: 1" @change="onAlgFileChange" />
          <button class="btn btn-sm btn-primary" :disabled="!algFile || parsingAlg" @click="onParseAlgFile">{{ parsingAlg ? '解析中…' : '自动解析回填' }}</button>
        </div>
        <div v-if="!algEditing && algFile" class="form-row" style="margin-top: 0">
          <span class="muted">已选择：{{ algFile.name }}（{{ (algFile.size / 1024).toFixed(1) }} KB）· 解析后自动识别类目/参数模板/说明，可继续手动调整</span>
        </div>
        <div v-if="algArchive" class="form-row" style="flex-wrap: wrap; margin-top: 0">
          <span class="badge badge-blue">打包算法解析</span>
          <span class="muted">{{ algArchive.summary || '（未识别到清单/参数模板，可手动补充说明）' }}</span>
        </div>
        <div class="form-row">
          <input v-model="algForm.algCode" placeholder="算法编码（如 HEDGE-STRATEGY-2）" :disabled="!!algEditing" style="flex: 1" />
          <input v-model="algForm.algName" placeholder="算法名称（如 套期保值策略 v2）" style="flex: 1" />
        </div>
        <div class="form-row">
          <select v-model="algForm.category">
            <option v-for="c in algCategories" :key="c.value" :value="c.value">{{ c.label }}</option>
          </select>
          <select v-model="algForm.spiKey" style="flex: 1">
            <option value="">（按类目默认）</option>
            <option v-for="s in spis" :key="s.spiKey" :value="s.spiKey">{{ s.label }}（{{ s.spiKey }}）</option>
          </select>
          <input v-model="algForm.version" placeholder="版本（如 v2.0）" style="width: 110px" />
          <select v-model="algForm.status">
            <option value="enabled">启用</option>
            <option value="disabled">停用</option>
          </select>
        </div>
        <textarea v-model="algForm.description" rows="2" placeholder="算法说明（适用边界/计算口径）" style="width: 100%" />
        <textarea v-model="algForm.paramsSchema" rows="3" class="mono" placeholder='参数模板 JSON，如 {"hedgeRatio":0.8,"lookback":20}' style="width: 100%" />
        <div class="muted">决策时按智能体类目匹配最新启用版本并真实执行绑定 SPI（reasoning 留痕）；替换算法=新版本启用+旧版停用</div>
        <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
          <button class="btn" @click="algDialog = false">取消</button>
          <button class="btn btn-primary" :disabled="!algForm.algCode || !algForm.algName" @click="onSaveAlg">保存</button>
        </div>
      </div>
    </div>

    <!-- ── 模型注册表 ── -->
    <div class="card">
      <h3>模型注册表（GET /model/registry · 与 MLflow 同步）</h3>
      <table>
        <thead>
          <tr><th>编码</th><th>名称</th><th>版本</th><th>框架</th><th>指标（MAPE/方向准确率）</th><th>状态</th></tr>
        </thead>
        <tbody>
          <tr v-if="models.length === 0"><td colspan="6" class="muted">暂无数据</td></tr>
          <tr v-for="m in models" :key="m.modelCode + m.version">
            <td class="mono">{{ m.modelCode }}</td>
            <td>{{ m.modelName }}</td>
            <td class="mono">{{ m.version }}</td>
            <td class="mono">{{ m.framework }}</td>
            <td class="mono muted">{{ metricsText(m.metrics) }}</td>
            <td><span class="badge" :class="statusClass(m.status)">{{ statusLabel(m.status) }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ── 训练触发 ── -->
    <div class="card">
      <h3>训练触发（POST /model/tasks/train · 日增量/周全量，生成训练报告）</h3>
      <div class="form-row">
        <select v-model="trainForm.modelCode">
          <option value="price">价格预测（price）</option>
          <option value="load">负荷预测（load）</option>
          <option value="generation">新能源出力（generation）</option>
        </select>
        <select v-model="trainForm.mode">
          <option value="daily_increment">日增量</option>
          <option value="weekly_full">周全量</option>
        </select>
        <button class="btn btn-primary" :disabled="training" @click="onTrain">{{ training ? '训练中…' : '触发训练' }}</button>
      </div>
      <div v-if="trainReport" class="form-row" style="flex-wrap: wrap">
        <span class="badge" :class="taskStatusClass(trainReport.status)">{{ taskStatusLabel(trainReport.status) }}</span>
        <span class="muted">任务 #{{ trainReport.id }} · {{ trainReport.latencyMs }}ms · {{ reportSummary(trainReport.result) }}</span>
        <span v-if="trainReport.compare?.summary" class="badge badge-blue">对标：{{ trainReport.compare.summary }}</span>
        <button class="btn btn-sm" @click="openReport(trainReport)">查看完整报告</button>
      </div>
    </div>

    <!-- ── 离线评估 ── -->
    <div class="card">
      <h3>离线评估（POST /model/tasks/evaluate · 锁定测试集，双指标判定）</h3>
      <div class="form-row">
        <select v-model="evalForm.modelVersion">
          <option v-for="m in models" :key="m.version" :value="m.version">{{ m.modelName }}（{{ m.version }}）</option>
        </select>
        <input v-model="evalForm.testSetVersion" placeholder="测试集版本（如 v2026-07）" style="width: 180px" />
        <button class="btn" :disabled="evaluating" @click="onEvaluate">{{ evaluating ? '评估中…' : '开始评估' }}</button>
      </div>
      <div v-if="evalReport" class="form-row" style="flex-wrap: wrap">
        <span class="badge" :class="evalReport.status === 'success' ? (evalReport.result.passed ? 'badge-green' : 'badge-red') : 'badge-red'">
          {{ evalReport.status === 'success' ? (evalReport.result.passed ? '通过' : '不达标') : taskStatusLabel(evalReport.status) }}
        </span>
        <span class="muted">任务 #{{ evalReport.id }} · {{ evalReport.latencyMs }}ms · {{ reportSummary(evalReport.result) }}</span>
        <span v-if="evalReport.compare?.summary" class="badge badge-blue">对标：{{ evalReport.compare.summary }}</span>
        <button class="btn btn-sm" @click="openReport(evalReport)">查看完整报告</button>
      </div>
    </div>

    <!-- ── 在线推理 ── -->
    <div class="card">
      <h3>在线推理（POST /model/tasks/inference · LLM 问答/预测增强，生成推理报告）</h3>
      <div class="form-row">
        <select v-model="infForm.modelCode">
          <option value="price">price</option>
          <option value="load">load</option>
          <option value="generation">generation</option>
        </select>
        <input v-model="infForm.inputText" placeholder='输入（如 {"date":"2026-08-21"}）' style="width: 260px" />
        <button class="btn" :disabled="inferring" @click="onInfer">{{ inferring ? '推理中…' : '推理' }}</button>
      </div>
      <div v-if="infReport" class="form-row" style="flex-wrap: wrap">
        <span class="badge" :class="taskStatusClass(infReport.status)">{{ taskStatusLabel(infReport.status) }}</span>
        <span class="muted">任务 #{{ infReport.id }} · {{ infReport.latencyMs }}ms · {{ reportSummary(infReport.result) }}</span>
        <span v-if="infStats" class="muted">· 96 点序列：均值 {{ infStats.avg }} · 峰 {{ infStats.peak }} · 谷 {{ infStats.trough }} · 置信度 {{ infReport.result.confidence }}</span>
        <span v-if="infReport.compare?.summary" class="badge badge-blue">对标：{{ infReport.compare.summary }}</span>
        <button class="btn btn-sm" @click="openReport(infReport)">查看完整报告</button>
      </div>
    </div>

    <!-- ── 模型任务报告（V2.4：训练/评估/推理 过程步骤+结果+与前面对标） ── -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">模型任务报告（GET /model/tasks · 训练/评估/推理 执行过程与对标）</h3>
        <select v-model="taskFilter" style="width: 150px" @change="loadTasks">
          <option value="">全部类型</option>
          <option value="train">训练</option>
          <option value="evaluate">评估</option>
          <option value="inference">推理</option>
        </select>
        <button class="btn btn-sm" style="margin-left: auto" @click="loadTasks">刷新</button>
      </div>
      <table>
        <thead>
          <tr><th>任务ID</th><th>任务名称</th><th>类型</th><th>模型</th><th>状态</th><th>耗时</th><th>完成时间</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="tasks.length === 0"><td colspan="8" class="muted">暂无任务报告（执行训练/评估/推理后自动生成）</td></tr>
          <tr v-for="t in tasks" :key="t.id">
            <td class="mono">#{{ t.id }}</td>
            <td>{{ t.taskName }}</td>
            <td><span class="badge badge-blue">{{ taskTypeLabel(t.taskType) }}</span></td>
            <td class="mono">{{ t.modelCode }}</td>
            <td><span class="badge" :class="taskStatusClass(t.status)">{{ taskStatusLabel(t.status) }}</span></td>
            <td class="mono">{{ t.latencyMs ?? '-' }}ms</td>
            <td class="mono muted">{{ t.finishedAt ?? '-' }}</td>
            <td><button class="btn btn-sm" @click="openTask(t.id)">报告</button></td>
          </tr>
        </tbody>
      </table>
      <div class="muted" style="margin-top: 4px">每类任务自动记录执行过程步骤、结果指标与耗时，并与该模型上一次同类型成功任务对标（关键指标变化），方便用户理解模型运行情况</div>
    </div>

    <!-- ── 任务报告详情弹窗 ── -->
    <div v-if="reportDialog" class="modal-mask" @click.self="reportDialog = false">
      <div class="modal" style="width: 760px">
        <h3>{{ detail?.taskName ?? '任务报告' }}（任务 #{{ detail?.id }}）</h3>
        <div class="form-row" style="flex-wrap: wrap">
          <span class="badge" :class="taskStatusClass(detail?.status ?? '')">{{ taskStatusLabel(detail?.status ?? '') }}</span>
          <span class="muted">类型 {{ taskTypeLabel(detail?.taskType ?? '') }} · 模型 {{ detail?.modelCode }} · 耗时 {{ detail?.latencyMs }}ms · 完成 {{ detail?.finishedAt ?? '-' }}</span>
        </div>
        <h4 style="margin-bottom: 4px">执行过程（{{ detail?.processSteps?.length ?? 0 }} 步）</h4>
        <table>
          <thead><tr><th>#</th><th>步骤</th><th>说明</th><th>耗时</th></tr></thead>
          <tbody>
            <tr v-if="!detail?.processSteps?.length"><td colspan="4" class="muted">无过程记录</td></tr>
            <tr v-for="(s, i) in detail?.processSteps ?? []" :key="i">
              <td class="mono">{{ i + 1 }}</td>
              <td class="mono">{{ s.step }}</td>
              <td>{{ s.detail }}</td>
              <td class="mono">{{ s.timeMs }}ms</td>
            </tr>
          </tbody>
        </table>
        <h4 style="margin-bottom: 4px">结果指标</h4>
        <table>
          <thead><tr><th>指标</th><th>值</th></tr></thead>
          <tbody>
            <tr v-for="(v, k) in resultRows(detail?.result)" :key="k">
              <td class="mono">{{ k }}</td>
              <td class="mono">{{ v }}</td>
            </tr>
          </tbody>
        </table>
        <h4 v-if="detail?.compare?.summary" style="margin-bottom: 4px">与前面对标</h4>
        <div v-if="detail?.compare?.summary" class="form-row" style="flex-wrap: wrap">
          <span class="badge badge-blue">{{ detail.compare.summary }}</span>
          <span class="muted" v-if="detail.compare.baselineTaskId">基线：任务 #{{ detail.compare.baselineTaskId }}（{{ detail.compare.baselineCreatedAt ?? '-' }}）</span>
        </div>
        <table v-if="detail?.compare?.delta && Object.keys(detail.compare.delta).length > 0">
          <thead><tr><th>指标</th><th>上次</th><th>本次</th><th>变化</th></tr></thead>
          <tbody>
            <tr v-for="(v, k) in detail!.compare.delta" :key="k">
              <td class="mono">{{ k }}</td>
              <td class="mono">{{ fmtMetric(detail!.compare.baselineMetrics?.[k]) }}</td>
              <td class="mono">{{ fmtMetric(detail!.result[k]) }}</td>
              <td><span class="badge" :class="deltaBadgeClass(k, v)">{{ Number(v) > 0 ? '↑' : Number(v) < 0 ? '↓' : '→' }} {{ Math.abs(Number(v)) }}</span></td>
            </tr>
          </tbody>
        </table>
        <div class="form-row" style="margin-top: 12px; justify-content: flex-end">
          <button class="btn" @click="reportDialog = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createAlgorithm, createLlmModel, getAlgorithmSpis, getAlgorithms, getLlmModels, getModelTaskDetail, getModelTasks, getModelRegistry, postModelTask, updateAlgorithm, updateLlmModel, uploadAlgorithmFile } from '@/api/model'
import type { AlgorithmItem, LlmModelItem, ModelTaskDetail, ModelTaskItem, SpiItem } from '@/api/model'
import type { ModelInfo } from '@/api/types'

const models = ref<ModelInfo[]>([])
const trainForm = ref({ modelCode: 'price', mode: 'daily_increment' })
const training = ref(false)
const trainReport = ref<ModelTaskDetail | null>(null)
const evalForm = ref({ modelVersion: '', testSetVersion: 'v2026-07' })
const evaluating = ref(false)
const evalReport = ref<ModelTaskDetail | null>(null)
const infForm = ref({ modelCode: 'price', inputText: '{"date":"2026-08-21"}' })
const inferring = ref(false)
const infReport = ref<ModelTaskDetail | null>(null)
const infStats = ref<{ avg: number; peak: number; trough: number } | null>(null)

// ── V2.4 模型任务报告（列表/详情弹窗） ──
const tasks = ref<ModelTaskItem[]>([])
const taskFilter = ref('')
const reportDialog = ref(false)
const detail = ref<ModelTaskDetail | null>(null)

// ── LLM 模型管理（V2.2） ──
const llms = ref<LlmModelItem[]>([])
const llmDialog = ref(false)
const llmEditing = ref<LlmModelItem | null>(null)
const llmForm = reactive({
  modelCode: '', modelName: '', provider: 'deepseek', baseModel: '', endpoint: '', apiKeyRef: '',
  temperature: 0.7, maxTokens: 4096, status: 'enabled',
})

async function loadLlms() {
  try {
    llms.value = await getLlmModels()
  } catch {
    llms.value = []
  }
}

function openLlmCreate() {
  llmEditing.value = null
  Object.assign(llmForm, { modelCode: '', modelName: '', provider: 'deepseek', baseModel: '', endpoint: '', apiKeyRef: '', temperature: 0.7, maxTokens: 4096, status: 'enabled' })
  llmDialog.value = true
}

function openLlmEdit(m: LlmModelItem) {
  llmEditing.value = m
  Object.assign(llmForm, {
    modelCode: m.modelCode, modelName: m.modelName, provider: m.provider,
    baseModel: m.baseModel ?? '', endpoint: m.endpoint ?? '', apiKeyRef: m.apiKeyRef ?? '',
    temperature: m.temperature, maxTokens: m.maxTokens, status: m.status,
  })
  llmDialog.value = true
}

async function onSaveLlm() {
  try {
    if (llmEditing.value) {
      await updateLlmModel(llmEditing.value.id, {
        modelName: llmForm.modelName, provider: llmForm.provider, baseModel: llmForm.baseModel || undefined,
        endpoint: llmForm.endpoint || undefined, apiKeyRef: llmForm.apiKeyRef || undefined,
        temperature: llmForm.temperature, maxTokens: llmForm.maxTokens, status: llmForm.status,
      })
    } else {
      await createLlmModel({
        modelCode: llmForm.modelCode, modelName: llmForm.modelName, provider: llmForm.provider,
        baseModel: llmForm.baseModel || undefined, endpoint: llmForm.endpoint || undefined,
        apiKeyRef: llmForm.apiKeyRef || undefined, temperature: llmForm.temperature,
        maxTokens: llmForm.maxTokens, status: llmForm.status,
      })
    }
    llmDialog.value = false
    await loadLlms()
  } catch (e) {
    alert((e as Error).message || '保存失败（编码唯一）')
  }
}

// ── 算法注册表（V2.2） ──
const algCategories = [
  { value: 'forecast', label: 'forecast 预测' },
  { value: 'market_analysis', label: 'market_analysis 行情分析' },
  { value: 'quote_strategy', label: 'quote_strategy 报价策略' },
  { value: 'risk_measure', label: 'risk_measure 风险计量' },
  { value: 'optimize', label: 'optimize 优化求解' },
  { value: 'settlement', label: 'settlement 结算' },
  { value: 'review', label: 'review 复盘评估' },
  { value: 'rule_engine', label: 'rule_engine 规则引擎' },
]
const algs = ref<AlgorithmItem[]>([])
const spis = ref<SpiItem[]>([])
const algDialog = ref(false)
const algEditing = ref<AlgorithmItem | null>(null)
const algForm = reactive({
  algCode: '', algName: '', category: 'forecast', spiKey: '', version: 'v1.0', description: '', paramsSchema: '{}', status: 'enabled',
})

// ── 算法文件自动解析（操作友好性：上传专业算法文件自动回填注册表单；V2.4 打包算法深度解析） ──
const algFileInput = ref<HTMLInputElement | null>(null)
const algFile = ref<File | null>(null)
const parsingAlg = ref(false)
const algArchive = ref<{ mainClass?: string; manifestDesc?: string; version?: string; fileCount?: number; summary?: string } | null>(null)

function onAlgFileChange(e: Event) {
  const el = e.target as HTMLInputElement
  algFile.value = el.files && el.files.length > 0 ? el.files[0] : null
  algArchive.value = null
}

async function onParseAlgFile() {
  if (!algFile.value) return
  parsingAlg.value = true
  try {
    const r = await uploadAlgorithmFile(algFile.value, algForm.category)
    Object.assign(algForm, {
      algCode: r.algCode, algName: r.algName, category: r.category,
      description: r.description, paramsSchema: r.paramsSchema, version: r.version,
    })
    algArchive.value = r.archiveInfo ?? null
    alert(`已解析算法文件「${r.fileName}」并回填注册表单（类目 ${r.category}，参数模板已识别，可调整后保存）`)
  } catch (e) {
    alert((e as Error).message || '算法文件解析失败')
  } finally {
    parsingAlg.value = false
  }
}

function clearAlgFile() {
  algFile.value = null
  if (algFileInput.value) algFileInput.value.value = ''
}

async function loadAlgs() {
  try {
    algs.value = await getAlgorithms()
  } catch {
    algs.value = []
  }
}

async function loadSpis() {
  try {
    spis.value = await getAlgorithmSpis()
  } catch {
    spis.value = []
  }
}

function spiLabel(key?: string | null): string {
  if (!key) return '（按类目默认）'
  const hit = spis.value.find((s) => s.spiKey === key)
  return hit ? hit.label : key
}

function openAlgCreate() {
  algEditing.value = null
  clearAlgFile()
  Object.assign(algForm, { algCode: '', algName: '', category: 'forecast', spiKey: '', version: 'v1.0', description: '', paramsSchema: '{}', status: 'enabled' })
  algDialog.value = true
}

function openAlgEdit(a: AlgorithmItem) {
  algEditing.value = a
  clearAlgFile()
  Object.assign(algForm, {
    algCode: a.algCode, algName: a.algName, category: a.category, spiKey: a.spiKey ?? '',
    version: a.version, description: a.description ?? '', paramsSchema: schemaText(a.paramsSchema), status: a.status,
  })
  algDialog.value = true
}

async function onSaveAlg() {
  try {
    if (algEditing.value) {
      await updateAlgorithm(algEditing.value.id, {
        algName: algForm.algName, description: algForm.description || undefined,
        paramsSchema: algForm.paramsSchema || undefined, version: algForm.version,
        spiKey: algForm.spiKey || undefined, status: algForm.status,
      })
    } else {
      await createAlgorithm({
        algCode: algForm.algCode, algName: algForm.algName, category: algForm.category,
        description: algForm.description || undefined, paramsSchema: algForm.paramsSchema || undefined,
        version: algForm.version, spiKey: algForm.spiKey || undefined, status: algForm.status,
      })
    }
    algDialog.value = false
    await loadAlgs()
  } catch (e) {
    alert((e as Error).message || '保存失败（编码+版本唯一）')
  }
}

onMounted(async () => {
  try {
    models.value = await getModelRegistry()
  } catch {
    models.value = []
  }
  if (models.value.length > 0) {
    evalForm.value.modelVersion = models.value[0].version
  }
  loadLlms()
  loadAlgs()
  loadSpis()
  loadTasks()
})

// ── V2.4 模型任务报告（训练/评估/推理 → 详细报告与对标） ──

async function loadTasks() {
  try {
    tasks.value = await getModelTasks({ taskType: taskFilter.value || undefined })
  } catch {
    tasks.value = []
  }
}

async function onTrain() {
  training.value = true
  try {
    trainReport.value = await postModelTask('train', {
      modelCode: trainForm.value.modelCode,
      mode: trainForm.value.mode,
    })
    await loadTasks()
  } catch (e) {
    alert((e as Error).message || '训练触发失败')
  } finally {
    training.value = false
  }
}

async function onEvaluate() {
  if (!evalForm.value.modelVersion) return
  evaluating.value = true
  try {
    evalReport.value = await postModelTask('evaluate', {
      modelVersion: evalForm.value.modelVersion,
      testSetVersion: evalForm.value.testSetVersion,
    })
    await loadTasks()
  } catch (e) {
    alert((e as Error).message || '离线评估失败')
  } finally {
    evaluating.value = false
  }
}

async function onInfer() {
  inferring.value = true
  try {
    let input: Record<string, unknown> = {}
    try {
      input = JSON.parse(infForm.value.inputText)
    } catch {
      input = { text: infForm.value.inputText }
    }
    infReport.value = await postModelTask('inference', {
      modelCode: infForm.value.modelCode,
      input,
    })
    infStats.value = (infReport.value.result.output as Record<string, unknown> | undefined)?.seriesStats as { avg: number; peak: number; trough: number } | null ?? null
    await loadTasks()
  } catch (e) {
    alert((e as Error).message || '在线推理失败')
  } finally {
    inferring.value = false
  }
}

async function openTask(id: string) {
  try {
    detail.value = await getModelTaskDetail(id)
    reportDialog.value = true
  } catch (e) {
    alert((e as Error).message || '任务详情加载失败')
  }
}

function openReport(r: ModelTaskDetail) {
  detail.value = r
  reportDialog.value = true
}

function taskTypeLabel(t: string): string {
  return { train: '训练', evaluate: '评估', inference: '推理' }[t] ?? t
}

function taskStatusLabel(s: string): string {
  return { queued: '排队中', running: '执行中', success: '成功', failed: '失败' }[s] ?? s
}

function taskStatusClass(s: string): string {
  return { queued: 'badge-gray', running: 'badge-orange', success: 'badge-green', failed: 'badge-red' }[s] ?? ''
}

function reportSummary(r: Record<string, unknown>): string {
  const s = r.summary
  return typeof s === 'string' ? s : JSON.stringify(r)
}

/** 结果指标表行（跳过 summary 大字段，嵌套对象 JSON 化展示） */
function resultRows(r: Record<string, unknown> | undefined): Array<[string, string]> {
  if (!r) return []
  const rows: Array<[string, string]> = []
  for (const [k, v] of Object.entries(r)) {
    if (k === 'summary') continue
    if (v === null || v === undefined) continue
    rows.push([k, typeof v === 'object' ? JSON.stringify(v) : String(v)])
  }
  return rows
}

function fmtMetric(v: unknown): string {
  if (v === null || v === undefined) return '-'
  return String(v)
}

/** 指标变化好坏：mape/rmse 越低越好，方向准确率/置信度/均值越高越好 */
function deltaBadgeClass(k: string, v: unknown): string {
  const n = Number(v)
  const betterWhenUp = ['directionAccuracy', 'confidence', 'avg', 'peak'].includes(k)
  const good = betterWhenUp ? n >= 0 : n <= 0
  return good ? 'badge-green' : 'badge-red'
}

function metricsText(v: Record<string, unknown> | string | undefined): string {
  if (!v) return '-'
  if (typeof v === 'string') return v
  return Object.entries(v)
    .map(([k, val]) => `${k}: ${String(val)}`)
    .join(' · ')
}

function schemaText(v: Record<string, unknown> | string | undefined): string {
  if (!v) return '{}'
  if (typeof v === 'string') return v
  return JSON.stringify(v)
}

function statusLabel(s: string): string {
  return { online: '在线', evaluating: '评估中', training: '训练中', rolled_back: '已回滚' }[s] ?? s
}

function statusClass(s: string): string {
  return { online: 'badge-green', evaluating: 'badge-orange', training: 'badge-orange', rolled_back: 'badge-red' }[s] ?? ''
}
</script>
