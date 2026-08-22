<script setup lang="ts">
import type { MenuResponseParams, ConfigParams } from "@/types";
const props = defineProps<{
    auth: string;
    startAuth: { type: boolean; default: true }; // 是否开启权限，默认开启
}>();

const userStore = useUserStore();
const sysStore = useSysConfigStore();
const route = useRoute();

// PTIDSS：模板遗留 sysBtnAuthList（旧 store API）已移除，改用 hasPermission/isAdmin（三级权限）
const show = computed(() => {
    if (!props.startAuth) {
        return true;
    }
    if (userStore.isAdmin()) {
        return true;
    }
    const path = route.name as string;
    return userStore.hasPermission(`btn:${path}:${props.auth}`);
});
const btnRef = ref();
const colorArr = [
    "#108cfb",
    "#0cd29c",
    "#fbb016",
    "#b8a0fc",
    "#f6769d",
    "#ffffff",
    "#121416",
];
const colorEditArr = [
    "#00BB8C",
    "#31C0FF",
    "#31C0FF",
    "#31C0FF",
    "#31C0FF",
    "#31C0FF",
    "#31C0FF",
];
const btnColor = computed(() => {
    let color = 0;
    sysStore.systemConfig.forEach((item: ConfigParams) => {
        if (item.configKey == "system_theme_color") {
            color = Number(item.configValue);
            return;
        }
    });
    color = color - 1;
    if (props.auth == "edit") {
        return colorEditArr[color];
    } else if (props.auth == "del") {
        return "red";
    } else {
        return colorArr[color];
    }
});
</script>

<template>
    <!-- <span class="global-auth-btn"> -->
    <el-button ref="btnRef" :="$attrs" v-if="show">
        <slot></slot>
    </el-button>

    <!-- <div ref="btnRef" :="$attrs" v-if="show" :style="{ color: btnColor }">
            <slot></slot>
        </div> -->
    <!-- </span> -->
</template>

<style scoped lang="scss">
.el-button--primary {
    // background-color: var(--sys-theme-btn-primary-background);
    // --el-button-border-color: var(
    //     --sys-theme-btn-primary-border-color
    // ) !important;
    color: v-bind(btnColor) !important;
}

.el-button:hover {
    //  border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
}
</style>

<script lang="ts">
import { useUserStore } from "@/stores/modules/user";
import { useSysConfigStore } from "@/stores/modules/sysConfig";
import { defineComponent, computed, ref } from "vue";
import { useRoute } from "vue-router";
export default defineComponent({
    title: "全局权限按钮",
    name: "GlobalAuthElButtonLink",
});
</script>
