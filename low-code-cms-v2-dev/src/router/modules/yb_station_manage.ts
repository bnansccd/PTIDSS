import type { RouteRecordRaw } from "vue-router";

export const ybStationManage: Array<RouteRecordRaw> = [
    {
        path: "/yb_station_manage",
        name: "yb_station_manage",
        component: () => import("../../pages/yb_station_manage/HomeView.vue"),
        meta: {
            title: "场站管理",
        },
    },
];
