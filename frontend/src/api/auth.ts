import { request } from './http'
import type { CaptchaResult, CurrentUser, LoginResult } from './types'

export interface LoginPayload {
  username: string
  password: string
  captchaKey?: string
  captchaCode?: string
}

/** GET /auth/captcha 获取验证码（键 + 图片 Base64） */
export function getCaptcha() {
  return request<CaptchaResult>({ url: '/auth/captcha', method: 'get' })
}

/** POST /auth/login */
export function login(payload: LoginPayload) {
  return request<LoginResult>({ url: '/auth/login', method: 'post', data: payload })
}

/** POST /auth/refresh */
export function refresh(refreshToken: string) {
  return request<{ accessToken: string; expiresIn: number }>({
    url: '/auth/refresh',
    method: 'post',
    data: { refreshToken },
  })
}

/** GET /auth/current */
export function getCurrent() {
  return request<CurrentUser>({ url: '/auth/current', method: 'get' })
}

/** POST /auth/logout */
export function logout() {
  return request<null>({ url: '/auth/logout', method: 'post' })
}
