<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">政策中心（FR-PD-01 政策研判 · GET/POST /policy/**）</h3>
        <span class="muted">政策库分类/标签/版本管理 · LLM 智能解析（条款→规则候选，置信度标注）· 影响研判 · 简报导出</span>
      </div>
      <div class="form-row">
        <select v-model="query.category">
          <option value="">全部分类</option>
          <option value="national">国家</option>
          <option value="regional">区域</option>
          <option value="provincial">省内</option>
        </select>
        <input v-model="query.keyword" placeholder="标题模糊搜索" @keyup.enter="load(1)" />
        <select v-model="query.status">
          <option value="">全部状态</option>
          <option value="published">已发布</option>
          <option value="draft">草稿</option>
          <option value="expired">已过期</option>
        </select>
        <button class="btn btn-primary" @click="load(1)">查询</button>
        <button class="btn" @click="reset">重置</button>
        <button class="btn" @click="openUpload">上传新政策</button>
        <span class="badge badge-blue">共 {{ total }} 份政策</span>
      </div>
    </div>

    <!-- ── 上传/新建政策弹窗（POST /policy/upload，任务 2 新建增补齐） ── -->
    <div v-if="uploadDialog" class="modal-mask" @click.self="uploadDialog = false">
      <div class="modal-card">
        <h3>上传新政策（登记入政策库，登记后可一键触发 LLM 解析）</h3>
        <div class="form-row">
          <label class="f">政策标题</label>
          <input v-model="uploadForm.title" placeholder="必填" style="flex: 1" />
        </div>
        <div class="form-row">
          <label class="f">发布机构</label>
          <input v-model="uploadForm.issuingBody" style="flex: 1" />
        </div>
        <div class="form-row">
          <label class="f">分类</label>
          <select v-model="uploadForm.category" style="width: 140px">
            <option value="provincial">省内</option>
            <option value="regional">区域</option>
            <option value="national">国家</option>
          </select>
        </div>
        <div class="form-row">
          <label class="f">标签</label>
          <input v-model="uploadForm.tagsText" placeholder="逗号分隔：现货,中长期,结算" style="flex: 1" />
        </div>
        <div class="form-row">
          <label class="f">发布日期</label>
          <input v-model="uploadForm.publishDate" type="datetime-local" style="flex: 1" />
        </div>
        <div class="form-row">
          <label class="f">生效日期</label>
          <input v-model="uploadForm.effectiveDate" type="datetime-local" style="flex: 1" />
        </div>
        <div class="form-row">
          <label class="f">状态</label>
          <select v-model="uploadForm.status" style="width: 140px">
            <option value="published">已发布</option>
            <option value="draft">草稿</option>
            <option value="expired">已过期</option>
          </select>
        </div>
        <div class="form-row">
          <label class="f">政策文件</label>
          <input ref="policyFileInput" type="file" accept=".pdf,.txt,.doc,.docx,.md,.json" style="flex: 1" @change="onPolicyFileChange" />
        </div>
        <div v-if="policyFile" class="form-row" style="margin-top: 0">
          <span class="muted">已选择：{{ policyFile.name }}（{{ (policyFile.size / 1024).toFixed(1) }} KB），上传后可在列表点击「下载」取回原文</span>
        </div>
        <div class="form-row" style="justify-content: flex-end; margin-top: 16px">
          <button class="btn" @click="uploadDialog = false">取消</button>
          <button class="btn btn-primary" :disabled="!uploadForm.title" @click="onUpload">上传</button>
        </div>
      </div>
    </div>

    <div class="card">
      <table>
        <thead>
          <tr><th>标题</th><th>发布机构</th><th>分类</th><th>标签</th><th>发布日期</th><th>生效日期</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="docs.length === 0"><td colspan="8" class="muted">暂无数据</td></tr>
          <tr v-for="d in docs" :key="d.id">
            <td>{{ d.title }}</td>
            <td>{{ d.issuingBody }}</td>
            <td><span class="badge" :class="categoryClass(d.category)">{{ categoryLabel(d.category) }}</span></td>
            <td class="muted">{{ tagsText(d.tags) }}</td>
            <td class="mono">{{ d.publishDate }}</td>
            <td class="mono">{{ d.effectiveDate }}</td>
            <td><span class="badge" :class="d.status === 'published' ? 'badge-green' : 'badge-orange'">{{ statusLabel(d.status) }}（{{ d.status }}）</span></td>
            <td>
              <button class="btn" @click="openDetail(d)">详情</button>
              <button class="btn" @click="onParse(d)">解析</button>
              <button class="btn" @click="onBrief(d)">简报</button>
              <button v-if="d.fileUrl?.startsWith('local://')" class="btn" @click="onFileDownload(d)">下载</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="form-row">
        <button class="btn" :disabled="pageNo <= 1" @click="load(pageNo - 1)">上一页</button>
        <span class="muted">第 {{ pageNo }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="pageNo >= totalPages" @click="load(pageNo + 1)">下一页</button>
      </div>
    </div>

    <!-- ── 政策详情：条款 / 研判 / 规则 三栏 ── -->
    <div v-if="detail" class="card">
      <div class="form-row">
        <h3 style="margin: 0">{{ detail.title }}</h3>
        <span class="muted">{{ detail.issuingBody }} · {{ categoryLabel(detail.category) }} · 生效 {{ detail.effectiveDate }}</span>
        <span v-if="parseResult" class="badge badge-green">
          解析 {{ parseResult.articlesParsed }} 条款 / 规则候选 {{ parseResult.ruleCandidates }} / 平均置信度 {{ parseResult.avgConfidence }}
        </span>
      </div>

      <h4>一、解析条款（置信度 / 人工确认状态）</h4>
      <table>
        <thead>
          <tr><th>条款类型</th><th>条款原文</th><th>置信度</th><th>复核状态</th></tr>
        </thead>
        <tbody>
          <tr v-if="detail.articles.length === 0"><td colspan="4" class="muted">未解析，点击上方「解析」触发 LLM 抽取</td></tr>
          <tr v-for="a in detail.articles" :key="a.id">
            <td>{{ clauseLabel(a.clauseType) }}</td>
            <td>{{ a.originalText }}</td>
            <td>{{ a.confidence }}</td>
            <td><span class="badge" :class="a.reviewStatus === 'confirmed' ? 'badge-green' : a.reviewStatus === 'revised' ? 'badge-orange' : 'badge-blue'">{{ a.reviewStatus }}</span></td>
          </tr>
        </tbody>
      </table>

      <h4>二、影响研判（变化点 → 影响环节 → 影响程度，可追溯）</h4>
      <table>
        <thead>
          <tr><th>政策变化点</th><th>影响环节</th><th>影响程度</th><th>研判人</th></tr>
        </thead>
        <tbody>
          <tr v-if="detail.analysis.length === 0"><td colspan="4" class="muted">暂无研判记录</td></tr>
          <tr v-for="an in detail.analysis" :key="an.id">
            <td>{{ an.changePoint }}</td>
            <td>{{ an.affectedLink }}</td>
            <td><span class="badge" :class="an.impactLevel === 'high' ? 'badge-red' : an.impactLevel === 'medium' ? 'badge-orange' : 'badge-blue'">{{ an.impactLevel }}</span></td>
            <td>{{ an.analyst }}</td>
          </tr>
        </tbody>
      </table>

      <h4>三、规则库沉淀（版本化，供合规校验复用）</h4>
      <table>
        <thead>
          <tr><th>规则编码</th><th>规则名称</th><th>类型</th><th>版本</th><th>状态</th></tr>
        </thead>
        <tbody>
          <tr v-if="detail.rules.length === 0"><td colspan="5" class="muted">暂无沉淀规则</td></tr>
          <tr v-for="r in detail.rules" :key="r.id">
            <td class="mono">{{ r.ruleCode }}</td>
            <td>{{ r.ruleName }}</td>
            <td>{{ r.ruleType }}</td>
            <td class="mono">v{{ r.version }}</td>
            <td><span class="badge" :class="r.status === 'active' ? 'badge-green' : 'badge-blue'">{{ statusLabel(r.status) }}（{{ r.status }}）</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { downloadPolicyBrief, downloadPolicyFile, getPolicyDetail, getPolicyList, postPolicyParse, postPolicyUpload, uploadPolicyFile } from '@/api/policy'
import type { PolicyDetail, PolicyDocument, PolicyParseResult } from '@/api/types'

const docs = ref<PolicyDocument[]>([])
const detail = ref<PolicyDetail | null>(null)
const parseResult = ref<PolicyParseResult | null>(null)
const pageNo = ref(1)
const pageSize = 10
const total = ref(0)
const query = ref({ category: '', keyword: '', status: '' })

const totalPages = ref(1)

/** JSONB 标签兼容解析（后端经 JsonStringTypeHandler 返回 JSON 字符串） */
function tagsText(v?: string[] | string): string {
  if (!v) return '-'
  if (Array.isArray(v)) return v.join('、')
  try {
    const parsed = JSON.parse(v) as unknown
    return Array.isArray(parsed) ? parsed.map(String).join('、') : String(parsed)
  } catch {
    return v
  }
}

function categoryLabel(c: string): string {
  return c === 'national' ? '国家' : c === 'regional' ? '区域' : '省内'
}

/** V2.4 编码+名称：政策/规则状态中文标签（draft 草稿 / published 已发布 / active 启用 / inactive 停用） */
function statusLabel(s: string): string {
  return { draft: '草稿', published: '已发布', active: '启用', inactive: '停用' }[s] ?? s
}

function categoryClass(c: string): string {
  return c === 'national' ? 'badge-red' : c === 'regional' ? 'badge-blue' : 'badge-green'
}

function clauseLabel(t: string): string {
  const map: Record<string, string> = {
    trade_rule: '交易规则',
    price_mechanism: '价格机制',
    assessment: '考核条款',
    settlement: '结算规则',
  }
  return map[t] ?? t
}

async function load(p: number) {
  pageNo.value = p
  const res = await getPolicyList({ ...query.value, pageNo: p, pageSize })
  docs.value = res.list ?? []
  total.value = res.total ?? 0
  totalPages.value = Math.max(1, Math.ceil(total.value / pageSize))
}

async function reset() {
  query.value = { category: '', keyword: '', status: '' }
  await load(1)
}

async function openDetail(d: PolicyDocument) {
  parseResult.value = null
  detail.value = await getPolicyDetail(d.id)
}

async function onParse(d: PolicyDocument) {
  parseResult.value = await postPolicyParse({ policyId: d.id })
  detail.value = await getPolicyDetail(d.id)
}

function onBrief(d: PolicyDocument) {
  void downloadPolicyBrief(d.id)
}

function onFileDownload(d: PolicyDocument) {
  void downloadPolicyFile(d.id, d.title)
}

// ── 上传/新建政策 ──
const uploadDialog = ref(false)
const uploadForm = reactive({
  title: '', issuingBody: '', category: 'provincial', tagsText: '',
  publishDate: '', effectiveDate: '', status: 'published',
})
const policyFileInput = ref<HTMLInputElement | null>(null)
const policyFile = ref<File | null>(null)

function onPolicyFileChange(e: Event) {
  const el = e.target as HTMLInputElement
  policyFile.value = el.files && el.files.length > 0 ? el.files[0] : null
}

function clearPolicyFile() {
  policyFile.value = null
  if (policyFileInput.value) policyFileInput.value.value = ''
}

function openUpload() {
  clearPolicyFile()
  Object.assign(uploadForm, {
    title: '', issuingBody: '', category: 'provincial', tagsText: '',
    publishDate: '', effectiveDate: '', status: 'published',
  })
  uploadDialog.value = true
}

async function onUpload() {
  try {
    const tags = uploadForm.tagsText.split(/[,，]/).map((s) => s.trim()).filter(Boolean)
    // 选择政策文件 → multipart 上传落盘（列表可下载原文）；未选择 → 原 JSON 登记接口
    const doc = policyFile.value
      ? await uploadPolicyFile({
          file: policyFile.value,
          title: uploadForm.title,
          issuingBody: uploadForm.issuingBody,
          category: uploadForm.category,
          tags,
          publishDate: uploadForm.publishDate || undefined,
          effectiveDate: uploadForm.effectiveDate || undefined,
          status: uploadForm.status,
        })
      : await postPolicyUpload({
          title: uploadForm.title,
          issuingBody: uploadForm.issuingBody,
          category: uploadForm.category,
          tags,
          publishDate: uploadForm.publishDate || undefined,
          effectiveDate: uploadForm.effectiveDate || undefined,
          status: uploadForm.status,
        })
    uploadDialog.value = false
    alert(`政策「${doc.title}」已上传（id=${doc.id}），可在列表点击「解析」触发 LLM 条款抽取`)
    await load(1)
  } catch (e) {
    alert((e as Error).message || '上传失败')
  }
}

onMounted(() => load(1))
</script>
