<template>
  <div class="lineage-graph" @wheel.prevent="onWheel">
    <div class="graph-toolbar">
      <span>节点 {{ nodes.length }} · 边 {{ edges.length }} 条</span>
      <span class="hint">滚轮缩放 · 拖拽平移 · 点击节点高亮上下游</span>
      <span class="legend">
        <span v-for="lg in legend" :key="lg.k" class="lg-item"><i :style="{ background: lg.color }"></i>{{ lg.label }}</span>
      </span>
    </div>
    <div
      class="graph-canvas"
      @mousedown="onMouseDown"
      @mousemove="onMouseMove"
      @mouseup="onMouseUp"
      @mouseleave="onMouseUp"
    >
      <svg :width="viewW" :height="viewH" :viewBox="`0 0 ${viewW} ${viewH}`">
        <g :transform="`translate(${tx},${ty}) scale(${k})`">
          <!-- 层/组标题 -->
          <text v-for="g in groupLabels" :key="'gl-' + g.key" :x="g.x" :y="g.y" class="group-label" text-anchor="middle">{{ g.label }}</text>
          <!-- 连线 -->
          <path
            v-for="e in edges"
            :key="e.from + '|' + e.to"
            :d="edgePath(e)"
            class="edge"
            :class="edgeClass(e)"
            :stroke-dasharray="isSupport(e) ? '6 4' : undefined"
          />
          <!-- 节点 -->
          <g
            v-for="n in layoutNodes"
            :key="n.nodeId"
            :transform="`translate(${n.x},${n.y})`"
            class="graph-node"
            :class="nodeStateClass(n.nodeId)"
            @click.stop="selected = selected === n.nodeId ? null : n.nodeId"
            @mouseenter="hoverId = n.nodeId"
            @mouseleave="hoverId = null"
          >
            <rect
              :width="NODE_W"
              :height="NODE_H"
              :rx="n.nodeType === 'business' ? 10 : 5"
              :fill="typeColor(n.nodeType)"
            />
            <text :x="NODE_W / 2" :y="NODE_H / 2 - 4" class="node-name" text-anchor="middle">{{ nodeName(n) }}</text>
            <text :x="NODE_W / 2" :y="NODE_H / 2 + 12" class="node-id" text-anchor="middle">{{ n.nodeId }} · {{ typeLabel(n.nodeType) }}</text>
          </g>
        </g>
      </svg>
    </div>
    <div v-if="detailNode" class="graph-detail">
      <span class="det-name">{{ detailNode.nodeName || detailNode.nodeId }}</span>
      <span class="det-tag" :style="{ background: typeColor(detailNode.nodeType) }">{{ typeLabel(detailNode.nodeType) }}</span>
      <span class="det-tag det-gray">{{ layerLabel(detailNode.layer) }}</span>
      <span class="det-tag det-gray">{{ domainLabel(detailNode.domain) }}</span>
      <span class="det-desc">{{ detailNode.description || '（无说明）' }}</span>
      <span class="det-map" v-if="hasMapping(detailNode)">字段映射：{{ mappingText(detailNode.fieldMapping) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { LineageNode } from '@/api/types'

const props = defineProps<{
  nodes: LineageNode[]
  /** 视图视角：data=数据视角（分层），business=业务视角（分域） */
  view: 'data' | 'business'
}>()

const NODE_W = 180
const NODE_H = 50
const PAD = 40
const GAP_X = 34
const GAP_Y = 80
const COL_W = 240
const COL_GAP = 24
const ROW_GAP = 28

const TYPE_COLORS: Record<string, string> = {
  table: '#3b82f6',
  task: '#f59e0b',
  model: '#8b5cf6',
  report: '#10b981',
  business: '#ec4899',
}
const TYPE_LABELS: Record<string, string> = {
  table: '数据表',
  task: '采集/加工',
  model: '模型',
  report: '报表',
  business: '业务应用',
}
const LAYER_ORDER = ['source', 'collect', 'detail', 'indicator', 'model', 'report', 'business']
const LAYER_LABELS: Record<string, string> = {
  source: '外部数据源',
  collect: '采集任务',
  detail: '明细数据',
  indicator: '指标层',
  model: '模型层',
  report: '报表层',
  business: '业务应用',
}
const DOMAIN_LABELS: Record<string, string> = {
  marketing: '营销域',
  exchange: '交易中心域',
  weather: '气象域',
  common: '公共数据底座',
  trade: '交易域',
  settle: '结算域',
  policy: '政策域',
  intel: '情报域',
  forecast: '预测域',
  model: '模型域',
  decision: '决策域',
  optimize: '优化域',
  assess: '评估域',
  report: '报表域',
  system: '系统支撑',
}
const DOMAIN_ORDER = ['exchange', 'marketing', 'weather', 'common', 'trade', 'settle', 'policy', 'intel', 'forecast', 'model', 'decision', 'optimize', 'assess', 'report', 'system']

const legend = [
  { k: 'table', label: '数据表', color: TYPE_COLORS.table },
  { k: 'task', label: '采集/加工', color: TYPE_COLORS.task },
  { k: 'model', label: '模型', color: TYPE_COLORS.model },
  { k: 'report', label: '报表', color: TYPE_COLORS.report },
  { k: 'business', label: '业务应用', color: TYPE_COLORS.business },
]

const selected = ref<string | null>(null)
const hoverId = ref<string | null>(null)
const k = ref(1)
const tx = ref(0)
const ty = ref(0)
const dragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })
const dragPos = ref({ x: 0, y: 0 })

