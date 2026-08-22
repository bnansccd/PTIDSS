<template>
    <div class="Gw-WrapTableLayout" ref="warp">
        <div class="Gw-WrapTableLayout-head" ref="header" v-if="showHeader">
            <slot name="header" />
            <el-button
                type="primary"
                @click.stop="queryList"
                style="margin-left: 12px; width: 92px"
                >搜索</el-button
            >

            <el-button
                type="primary"
                plain
                @click.stop="refreshParams"
                style="width: 92px"
                >重置</el-button
            >
        </div>

        <div ref="toolbar">
            <slot name="toolbar"></slot>
        </div>
        <div
            class="Gw-table-WrapTableLayout-table"
            :style="`height: ${height}px;`"
        >
            <slot />
        </div>

        <div class="Gw-table-WrapTableLayout-pagination" ref="pagination">
            <slot name="pagination"></slot>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from "vue";
const emit = defineEmits(["query", "reset"]);

const warp = ref();
const header = ref();
const toolbar = ref();
const pagination = ref();

const props = defineProps({
    showHeader: { type: Boolean, default: true },
});

const queryList = () => {
    emit("query");
};
const refreshParams = () => {
    emit("reset");
};

nextTick(() => {
    let tempHeight = warp.value.getBoundingClientRect().height - 50;
    if (header.value) {
        tempHeight = tempHeight - header.value.getBoundingClientRect().height;
    }
    if (toolbar.value) {
        tempHeight = tempHeight - toolbar.value.getBoundingClientRect().height;
    }
    if (pagination.value) {
        tempHeight =
            tempHeight - pagination.value.getBoundingClientRect().height;
    }

    height.value = tempHeight;
});
const height = ref(100);
</script>

<style lang="scss" scoped>
.Gw-WrapTableLayout {
    height: calc(100% - 60px);
    overflow: hidden;
    padding: 20px;
    display: flex;
    flex-direction: column;
    background: var(--sys-theme-warp-background);
    border-radius: 12px 12px 12px 12px;
    margin: 0px 20px 0 20px;
}

.Gw-table-WrapTableLayout-table {
    // border: 1px solid black;
}
.Gw-WrapTableLayout-head {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
    :deep(.el-button--primary) {
        background-color: var(--sys-theme-btn-primary-background) !important;
        --el-button-border-color: var(
            --sys-theme-btn-primary-border-color
        ) !important;
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }
    :deep(.el-button:hover) {
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }
    :deep(.el-button:focus, .el-button:hover) {
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }

    :deep(.el-button.is-plain) {
        --el-button-hover-text-color: var(
            --sys-theme-btn-primary-background
        ) !important;
        --el-button-hover-bg-color: #ffffff00 !important;
        --el-button-hover-border-color: var(--el-color-primary) !important;
        background: #ffffff00 !important;
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
        // color: var(--sys-theme-btn-plain-color) !important;
    }
}

.Gw-table-WrapTableLayout-pagination {
    margin-top: 10px;
    display: flex;
    justify-content: center;
}
.Gw-WrapTableLayout ::-webkit-scrollbar {
    width: 0;
    //  background-color: transparent;
    /* 整个滚动条 */
}

::-webkit-scrollbar {
    width: 0;
    /* 整个滚动条 */
}
</style>
