// 复盘报告详情：三层归因（预测/决策/执行）+ 策略评估 + 改进建议，支持分享
const { request } = require('../../utils/request')
const util = require('../../utils/util')

/** 对象/数组/字符串 → 文本展示 */
function textOf(v) {
  if (!v) return ''
  if (typeof v === 'string') return v
  if (Array.isArray(v)) return v.map(textOf).filter(Boolean).join('；')
  if (typeof v === 'object') {
    return Object.keys(v)
      .map((k) => k + '：' + textOf(v[k]))
      .filter(Boolean)
      .join('；')
  }
  return String(v)
}

/** 摘要对象 → 键值行 */
function kvRows(s) {
  if (!s || typeof s !== 'object' || Array.isArray(s)) return []
  return Object.keys(s).map((k) => ({ key: k, value: textOf(s[k]) }))
}

Page({
  data: {
    reportId: '',
    detail: null,
    summaryRows: [],
    layers: [],
    strategyEvalRows: [],
    suggestions: [],
    loaded: false
  },

  onLoad(options) {
    this.setData({ reportId: options.id || '' })
    this.load()
  },

  onPullDownRefresh() {
    this.load().finally(() => wx.stopPullDownRefresh())
  },

  load() {
    const { reportId } = this.data
    if (!reportId) return Promise.resolve()
    return request({ url: '/review/reports/' + reportId })
      .then((d) => {
        const layers = ((d && d.deviationAnalysis && d.deviationAnalysis.layers) || []).map((l) => ({
          layer: l.layer,
          layerName: util.layerName(l.layer),
          items: (l.items || []).map((it) => ({
            item: it.item,
            value: it.value,
            impactAmount: it.impactAmount,
            reason: it.reason,
            direction: it.direction,
            directionName: util.directionName(it.direction)
          }))
        }))
        this.setData({
          detail: {
            reportType: d.reportType,
            reportTypeName: util.reportTypeName(d.reportType),
            period: (util.formatDate(d.periodStart) || '') + ' ~ ' + (util.formatDate(d.periodEnd) || ''),
            status: d.status,
            statusName: util.reportStatusName(d.status)
          },
          summaryRows: kvRows(d.summary),
          layers: layers,
          strategyEvalRows: kvRows(d.strategyEval),
          suggestions: textOf(d.suggestions).split('；').filter(Boolean),
          loaded: true
        })
      })
  },

  /** 查看与分享（PRD FR-PL-05 ④） */
  onShareAppMessage() {
    const d = this.data.detail || {}
    return {
      title: '复盘报告（' + d.reportTypeName + '）' + d.period,
      path: '/pages/review/review'
    }
  }
})
