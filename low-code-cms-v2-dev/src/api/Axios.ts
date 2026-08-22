import axios from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import { baseUrl } from "@/env/index";
import router from "@/router/index";

// PTIDSS 后端地址（Spring Boot：/ptidss）
axios.defaults.baseURL = baseUrl;

// 添加请求拦截器
axios.interceptors.request.use(
    function (config: any) {
        const userStore = useUserStore();

        // 删除空参数
        if (config.params && config.method == "get") {
            Object.keys(config.params).map((key: string) => {
                if (
                    config.params[key] === "" ||
                    config.params[key] === null ||
                    config.params[key] === undefined
                ) {
                    delete config.params[key];
                }
            });
        }

        // PTIDSS：JWT 令牌（Bearer 前缀）+ 会话区域（多省数据权限，评审决议⑤）
        if (userStore.access_token) {
            config.headers["Authorization"] = "Bearer " + userStore.access_token;
        }
        const regionCode =
            userStore.currentRegion || sessionStorage.getItem("region_code");
        if (regionCode) {
            config.headers["X-Region-Code"] = regionCode;
        }
        return config;
    },
    function (error) {
        return Promise.reject(error);
    }
);

// 添加响应拦截器（PTIDSS 业务契约：code=0 成功；14001 未认证；14003 无权限）
axios.interceptors.response.use(
    function (response) {
        if (response.status == 200) {
            const { code, message = "服务器报错" } = response.data;
            if (code !== 0) {
                ElMessage({
                    message: message,
                    type: code === 14001 || code === 14003 ? "warning" : "error",
                });
            }
            // 未认证/登录失效：清理并跳转登录
            if (code === 14001) {
                const userStore = useUserStore();
                userStore.logout();
                router.replace({ name: "login" });
            }
        }
        return response.data;
    },
    function (error) {
        ElMessage({
            message: "网络错误，请检查后端服务",
            type: "error",
        });
        return Promise.reject(error);
    }
);

// PTIDSS：响应拦截器已解包返回 ApiResponse（{code,message,data}），
// 统一声明为 Promise<T>，避免 AxiosResponse 误类型（G3 类型门禁）
export interface ApiClient {
    get<T = any>(url: string, config?: any): Promise<T>;
    post<T = any>(url: string, data?: any, config?: any): Promise<T>;
    put<T = any>(url: string, data?: any, config?: any): Promise<T>;
    delete<T = any>(url: string, config?: any): Promise<T>;
}

declare module "vue" {
    interface ComponentCustomProperties {
        $api: ApiClient;
    }
}

export default axios as unknown as ApiClient;
