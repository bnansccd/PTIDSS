<template>
  <div>
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">消息中心（GET/POST /message/**）</h3>
        <span class="muted">个人消息 · 分类/未读筛选 · 标记已读（预测摘要/行情预警/决策待办/结算差异/考核提醒）</span>
      </div>
      <div class="form-row">
        <select v-model="query.msgType">
          <option value="">全部类型</option>
          <option value="forecast_summary">预测摘要</option>
          <option value="market_alert">行情预警</option>
          <option value="decision_todo">决策待办</option>
          <option value="settlement_diff">结算差异</option>
          <option value="assess_reminder">考核提醒</option>
        </select>
        <label class="form-check">
          <input v-model="query.unreadOnly" type="checkbox" @change="load(1)" />
          仅看未读
        </label>
        <button class="btn btn-primary" @click="load(1)">查询</button>
        <span class="badge badge-blue">共 {{ total }} 条 · 未读 {{ unreadCount }} 条</span>
      </div>
    </div>

    <div class="card">
      <table>
        <thead>
          <tr><th>类型</th><th>标题</th><th>内容</th><th>渠道</th><th>时间</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-if="messages.length === 0"><td colspan="7" class="muted">暂无消息</td></tr>
          <tr v-for="m in messages" :key="m.id" :class="m.readStatus === 'unread' ? 'row-unread' : ''">
            <td><span class="badge" :class="typeClass(m.msgType)">{{ typeLabel(m.msgType) }}</span></td>
            <td>{{ m.title }}</td>
            <td class="muted">{{ m.content }}</td>
            <td class="mono">{{ channelText(m.channel) }}</td>
            <td class="mono">{{ m.createdAt }}</td>
            <td>
              <span class="badge" :class="m.readStatus === 'read' ? 'badge-gray' : 'badge-green'">
                {{ m.readStatus === 'read' ? '已读' : '未读' }}
              </span>
            </td>
            <td>
              <button v-if="m.readStatus === 'unread'" class="btn" @click="onRead(m.id)">标记已读</button>
              <span v-else class="muted">-</span>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getMessageList, postMessageRead } from '@/api/message'
import type { MessageRecord } from '@/api/types'

const messages = ref<MessageRecord[]>([])
const pageNo = ref(1)
const pageSize = 10
const total = ref(0)
const query = ref({ msgType: '', unreadOnly: false })

const totalPages = ref(1)

const unreadCount = computed(() => messages.value.filter((m) => m.readStatus === 'unread').length)

function typeLabel(t: string): string {
  const map: Record<string, string> = {
    forecast_summary: '预测摘要',
    market_alert: '行情预警',
    decision_todo: '决策待办',
    settlement_diff: '结算差异',
    assess_reminder: '考核提醒',
    approval_task: '审批待办',
  }
  return map[t] ?? t
}

function typeClass(t: string): string {
  const map: Record<string, string> = {
    forecast_summary: 'badge-blue',
    market_alert: 'badge-red',
    decision_todo: 'badge-orange',
    settlement_diff: 'badge-purple',
    assess_reminder: 'badge-green',
    approval_task: 'badge-purple',
  }
  return map[t] ?? 'badge-gray'
}

/** JSONB 渠道兼容解析（后端经 JsonStringTypeHandler 返回 JSON 字符串） */
function channelText(v?: string[] | string): string {
  if (!v) return '-'
  if (Array.isArray(v)) return v.join('、')
  try {
    const parsed = JSON.parse(v) as unknown
    return Array.isArray(parsed) ? parsed.map(String).join('、') : String(parsed)
  } catch {
    return v
  }
}

async function load(p: number) {
  pageNo.value = p
  const res = await getMessageList({ ...query.value, pageNo: p, pageSize })
  messages.value = res.list ?? []
  total.value = res.total ?? 0
  totalPages.value = Math.max(1, Math.ceil(total.value / pageSize))
}

async function onRead(id: string) {
  await postMessageRead(id)
  await load(pageNo.value)
}

onMounted(() => load(1))
</script>
