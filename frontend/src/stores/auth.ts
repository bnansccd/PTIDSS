import { defineStore } from 'pinia'
import * as authApi from '@/api/auth'
import type { CurrentUser } from '@/api/types'

const TOKEN_KEY = 'ptidss_access_token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: localStorage.getItem(TOKEN_KEY) ?? '',
    user: null as CurrentUser | null,
    loaded: false,
  }),
  getters: {
    isAuthenticated: (s) => !!s.accessToken,
    displayName: (s) => s.user?.displayName ?? s.user?.username ?? '',
  },
  actions: {
    setToken(accessToken: string) {
      this.accessToken = accessToken
      localStorage.setItem(TOKEN_KEY, accessToken)
    },
    async login(username: string, password: string, captcha?: { captchaKey: string; captchaCode: string }) {
      const res = await authApi.login({
        username,
        password,
        captchaKey: captcha?.captchaKey,
        captchaCode: captcha?.captchaCode,
      })
      // 契约 LoginResult 无 refreshToken 字段：令牌续期由 /auth/refresh 用当前 accessToken 完成
      this.setToken(res.accessToken)
      await this.loadCurrent()
    },
    async loadCurrent() {
      this.user = await authApi.getCurrent()
      this.loaded = true
    },
    /** 令牌续期：用当前 accessToken 调 /auth/refresh 换新，成功返回新 token，失败返回 null */
    async refreshToken(): Promise<string | null> {
      if (!this.accessToken) return null
      try {
        const res = await authApi.refresh(this.accessToken)
        this.setToken(res.accessToken)
        return res.accessToken
      } catch {
        return null
      }
    },
    logout() {
      this.accessToken = ''
      this.user = null
      this.loaded = false
      localStorage.removeItem(TOKEN_KEY)
    },
  },
})
