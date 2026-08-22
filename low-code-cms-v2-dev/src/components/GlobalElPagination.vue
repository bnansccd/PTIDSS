<script setup lang="ts"></script>

<template>
    <div class="pagination-box">
        <el-pagination
            :="$attrs"
            background
            :page-sizes="[5, 10, 20, 30, 50, 100]"
            :small="false"
            layout="total, sizes, prev, pager, next, jumper"
        />
    </div>
</template>

<style lang="scss" scoped>
.pagination-box {
    :deep(.el-pagination.is-background
            .el-pager
            li:not(.is-disabled).is-active) {
        background-color: var(--sys-theme-pagination-active-color) !important;
        border: none !important;
    }
    :deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
        color: var(--sys-theme-pagination-active-color) !important;
    }
    :deep(.el-pagination.is-background .btn-prev:disabled) {
        background-color: #1059c704 !important;
        border: 1px solid var(--sys-theme-input-border-color) !important;
    }
    :deep(.el-pagination.is-background .btn-next:disabled, ) {
        background-color: #1059c704 !important;
        border: 1px solid var(--sys-theme-input-border-color) !important;
    }
    :deep(.el-input) {
        --el-input-bg-color: #1059c704 !important;
    }
    :deep(.el-pagination.is-background .el-pager li) {
        background-color: #1059c704;
        border: 1px solid var(--sys-theme-input-border-color) !important;
    }
    :deep(.el-pagination.is-background
            .el-pager
            li:not(.is-disabled).is-active) {
        color: #ffffff !important;
    }
    :deep(.el-pagination.is-background .btn-next) {
        background-color: #1059c704;
        border: 1px solid var(--sys-theme-input-border-color) !important;
    }
    :deep(.el-pagination.is-background .btn-prev) {
        background-color: #1059c704;
        border: 1px solid var(--sys-theme-input-border-color) !important;
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "二次封装全局分页插件",
    name: "GlobalElPagination",
});
</script>
