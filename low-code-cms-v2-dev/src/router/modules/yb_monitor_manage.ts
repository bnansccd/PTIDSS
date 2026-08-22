import type { RouteRecordRaw } from "vue-router";

export const ybMonitorManage: Array<RouteRecordRaw> = [
    {
        path: "/yb_realtime_monitor",
        name: "yb_realtime_monitor",
        component: () =>
            import(
                "../../pages/yb_monitor_manage/yb_realtime_monitor/HomeView.vue"
            ),
        meta: {
            title: "视频巡逻",
        },
    },
    {
        path: "/yb_playback_monitor",
        name: "yb_playback_monitor",
        component: () =>
            import(
                "../../pages/yb_monitor_manage/yb_playback_monitor/HomeView.vue"
            ),
        meta: {
            title: "视频回放",
        },
    },
];
