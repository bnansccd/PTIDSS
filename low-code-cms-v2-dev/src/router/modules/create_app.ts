import type { RouteRecordRaw } from "vue-router";

export const createApp: Array<RouteRecordRaw> = [
    {
        path: "/createForm",
        name: "createForm",
        component: () =>
            import("../../pages/createApp/createForm/HomeView.vue"),
        meta: {
            title: "表单设计器",
        },
    },
    {
        path: "/app_develop",
        name: "app_develop",
        component: () =>
            import("../../pages/createApp/app_develop/HomeView.vue"),
        meta: {
            title: "应用开发",
        },
    },
];
