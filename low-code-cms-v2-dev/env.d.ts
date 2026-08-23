/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_APP_TITLE: string;
    // 更多环境变量...
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}

// @wangeditor/editor-for-vue 5.x 未提供类型声明
declare module "@wangeditor/editor-for-vue";
