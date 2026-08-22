import type { RouteRecordRaw } from "vue-router";

export const ybMap: Array<RouteRecordRaw> = [
    {
        path: "/yb_map",
        name: "yb_map",
        component: () => import("../../pages/yb_map/HomeView.vue"),
        meta: {
            title: "电子地图",
        },
    },
];
