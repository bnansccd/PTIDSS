// 行情速览（Tab1，PRD FR-PL-05 ①）：现货 96 点曲线 / 供需形势 / 中长期价格 / 量价热力图
const app = getApp()
const { request } = require('../../utils/request')
const util = require('../../utils/util')

Page({
  data: {
    region: '',
    date: util.today(),
    spotStage: 'day_ahead', // day_ahead/real_time
    spotStats: { avg: '-', max: '-', min: '-', current: '-' },
    spotPoints: [],
    supply: { loadValue: '-', availableCapacity: '-', renewableOutput: '-' },
    midlongVariety: 'weekly', // weekly/monthly/annual
    midlongList: [],
    heatDates: [],
    heatGrid: [],
    loading: true
  },

  onShow() {
    if (!wx.getStorageSync('ptidss_token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.setData({ region: wx.getStorageSync('ptidss_region') || '' })
    this.loadAll()
  },

  onPullDownRefresh() {
    this.loadAll().finally(() => wx.stopPullDownRefresh())
  },

  onDateChange(e) {
    this.setData({ date: e.detail.value })
    this.loadAll()
  },

  onSpotStage(e) {
    this.setData({ spotStage: e.currentTarget.dataset.stage })
    this.loadSpot()
  },

  onMidlongVariety(e) {
    this.setData({ midlongVariety: e.currentTarget.dataset.variety })
    this.loadMidlong()
  },

  loadAll() {
    this.setData({ loading: true })
    return Promise.all([this.loadSpot(), this.loadSupply(), this.loadMidlong(), this.loadHeatmap()])
      .catch(() => {})
      .finally(() => this.setData({ loading: false }))
  },

  /** 现货价格（96 点，15 分钟粒度） */
  loadSpot() {
    const { date, spotStage } = this.data
    return request({
      url: '/market/price/spot',
      data: {
        marketType: 'intra_province',
        stage: spotStage,
        startAt: date + ' 00:00:00',
        endAt: date + ' 23:59:59'
      }
    })
      .then((points) => {
        const prices = (points || []).map((p) => Number(p.price))
        const stats = { avg: '-', max: '-', min: '-', current: '-' }
        if (prices.length) {
          stats.avg = util.fixed2(prices.reduce((a, b) => a + b, 0) / prices.length)
          stats.max = util.fixed2(Math.max.apply(null, prices))
          stats.min = util.fixed2(Math.min.apply(null, prices))
          stats.current = util.fixed2(prices[prices.length - 1])
        }
        this.setData({ spotStats: stats, spotPoints: points || [] })
        this.drawPriceChart(points || [])
      })
  },

  /** 供需形势（负荷/可用能力/新能源，取最新时点） */
  loadSupply() {
    const { date } = this.data
    return request({
      url: '/market/supply-demand',
      data: { startAt: date + ' 00:00:00', endAt: date + ' 23:59:59' }
    })
      .then((list) => {
        const last = (list || []).length ? list[list.length - 1] : null
        this.setData({
          supply: last
            ? {
                loadValue: util.formatWan(last.loadValue) + ' 万kW',
                availableCapacity: util.formatWan(last.availableCapacity) + ' 万kW',
                renewableOutput: util.formatWan(last.renewableOutput) + ' 万kW'
              }
            : { loadValue: '-', availableCapacity: '-', renewableOutput: '-' }
        })
      })
  },

  /** 中长期成交价格（周/月/年粒度） */
  loadMidlong() {
    const { midlongVariety } = this.data
    return request({ url: '/market/price/midlong', data: { variety: midlongVariety } })
      .then((list) => {
        const items = (list || []).slice(0, 12).map((p) => ({
          ts: util.formatDate(p.ts),
          price: util.fixed2(p.price),
          volume: Number(p.volume).toLocaleString()
        }))
        this.setData({ midlongList: items })
      })
  },

  /** 量价热力图（近 7 天 × 24 时段，每 4 个 15 分钟点聚合为 1 时段） */
  loadHeatmap() {
    const end = new Date()
    const start = new Date(end.getTime() - 6 * 86400000)
    return request({
      url: '/market/heatmap',
      data: { startDate: util.formatDate(start), endDate: util.formatDate(end) }
    })
      .then((data) => {
        const dates = (data && data.dates) || []
        const points = (data && data.points) || []
        const dayCount = Math.min(dates.length, 7)
        const grid = []
        let heatMin = Infinity
        let heatMax = -Infinity
        for (let d = 0; d < dayCount; d++) {
          const row = []
          for (let h = 0; h < 24; h++) {
            let sum = 0
            let n = 0
            for (let j = h * 4; j < h * 4 + 4; j++) {
              const idx = d * 96 + j
              if (idx < points.length) {
                sum += points[idx]
                n++
              }
            }
            const v = n ? Math.round(sum / n) : 0
            row.push(v)
            if (v < heatMin) heatMin = v
            if (v > heatMax) heatMax = v
          }
          grid.push(row)
        }
        this.setData({
          heatDates: dates.slice(0, dayCount).map((d) => d.substring(5)),
          heatGrid: grid,
          heatMin: heatMin === Infinity ? 0 : heatMin,
          heatMax: heatMax === -Infinity ? 0 : heatMax
        })
      })
  },

  /** canvas 2d 绘制 96 点价格曲线（零依赖） */
  drawPriceChart(points) {
    if (!points || !points.length) return
    wx.createSelectorQuery()
      .select('#priceChart')
      .fields({ node: true, size: true })
      .exec((res) => {
        if (!res || !res[0] || !res[0].node) return
        const canvas = res[0].node
        const ctx = canvas.getContext('2d')
        const width = res[0].width
        const height = res[0].height
        const dpr = wx.getSystemInfoSync().pixelRatio
        canvas.width = width * dpr
        canvas.height = height * dpr
        ctx.scale(dpr, dpr)

        const prices = points.map((p) => Number(p.price))
        let min = Math.min.apply(null, prices)
        let max = Math.max.apply(null, prices)
        const pad = Math.max((max - min) * 0.15, 1)
        min -= pad
        max += pad
        const n = prices.length
        const padL = 8
        const padR = 8
        const padT = 16
        const padB = 24
        const plotW = width - padL - padR
        const plotH = height - padT - padB
        const x = (i) => padL + (i / (n - 1)) * plotW
        const y = (v) => padT + (1 - (v - min) / (max - min)) * plotH

        ctx.clearRect(0, 0, width, height)
        // 网格（4 横线）
        ctx.strokeStyle = '#eef0f4'
        ctx.lineWidth = 1
        for (let g = 0; g <= 4; g++) {
          const gy = padT + (g / 4) * plotH
          ctx.beginPath()
          ctx.moveTo(padL, gy)
          ctx.lineTo(width - padR, gy)
          ctx.stroke()
        }
        // 面积填充
        ctx.beginPath()
        ctx.moveTo(x(0), y(prices[0]))
        for (let i = 1; i < n; i++) ctx.lineTo(x(i), y(prices[i]))
        ctx.lineTo(x(n - 1), height - padB)
        ctx.lineTo(x(0), height - padB)
        ctx.closePath()
        const grad = ctx.createLinearGradient(0, padT, 0, height - padB)
        grad.addColorStop(0, 'rgba(31, 111, 184, 0.35)')
        grad.addColorStop(1, 'rgba(31, 111, 184, 0.02)')
        ctx.fillStyle = grad
        ctx.fill()
        // 折线
        ctx.beginPath()
        ctx.moveTo(x(0), y(prices[0]))
        for (let i = 1; i < n; i++) ctx.lineTo(x(i), y(prices[i]))
        ctx.strokeStyle = '#1f6fb8'
        ctx.lineWidth = 2
        ctx.stroke()
        // 起止时间标签
        ctx.fillStyle = '#7a8599'
        ctx.font = '10px sans-serif'
        ctx.fillText(points[0].ts.substring(11, 16), padL, height - 8)
        ctx.fillText(points[n - 1].ts.substring(11, 16), width - padR - 40, height - 8)
      })
  }
})