function nodeName(n: LineageNode): string {
  return n.nodeName || n.nodeId
}
function typeLabel(t?: string): string {
  return TYPE_LABELS[t ?? ''] ?? t ?? ''
}
function typeColor(t?: string): string {
  return TYPE_COLORS[t ?? ''] ?? '#64748b'
}
function layerLabel(l?: string): string {
  return LAYER_LABELS[l ?? ''] ?? l ?? '未分层'
}
function domainLabel(d?: string): string {
  return DOMAIN_LABELS[d ?? ''] ?? d ?? '未分组'
}
function hasMapping(n: LineageNode): boolean {
  return !!n.fieldMapping && Object.keys(n.fieldMapping as object).length > 0
}
function mappingText(v: Record<string, unknown> | string | undefined): string {
  if (!v) return '-'
  if (typeof v === 'string') return v
  return Object.entries(v).map(([key, val]) => `${key}→${String(val)}`).join('，') || '-'
}
function isSupport(e: { from: string; to: string }): boolean {
  const nodes = layoutNodes.value
  const f = nodes.find((n) => n.nodeId === e.from)
  const t = nodes.find((n) => n.nodeId === e.to)
  return f?.domain === 'system' || t?.domain === 'system'
}

/** 数据视角：按 layer 分层（行），层内按 domain 排序均分 */
function layoutDataView(nodes: LineageNode[]) {
  const rows = new Map<string, LineageNode[]>()
  for (const n of nodes) {
    const l = n.layer || 'detail'
    if (!rows.has(l)) rows.set(l, [])
    rows.get(l)!.push(n)
  }
  const pos = new Map<string, { x: number; y: number }>()
  const labels: { key: string; x: number; y: number; label: string }[] = []
  let maxW = 0
  const orderedLayers = LAYER_ORDER.filter((l) => rows.has(l))
  orderedLayers.forEach((l, row) => {
    const list = rows.get(l)!.sort((a, b) => (a.domain || '').localeCompare(b.domain || '') || a.nodeId.localeCompare(b.nodeId))
    const y = PAD + row * (NODE_H + GAP_Y)
    const innerW = list.length * NODE_W + (list.length - 1) * GAP_X
    const startX = PAD + (list.length > 1 ? (Math.max(...orderedLayers.map((ll) => rows.get(ll)!.length)) * NODE_W - innerW) / 2 : 0)
    list.forEach((n, col) => {
      pos.set(n.nodeId, { x: startX + col * (NODE_W + GAP_X), y })
    })
    labels.push({ key: l, x: PAD - 14, y: y + NODE_H / 2 + 5, label: LAYER_LABELS[l] })
    maxW = Math.max(maxW, startX + innerW + PAD)
  })
  return { pos, labels, width: maxW, height: PAD + orderedLayers.length * (NODE_H + GAP_Y) }
}

