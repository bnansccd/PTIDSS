import type { RouteRecordRaw } from "vue-router";

const LoginView = () => import("@/views/login/HomeView.vue");

const ErrorCode401 = () => import("@/views/error/ErrorCode401.vue");

const ErrorCode403 = () => import("@/views/error/ErrorCode403.vue");

const NotFound = () => import("@/views/error/NotFound.vue");

const HomeView = () => import("@/layout/HomeView.vue");

// PTIDSS 系统管理（组织架构与用户权限管理）
import { ptidssSystem } from "./modules/ptidss_system";

const children = ptidssSystem;

const routes: Array<RouteRecordRaw> = [
    {
        path: "/",
        redirect: "/dashboard",
    },
    {
        path: "/login",
        name: "login",
        component: LoginView,
        meta: {
            title: "登录",
        },
    },
    {
        path: "/code401",
        name: "code401",
        component: ErrorCode401,
        meta: {
            title: "权限不足",
        },
    },
    {
        path: "/code403",
        name: "code403",
        component: ErrorCode403,
        meta: {
            title: "没有权限",
        },
    },
    {
        path: "/dashboard",
        name: "dashboard",
        component: HomeView,
        meta: {
            title: "首页",
        },
        children,
    },
    {
        path: "/:pathMatch(.*)*",
        name: "not-found",
        component: NotFound,
        meta: {
            title: "找不到网页",
        },
    },
];

export default routes;
