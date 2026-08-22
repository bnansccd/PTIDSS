import type { RouteRecordRaw } from "vue-router";

export const ybWorkorderManage: Array<RouteRecordRaw> = [
    {
        path: "/yb_workorder_manage",
        name: "yb_workorder_manage",
        component: () => import("../../pages/yb_workorder_manage/HomeView.vue"),
        meta: {
            title: "工单管理",
        },
    },
];
