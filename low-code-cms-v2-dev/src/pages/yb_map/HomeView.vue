<template>
    <div class="yb-map-main-box" v-loading="loading">
        <iframe
            class="my-map"
            importance="high"
            id="yb_map"
            name="yb_map"
            frameborder="0"
            :src="`${BASE_URL}/t_map/yb_map/index.html`"
        >
        </iframe>
        <left-cards
            class="map-left-cards"
            @onChose="onChose"
            @onChoseNode="onChoseNode"
        ></left-cards>
        <right-one class="map-right-one"></right-one>
        <right-two
            v-if="showVideo"
            class="map-right-one"
            @close="onClose"
        ></right-two>
        <div class="bottom-box">
            <div
                class="bottom-left-btn"
                :class="{
                    'bottom-active-btn': activeIndex == 1 ? true : false,
                }"
                @click="changeLayer(1)"
            >
                场站
            </div>
            <div
                class="bottom-right-btn"
                :class="{
                    'bottom-active-btn': activeIndex == 2 ? true : false,
                }"
                @click="changeLayer(2)"
            >
                摄像头
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { nextTick, ref, computed } from "vue";
import LeftCards from "./components/LeftCards.vue";
import RightOne from "./components/RightOne.vue";
import RightTwo from "./components/RightTwo.vue";
let dom: HTMLIFrameElement;
const activeIndex = ref(1); // 控制图层
const carIndex = ref(0); // 控制场站 默认所有

const showVideo = ref<boolean>(false);
const BASE_URL = computed(() => {
    const BASE_URL = import.meta.env.BASE_URL;
    return window.location.origin + (BASE_URL === "/" ? "" : BASE_URL);
});

const loading = ref<boolean>(true);
const p2 = new Promise((resolve, reject) => {
    nextTick(() => {
        dom = document.getElementById("yb_map") as HTMLIFrameElement;

        dom.addEventListener("load", function () {
            resolve("1");
            loading.value = false;
            drawMakers();
        });
    });
});

const onClose = () => {
    showVideo.value = false;
};
const onChose = (index: number) => {
    carIndex.value = index;
    (dom.contentWindow as any).removeLayer();
    if (activeIndex.value == 1) {
        drawMakers();
    } else {
        drawMakers2();
    }
};

