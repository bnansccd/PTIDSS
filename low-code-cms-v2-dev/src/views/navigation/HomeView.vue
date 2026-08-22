<template>
    <div class="navigation-box" v-loading="loading">
        <img
            src="./imgs/navbg.png"
            v-if="activeIndex == 2"
            class="navigation-bg"
        />
        <img
            src="./imgs/swiperBg.png"
            v-if="activeIndex == 1"
            class="navigation-bg"
        />
        <div class="tool-box">
            <!-- <el-icon
                color="#ffffff"
                size="30px"
                style="cursor: pointer"
                @click="onBack"
                ><Back
            /></el-icon> -->
            <img
                src="./imgs/back.png"
                style="width: 38px; height: 38px; cursor: pointer"
                @click="onBack"
                v-show="showBack"
            />

            <div class="right-box">
                <div
                    @click="activeIndex = 1"
                    class="right-item"
                    :class="{ 'active-item': activeIndex == 1 ? true : false }"
                >
                    <img
                        src="./imgs/y1-active.png"
                        v-if="activeIndex == 1"
                        style="width: 25px"
                    />
                    <img src="./imgs/y1.png" v-else style="width: 25px" />
                </div>
                <div
                    @click="activeIndex = 2"
                    class="right-item"
                    :class="{ 'active-item': activeIndex == 2 ? true : false }"
                >
                    <img
                        src="./imgs/y2-active.png"
                        v-if="activeIndex == 2"
                        style="width: 18px"
                    />
                    <img src="./imgs/y2.png" v-else style="width: 18px" />
                </div>
            </div>
        </div>
        <ContentApp
            v-if="activeIndex == 2"
            style="z-index: 9"
            :app-list="response.records"
        />

        <CamberSwiper v-if="activeIndex == 1" style="z-index: 9">
            <CamberSwiperSlide
                v-for="(item, index) in response.records"
                :key="index"
                :slide="5"
            >
                <div class="swiper-item" @click="onClick(item)">
                    <img
                        :src="baseStaticUrl + item.background"
                        style="width: 100%; height: 100%"
                        class="swiper-item-bg"
                    />
                    <img
                        :src="baseStaticUrl + item.icon"
                        class="swiper-item-icon"
                    />
                    <div class="swiper-item-name">{{ item.name }}</div>
                </div>
            </CamberSwiperSlide>
            <!-- <CamberSwiperSlide>
                <img src="./imgs/2.png" style="width: 100%; height: 100%" />
            </CamberSwiperSlide>
            <CamberSwiperSlide>
                <img src="./imgs/3.png" style="width: 100%; height: 100%" />
            </CamberSwiperSlide>
            <CamberSwiperSlide>
                <img src="./imgs/4.png" style="width: 100%; height: 100%" />
            </CamberSwiperSlide>
            <CamberSwiperSlide>
                <img src="./imgs/5.png" style="width: 100%; height: 100%" />
            </CamberSwiperSlide>
            <CamberSwiperSlide>
                <img src="./imgs/1.png" style="width: 100%; height: 100%" />
            </CamberSwiperSlide>
            <CamberSwiperSlide>
                <img src="./imgs/1.png" style="width: 100%; height: 100%" />
            </CamberSwiperSlide>
            <CamberSwiperSlide>
                <img src="./imgs/1.png" style="width: 100%; height: 100%" />
            </CamberSwiperSlide> -->
        </CamberSwiper>

        <!-- <MySwiper :slides="sileeArr" v-if="activeIndex == 1" /> -->
    </div>
</template>