/** 业务视角：按 domain 分组（列），组内按 layer 纵向排列 */
function layoutBizView(nodes: LineageNode[]) {
  const groups = new Map<string, LineageNode[]>()
  for (const n of nodes) {
    const d = n.domain || 'common'
    if (!groups.has(d)) groups.set(d, [])
    groups.get(d)!.push(n)
  }
  const pos = new Map<string, { x: number; y: number }>()
  const labels: { key: string; x: number; y: number; label: string }[] = []
  const ordered = DOMAIN_ORDER.filter((d) => groups.has(d))
  const maxRows = Math.max(...ordered.map((d) => groups.get(d)!.length))
  let col = 0
  let totalW = PAD
  for (const d of ordered) {
    const list = groups
      .get(d)!
      .sort((a, b) => (LAYER_ORDER.indexOf(a.layer || '') - LAYER_ORDER.indexOf(b.layer || '')) || a.nodeId.localeCompare(b.nodeId))
    const x = PAD + col * (COL_W + COL_GAP)
    list.forEach((n, row) => {
      const y = PAD + 26 + row * (NODE_H + ROW_GAP)
      pos.set(n.nodeId, { x: x + (COL_W - NODE_W) / 2, y })
    })
    labels.push({ key: d, x: x + COL_W / 2, y: PAD + 8, label: DOMAIN_LABELS[d] })
    col += 1
    totalW = x + COL_W
  }
  return { pos, labels, width: totalW + PAD, height: PAD + 26 + maxRows * (NODE_H + ROW_GAP) + PAD }
}

const layout = computed(() => (props.view === 'business' ? layoutBizView(props.nodes) : layoutDataView(props.nodes)))
const layoutNodes = computed(() =>
  props.nodes.map((n) => {
    const p = layout.value.pos.get(n.nodeId) || { x: 0, y: 0 }
    return { ...n, x: p.x, y: p.y }
  }),
)
const groupLabels = computed(() => layout.value.labels)
const viewW = computed(() => layout.value.width)
const viewH = computed(() => layout.value.height)

/** 边：由各节点 downstream 生成（去重） */
const edges = computed(() => {
  const set = new Map<string, { from: string; to: string }>()
  for (const n of props.nodes) {
    const ds = Array.isArray(n.downstream) ? n.downstream.map((d) => (typeof d === 'string' ? d : d.nodeId)) : []
    for (const t of ds) {
      if (props.nodes.some((x) => x.nodeId === t) && n.nodeId !== t) {
        set.set(`${n.nodeId}|${t}`, { from: n.nodeId, to: t })
      }
    }
  }
  return [...set.values()]
})

function edgePath(e: { from: string; to: string }): string {
  const a = layout.value.pos.get(e.from)
  const b = layout.value.pos.get(e.to)
  if (!a || !b) return ''
  const vertical = props.view === 'data'
  if (vertical) {
    const x1 = a.x + NODE_W / 2
    const y1 = a.y + NODE_H
    const x2 = b.x + NODE_W / 2
    const y2 = b.y
    const midY = (y1 + y2) / 2
    return `M ${x1} ${y1} C ${x1} ${midY}, ${x2} ${midY}, ${x2} ${y2}`
  }
  const x1 = a.x + NODE_W
  const y1 = a.y + NODE_H / 2
  const x2 = b.x
  const y2 = b.y + NODE_H / 2
  const midX = (x1 + x2) / 2
  return `M ${x1} ${y1} C ${midX} ${y1}, ${midX} ${y2}, ${x2} ${y2}`
}

