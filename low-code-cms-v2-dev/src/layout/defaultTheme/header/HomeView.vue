<script setup lang="ts">
import UserInfo from "../../components/user/HomeView.vue";

const sysConfig = useSysConfigStore();
const editableTabsValue = ref(sysConfig.tabsValue);

const route = useRoute();
const router = useRouter();

// PTIDSS 系统标题（固定，不再依赖系统配置接口）
const sysTitle = computed(() => "电力交易智能辅助决策系统 PTIDSS");
const sysTitleSize = computed(() => "16px");
const sysTitleColor = computed(() => "#1f3b6b");
const sysTitleFont = computed(() => "");

// 是否展示tab菜单
const showTab = computed(() => false);

watch(
    () => sysConfig.tabsValue,
    (newVal) => {
        // console.log("change..", newVal);
        editableTabsValue.value = newVal;
    }
);

const editableTabs = computed(() => sysConfig.tabs);

// 移除tab
const removeTab = (targetName: string | number) => {
    let tabs = sysConfig.tabs;
    if (tabs.length <= 1) {
        // 只剩最后一个 不可删除
        ElMessage({
            type: "warning",
            message: "最后一个tab不可删除",
        });
        return;
    }

    let activeName = editableTabsValue.value;
    let flag = false; // 默认不需要刷新路由
    if (activeName === targetName) {
        flag = true;
        tabs.forEach((tab: any, index: number) => {
            if (tab.name === targetName) {
                const nextTab = tabs[index + 1] || tabs[index - 1];
                if (nextTab) {
                    activeName = nextTab.name;
                }
            }
        });
    }

    editableTabsValue.value = activeName;
    tabs = tabs.filter((tab: any) => tab.name !== targetName);
    localStorage.setItem("tabs", JSON.stringify(tabs));
    sysConfig.tabs = tabs;

    // 如果删掉当前标签页 需要跳转页面
    if (flag) {
        router.push({ path: activeName });
    }
};

// 点击tab
const onTabChange = (targetName: string | number) => {
    router.push({ path: String(targetName) });

    sysConfig.tabsValue = editableTabsValue.value;
};

// 收缩菜单
const onClick = () => {
    sysConfig.collapsed = !sysConfig.collapsed;
};

const onRightClick = () => {
    // console.log("1111");
};
</script>

<template>
    <div class="DefaultHeader">
        <div class="d-head-left">
            <el-icon
                size="22px"
                color="#6d7585"
                @click="onClick"
                class="icon-arr"
                v-if="sysConfig.collapsed"
                ><Expand
            /></el-icon>
            <el-icon
                size="22px"
                color="#6d7585"
                @click="onClick"
                class="icon-arr"
                v-else
                ><Fold
            /></el-icon>
            <div class="sys-title">{{ sysTitle }}</div>
        </div>

        <UserInfo />
    </div>
    <div
        class="default-tab-box"
        v-if="showTab"
        @contextmenu.prevent.stop="onRightClick"
    >
        <el-tabs
            v-model="editableTabsValue"
            closable
            @tab-change="onTabChange"
            @tab-remove="removeTab"
        >
            <el-tab-pane
                v-for="item in editableTabs"
                :key="item.name"
                :label="item.title"
                :name="item.name"
            >
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<style scoped lang="scss">
.DefaultHeader {
    width: 100%;
    height: 50px;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    > div:nth-child(1) {
        flex-grow: 1;
    }

    .d-head-left {
        display: flex;
        margin-left: 20px;
        align-items: center;
    }
}

.icon-arr {
    cursor: pointer;
}

.sys-title {
    margin-left: 10px;
    font-size: v-bind(sysTitleSize);
    color: v-bind(sysTitleColor);
    font-family: v-bind(sysTitleFont);
}

.default-tab-box {
    // width: calc(100% - 60px);
    height: 38px;
    padding: 0 20px;

    :deep(.el-tabs) {
        // height: 38px !important;
        --el-tabs-header-height: 30px !important;
    }
    :deep(.el-tabs__header) {
        height: 38px !important;
        border-bottom: none !important;
        margin-bottom: 0 !important;
    }
    :deep(.el-tabs__nav-prev) {
        line-height: 33px !important;
    }
    :deep(.el-tabs__nav-next) {
        line-height: 33px !important;
    }

    :deep(.el-tabs__item) {
        background: #e9e9e9;
        border-radius: 4px !important;
        margin: 0 3px;
        padding: 0 20px !important;
    }
    :deep(.el-tabs__item.is-active) {
        // border-bottom: none !important;
        padding: 0 20px !important;
        // border: 1px solid red;
        background: var(--sys-theme-tab-active-background) !important;
        border-radius: 4px;
        color: var(--sys-theme-tab-active-color) !important;
    }
    :deep(.el-tabs__item:hover) {
        color: var(--sys-theme-tab-active-color) !important;
    }
    :deep(.el-tabs__active-bar) {
        display: none;
    }
    :deep(.el-tabs__nav-wrap::after) {
        display: none;
    }

    :deep(.is-icon-close:hover) {
        background-color: var(--sys-theme-tab-active-color) !important;
        color: #fff;
    }
}
</style>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useSysConfigStore } from "@/stores/modules/sysConfig";
import { ElMessage } from "element-plus";
import type { ConfigParams } from "@/types";
export default defineComponent({
    title: "默认头部",
    name: "DefaultHeader",
});
</script>
