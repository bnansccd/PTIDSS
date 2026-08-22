<script setup lang="ts">
const props = defineProps<{
    auth: string;
}>();

const userStore = useUserStore();
const route = useRoute();

// PTIDSS：模板遗留 sysBtnAuthList（旧 store API）已移除，改用 hasPermission/isAdmin（三级权限）
const show = computed(() => {
    if (userStore.isAdmin()) {
        return true;
    }
    const path = route.name as string;
    return userStore.hasPermission(`btn:${path}:${props.auth}`);
});
const btnRef = ref();
const btnColor = computed(() => {
    return "red";
});
</script>

<template>
    <span class="global-auth-btn">
        <el-button ref="btnRef" :="$attrs" v-if="show">
            <slot></slot>
        </el-button>
    </span>
</template>

<style scoped lang="scss">
.global-auth-btn {
    margin-right: 8px;
    :deep(.el-button--primary) {
        background-color: var(--sys-theme-btn-primary-background);
        --el-button-border-color: var(
            --sys-theme-btn-primary-border-color
        ) !important;
    }

    :deep(.el-button:hover) {
        //  border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }
    :deep(.el-button:focus) {
        //  border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }
}
</style>

<script lang="ts">
import { useUserStore } from "@/stores/modules/user";
import { defineComponent, computed, ref } from "vue";
import { useRoute } from "vue-router";
export default defineComponent({
    title: "全局权限按钮",
    name: "GlobalAuthElButton",
});
</script>
