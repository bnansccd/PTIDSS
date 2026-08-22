import type { RouteRecordRaw } from "vue-router";

export const organizationStructure: Array<RouteRecordRaw> = [
    {
        path: "/tenant_manage",
        name: "tenant_manage",
        component: () =>
            import(
                "../../pages/organization_structure/tenant_manage/HomeView.vue"
            ),
        meta: {
            title: "租户管理",
        },
    },
    {
        path: "/depart_manage",
        name: "depart_manage",
        component: () =>
            import(
                "../../pages/organization_structure/depart_manage/HomeView.vue"
            ),
        meta: {
            title: "组织管理",
        },
    },

    {
        path: "/post_manage",
        name: "post_manage",
        component: () =>
            import(
                "../../pages/organization_structure/post_manage/HomeView.vue"
            ),
        meta: {
            title: "岗位管理",
        },
    },

    {
        path: "/user_manage",
        name: "user_manage",
        component: () =>
            import(
                "../../pages/organization_structure/user_manage/HomeView.vue"
            ),
        meta: {
            title: "用户管理",
        },
    },
];
