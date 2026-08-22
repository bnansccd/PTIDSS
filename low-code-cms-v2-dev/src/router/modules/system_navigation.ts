import type { RouteRecordRaw } from "vue-router";

export const systemNavigation: Array<RouteRecordRaw> = [
    {
        path: "/system_navigation",
        name: "system_navigation",
        component: () => import("../../pages/system_navigation/HomeView.vue"),
        meta: {
            title: "系统导航",
        },
    },
];
