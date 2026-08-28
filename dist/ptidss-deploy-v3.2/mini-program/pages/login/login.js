// 登录页：账号密码登录；验证码按服务端开关自适应
// （GET /auth/captcha 加载验证码 → POST /auth/login {username, password, captchaKey, captchaCode}）
const app = getApp()
const { request, BASE_URL } = require('../../utils/request')

Page({
  data: {
    baseUrl: BASE_URL,
    username: '',
    password: '',
    showCaptcha: false,
    captchaKey: '',
    captchaImage: '',
    captchaCode: '',
    loading: false
  },

  onLoad() {
    // 未登录直接展示；已登录跳转行情页
    const token = wx.getStorageSync('ptidss_token')
    if (token) {
      wx.switchTab({ url: '/pages/market/market' })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [field]: e.detail.value })
  },

  /** 加载验证码（服务端开启验证码时使用；默认折叠） */
  loadCaptcha() {
    request({ url: '/auth/captcha' })
      .then((data) => {
        this.setData({ showCaptcha: true, captchaKey: data.captchaKey, captchaImage: data.image, captchaCode: '' })
      })
      .catch(() => {})
  },

  /** 登录：未填验证码时若服务端要求（captcha.enabled=true）会提示，再加载验证码重试 */
  doLogin() {
    const { username, password, showCaptcha, captchaKey, captchaCode, loading } = this.data
    if (loading) return
    if (!username || !password) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' })
      return
    }
    if (showCaptcha && !captchaCode) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    const body = { username, password }
    if (showCaptcha) {
      body.captchaKey = captchaKey
      body.captchaCode = captchaCode
    }
    request({ url: '/auth/login', method: 'POST', data: body })
      .then((data) => {
        app.setSession(data)
        wx.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => wx.switchTab({ url: '/pages/market/market' }), 500)
      })
      .catch((err) => {
        // 服务端开启验证码但未提供 → 自动加载验证码
        const msg = (err && err.message) || ''
        if (!showCaptcha && msg.indexOf('验证码') >= 0) {
          this.loadCaptcha()
        }
      })
      .finally(() => this.setData({ loading: false }))
  }
})
