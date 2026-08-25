// PTIDSS 移动端小程序（M7 里程碑）：全局逻辑
// 登录态：token + currentRegion 存本地缓存；页面请求统一走 utils/request.js

const TOKEN_KEY = 'ptidss_token'
const REGION_KEY = 'ptidss_region'
const USER_KEY = 'ptidss_user'

App({
  globalData: {
    token: '',
    region: '',
    userInfo: null
  },

  onLaunch() {
    const token = wx.getStorageSync(TOKEN_KEY)
    const region = wx.getStorageSync(REGION_KEY)
    const user = wx.getStorageSync(USER_KEY)
    this.globalData.token = token || ''
    this.globalData.region = region || ''
    this.globalData.userInfo = user || null
  },

  /** 登录成功回写全局态 */
  setSession(loginResult) {
    this.globalData.token = loginResult.accessToken
    this.globalData.region = loginResult.currentRegion || (loginResult.regions && loginResult.regions[0]) || ''
    this.globalData.userInfo = loginResult
    wx.setStorageSync(TOKEN_KEY, this.globalData.token)
    wx.setStorageSync(REGION_KEY, this.globalData.region)
    wx.setStorageSync(USER_KEY, loginResult)
  },

  /** 切换区域（个人中心） */
  setRegion(region) {
    this.globalData.region = region
    wx.setStorageSync(REGION_KEY, region)
  },

  /** 清除登录态（退出/401） */
  clearSession() {
    this.globalData.token = ''
    this.globalData.region = ''
    this.globalData.userInfo = null
    wx.removeStorageSync(TOKEN_KEY)
    wx.removeStorageSync(REGION_KEY)
    wx.removeStorageSync(USER_KEY)
  }
})