const onChoseNode = (node: any) => {
    changeLayer(2);
    (dom.contentWindow as any).createInfoWindow4({
        lng: "104.623872",
        lat: "28.745312",
    });
};
const changeLayer = (index: number) => {
    activeIndex.value = index;
    (dom.contentWindow as any).removeLayer();
    if (index == 1) {
        drawMakers();
    } else {
        drawMakers2();
    }
};
function drawMakers() {
    const positions = [
        ["104.623872", "28.745312"],
        ["104.629451", "28.74456"],
        ["104.632369", "28.740948"],
        ["104.644686", "28.743506"],
        ["104.697231", "28.750467"],
        ["104.695416", "28.743043"],
        ["104.655195", "28.787314"],
        ["104.659732", "28.776712"],
        ["104.600161", "28.774566"],
        ["104.581688", "28.776038"],
        ["104.562544", "28.774418"],
        ["104.541887", "28.764261"],
        ["104.616955", "28.801941"],
        ["104.649871", "28.809594"],
        ["104.70042", "28.781631"],
        ["104.716552", "28.750654"],
        ["104.71879", "28.736099"],
        ["104.709837", "28.719743"],
        ["104.708376", "28.703162"],
        ["104.668802", "28.697309"],
        ["104.773418", "28.708181"],
        ["104.651043", "28.678395"],
        ["104.557457", "28.680923"],
        ["104.513239", "28.735359"],
    ];

    const positions2 = [
        ["104.619152", "28.737561"],
        ["104.633571", "28.737109"],
        ["104.638893", "28.75058"],
        ["104.639408", "28.755923"],
        ["104.640609", "28.763071"],
        ["104.644386", "28.7645"],
        ["104.646274", "28.758932"],
        ["104.649879", "28.758782"],
        ["104.646617", "28.775635"],
        ["104.65211", "28.774507"],
        ["104.664041", "28.769165"],
        ["104.666873", "28.764952"],
        ["104.620439", "28.768262"],
        ["104.597351", "28.765403"],
        ["104.591171", "28.762318"],
        ["104.700074", "28.768642"],
        ["104.708904", "28.751614"],
        ["104.70471", "28.737486"],
        ["104.738923", "28.730518"],
        ["104.758568", "28.740389"],
        ["104.748414", "28.76245"],
        ["104.721927", "28.795146"],
        ["104.751283", "28.792438"],
        ["104.532474", "28.736114"],
        ["104.601906", "28.693369"],
        ["104.624164", "28.68642"],
        ["104.637772", "28.710294"],
        ["104.659848", "28.732571"],
        ["104.639587", "28.807589"],
        ["104.635655", "28.799109"],
    ];

    const positions3 = [
        ["104.599638", "28.715577"],
        ["104.600218", "28.714941"],
        ["104.616319", "28.715068"],
        ["104.624297", "28.730206"],
        ["104.614869", "28.745596"],
        ["104.626328", "28.753863"],
        ["104.587453", "28.753608"],
        ["104.580055", "28.747758"],
        ["104.625458", "28.789846"],
        ["104.634887", "28.791626"],
        ["104.688993", "28.740255"],
        ["104.671006", "28.74"],
        ["104.677679", "28.724864"],
        ["104.68029", "28.712778"],
        ["104.69407", "28.72321"],
        ["104.707996", "28.730969"],
        ["104.727143", "28.7901"],
        ["104.574108", "28.780311"],
        ["104.532476", "28.759204"],
        ["104.520871", "28.749666"],
        ["104.520871", "28.733005"],
        ["104.539004", "28.725627"],
    ];

    if (carIndex.value == 0) {
        (dom.contentWindow as any).addMakers(positions);
        (dom.contentWindow as any).addMakers2(positions2);
        (dom.contentWindow as any).addMakers3(positions3);
    } else if (carIndex.value == 1) {
        (dom.contentWindow as any).addMakers(positions);
    } else if (carIndex.value == 2) {
        (dom.contentWindow as any).addMakers2(positions2);
    } else if (carIndex.value == 3) {
        (dom.contentWindow as any).addMakers3(positions3);
    } else {
        (dom.contentWindow as any).addMakers(positions);
        (dom.contentWindow as any).addMakers2(positions2);
        (dom.contentWindow as any).addMakers3(positions3);
    }
}

