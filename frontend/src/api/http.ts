/**
 * Axios 封装（对齐 OpenAPI V1.1 契约）：
 * - baseURL 取 VITE_API_BASE（开发期指向本地 9080/ptidss）
 * - 请求：Bearer Token + X-Region-Code（多省路由头，见《多省 region 路由与数据 隔离方案》)
 * - 响应：解包 ApiResponse，code !== 0 抛业务错误；14001 先尝试 /auth/refresh 续期重放，失败再登出
 */
import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { ApiResponse } from './types'
import { useAuthStore } from '@/stores/auth'
import { useRegionStore } from '@/stores/region'
import router from '@/router'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE as string,
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  const region = useRegionStore()
  if (auth.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`
  }
  if (region.currentRegion) {
    config.headers['X-Region-Code'] = region.currentRegion
  }
  return config
})

/** 令牌续期重试互斥标记（避免并发 14001 时多次 refresh） */
let refreshing = false

http.interceptors.response.use(
  async (response) => {
    // V2.4 滑动续期：后端剩余有效期不足阈值时重签 JWT 并经 X-New-Token 响应头回传，
    // 前端即时替换本地令牌（localStorage），避免长时间操作后"刷新即退出登录"
    const newToken = response.headers['x-new-token']
    if (newToken) {
      useAuthStore().setToken(String(newToken))
    }
    const body = response.data as ApiResponse
    if (body && typeof body.code === 'number' && body.code !== 0) {
      if (body.code === 14001 && !refreshing) {
        const auth = useAuthStore()
        if (auth.accessToken) {
          refreshing = true
          try {
            const newToken = await auth.refreshToken()
            if (newToken) {
              // 用新令牌重放原请求
              const retry = await http.request<ApiResponse>({
                ...response.config,
                headers: { ...response.config.headers, Authorization: `Bearer ${newToken}` },
              })
              return retry as AxiosResponse
            }
          } catch {
            // refresh 失败，走登出
          } finally {
            refreshing = false
          }
        }
        forceLogout()
      }
      return Promise.reject(new Error(body.message || `业务错误 ${body.code}`))
    }
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      forceLogout()
    }
    return Promise.reject(error)
  },
)

/** 清空登录态并跳登录（带 redirect 回跳） */
function forceLogout() {
  const auth = useAuthStore()
  const region = useRegionStore()
  auth.logout()
  region.reset()
  router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
}

/** 契约请求：返回 data 负载（解包 ApiResponse） */
export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const res = await http.request<ApiResponse<T>>(config)
  return res.data.data as T
}
