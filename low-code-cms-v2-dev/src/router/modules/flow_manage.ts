import type { RouteRecordRaw } from "vue-router";

export const flowManage: Array<RouteRecordRaw> = [
    {
        path: "/flow_design",
        name: "flow_design",
        component: () =>
            import("../../pages/flow_manage/flow_design/HomeView.vue"),
        meta: {
            title: "流程设计器",
        },
    },
];
