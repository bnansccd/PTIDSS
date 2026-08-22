import type { RouteRecordRaw } from "vue-router";

export const systemConfig: Array<RouteRecordRaw> = [
    {
        path: "/params_config",
        name: "params_config",
        component: () =>
            import("../../pages/system_manage/params_config/HomeView.vue"),
        meta: {
            title: "参数配置",
        },
    },
    {
        path: "/data_dictionary",
        name: "data_dictionary",
        component: () =>
            import("../../pages/system_manage/data_dictionary/HomeView.vue"),
        meta: {
            title: "数据字典",
        },
    },
    {
        path: "/app_manage",
        name: "app_manage",
        component: () =>
            import("../../pages/system_manage/app_manage/HomeView.vue"),
        meta: {
            title: "应用管理",
        },
    },
    {
        path: "/integrity_check",
        name: "integrity_check",
        component: () =>
            import("../../pages/system_manage/integrity_check/HomeView.vue"),
        meta: {
            title: "应用管理",
        },
    },
];
