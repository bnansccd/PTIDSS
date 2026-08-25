<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="brand">⚡ PTIDSS 电力交易决策</div>
      <nav class="nav">
        <template v-for="g in visibleGroups" :key="g.group">
          <div class="nav-group" :class="{ expanded: !isCollapsed(g.group) }" @click="toggleGroup(g.group)">
            <span>{{ g.group }}</span>
            <span class="nav-arrow">{{ isCollapsed(g.group) ? '▸' : '▾' }}</span>
          </div>
          <router-link v-if="!isCollapsed(g.group)" v-for="m in g.items" :key="m.path" :to="m.path">{{ m.title }}</router-link>
        </template>
      </nav>
    </aside>
    <div class="main">
      <header class="topbar">
        <div class="form-row" style="margin-bottom: 0">
          <span class="muted">区域：</span>
          <select
            :value="region.currentRegion"
            @change="onRegionChange($event)"
            :disabled="region.availableRegions.length <= 1"
          >
            <option v-for="r in region.availableRegions" :key="r" :value="r">{{ r }}</option>
          </select>
          <span v-if="region.currentRegion" class="badge badge-blue">{{ region.currentRegion }}</span>
          <span class="muted">结算口径：{{ periodMode }}</span>
        </div>
        <div class="form-row" style="margin-bottom: 0">
          <span class="muted">{{ auth.displayName }}</span>
          <button class="btn" @click="onLogout">退出</button>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useRegionStore } from '@/stores/region'
import * as authApi from '@/api/auth'
import { canAccess } from '@/utils/permission'

const auth = useAuthStore()
const region = useRegionStore()
const route = useRoute()

// 分组折叠：默认全部收起（标题点击展开/收起）；进入页面时自动展开当前路由所在分组
const collapsed = reactive<Record<string, boolean>>({})
function isCollapsed(group: string): boolean {
  return collapsed[group] !== false
}
function toggleGroup(group: string) {
  collapsed[group] = isCollapsed(group) ? false : true
}
onMounted(() => {
  const cur = menuGroups.find((g) => g.items.some((m) => route.path === m.path || route.path.startsWith(m.path + '/')))
  if (cur) collapsed[cur.group] = false
})

// 菜单（对齐前端原型 dashboard.html 左侧 5 分组：总览/行情与预测/决策与交易/结算与复盘/政策与系统；
// 19 项功能全部保留，仅按原型分组归位；可见性按登录权限码过滤，见 utils/permission.ts）
const menuGroups = [
  {
    group: '总览',
    items: [{ path: '/dashboard', title: '决策驾驶舱' }],
  },
  {
    group: '行情与预测',
    items: [
      { path: '/market', title: '市场行情' },
      { path: '/forecast', title: '预测中心' },
      { path: '/data-manage', title: '数据管理' },
    ],
  },
  {
    group: '决策与交易',
    items: [
      { path: '/decision', title: '智能决策' },
      { path: '/trade', title: '交易申报' },
      { path: '/optimize', title: '联合优化' },
      { path: '/agent', title: '智能体管理' },
      { path: '/model', title: '模型平台' },
    ],
  },
  {
    group: '结算与复盘',
    items: [
      { path: '/settlement', title: '结算管理' },
      { path: '/review', title: '复盘分析' },
      { path: '/assess', title: '成效考核' },
      { path: '/report', title: '报表中心' },
    ],
  },
  {
    group: '政策与系统',
    items: [
      { path: '/intel', title: '情报中心' },
      { path: '/policy', title: '政策中心' },
      { path: '/message', title: '消息中心' },
      { path: '/data-platform', title: '数据底座' },
      { path: '/flow', title: '审批流' },
      { path: '/admin', title: '系统管理' },
    ],
  },
]

/** 按当前用户权限码过滤菜单组（无权限的菜单不渲染；整组无可见项则不渲染分组标题） */
const visibleGroups = computed(() =>
  menuGroups
    .map((g) => ({ group: g.group, items: g.items.filter((m) => canAccess(m.path, auth.user?.permissions)) }))
    .filter((g) => g.items.length > 0),
)

// 结算口径展示：按当前区域（sys_region.settlement_period），响应式随切区更新
// V3.1：种子中 trading_month 口径省份为 CN-33 浙江 / CN-14 山西 / CN-15 蒙西（07_seed_data + 14_market_regions_v2_4），其余自然月
const TRADING_MONTH_REGIONS = ['CN-33', 'CN-14', 'CN-15']
const periodMode = computed(() =>
  region.currentRegion && TRADING_MONTH_REGIONS.includes(region.currentRegion) ? 'trading_month' : 'natural_month',
)

function onRegionChange(e: Event) {
  region.setRegion((e.target as HTMLSelectElement).value)
  // 区域上下文变更后路由不变化、页面 onMounted 不会重跑：骨架阶段整页重载刷新数据
  // （正式实现：全局 region 变更事件 + 页面级数据重载，避免整页刷新）
  window.location.reload()
}

async function onLogout() {
  try {
    await authApi.logout() // 通知后端清除令牌缓存（Caffeine）
  } catch {
    // 后端登出失败不阻塞本地清理
  }
  auth.logout()
  region.reset()
  window.location.href = '/login'
}
</script>
