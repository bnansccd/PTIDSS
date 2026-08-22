import type { RouteRecordRaw } from "vue-router";

export const msgManage: Array<RouteRecordRaw> = [
    {
        path: "/base_manage",
        name: "base_manage",
        component: () =>
            import("../../pages/msg_manage/base_manage/HomeView.vue"),
        meta: {
            title: "基础配置",
        },
    },
    {
        path: "/package_manage",
        name: "package_manage",
        component: () =>
            import("../../pages/msg_manage/package_manage/HomeView.vue"),
        meta: {
            title: "套餐包管理",
        },
    },
    {
        path: "/safe_manage",
        name: "safe_manage",
        component: () =>
            import("../../pages/msg_manage/safe_manage/HomeView.vue"),
        meta: {
            title: "安全管理",
        },
    },
    {
        path: "/send_manage",
        name: "send_manage",
        component: () =>
            import("../../pages/msg_manage/send_manage/HomeView.vue"),
        meta: {
            title: "发送管理",
        },
    },
    {
        path: "/sign_manage",
        name: "sign_manage",
        component: () =>
            import("../../pages/msg_manage/sign_manage/HomeView.vue"),
        meta: {
            title: "签名管理",
        },
    },
    {
        path: "/statistic_analysis",
        name: "statistic_analysis",
        component: () =>
            import("../../pages/msg_manage/statistic_analysis/HomeView.vue"),
        meta: {
            title: "统计分析",
        },
    },
    {
        path: "/template_manage",
        name: "template_manage",
        component: () =>
            import("../../pages/msg_manage/template_manage/HomeView.vue"),
        meta: {
            title: "模板管理",
        },
    },
    {
        path: "/warning_manage",
        name: "warning_manage",
        component: () =>
            import("../../pages/msg_manage/warning_manage/HomeView.vue"),
        meta: {
            title: "告警管理",
        },
    },
];
