// 复盘摘要（Tab4，PRD FR-PL-05 ④）：复盘报告列表 /review/reports（类型筛选）+ 查看与分享
const { request } = require('../../utils/request')
const util = require('../../utils/util')

const TABS = [
  { key: '', name: '全部' },
  { key: 'weekly', name: '周报' },
  { key: 'monthly', name: '月报' },
  { key: 'special', name: '专项' }
]

/** 摘要对象 → 短文本（列表卡片展示） */
function summaryText(s) {
  if (!s) return ''
  if (typeof s === 'string') return s.length > 60 ? s.substring(0, 60) + '…' : s
  if (Array.isArray(s)) return s.length ? JSON.stringify(s).substring(0, 60) : ''
  const keys = Object.keys(s)
  return keys.length
    ? keys
        .slice(0, 4)
        .map((k) => k + ' ' + (typeof s[k] === 'object' ? JSON.stringify(s[k]) : s[k]))
        .join('；')
        .substring(0, 60)
    : ''
}

Page({
  data: {
    tabs: TABS,
    activeTab: 0,
    list: [],
    loading: false,
    hasLoaded: false
  },

  onShow() {
    if (!wx.getStorageSync('ptidss_token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.reload()
  },

  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh())
  },

  onTab(e) {
    this.setData({ activeTab: Number(e.currentTarget.dataset.index) })
    this.reload()
  },

  reload() {
    const { activeTab, loading } = this.data
    if (loading) return Promise.resolve()
    this.setData({ loading: true })
    const tab = TABS[activeTab]
    return request({
      url: '/review/reports',
      data: { reportType: tab.key || undefined }
    })
      .then((list) => {
        const items = (list || []).map((r) => ({
          id: r.id,
          reportType: r.reportType,
          reportTypeName: util.reportTypeName(r.reportType),
          period: (util.formatDate(r.periodStart) || '') + ' ~ ' + (util.formatDate(r.periodEnd) || ''),
          status: r.status,
          statusName: util.reportStatusName(r.status),
          summary: summaryText(r.summary),
          createdAt: util.formatDateTime(r.createdAt)
        }))
        this.setData({ list: items, hasLoaded: true })
      })
      .finally(() => this.setData({ loading: false }))
  },

  onItemTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/review-detail/review-detail?id=' + id })
  },

  /** 分享当前复盘摘要（PRD FR-PL-05 ④：查看与分享） */
  onShareAppMessage() {
    return {
      title: '电力交易智能复盘摘要',
      path: '/pages/review/review'
    }
  }
})
