// 解决ts 中引入图片 es-lint 报错误提示
declare module "*.png" {
    const png: string;
    export default png;
}
