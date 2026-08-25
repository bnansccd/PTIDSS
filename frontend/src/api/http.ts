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
    // 非契约响应（如代理失效时返回的 SPA HTML / 网关错误页）：明确报错而非静默 undefined，
    // 避免验证码等组件拿到空值导致界面空白且无法定位问题
    if (!body || typeof body !== 'object' || typeof body.code !== 'number') {
      return Promise.reject(new Error('服务响应格式异常（请确认后端 9080 可用且代理正常）'))
    }
    if (body.code !== 0) {
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
        forceLogout('登录状态已失效，请重新登录')
      }
      return Promise.reject(new Error(body.message || `业务错误 ${body.code}`))
    }
    return response
  },
  async (error) => {
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      // X-New-Token 丢失/令牌过期等极端场景：本地令牌失效但后端会话可能仍在，
      // 先尝试一次续期并用新令牌重放原请求，成功则用户无感恢复，失败才登出
      if (auth.accessToken && !refreshing) {
        refreshing = true
        try {
          const newToken = await auth.refreshToken()
          if (newToken) {
            return await http.request<ApiResponse>({
              ...error.config,
              headers: { ...error.config.headers, Authorization: `Bearer ${newToken}` },
            })
          }
        } catch {
          // 续期失败，走登出
        } finally {
          refreshing = false
        }
      }
      forceLogout('登录状态已失效，请重新登录')
    }
    return Promise.reject(error)
  },
)

/** 清空登录态并跳登录（带 redirect 回跳与原因提示） */
function forceLogout(msg?: string) {
  const auth = useAuthStore()
  const region = useRegionStore()
  auth.logout()
  region.reset()
  router.push({
    name: 'login',
    query: {
      redirect: router.currentRoute.value.fullPath,
      ...(msg ? { msg } : {}),
    },
  })
}

/** 契约请求：返回 data 负载（解包 ApiResponse） */
export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const res = await http.request<ApiResponse<T>>(config)
  return res.data.data as T
}
