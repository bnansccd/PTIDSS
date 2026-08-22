<template>
    <div class="app-box">
        <div
            class="app-item-box"
            @click="onClick(item)"
            v-for="(item, index) in appList"
            :key="index"
        >
            <img
                :src="baseStaticUrl + item.background"
                class="app-background"
            />
            <img :src="baseStaticUrl + item.icon" class="app-img" />
            <div class="app-title">{{ item.name }}</div>
        </div>

        <!-- <div class="app-item-box" @click="onClick('1')">
            <img src="../imgs/app2.png" class="app-img" />
            <div class="app-title">悟空护驾</div>
        </div>
        <div class="app-item-box" @click="onClick('1')">
            <img src="../imgs/app3.png" class="app-img" />
            <div class="app-title">市级公路平台</div>
        </div>
        <div class="app-item-box" @click="onClick('1')">
            <img src="../imgs/app1.png" class="app-img" />
            <div class="app-title">道路养护</div>
        </div>
        <div class="app-item-box" @click="onClick('1')">
            <img src="../imgs/app3.png" class="app-img" />
            <div class="app-title">市级公路平台</div>
        </div>
        <div class="app-item-box" @click="onClick('1')">
            <img src="../imgs/app1.png" class="app-img" />
            <div class="app-title">道路养护</div>
        </div> -->
    </div>
</template>
<script lang="ts" setup>
import type { AppParams } from "@/pages/system_manage/app_manage/components/api";
import { baseStaticUrl } from "@/env";
import { useUserStore } from "@/stores/modules/user";
import { ElMessage } from "element-plus";
const props = defineProps<{ appList: Array<AppParams> }>();
const userStore = useUserStore();
const onClick = (item: AppParams) => {
    // console.log(item, "item");

    let url = "";
    if (item.type == "1") {
        url = `${location.origin}${item.url}?appId=${item.id}&token=${userStore.access_token}`;
    } else {
        url = `${item.url}?&token=${userStore.access_token}`;
    }

    console.log(url, "url");

    url ? window.open(url) : ElMessage.error("应用链接不存在");
};
</script>
<style lang="scss" scoped>
.app-box {
    width: calc(100% - 400px);
    // height: calc(100% - 100px);
    padding: 50px 200px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;

    .app-item-box {
        width: 150px;
        height: 200px;
        cursor: pointer;
        margin: 30px;
        position: relative;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        .app-background {
            width: 150px;
            height: 150px;
            position: absolute;
            top: 0;
            right: 0;
            z-index: 8;
            border-radius: 50px;
        }
        .app-img {
            margin-top: 40px;
            margin-bottom: 50px;
            width: 80px;
            height: 80px;
            z-index: 99;
            // margin-bottom: 20px;
        }

        .app-title {
            width: 150px;
            text-align: center;
            font-size: 22px;
            font-family: Source Han Sans CN;
            font-weight: 500;
            color: #ffffff;
            line-height: 34px;
            // margin-top: 18px;

            z-index: 99;
        }
    }
}
</style>
