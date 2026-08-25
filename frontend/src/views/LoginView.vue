<template>
  <div class="login-page">
    <div class="login-card">
      <h1>电力交易智能辅助决策系统</h1>
      <div class="sub">PTIDSS · 对接后端服务（OpenAPI V1.1）</div>
      <form @submit.prevent="onSubmit">
        <div class="form-row">
          <input v-model="username" placeholder="账号（如 trader01 / admin）" autocomplete="username" required />
        </div>
        <div class="form-row">
          <input v-model="password" type="password" placeholder="密码（如 Ptidss@2026）" autocomplete="current-password" required />
        </div>
        <div class="form-row">
          <input
            v-model="captchaCode"
            placeholder="验证码"
            style="flex: 1"
            autocomplete="off"
            maxlength="6"
            @keyup.enter="onSubmit"
          />
          <div
            class="captcha-box"
            :title="captcha ? '点击刷新' : '点击获取验证码'"
            @click="refreshCaptcha"
          >
            <img
              v-if="captcha && captcha.image"
              :src="captcha.image"
              alt="验证码"
              @error="onImageError"
            />
            <span v-else-if="captchaLoading">加载中…</span>
            <span v-else>点击获取</span>
          </div>
        </div>
        <div v-if="error" class="muted" style="color: #b91c1c; margin-bottom: 10px">{{ error }}</div>
        <button class="btn btn-primary" type="submit" :disabled="loading">{{ loading ? '登录中…' : '登 录' }}</button>
      </form>
      <div class="muted" style="margin-top: 14px; font-size: 12px">
        验证码 5 分钟有效、一次性消费；登录失败请点击验证码图片刷新后重试。
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCaptcha } from '@/api/auth'
import type { CaptchaResult } from '@/api/types'
import { useAuthStore } from '@/stores/auth'
import { useRegionStore } from '@/stores/region'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const region = useRegionStore()

const username = ref('trader01')
const password = ref('Ptidss@2026')
const captchaCode = ref('')
const captcha = ref<CaptchaResult | null>(null)
const captchaLoading = ref(false)
const loading = ref(false)
const error = ref('')

// 验证码图片解码失败自动重试上限（防止代理异常/网络抖动时无限刷新）
const CAPTCHA_MAX_RETRY = 3
let captchaRetry = 0

// 登出/会话失效跳转提示（forceLogout 经 ?msg= 传入），登录页停留时展示
const sessionMsg = (route.query.msg as string) || ''
if (sessionMsg) {
  error.value = sessionMsg
}

async function refreshCaptcha() {
  captchaLoading.value = true
  captchaRetry = 0
  try {
    captcha.value = await getCaptcha()
    captchaCode.value = ''
  } catch (e) {
    captcha.value = null
    error.value = e instanceof Error ? `验证码加载失败：${e.message}` : '验证码加载失败，请点击重试'
  } finally {
    captchaLoading.value = false
  }
}

// 图片解码失败（如代理返回 HTML/响应截断）：自动刷新重试，最多 3 次后提示
function onImageError() {
  if (captchaRetry < CAPTCHA_MAX_RETRY) {
    captchaRetry += 1
    captcha.value = null
    refreshCaptcha()
  } else {
    captcha.value = null
    error.value = '验证码图片加载失败，请检查网络后点击图片刷新'
  }
}

async function onSubmit() {
  error.value = ''
  if (captchaLoading.value || loading.value) return
  if (!captcha.value) {
    error.value = '验证码尚未加载，请点击图片获取'
    refreshCaptcha()
    return
  }
  if (!captchaCode.value.trim()) {
    error.value = '请输入验证码'
    return
  }
  loading.value = true
  try {
    await auth.login(username.value, password.value, {
      captchaKey: captcha.value.captchaKey,
      captchaCode: captchaCode.value.trim(),
    })
    region.init(auth.user?.regions ?? [])
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch (e) {
    error.value = e instanceof Error ? e.message : '登录失败'
    // 验证码一次性消费：失败即刷新（静默，不覆盖登录错误提示）
    refreshCaptcha().catch(() => {
      /* 验证码刷新失败时保留登录错误提示 */
    })
  } finally {
    loading.value = false
  }
}

onMounted(refreshCaptcha)
</script>
