import type { RouteRecordRaw } from "vue-router";

export const ybDeviceManage: Array<RouteRecordRaw> = [
    {
        path: "/yb_camera_manage",
        name: "yb_camera_manage",
        component: () =>
            import(
                "../../pages/yb_device_manage/yb_camera_manage/HomeView.vue"
            ),
        meta: {
            title: "摄像头管理",
        },
    },
];
