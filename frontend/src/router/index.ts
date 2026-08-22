import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useRegionStore } from '@/stores/region'
import { canAccess } from '@/utils/permission'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { public: true, title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '决策驾驶舱' } },
      { path: 'market', name: 'market', component: () => import('@/views/MarketView.vue'), meta: { title: '市场行情' } },
      { path: 'trade', name: 'trade', component: () => import('@/views/TradeView.vue'), meta: { title: '交易申报' } },
      { path: 'decision', name: 'decision', component: () => import('@/views/DecisionView.vue'), meta: { title: '智能决策' } },
      { path: 'settlement', name: 'settlement', component: () => import('@/views/SettlementView.vue'), meta: { title: '结算管理' } },
      { path: 'data-manage', name: 'data-manage', component: () => import('@/views/DataManageView.vue'), meta: { title: '数据管理' } },
      { path: 'intel', name: 'intel', component: () => import('@/views/IntelView.vue'), meta: { title: '情报中心' } },
      { path: 'policy', name: 'policy', component: () => import('@/views/PolicyView.vue'), meta: { title: '政策中心' } },
      { path: 'message', name: 'message', component: () => import('@/views/MessageView.vue'), meta: { title: '消息中心' } },
      { path: 'data-platform', name: 'data-platform', component: () => import('@/views/DataPlatformView.vue'), meta: { title: '数据底座' } },
      { path: 'forecast', name: 'forecast', component: () => import('@/views/ForecastView.vue'), meta: { title: '预测中心' } },
      { path: 'model', name: 'model', component: () => import('@/views/ModelView.vue'), meta: { title: '模型平台' } },
      { path: 'agent', name: 'agent', component: () => import('@/views/AgentView.vue'), meta: { title: '智能体管理' } },
      { path: 'optimize', name: 'optimize', component: () => import('@/views/OptimizeView.vue'), meta: { title: '联合优化' } },
      { path: 'flow', name: 'flow', component: () => import('@/views/FlowView.vue'), meta: { title: '审批流' } },
      { path: 'review', name: 'review', component: () => import('@/views/ReviewView.vue'), meta: { title: '复盘分析', perm: 'menu:review' } },
      { path: 'assess', name: 'assess', component: () => import('@/views/AssessView.vue'), meta: { title: '成效考核', perm: 'menu:review' } },
      { path: 'report', name: 'report', component: () => import('@/views/ReportView.vue'), meta: { title: '报表中心', perm: 'menu:report' } },
      { path: 'admin', name: 'admin', component: () => import('@/views/AdminView.vue'), meta: { title: '系统管理' } },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  document.title = to.meta.title ? `${to.meta.title as string} - PTIDSS` : 'PTIDSS'
  if (to.meta.public) {
    if (to.name === 'login' && auth.isAuthenticated) {
      return { path: '/dashboard' }
    }
    return true
  }
  if (!auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  // 已登录但用户态未恢复（F5 刷新/直链进入）：补拉 /auth/current + 区域初始化
  if (!auth.loaded) {
    try {
      await auth.loadCurrent()
      const region = useRegionStore()
      region.init(auth.user?.regions)
    } catch {
      // 令牌失效：14001 由 http 拦截器统一处理（refresh 重试 + 登出）
    }
  }
  // 菜单/页面权限过滤：无权限访问时回落到驾驶舱
  if (to.name !== 'dashboard' && !canAccess(to.path, auth.user?.permissions)) {
    return { path: '/dashboard' }
  }
  return true
})

export default router
