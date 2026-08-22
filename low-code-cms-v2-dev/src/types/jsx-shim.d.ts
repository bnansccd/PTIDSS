// vue-tsc 0.40（Volar 1）兼容层：Vue 3.5 移除旧式全局 JSX 声明（改由 vue/jsx-runtime 提供），
// 而 vue-tsc 0.40.x 对 .vue 模板仍按 JSX namespace 检查 → 无兜底声明时报 TS7026。
// 本 shim 提供宽松 IntrinsicElements 兜底（模板元素类型不做精细化校验），
// script/模板属性仍走 Element Plus 全局组件类型检查（"types": ["element-plus/global"]）。
declare namespace JSX {
    interface IntrinsicElements {
        [elem: string]: any;
    }
}
