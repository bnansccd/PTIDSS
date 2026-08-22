/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** 后端/API 基础地址（开发指向 Prism Mock，生产指向网关） */
  readonly VITE_API_BASE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
