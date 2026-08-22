import type { RouteRecordRaw } from "vue-router";

export const authManage: Array<RouteRecordRaw> = [
    {
        path: "/role_manage",
        name: "role_manage",
        component: () =>
            import("../../pages/auth_manage/role_manage/HomeView.vue"),
        meta: {
            title: "角色管理",
        },
    },
    {
        path: "/menu_manage",
        name: "menu_manage",
        component: () =>
            import("../../pages/auth_manage/menu_manage/HomeView.vue"),
        meta: {
            title: "菜单管理",
        },
    },
    {
        path: "/auth_ukey",
        name: "auth_ukey",
        component: () =>
            import("../../pages/auth_manage/auth_ukey/HomeView.vue"),
        meta: {
            title: "UKEY管理",
        },
    },
];
