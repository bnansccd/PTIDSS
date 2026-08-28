// 决策审批（Tab3，SRS 9.2 决议④：移动端含审批操作）：
// 待办/我发起/全部（GET /flow/instances?scope=）、发起流程（/flow/biz-types → /flow/biz-options → /flow/start）
// 权限说明：/flow/** 需 menu:flow 权限（FR-INT-03），无权限账号仅展示列表只读态
const { request } = require('../../utils/request')
const util = require('../../utils/util')

const SCOPES = [
  { key: 'todo', name: '待办' },
  { key: 'started', name: '我发起的' },
  { key: 'all', name: '全部' }
]

Page({
  data: {
    scopes: SCOPES,
    activeScope: 0,
    list: [],
    pageNo: 1,
    pageSize: 20,
    total: 0,
    loading: false,
    finished: false,
    defMap: {}, // processKey → processName
    showStart: false,
    bizTypes: [],
    defs: [],
    startBizType: '',
    startBizName: '',
    startDefs: [],
    startDefIndex: 0,
    startOptions: [],
    startOptionsIndex: 0,
    startAutoNo: true,
    starting: false
  },

  onShow() {
    if (!wx.getStorageSync('ptidss_token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.loadDefs()
    this.reload()
  },

  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    const { pageNo, total, list, loading, finished } = this.data
    if (loading || finished || list.length >= total) return
    this.setData({ pageNo: pageNo + 1 })
    this.load(true)
  },

  onScope(e) {
    this.setData({ activeScope: Number(e.currentTarget.dataset.index) })
    this.reload()
  },

  /** 流程定义缓存（processKey → 流程名；无 menu:flow 权限时容错跳过） */
  loadDefs() {
    request({ url: '/flow/definitions' })
      .then((defs) => {
        const map = {}
        ;(defs || []).forEach((d) => {
          map[d.processKey] = d.processName
        })
        this.setData({ defMap: map, defs: defs || [] })
      })
      .catch(() => {})
  },

  reload() {
    this.setData({ pageNo: 1, finished: false })
    return this.load(false)
  },

  load(append) {
    const { activeScope, pageNo, pageSize, loading } = this.data
    if (loading) return Promise.resolve()
    this.setData({ loading: true })
    const scope = SCOPES[activeScope].key
    return request({
      url: '/flow/instances',
      data: { scope: scope, pageNo: pageNo, pageSize: pageSize }
    })
      .then((page) => {
        const items = ((page && page.list) || []).map((f) => ({
          instanceId: f.instanceId,
          processKey: f.processKey,
          processName: this.data.defMap[f.processKey] || f.processKey,
          bizType: f.bizType,
          bizTypeName: util.bizTypeName(f.bizType),
          bizId: f.bizId,
          status: f.status,
          statusName: util.flowStatusName(f.status),
          currentNode: f.currentNode,
          currentAssignee: f.currentAssignee,
          startBy: f.startBy,
          startTime: util.formatDateTime(f.startTime)
        }))
        this.setData({
          list: append ? this.data.list.concat(items) : items,
          total: (page && page.total) || 0,
          finished: !items.length || items.length < pageSize
        })
      })
      .finally(() => this.setData({ loading: false }))
  },

  onItemTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/flow-detail/flow-detail?id=' + id })
  },

  // ---------- 发起流程 ----------

  openStart() {
    request({ url: '/flow/biz-types' })
      .then((types) => {
        if (!types || !types.length) {
          wx.showToast({ title: '无可用业务类型', icon: 'none' })
          return
        }
        this.setData({ showStart: true, bizTypes: types, startBizType: types[0].bizType, startBizName: types[0].bizName })
        this.onStartBizChange(types[0].bizType)
      })
      .catch(() => {})
  },

  closeStart() {
    this.setData({ showStart: false })
  },

  /** 业务类型切换 → 过滤流程定义 + 加载已有单号 */
  onStartBizChange(bizType) {
    const { defs } = this.data
    const matched = (defs || []).filter((d) => d.bizType === bizType)
    this.setData({
      startDefs: matched,
      startDefIndex: 0,
      startDefName: matched.length ? matched[0].processName : '',
      startBizType: bizType
    })
    request({ url: '/flow/biz-options', data: { bizType: bizType } })
      .then((resp) => {
        this.setData({ startOptions: (resp && resp.options) || [], startOptionsIndex: 0 })
      })
      .catch(() => {})
  },

  onStartBizPicker(e) {
    const idx = Number(e.detail.value)
    const t = this.data.bizTypes[idx]
    if (!t) return
    this.onStartBizChange(t.bizType)
    this.setData({ startBizName: t.bizName })
  },

  onStartDefPicker(e) {
    const idx = Number(e.detail.value)
    const def = this.data.startDefs[idx]
    if (!def) return
    this.setData({ startDefIndex: idx, startDefName: def.processName })
  },

  onStartOptionPicker(e) {
    this.setData({ startOptionsIndex: Number(e.detail.value) })
  },

  onAutoNoSwitch(e) {
    this.setData({ startAutoNo: e.detail.value })
  },

  /** 提交发起（bizId 为空时服务端按业务类型自动生成单号，V2.4 规则） */
  doStart() {
    const { startDefs, startDefIndex, startBizType, startOptions, startOptionsIndex, startAutoNo, starting } = this.data
    if (starting) return
    const def = startDefs[startDefIndex]
    if (!def) {
      wx.showToast({ title: '该业务类型无可用流程定义，请先在审批流管理页配置', icon: 'none' })
      return
    }
    const option = startOptions[startOptionsIndex]
    const bizId = startAutoNo ? '' : (option ? option.value : '')
    if (!startAutoNo && !bizId) {
      wx.showToast({ title: '请选择业务单号或勾选自动生成', icon: 'none' })
      return
    }
    this.setData({ starting: true })
    request({
      url: '/flow/start',
      method: 'POST',
      data: { processKey: def.processKey, bizId: bizId, variables: { source: 'miniapp' } }
    })
      .then((resp) => {
        wx.showToast({ title: '流程已发起', icon: 'success' })
        this.setData({ showStart: false })
        this.reload()
      })
      .catch(() => {})
      .finally(() => this.setData({ starting: false }))
  }
})
