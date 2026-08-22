import type { RouteRecordRaw } from "vue-router";

/**
 * PTIDSS 系统管理路由模块（组织架构与用户权限管理）
 * 基于 low-code-cms-v2-dev organization_structure/auth_manage/logs_manage 改造：
 * 区域管理（sys_region）/ 用户管理（sys_user）/ 角色管理（sys_role）/ 权限管理（sys_permission）/ 审计日志（audit_log）
 */
export const ptidssSystem: Array<RouteRecordRaw> = [
    {
        path: "",
        name: "dashboard_home",
        component: () => import("../../pages/ptidss/dashboard/HomeView.vue"),
        meta: {
            title: "工作台",
        },
    },
    {
        path: "/region_manage",
        name: "region_manage",
        component: () => import("../../pages/ptidss/region_manage/HomeView.vue"),
        meta: {
            title: "区域管理",
            perm: "menu:admin",
        },
    },
    {
        path: "/org_manage",
        name: "org_manage",
        component: () => import("../../pages/ptidss/org_manage/HomeView.vue"),
        meta: {
            title: "机构管理",
            perm: "menu:admin",
        },
    },
    {
        path: "/user_manage",
        name: "user_manage",
        component: () => import("../../pages/ptidss/user_manage/HomeView.vue"),
        meta: {
            title: "用户管理",
            perm: "menu:admin",
        },
    },
    {
        path: "/role_manage",
        name: "role_manage",
        component: () => import("../../pages/ptidss/role_manage/HomeView.vue"),
        meta: {
            title: "角色管理",
            perm: "menu:admin",
        },
    },
    {
        path: "/permission_manage",
        name: "permission_manage",
        component: () =>
            import("../../pages/ptidss/permission_manage/HomeView.vue"),
        meta: {
            title: "权限管理",
            perm: "menu:admin",
        },
    },
    {
        path: "/audit_log",
        name: "audit_log",
        component: () => import("../../pages/ptidss/audit_log/HomeView.vue"),
        meta: {
            title: "审计日志",
            perm: "menu:admin",
        },
    },
    {
        path: "/market_manage",
        name: "market_manage",
        component: () => import("../../pages/ptidss/market_manage/HomeView.vue"),
        meta: {
            title: "市场行情",
            perm: "menu:market",
        },
    },
    {
        path: "/trade_manage",
        name: "trade_manage",
        component: () => import("../../pages/ptidss/trade_manage/HomeView.vue"),
        meta: {
            title: "交易申报",
            perm: "menu:trade",
        },
    },
    {
        path: "/decision_manage",
        name: "decision_manage",
        component: () => import("../../pages/ptidss/decision_manage/HomeView.vue"),
        meta: {
            title: "辅助决策",
            perm: "menu:decision",
        },
    },
    {
        path: "/settlement_manage",
        name: "settlement_manage",
        component: () => import("../../pages/ptidss/settlement_manage/HomeView.vue"),
        meta: {
            title: "结算管理",
            perm: "menu:settlement",
        },
    },
    {
        path: "/review_manage",
        name: "review_manage",
        component: () => import("../../pages/ptidss/review_manage/HomeView.vue"),
        meta: {
            title: "复盘考核",
            perm: "menu:review",
        },
    },
    {
        path: "/data_source_manage",
        name: "data_source_manage",
        component: () => import("../../pages/ptidss/data_source_manage/HomeView.vue"),
        meta: {
            title: "数据源管理",
            perm: "menu:data",
        },
    },
    {
        path: "/intel_center",
        name: "intel_center",
        component: () => import("../../pages/ptidss/intel_center/HomeView.vue"),
        meta: {
            title: "情报中心",
            perm: "menu:intel",
        },
    },
    {
        path: "/policy_center",
        name: "policy_center",
        component: () => import("../../pages/ptidss/policy_center/HomeView.vue"),
        meta: {
            title: "政策中心",
            perm: "menu:policy",
        },
    },
];