function drawMakers2() {
    const positions = [
        ["104.623872", "28.745312"],
        ["104.629451", "28.74456"],
        ["104.632369", "28.740948"],
        ["104.644686", "28.743506"],
        ["104.697231", "28.750467"],
        ["104.695416", "28.743043"],
        ["104.655195", "28.787314"],
        ["104.659732", "28.776712"],
        ["104.600161", "28.774566"],
        ["104.581688", "28.776038"],
        ["104.562544", "28.774418"],
        ["104.541887", "28.764261"],
        ["104.616955", "28.801941"],
        ["104.649871", "28.809594"],
        ["104.70042", "28.781631"],
        ["104.716552", "28.750654"],
        ["104.71879", "28.736099"],
        ["104.709837", "28.719743"],
        ["104.708376", "28.703162"],
        ["104.668802", "28.697309"],
        ["104.773418", "28.708181"],
        ["104.651043", "28.678395"],
        ["104.557457", "28.680923"],
        ["104.513239", "28.735359"],
    ];

    const positions2 = [
        ["104.619152", "28.737561"],
        ["104.633571", "28.737109"],
        ["104.638893", "28.75058"],
        ["104.639408", "28.755923"],
        ["104.640609", "28.763071"],
        ["104.644386", "28.7645"],
        ["104.646274", "28.758932"],
        ["104.649879", "28.758782"],
        ["104.646617", "28.775635"],
        ["104.65211", "28.774507"],
        ["104.664041", "28.769165"],
        ["104.666873", "28.764952"],
        ["104.620439", "28.768262"],
        ["104.597351", "28.765403"],
        ["104.591171", "28.762318"],
        ["104.700074", "28.768642"],
        ["104.708904", "28.751614"],
        ["104.70471", "28.737486"],
        ["104.738923", "28.730518"],
        ["104.758568", "28.740389"],
        ["104.748414", "28.76245"],
        ["104.721927", "28.795146"],
        ["104.751283", "28.792438"],
        ["104.532474", "28.736114"],
        ["104.601906", "28.693369"],
        ["104.624164", "28.68642"],
        ["104.637772", "28.710294"],
        ["104.659848", "28.732571"],
        ["104.639587", "28.807589"],
        ["104.635655", "28.799109"],
    ];

    const positions3 = [
        ["104.599638", "28.715577"],
        ["104.600218", "28.714941"],
        ["104.616319", "28.715068"],
        ["104.624297", "28.730206"],
        ["104.614869", "28.745596"],
        ["104.626328", "28.753863"],
        ["104.587453", "28.753608"],
        ["104.580055", "28.747758"],
        ["104.625458", "28.789846"],
        ["104.634887", "28.791626"],
        ["104.688993", "28.740255"],
        ["104.671006", "28.74"],
        ["104.677679", "28.724864"],
        ["104.68029", "28.712778"],
        ["104.69407", "28.72321"],
        ["104.707996", "28.730969"],
        ["104.727143", "28.7901"],
        ["104.574108", "28.780311"],
        ["104.532476", "28.759204"],
        ["104.520871", "28.749666"],
        ["104.520871", "28.733005"],
        ["104.539004", "28.725627"],
    ];

    if (carIndex.value == 0) {
        (dom.contentWindow as any).addMakers4(positions);
        (dom.contentWindow as any).addMakers4(positions2);
        (dom.contentWindow as any).addMakers4(positions3);
    } else if (carIndex.value == 1) {
        (dom.contentWindow as any).addMakers4(positions);
    } else if (carIndex.value == 2) {
        (dom.contentWindow as any).addMakers4(positions2);
    } else if (carIndex.value == 3) {
        (dom.contentWindow as any).addMakers4(positions3);
    } else {
        (dom.contentWindow as any).addMakers4(positions);
        (dom.contentWindow as any).addMakers4(positions2);
        (dom.contentWindow as any).addMakers4(positions3);
    }
}

window.addEventListener("message", function (event) {
    // 地图通信 子到父
    if (event.data.command == "handleClick") {
        showVideo.value = true;
    }
});
</script>

<style lang="scss" scoped>
.yb-map-main-box {
    width: 100%;
    height: calc(100vh - 100px);
    position: relative;
}

.bottom-box {
    width: 100%;
    height: 50px;
    // background: #ffffff;
    position: absolute;
    left: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;

    .bottom-left-btn {
        width: 100px;
        height: 40px;
        background: #e8f8ef;
        border-radius: 12px 0px 0px 0px;
        font-size: 14px;
        font-family: Source Han Sans CN-Regular, Source Han Sans CN;
        font-weight: 400;
        color: #41c980;
        line-height: 40px;
        text-align: center;
        cursor: pointer;
    }
    .bottom-right-btn {
        width: 100px;
        height: 40px;
        background: #e8f8ef;
        border-radius: 0px 12px 0px 0px;
        font-size: 14px;
        font-family: Source Han Sans CN-Regular, Source Han Sans CN;
        font-weight: 400;
        color: #41c980;
        line-height: 40px;
        text-align: center;
        cursor: pointer;
    }
    .bottom-active-btn {
        background: #41c980;
        color: #ffffff;
    }
}
.my-map {
    width: 100%;
    height: calc(100vh - 100px);
}

.map-left-cards {
    position: absolute;
    left: 20px;
    top: 20px;
}

.map-right-one {
    position: absolute;
    right: 20px;
    top: 20px;
}
</style>
