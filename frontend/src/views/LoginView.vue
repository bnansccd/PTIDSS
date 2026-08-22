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
          <input v-model="captchaCode" placeholder="验证码" style="flex: 1" required />
          <img
            :src="captcha?.image || undefined"
            alt="验证码"
            title="点击刷新"
            class="captcha-img"
            @click="refreshCaptcha"
          />
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
const loading = ref(false)
const error = ref('')

async function refreshCaptcha() {
  captcha.value = await getCaptcha()
  captchaCode.value = ''
}

async function onSubmit() {
  error.value = ''
  if (!captcha.value) {
    error.value = '验证码加载失败，请点击图片刷新'
    return
  }
  loading.value = true
  try {
    await auth.login(username.value, password.value, {
      captchaKey: captcha.value.captchaKey,
      captchaCode: captchaCode.value,
    })
    region.init(auth.user?.regions ?? [])
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch (e) {
    error.value = e instanceof Error ? e.message : '登录失败'
    // 验证码一次性消费：失败即刷新
    await refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(refreshCaptcha)
</script>
