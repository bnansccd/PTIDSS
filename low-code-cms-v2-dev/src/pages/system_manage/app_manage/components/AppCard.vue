<script setup lang="ts">
import type { AppParams } from "./api";
import { baseStaticUrl } from "@/env";
const settingBtnShow = ref(false);
const settingRef = ref();

const props = defineProps<{ appObj: AppParams; currentAppId: string }>();

const emits = defineEmits([
    "onChose",
    "onConfig",
    "onEdit",
    "onDel",
    "onReset",
    "onLook",
]);
const onHide = () => {
    settingBtnShow.value = false;
};
const onClickSetting = () => {
    settingBtnShow.value = true;
};
const onClick = (appId: string) => {
    emits("onChose", appId);
};
const onEdit = () => {
    emits("onEdit", props.appObj);
};
const onDel = () => {
    emits("onDel", props.appObj);
};
const onConfig = () => {
    emits("onConfig", props.appObj);
};

const onReset = () => {
    emits("onReset", props.appObj);
};
const onLook = () => {
    emits("onLook", props.appObj);
};
</script>

<template>
    <div
        class="app-card-box"
        :class="{ 'app-active': appObj.id == currentAppId }"
        @click="onClick(appObj.id)"
    >
        <div class="setting-box" ref="settingRef" @mouseleave="onHide">
            <el-icon
                @click.stop="onClickSetting"
                color="#5f5f5f"
                style="cursor: pointer"
                :size="18"
                ><Setting
            /></el-icon>
            <div v-if="settingBtnShow" class="setting-btn-box">
                <!-- <div
                    class="setting-btn"
                    v-if="appObj.type == '1'"
                    @click.stop="onConfig"
                >
                    配置菜单
                </div> -->
                <div
                    class="setting-btn"
                    v-if="appObj.type == '2'"
                    @click.stop="onReset"
                >
                    重置秘钥
                </div>
                <div
                    class="setting-btn"
                    v-if="appObj.type == '2'"
                    @click.stop="onLook"
                >
                    查看秘钥
                </div>
                <div class="setting-btn" @click.stop="onEdit">编辑应用</div>
                <div class="setting-btn" @click.stop="onDel">删除应用</div>
            </div>
        </div>

        <div
            class="app-main-box"
            :style="{ filter: appObj.status == '0' ? 'grayscale(80%)' : '' }"
        >
            <!-- <el-icon color="#ffffff" style="cursor: pointer" :size="38"
                ><Setting
            /></el-icon> -->

            <img
                :src="baseStaticUrl + appObj.background"
                class="app-background"
            />

            <img
                :src="baseStaticUrl + appObj.icon"
                class="app-icon"
                v-if="appObj.type == '1'"
            />
            <el-badge value="外" type="primary" style="z-index: 11" v-else>
                <img :src="baseStaticUrl + appObj.icon" class="app-icon" />
            </el-badge>
        </div>

        <el-tooltip
            class="box-item"
            effect="dark"
            :content="appObj.name"
            placement="top-start"
        >
            <div class="app-title-box" :title="appObj.name">
                {{ appObj.name }}
            </div>
        </el-tooltip>
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

        .setting-btn-box {
            width: 82px;
            background: #ffffff;
            box-shadow: 0px 2px 6px 1px rgba(0, 0, 0, 0.1);
            border-radius: 8px 8px 8px 8px;
            opacity: 1;
            padding: 8px 0;
            border: 1px solid #eeeeee;
        }

        .setting-btn {
            z-index: 9;
            width: 58px;
            padding: 0px 12px;
            background: var(--sys-theme-warp-background);
            font-size: 14px;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 400;
            color: #212121;
            cursor: pointer;
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
        //  background: rgb(216, 179, 12);
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
        white-space: nowrap; /* 不换行 */
        overflow: hidden; /* 超出隐藏 */
        text-overflow: ellipsis; /* 显示省略号... */
        z-index: 99;
    }
}
.app-icon {
    width: 50px;
    height: 50px;
    z-index: 11;
}
.app-background {
    position: absolute;
    top: 0px;
    left: 0px;
    width: 90px;
    height: 90px;
    z-index: 10;
    border-radius: 12px;
}

.app-active {
    background: #f2fdfa;
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
