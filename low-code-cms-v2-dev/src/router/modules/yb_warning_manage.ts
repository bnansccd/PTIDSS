import type { RouteRecordRaw } from "vue-router";

export const ybWarningManage: Array<RouteRecordRaw> = [
    {
        path: "/yb_warning_manage",
        name: "yb_warning_manage",
        component: () => import("../../pages/yb_warning_manage/HomeView.vue"),
        meta: {
            title: "告警管理",
        },
    },
];