function nodeStateClass(id: string): string {
  if (!selected.value) return ''
  if (selected.value === id) return 'node-selected'
  const rel = edges.value.some((e) => (e.from === selected.value && e.to === id) || (e.to === selected.value && e.from === id))
  return rel ? 'node-related' : 'node-dim'
}
function edgeClass(e: { from: string; to: string }): string {
  if (!selected.value) return ''
  return e.from === selected.value || e.to === selected.value ? 'edge-hl' : 'edge-dim'
}

const detailNode = computed<LineageNode | null>(() => {
  const id = selected.value || hoverId.value
  return props.nodes.find((n) => n.nodeId === id) ?? null
})

function onWheel(e: WheelEvent) {
  const delta = e.deltaY > 0 ? 0.9 : 1.1
  k.value = Math.min(2, Math.max(0.4, k.value * delta))
}
function onMouseDown(e: MouseEvent) {
  dragging.value = true
  dragStart.value = { x: e.clientX, y: e.clientY }
  dragPos.value = { x: tx.value, y: ty.value }
}
function onMouseMove(e: MouseEvent) {
  if (!dragging.value) return
  tx.value = dragPos.value.x + (e.clientX - dragStart.value.x)
  ty.value = dragPos.value.y + (e.clientY - dragStart.value.y)
}
function onMouseUp() {
  dragging.value = false
}
</script>

<style scoped>
.lineage-graph {
  border: 1px solid var(--border, #e2e8f0);
  border-radius: 8px;
  overflow: hidden;
  background: #fbfcfe;
}
.graph-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 12px;
  font-size: 12px;
  background: #f1f5f9;
  border-bottom: 1px solid var(--border, #e2e8f0);
  flex-wrap: wrap;
}
.hint {
  color: #94a3b8;
}
.legend {
  margin-left: auto;
  display: flex;
  gap: 10px;
}
.lg-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.lg-item i {
  width: 10px;
  height: 10px;
  border-radius: 2px;
  display: inline-block;
}
.graph-canvas {
  overflow: hidden;
  cursor: grab;
  min-height: 320px;
  position: relative;
}
.graph-canvas:active {
  cursor: grabbing;
}
.group-label {
  font-size: 13px;
  font-weight: 600;
  fill: #64748b;
}
.edge {
  fill: none;
  stroke: #94a3b8;
  stroke-width: 1.5;
  opacity: 0.7;
  transition: opacity 0.15s;
}
.edge-hl {
  stroke: #ef4444;
  stroke-width: 2.5;
  opacity: 1;
}
.edge-dim {
  opacity: 0.12;
}
.graph-node {
  cursor: pointer;
  transition: opacity 0.15s;
}
.graph-node rect {
  stroke: #ffffff;
  stroke-width: 2;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.15));
}
.node-selected rect {
  stroke: #0f172a;
  stroke-width: 3;
}
.node-related {
  opacity: 1;
}
.node-dim {
  opacity: 0.18;
}
.node-name {
  font-size: 12px;
  font-weight: 600;
  fill: #ffffff;
}
.node-id {
  font-size: 9px;
  fill: rgba(255, 255, 255, 0.85);
}
.graph-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 12px;
  border-top: 1px solid var(--border, #e2e8f0);
  background: #ffffff;
  flex-wrap: wrap;
}
.det-name {
  font-weight: 600;
}
.det-tag {
  color: #fff;
  font-size: 11px;
  padding: 1px 8px;
  border-radius: 10px;
}
.det-gray {
  background: #94a3b8;
}
.det-desc {
  color: #475569;
}
.det-map {
  color: #8b5cf6;
}
</style>
