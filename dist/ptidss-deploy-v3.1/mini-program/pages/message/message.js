// 预警推送（Tab2，PRD FR-PL-05 ②）：消息中心 /message/list 分类 + 未读 + 标记已读
// 实时性：轮询 /message/list 实现（≤30s 对齐 NFR-09；真实微信订阅消息为上线后动作）
const { request } = require('../../utils/request')
const util = require('../../utils/util')

const TABS = [
  { key: '', name: '全部' },
  { key: 'market_alert', name: '市场预警' },
  { key: 'forecast_summary', name: '预测摘要' },
  { key: 'decision_todo', name: '决策待办' },
  { key: 'settlement_diff', name: '结算差异' },
  { key: 'assess_reminder', name: '考核提醒' }
]

Page({
  data: {
    tabs: TABS,
    activeTab: 0,
    unreadOnly: false,
    list: [],
    pageNo: 1,
    pageSize: 10,
    total: 0,
    loading: false,
    finished: false,
    detail: null
  },

  onShow() {
    if (!wx.getStorageSync('ptidss_token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.reload()
    this.startPolling()
  },

  onHide() {
    this.stopPolling()
  },

  onUnload() {
    this.stopPolling()
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

  /** 30s 轮询（预警实时性降级方案，对齐 NFR-09 ≤30s） */
  startPolling() {
    this.stopPolling()
    this.pollTimer = setInterval(() => this.reload(true), 30000)
  },

  stopPolling() {
    if (this.pollTimer) {
      clearInterval(this.pollTimer)
      this.pollTimer = null
    }
  },

  onTab(e) {
    this.setData({ activeTab: Number(e.currentTarget.dataset.index) })
    this.reload()
  },

  onUnreadSwitch(e) {
    this.setData({ unreadOnly: e.detail.value })
    this.reload()
  },

  reload(silent) {
    this.setData({ pageNo: 1, finished: false })
    return this.load(false, silent)
  },

  load(append) {
    const { activeTab, unreadOnly, pageNo, pageSize, loading } = this.data
    if (loading) return Promise.resolve()
    this.setData({ loading: true })
    const tab = TABS[activeTab]
    return request({
      url: '/message/list',
      data: {
        msgType: tab.key || undefined,
        unreadOnly: unreadOnly,
        pageNo: pageNo,
        pageSize: pageSize
      }
    })
      .then((page) => {
        const items = ((page && page.list) || []).map((m) => ({
          id: m.id,
          title: m.title,
          content: m.content,
          msgType: m.msgType,
          msgTypeName: util.msgTypeName(m.msgType),
          readStatus: m.readStatus,
          isUnread: m.readStatus === 'unread',
          time: util.formatDateTime(m.createdAt)
        }))
        this.setData({
          list: append ? this.data.list.concat(items) : items,
          total: (page && page.total) || 0,
          finished: !items.length || items.length < pageSize
        })
      })
      .finally(() => this.setData({ loading: false }))
  },

  /** 点击消息：标记已读；决策待办可跳转审批 */
  onItemTap(e) {
    const { id, index } = e.currentTarget.dataset
    const item = this.data.list[index]
    if (item.isUnread) {
      request({ url: '/message/' + id + '/read', method: 'POST' })
        .then(() => {
          this.setData({ ['list[' + index + '].isUnread']: false, ['list[' + index + '].readStatus']: 'read' })
        })
        .catch(() => {})
    }
    if (item.msgType === 'decision_todo') {
      wx.showModal({
        title: '决策待办',
        content: '该消息为决策待办提醒，是否前往审批页查看流程？',
        confirmText: '去审批',
        success: (res) => {
          if (res.confirm) wx.switchTab({ url: '/pages/flow/flow' })
        }
      })
    } else {
      this.setData({ detail: item })
    }
  },

  closeDetail() {
    this.setData({ detail: null })
  }
})
