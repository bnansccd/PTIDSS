<template>
    <div class="menu-box" :style="{ width: collapsed ? '72px' : '240px' }">
        <div class="logo-box">
            <div class="logo-text" v-if="!collapsed">
                <div class="logo-main">PTIDSS</div>
                <div class="logo-sub">电力交易智能辅助决策系统</div>
            </div>
            <div class="logo-text" v-else>PT</div>
        </div>

        <WrapScrollPanel class="WrapScroll">
            <NMenu
                :indent="10"
                v-model:value="activeKey"
                :options="menuOptions"
                :collapsed-width="64"
                :collapsed-icon-size="22"
                :collapsed="collapsed"
                :on-update:value="onClick"
            />
        </WrapScrollPanel>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from "vue";
import { h } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { NIcon, NMenu } from "naive-ui";
import type { MenuOption } from "naive-ui";
import { useUserStore } from "@/stores/modules/user";
import { useSysConfigStore } from "@/stores/modules/sysConfig";
import WrapScrollPanel from "./WrapScroll.vue";

// PTIDSS 静态菜单 + 三级权限过滤（menu:* 权限码控制可见性，评审决议⑤）
const store = useUserStore();
const sysStore = useSysConfigStore();
const router = useRouter();

const activeKey = ref(sysStore.menuActiveKey);

watch(
    () => sysStore.tabsValue,
    (newVal) => {
        activeKey.value = newVal;
    }
);

const collapsed = computed(() => sysStore.collapsed);

const onClick = (key: string, item: any) => {
    activeKey.value = item.key || "";
    localStorage.setItem("menuActiveKey", item.key || "");
    // 以路由 name 跳转（key 与路由 name 一致，避免相对 path 解析失败）
    if (item.key && router.hasRoute(item.key)) {
        router.push({ name: item.key });
    }
};

function renderLink(name: string, path: string) {
    return () =>
        h(
            RouterLink,
            { to: { name, params: {} } },
            { default: () => path }
        );
}

// 菜单权限过滤：admin 全可见；其他按 permissions 中的 menu:* 过滤
const hasPerm = (perm: string) => {
    if (store.isAdmin()) return true;
    return store.userInfo.permissions?.includes(perm) || false;
};

const menuOptions: MenuOption[] = [
    {
        label: renderLink("dashboard_home", "工作台"),
        key: "dashboard_home",
    },
    {
        label: "系统管理",
        key: "system_manage",
        show: hasPerm("menu:admin"),
        children: [
            {
                label: renderLink("org_manage", "机构管理"),
                key: "org_manage",
            },
            {
                label: renderLink("region_manage", "区域管理"),
                key: "region_manage",
            },
            {
                label: renderLink("user_manage", "用户管理"),
                key: "user_manage",
            },
            {
                label: renderLink("role_manage", "角色管理"),
                key: "role_manage",
            },
            {
                label: renderLink("permission_manage", "权限管理"),
                key: "permission_manage",
            },
            {
                label: renderLink("audit_log", "审计日志"),
                key: "audit_log",
            },
        ],
    },
    {
        label: "数据底座",
        key: "menu_data",
        show: hasPerm("menu:data"),
        children: [
            {
                label: renderLink("data_source_manage", "数据源管理"),
                key: "data_source_manage",
            },
        ],
    },
    {
        label: "市场行情",
        key: "menu_market",
        show: hasPerm("menu:market"),
        children: [
            {
                label: renderLink("market_manage", "行情总览"),
                key: "market_manage",
            },
        ],
    },
    {
        label: "交易申报",
        key: "menu_trade",
        show: hasPerm("menu:trade"),
        children: [
            {
                label: renderLink("trade_manage", "申报管理"),
                key: "trade_manage",
            },
        ],
    },
    {
        label: "辅助决策",
        key: "menu_decision",
        show: hasPerm("menu:decision"),
        children: [
            {
                label: renderLink("decision_manage", "决策分析"),
                key: "decision_manage",
            },
        ],
    },
    {
        label: "结算管理",
        key: "menu_settlement",
        show: hasPerm("menu:settlement"),
        children: [
            {
                label: renderLink("settlement_manage", "结算核对"),
                key: "settlement_manage",
            },
        ],
    },
    {
        label: "复盘考核",
        key: "menu_review",
        show: hasPerm("menu:review"),
        children: [
            {
                label: renderLink("review_manage", "复盘工作台"),
                key: "review_manage",
            },
        ],
    },
    {
        label: "情报中心",
        key: "menu_intel",
        show: hasPerm("menu:intel"),
        children: [
            {
                label: renderLink("intel_center", "情报总览"),
                key: "intel_center",
            },
        ],
    },
    {
        label: "政策中心",
        key: "menu_policy",
        show: hasPerm("menu:policy"),
        children: [
            {
                label: renderLink("policy_center", "政策总览"),
                key: "policy_center",
            },
        ],
    },
].filter((item) => item.show !== false);

const options = ref(menuOptions);
</script>

<style lang="scss" scoped>
.WrapScroll {
    height: calc(100vh - 150px);
    overflow-y: auto;
    overflow-x: hidden;
}
.menu-box {
    min-height: calc(100vh - 0px);
    background: var(--sys-theme-left-menu-background);
    border-radius: 0px 16px 16px 0px;
    transition: width 0.5s;
    flex-shrink: 0;
    .logo-box {
        padding: 30px 0px 20px 0px;
        display: flex;
        justify-content: center;
        align-items: center;
        .logo-text {
            text-align: center;
            .logo-main {
                font-size: 20px;
                font-weight: 700;
                color: #fff;
                letter-spacing: 1px;
            }
            .logo-sub {
                font-size: 11px;
                color: rgba(255, 255, 255, 0.75);
                margin-top: 4px;
            }
        }
    }

    :deep(.n-menu .n-menu-item-content.n-menu-item-content--selected::before) {
        background: #f4f7ff;
    }
    :deep(.n-menu) {
        --n-item-text-color: var(--sys-theme-left-menu-color) !important;
        --n-item-text-color-hover: var(--sys-theme-left-menu-active-color) !important;
        --n-item-text-color-active: var(--sys-theme-left-menu-active-color) !important;
        --n-item-icon-color: var(--sys-theme-left-menu-color) !important;
        --n-item-icon-color-active: var(--sys-theme-left-menu-active-color) !important;
        --n-item-icon-color-hover: var(--sys-theme-left-menu-active-color) !important;
    }
    :deep(.n-menu-item-content) {
        --n-item-text-color-child-active: var(--sys-theme-left-menu-active-color) !important;
        padding-left: 20px !important;
    }
    :deep(.n-submenu-children) {
        padding-left: 20px !important;
    }
    :deep(.n-menu .n-menu-item-content.n-menu-item-content--selected::before) {
        background: var(--sys-theme-left-menu-active-background) !important;
    }
}
</style>
