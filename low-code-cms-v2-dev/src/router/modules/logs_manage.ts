import type { RouteRecordRaw } from "vue-router";

export const logsManage: Array<RouteRecordRaw> = [
    {
        path: "/operate_log",
        name: "operate_log",
        component: () =>
            import("../../pages/logs_manage/operate_log/HomeView.vue"),
        meta: {
            title: "操作日志记录",
        },
    },
    {
        path: "/system_visit",
        name: "system_visit",
        component: () =>
            import("../../pages/logs_manage/system_visit/HomeView.vue"),
        meta: {
            title: "登录日志",
        },
    },
];
