<script setup lang="ts">
import { Fold, Expand } from "@element-plus/icons-vue";

const props = defineProps<{
    flag: boolean;
}>();

const emit = defineEmits(["TreeExpand"]);
function TreeExpand(bool: boolean) {
    emit("TreeExpand", bool);
}
</script>

<template>
    <el-button-group>
        <el-button :type="props.flag ? `primary` : ``" @click="TreeExpand(true)"
            >展开</el-button
        >
        <el-button
            :type="!props.flag ? `primary` : ``"
            @click="TreeExpand(false)"
            >收起</el-button
        >
    </el-button-group>
</template>

<style scoped lang="scss">
.el-button-group {
    margin-left: 10px;

    :deep(.el-button--primary) {
        background-color: var(--sys-theme-btn-primary-background) !important;
        --el-button-border-color: var(
            --sys-theme-btn-primary-border-color
        ) !important;
    }
    :deep(.el-button:hover) {
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }
    :deep(.el-button:focus, .el-button:hover) {
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "树形展开或者收起",
    name: "GlobalUnfoldFewer",
});
</script>
