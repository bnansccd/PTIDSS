import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 开发期直连 Mock（VITE_API_BASE=http://localhost:4010）；
// 后端就绪后切换 VITE_API_BASE 为网关地址，或将 /api 代理到真实后端。
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      // 备用代理：后端就绪后打开注释并指向真实服务
      // '/api': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
  preview: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      // 本地预览 dist 时反代后端（生产由 nginx 同源托管，无需本配置）
      '/ptidss': { target: 'http://localhost:9080', changeOrigin: true },
    },
  },
})
