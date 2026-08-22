// PTIDSS 移动端请求封装（对齐 Web 端 request.ts）：
// - context-path：/ptidss（BASE_URL 已含）
// - 认证：Authorization: Bearer {accessToken}
// - 多省路由：X-Region-Code（登录响应 currentRegion，个人中心可切换）
// - 统一响应 {code, message, data, traceId}：code=0 成功；14001 未认证跳登录
// - 滑动续期：响应头 X-New-Token 时自动替换本地令牌（V2.4 刷新即退出修复）
// 生产部署：将 BASE_URL 改为 https 域名，并在微信公众平台配置 request 合法域名。

const BASE_URL = 'http://localhost:9080/ptidss'

const TOKEN_KEY = 'ptidss_token'
const REGION_KEY = 'ptidss_region'

function request(options) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync(TOKEN_KEY) || ''
    const region = wx.getStorageSync(REGION_KEY) || ''
    const header = { 'Content-Type': 'application/json' }
    if (token) header.Authorization = 'Bearer ' + token
    if (region) header['X-Region-Code'] = region

    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: header,
      success(res) {
        const body = res.data || {}
        if (body.code === 0) {
          // 滑动续期：令牌剩余不足阈值时服务端重签并回传新令牌
          const newToken = res.header && res.header['X-New-Token']
          if (newToken) wx.setStorageSync(TOKEN_KEY, newToken)
          resolve(body.data, body)
        } else if (body.code === 14001) {
          wx.removeStorageSync(TOKEN_KEY)
          wx.showToast({ title: '登录已失效，请重新登录', icon: 'none' })
          setTimeout(() => {
            wx.reLaunch({ url: '/pages/login/login' })
          }, 600)
          reject(body)
        } else {
          wx.showToast({ title: body.message || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常，请检查服务地址', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  BASE_URL,
  request
}
