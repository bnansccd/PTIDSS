import { defineStore } from "pinia";
import $api from "@/api/Axios";

// PTIDSS 认证契约：/auth/captcha、/auth/login、/auth/current、/auth/logout
// 返回体：{ code:0, message, data }；登录后 JWT 存 sessionStorage，刷新页面时凭 /auth/current 恢复

interface LoginParams {
    username: string;
    password: string;
    captchaKey?: string;
    captchaCode?: string;
}

interface CaptchaResult {
    captchaKey: string;
    image: string; // data:image/png;base64,...
}

export const useUserStore = defineStore("user", {
    state: () => {
        return {
            access_token: sessionStorage.getItem("access_token") || "",
            userInfo: JSON.parse(sessionStorage.getItem("userInfo") || "{}"),
            // 当前会话区域（多省切换，评审决议⑤）
            currentRegion:
                sessionStorage.getItem("region_code") ||
                JSON.parse(sessionStorage.getItem("userInfo") || "{}").currentRegion ||
                "",
        };
    },
    actions: {
        /** 获取图形验证码 */
        getCaptcha() {
            return new Promise<CaptchaResult>((resolve, reject) => {
                $api
                    .get(`/auth/captcha`)
                    .then((response: any) => {
                        const { code, data } = response;
                        if (code === 0) {
                            resolve(data);
                        }
                    })
                    .catch((err) => {
                        reject(err);
                    });
            });
        },

        /** 登录：用户名 + 密码 + 验证码 */
        login(ajaxParams: LoginParams) {
            return new Promise((resolve, reject) => {
                $api
                    .post(`/auth/login`, ajaxParams)
                    .then((response: any) => {
                        const { code, data } = response;
                        if (code === 0) {
                            this.access_token = data.accessToken;
                            sessionStorage.setItem("access_token", this.access_token);
                            // 登录响应中带角色/权限/区域，直接组装会话
                            this.currentRegion =
                                data.currentRegion || (data.regions && data.regions[0]) || "";
                            sessionStorage.setItem("region_code", this.currentRegion);
                        }
                        resolve(response);
                    })
                    .catch((err) => {
                        reject(err);
                    });
            });
        },

        /** 获取当前用户信息（角色/权限/区域），并初始化会话区域 */
        getUserInfo() {
            return new Promise((resolve, reject) => {
                $api
                    .get(`/auth/current`)
                    .then((response: any) => {
                        const { code, data } = response;
                        if (code === 0) {
                            this.userInfo = data;
                            sessionStorage.setItem(
                                "userInfo",
                                JSON.stringify(this.userInfo)
                            );
                            // 会话区域：已选区域保持；否则取默认区域
                            const saved = sessionStorage.getItem("region_code");
                            if (
                                !saved &&
                                data.regions &&
                                data.regions.length > 0
                            ) {
                                this.currentRegion = data.regions[0];
                                sessionStorage.setItem(
                                    "region_code",
                                    this.currentRegion
                                );
                            }
                        }
                        resolve(response);
                    })
                    .catch((err) => {
                        reject(err);
                    });
            });
        },

        /** 切换会话区域（多省数据权限） */
        setRegion(regionCode: string) {
            this.currentRegion = regionCode;
            sessionStorage.setItem("region_code", regionCode);
        },

        /** 登出：清理本地会话并通知后端 */
        logout() {
            const token = this.access_token;
            sessionStorage.setItem("access_token", "");
            sessionStorage.removeItem("userInfo");
            sessionStorage.removeItem("region_code");
            localStorage.clear();
            this.access_token = "";
            this.userInfo = {};
            this.currentRegion = "";
            if (token) {
                $api.post(`/auth/logout`).catch(() => {});
            }
            return Promise.resolve();
        },

        /** 是否拥有指定权限（菜单/接口/数据 三级） */
        hasPermission(perm: string) {
            if (!this.userInfo || !this.userInfo.permissions) {
                return false;
            }
            return this.userInfo.permissions.includes(perm);
        },

        /** 是否系统管理员 */
        isAdmin() {
            return (
                this.userInfo &&
                this.userInfo.roles &&
                this.userInfo.roles.includes("admin")
            );
        },
    },
});
