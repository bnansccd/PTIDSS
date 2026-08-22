<script setup lang="ts">
const settingBtnShow = ref(false);
const settingRef = ref();

const emits = defineEmits(["onChose"]);
const onHide = () => {
    settingBtnShow.value = false;
};
const onClickSetting = () => {
    settingBtnShow.value = true;
};
const onClick = () => {
    emits("onChose", {});
};
const onEdit = () => {};
const onDel = () => {};
</script>

<template>
    <div class="app-card-box" @click="onClick">
        <div class="setting-box" ref="settingRef" @mouseleave="onHide">
            <el-icon
                @click.stop="onClickSetting"
                color="#5f5f5f"
                style="cursor: pointer"
                :size="18"
                ><Setting
            /></el-icon>
            <div v-if="settingBtnShow">
                <div class="setting-btn" @click.stop="onEdit">编辑应用</div>
                <div class="setting-btn" @click.stop="onDel">删除应用</div>
            </div>
        </div>
        <div class="app-main-box">
            <el-icon color="#ffffff" style="cursor: pointer" :size="38"
                ><Setting
            /></el-icon>
        </div>
        <div class="app-title-box">请假流程</div>
    </div>
</template>

<style lang="scss" scoped>
.app-card-box {
    width: 120px;
    height: 130px;
    padding: 10px;

    border-radius: 12px;
    position: relative;

    .setting-box {
        width: 100%;
        height: 100%;
        border-radius: 12px;
        color: var(--el-text-color-primary);
        position: absolute;
        display: none;
        z-index: 10;

        .setting-btn {
            z-index: 9;
            width: 58px;
            padding: 0px 12px;
            background: var(--sys-theme-warp-background);
        }
        .setting-btn:hover {
            width: 58px;
            padding: 0px 12px;
            background: var(--sys-theme-table-row-hover);
        }
    }
    .app-main-box {
        z-index: 8;
        width: 30px;
        height: 30px;
        padding: 30px;
        border-radius: 12px;
        background: rgb(216, 179, 12);
        position: absolute;
        top: 25px;
        left: 25px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .app-title-box {
        position: absolute;
        bottom: 5px;
        width: 120px;
        text-align: center;
        color: var(--el-text-color-primary);
    }
}

.app-card-box:hover {
    // background: #f5f5f5;
    background: var(--sys-theme-app-card-background);
    .setting-box {
        width: 100%;
        height: 100%;
        border-radius: 12px;
        position: absolute;
        display: inline;
    }
}
</style>

<script lang="ts">
import { defineComponent, ref, nextTick } from "vue";
export default defineComponent({
    title: "App卡片",
    name: "AppCard",
});
</script>
