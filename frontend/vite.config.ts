import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 端口规范（V3.1）：前端唯一入口 5173（dev/preview 一致），后端 9080，数据库 5432。
// 开发期浏览器只访问前端 5173 单端口：/ptidss 由 vite 代理转发到后端 9080（同源，无跨域）；
// 生产由 nginx 同源托管（VITE_API_BASE=/ptidss），前端对外仅暴露一个端口。
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
      // 开发代理：/ptidss → 后端 9080（与 preview 一致）
      '/ptidss': { target: 'http://localhost:9080', changeOrigin: true },
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