<script lang="ts" setup>
import ContentApp from "./components/ContentApp.vue";
import SwiperApp from "./components/SwiperApp.vue";
import CamberSwiper from "./components/CamberSwiper.vue";
import MySwiper from "./components/MySwiper.vue";
import CamberSwiperSlide from "./components/CamberSwiperSlide.vue";
import { computed, onBeforeMount, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import img1 from "./imgs/1.png";
import { baseStaticUrl } from "@/env";
import { useUserStore } from "@/stores/modules/user";
import { apiDelete, initParams } from "@/pages/system_manage/app_manage/api";
import type { Response, BaseParams } from "@/types/index";
import $api from "@/api/Axios";
import { filterRecords } from "@/tools/function";
import { useSysConfigStore } from "@/stores/modules/sysConfig";
import type { AppParams } from "@/pages/system_manage/app_manage/components/api";
import { ElMessage } from "element-plus";
interface Slide {
    id: number;
    src: string;
}
type AjaxParams = BaseParams;

interface ResponseParams {
    records: Array<AppParams>;
    total: number;
}
const ApibaseURL = "/system/api/web/v1/sysApp/current";
const userStore = useUserStore();
async function apiGetList(ajaxParams: AjaxParams) {
    const response: Response = await $api.get(`${ApibaseURL}`, {
        // params: ajaxParams,
    });
    const total = 0;
    let records: never[] = [];
    const { code, data } = response;
    if (code === 200) {
        //  total = data.total * 1;
        records = data;
    }
    return {
        total,
        records,
    };
}
const activeIndex = ref<number>(2);

const response = reactive<ResponseParams>({
    records: [],
    total: 0,
});
const loading = ref<boolean>(false);
const ajaxParams = reactive({ ...initParams });
const getList = async () => {
    loading.value = true;
    const { total, records } = await apiGetList(ajaxParams);

    response.records = records || [];

    response.records = response.records.filter(
        (item: any) => item.code !== import.meta.env.VITE_APP_CODE
    );

    if (response.records.length === 0) {
        router.push({ path: "/code403" });
    } else if (response.records.length === 1) {
        const firstMenu = localStorage.getItem("firstMenu");

        const sysTenantVO = JSON.parse(
            localStorage.getItem("sysTenantVO") || "{}"
        );

        console.log(sysTenantVO, "sysTenantVO");
        console.log(firstMenu, "firstMenu");
        if (!firstMenu && sysTenantVO.code === "scjtt") {
            onGoSys(response.records[0]);
        }
    }

    response.total = total;

    //  onGoSys(response.records[0]);

    loading.value = false;
};
onBeforeMount(() => {
    getList();
});
const router = useRouter();
const onBack = () => {
    // router.back();

    // 跳转到第一个菜单页面

    const targetMenu = localStorage.getItem("firstMenu")
        ? JSON.parse(localStorage.getItem("firstMenu") || "{}")
        : null;

    if (targetMenu) {
        const sysConfig = useSysConfigStore();
        router.push({ path: targetMenu.href });
        sysConfig.menuActiveKey = targetMenu.href;
        sysConfig.tabs = [
            { name: targetMenu.href, title: targetMenu.menuName },
        ];
        sysConfig.tabsValue = targetMenu.href;
    }
};

const showBack = computed(() => {
    const firstMenu = localStorage.getItem("firstMenu");
    return !!firstMenu;
});

const onClick = (item: AppParams) => {
    let url = "";
    if (item.type == "1") {
        url = `${location.origin}${item.url}?appId=${item.id}&token=${userStore.access_token}`;
    } else {
        url = `${item.url}?&token=${userStore.access_token}`;
    }

    console.log(url, "url");

    url ? window.open(url) : ElMessage.error("应用链接不存在");
};

const onGoSys = (item: AppParams) => {
    let url = "";
    if (item.type == "1") {
        url = `${location.origin}${item.url}?appId=${item.id}&token=${userStore.access_token}`;
    } else {
        url = `${item.url}?&token=${userStore.access_token}`;
    }

    console.log(url, "url");

    url ? (window.location.href = url) : ElMessage.error("应用链接不存在");
};
</script>

<style lang="scss" scoped>
.navigation-box {
    width: 100%;
    height: 100%;
    //  background: url(./imgs/navbg.png);
    background-position: center;
    background-size: cover;
    display: flex;
    align-items: center;
    position: relative;

    .navigation-bg {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 1;
    }

    .tool-box {
        z-index: 2;
        position: absolute;
        top: 100px;
        width: calc(100% - 340px);
        padding: 0 170px;
        // background: #ffffff;
        display: flex;
        justify-content: space-between;

        .right-box {
            width: 70px;
            height: 30px;
            background: #010a18;
            //   border: 1px solid #1e5c76;
            border-radius: 4px 0px 0px 4px;
            display: flex;
            align-items: center;
            justify-content: center;

            .right-item {
                width: 50%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                cursor: pointer;
                border: 1px solid #1e5c76;
            }

            .active-item {
                background: #1a4656;
                border: 1px solid #3badd2;
            }
        }
    }
}

.swiper-item {
    width: 100%;
    height: 100%;
    padding-top: 20px;
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .swiper-item-bg {
        position: absolute;
        top: 0;
        right: 0;
    }

    .swiper-item-icon {
        width: 100px;
        height: 100px;
        z-index: 99;
    }

    .swiper-item-name {
        font-size: 22px;
        font-family: Source Han Sans CN;
        font-weight: 500;
        color: #ffffff;
        z-index: 99;
        margin-top: 20px;
    }
}
</style>
