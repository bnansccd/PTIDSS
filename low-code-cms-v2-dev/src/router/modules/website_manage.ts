import type { RouteRecordRaw } from "vue-router";

export const websiteManage: Array<RouteRecordRaw> = [
    {
        path: "/website_news",
        name: "website_news",
        component: () =>
            import("../../pages/website_manage/website_news/HomeView.vue"),
        meta: {
            title: "新闻管理",
        },
    },
    {
        path: "/company_culture",
        name: "company_culture",
        component: () =>
            import("../../pages/website_manage/company_culture/HomeView.vue"),
        meta: {
            title: "企业文化",
        },
    },
     {
        path: "/position_manage",
        name: "position_manage",
        component: () =>
            import("../../pages/website_manage/position_manage/HomeView.vue"),
        meta: {
            title: "招聘管理",
        },
    },
      {
        path: "/search_manage",
        name: "search_manage",
        component: () =>
            import("../../pages/website_manage/search_manage/HomeView.vue"),
        meta: {
            title: "招聘管理",
        },
    },
];
