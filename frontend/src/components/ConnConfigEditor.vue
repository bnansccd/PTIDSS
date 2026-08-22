<template>
  <div>
    <!-- 图形化连接参数（URL/账号/密码等必要对象值；敏感字段留空=保持原配置） -->
    <div class="form-row">
      <label class="f">Endpoint URL</label>
      <input v-model="form.url" placeholder="https://api.example.com/v1" style="flex: 1" @input="emitJson()" />
    </div>
    <div class="form-row">
      <label class="f">账号</label>
      <input v-model="form.username" placeholder="用户名（可选）" style="flex: 1" @input="emitJson()" />
    </div>
    <div class="form-row">
      <label class="f">密码</label>
      <input v-model="form.password" type="password" :placeholder="passwordPlaceholder" style="flex: 1" @input="emitJson()" />
    </div>
    <div class="form-row">
      <label class="f">API Key</label>
      <input v-model="form.apiKey" type="password" :placeholder="apiKeyPlaceholder" style="flex: 1" @input="emitJson()" />
    </div>
    <div class="form-row">
      <label class="f">密钥引用</label>
      <input v-model="form.tokenRef" placeholder="环境变量名（如 API_KEY_X）" style="flex: 1" @input="emitJson()" />
    </div>
    <div class="form-row">
      <button class="btn btn-sm" @click="showJson = !showJson">{{ showJson ? '收起 JSON 高级模式' : 'JSON 高级模式（保留完整配置）' }}</button>
      <span class="muted">密码/API Key 留空 = 保持原配置不修改；其余字段留空 = 清除</span>
    </div>
    <div v-if="showJson" style="display: flex; flex-direction: column; gap: 6px; margin-top: 4px">
      <textarea v-model="jsonText" rows="4" class="mono" placeholder="完整连接参数 JSON，与上方图形字段双向同步" style="width: 100%"></textarea>
      <div class="form-row">
        <button class="btn btn-sm btn-primary" @click="applyJson">应用 JSON</button>
        <button class="btn btn-sm" @click="syncFromJson">重新解析回填图形</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 连接参数图形化编辑器（操作友好性：URL/账号/密码等必要对象值即可完成配置）
 * - 与完整 JSON 双向同步：图形字段变更合并回 JSON（保留未知字段），JSON 高级模式可编辑完整配置
 * - 敏感字段（password/apiKey）语义：回显 ****** 占位 → 表单留空；提交留空 = 不修改（后端 mergeMasked 保留原密文）
 */
import { reactive, ref, watch } from 'vue'

const props = defineProps<{ modelValue: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>()

const MASK = '******'

const showJson = ref(false)
const jsonText = ref('')
const form = reactive({ url: '', username: '', password: '', apiKey: '', tokenRef: '' })
const passwordPlaceholder = ref('留空不修改')
const apiKeyPlaceholder = ref('留空不修改')

function tryParse(v: string): Record<string, unknown> | null {
  if (!v) return null
  try {
    const o = JSON.parse(v) as unknown
    return o && typeof o === 'object' && !Array.isArray(o) ? (o as Record<string, unknown>) : null
  } catch {
    return null
  }
}

/** 从外部 JSON 解析回填图形字段与高级模式文本 */
function syncFromJson() {
  const base = tryParse(props.modelValue) ?? {}
  const url = (base.endpoint ?? base.url) as string | undefined
  form.url = typeof url === 'string' ? url : ''
  form.username = typeof base.username === 'string' ? String(base.username) : ''
  form.tokenRef = typeof base.tokenRef === 'string' ? String(base.tokenRef) : ''
  // 敏感字段：密文/占位一律不显示原文，回显留空（编辑时留空=不修改）
  const pwd = base.password as string | undefined
  const ak = base.apiKey as string | undefined
  form.password = typeof pwd === 'string' && pwd !== MASK && !pwd.startsWith('enc:') ? pwd : ''
  form.apiKey = typeof ak === 'string' && ak !== MASK && !ak.startsWith('enc:') ? ak : ''
  passwordPlaceholder.value = typeof pwd === 'string' && pwd ? '已配置（留空不修改）' : '密码（可选）'
  apiKeyPlaceholder.value = typeof ak === 'string' && ak ? '已配置（留空不修改）' : 'API Key（可选）'
  jsonText.value = JSON.stringify(base, null, 2)
}

function setOrDelete(base: Record<string, unknown>, key: string, value: string) {
  if (value === '') {
    delete base[key]
  } else {
    base[key] = value
  }
}

/** 图形字段合并回 JSON（保留未知字段；url 键沿用 endpoint/url 原有命名） */
function emitJson() {
  const base = tryParse(props.modelValue) ?? {}
  const urlKey = 'endpoint' in base ? 'endpoint' : 'url'
  setOrDelete(base, urlKey, form.url)
  setOrDelete(base, 'username', form.username)
  setOrDelete(base, 'tokenRef', form.tokenRef)
  // 敏感字段：留空删除 key（后端 merge 时保留原密文）；非空才写入
  if (form.password) base.password = form.password
  else delete base.password
  if (form.apiKey) base.apiKey = form.apiKey
  else delete base.apiKey
  jsonText.value = JSON.stringify(base, null, 2)
  emit('update:modelValue', JSON.stringify(base))
}

/** JSON 高级模式：应用完整 JSON（解析失败提示，不破坏图形字段） */
function applyJson() {
  const parsed = tryParse(jsonText.value)
  if (!parsed) {
    alert('连接参数需为合法 JSON 对象')
    return
  }
  emit('update:modelValue', JSON.stringify(parsed))
}

watch(() => props.modelValue, () => syncFromJson(), { immediate: true })
</script>
