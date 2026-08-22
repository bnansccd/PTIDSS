<template>
    <div class="bottom-box">
        <div class="b-head-box">
            <WrapToolbarSearchLayout label="岗位名称">
                <el-select v-model="date" class="m-2" placeholder="请选择">
                    <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </WrapToolbarSearchLayout>
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
        <div class="b-centent-box">
            <BottomOne></BottomOne>
            <BottomTwo></BottomTwo>
            <BottomThree></BottomThree>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import BottomOne from "./BottomOne.vue";
import BottomTwo from "./BottomTwo.vue";
import BottomThree from "./BottomThree.vue";
const date = ref("1");
const options = [
    {
        value: "1",
        label: "近7天",
    },
    {
        value: "2",
        label: "近15天",
    },
    {
        value: "3",
        label: "近30天",
    },
];
const getList = () => {
    console.log("getList");
};

const queryList = () => {
    console.log("queryList");
};
const refreshParams = () => {
    date.value = "1";
};
</script>

<style lang="scss" scoped>
.bottom-box {
    min-width: 1500px;
    height: 100%;

    .b-head-box {
        display: flex;

        :deep(.el-button--primary) {
            background-color: var(
                --sys-theme-btn-primary-background
            ) !important;
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

    .b-centent-box {
        width: 100%;
        display: flex;
        justify-content: space-between;
    }
}
</style>
