// 个人中心（Tab5，PRD FR-PL-05 ⑤）：账户信息 /auth/current、区域切换、未读统计、订阅设置、退出
const app = getApp()
const { request, BASE_URL } = require('../../utils/request')

const SUB_TYPES = [
  { key: 'market_alert', name: '市场预警', desc: '价格异动、供需突变' },
  { key: 'forecast_summary', name: '预测摘要', desc: '日前/负荷/新能源预测' },
  { key: 'decision_todo', name: '决策待办', desc: '方案确认、审批提醒' },
  { key: 'settlement_diff', name: '结算差异', desc: '结算异常、差异工单' },
  { key: 'assess_reminder', name: '考核提醒', desc: '考核指标、申诉窗口' }
]

Page({
  data: {
    baseUrl: BASE_URL,
    user: null,
    avatarText: '',
    rolesText: '',
    regions: [],
    regionIndex: 0,
    unreadTotal: 0,
    subs: SUB_TYPES.map((s) => Object.assign({}, s, { on: true }))
  },

  onShow() {
    if (!wx.getStorageSync('ptidss_token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.loadUser()
    this.loadUnread()
    this.loadSubs()
  },

  onPullDownRefresh() {
    Promise.all([this.loadUser(), this.loadUnread()]).finally(() => wx.stopPullDownRefresh())
  },

  /** 当前用户（角色/区域/权限） */
  loadUser() {
    return request({ url: '/auth/current' })
      .then((u) => {
        const regions = u.regions || []
        const current = app.globalData.region || u.currentRegion || ''
        let regionIndex = 0
        regions.forEach((r, i) => {
          if (r === current) regionIndex = i
        })
        this.setData({
          user: {
            username: u.username,
            displayName: u.displayName || u.username,
            orgCode: u.orgCode || '-'
          },
          avatarText: (u.displayName || u.username).substring(0, 1),
          rolesText: (u.roles || []).join(' / '),
          regions: regions,
          regionIndex: regionIndex
        })
      })
  },

  /** 未读消息数（预警推送入口角标） */
  loadUnread() {
    return request({ url: '/message/list', data: { unreadOnly: true, pageNo: 1, pageSize: 1 } })
      .then((page) => this.setData({ unreadTotal: (page && page.total) || 0 }))
      .catch(() => {})
  },

  /** 订阅设置（本地偏好；真实微信订阅消息推送为上线后动作，见差异报告 D3） */
  loadSubs() {
    const saved = wx.getStorageSync('ptidss_subs')
    if (!saved) return
    const map = {}
    saved.forEach((s) => {
      map[s.key] = s.on
    })
    this.setData({ subs: SUB_TYPES.map((s) => Object.assign({}, s, { on: map[s.key] !== false })) })
  },

  onSubSwitch(e) {
    const idx = Number(e.currentTarget.dataset.index)
    const subs = this.data.subs
    subs[idx].on = e.detail.value
    this.setData({ subs: subs })
    wx.setStorageSync(
      'ptidss_subs',
      subs.map((s) => ({ key: s.key, on: s.on }))
    )
    wx.showToast({ title: '订阅设置已保存', icon: 'none' })
  },

  /** 区域切换（多省路由 X-Region-Code，对齐 C4/C5） */
  onRegionChange(e) {
    const idx = Number(e.detail.value)
    const region = this.data.regions[idx]
    if (!region) return
    app.setRegion(region)
    this.setData({ regionIndex: idx })
    wx.showToast({ title: '区域已切换为 ' + region, icon: 'none' })
    this.loadUser()
  },

  /** 跳转预警推送 */
  goMessage() {
    wx.switchTab({ url: '/pages/message/message' })
  },

  /** 退出登录：POST /auth/logout 后清除本地会话 */
  doLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定退出当前账号吗？',
      success: (res) => {
        if (!res.confirm) return
        request({ url: '/auth/logout', method: 'POST' })
          .catch(() => {})
          .finally(() => {
            app.clearSession()
            wx.reLaunch({ url: '/pages/login/login' })
          })
      }
    })
  }
})
