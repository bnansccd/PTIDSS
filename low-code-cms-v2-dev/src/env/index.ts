// PTIDSS：电力交易智能辅助决策系统前端环境配置
// 后端服务：ptidss-server（Spring Boot，端口 9080，context-path /ptidss）
// 开发环境直连后端（后端已配置 CORS）；生产环境可改为同域或网关地址
let baseUrl = "http://localhost:9080/ptidss";

let baseStaticUrl = "http://localhost:9080/ptidss";

export { baseUrl, baseStaticUrl };
