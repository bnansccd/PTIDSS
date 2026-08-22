<template>
    <div class="main-box" ref="mainBox">
        <div style="margin: 0 10px">
            <img
                src="../imgs/prev.png"
                :style="prevStyle"
                class="btn-img"
                @click="onPrev"
            />
        </div>
        <div class="swiper-box" :style="swiperBoxStyle">
            <div
                class="wrapper reflect-below-mask"
                :style="{
                    transform:
                        'translateX(' +
                        -state.currentIndex * state.slideWidth +
                        'px)',
                }"
                ref="wrapper"
            >
                <slot />
            </div>
        </div>

        <div style="margin: 0 10px">
            <img
                src="../imgs/prev.png"
                class="btn-img"
                :style="nextStyle"
                style="transform: rotateY(180deg)"
                @click="onNext"
            />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed, nextTick } from "vue";

const props = defineProps({
    slide: { type: Number, default: 5 },
});
const wrapper = ref();
const mainBox = ref();
const state = reactive({
    currentIndex: 0, // 当前索引
    slideWidth: 0, // 滑块slide宽度
    centerIndex: 3, // 中心位置索引
    slideNum: 0, // 滑块的总数量
    swiperWidth: 980, // 滑块可视区域的宽度
    slide: props.slide, // 可视区域滑块的展示数量
});
onMounted(() => {
    // console.log(wrapper, "wrapper");
    // console.log(mainBox.value.clientWidth, "mainBox");

    state.swiperWidth = mainBox.value.clientWidth * 0.7;

    state.slideNum = wrapper.value.children.length;

    state.slideWidth = state.swiperWidth / state.slide;

    // console.log(state.slideWidth, " state.slideWidth");
});

window.addEventListener("resize", function () {
    nextTick(() => {
        state.swiperWidth = mainBox.value.clientWidth * 0.7;
        state.slideWidth = state.swiperWidth / state.slide;
    });
});

// 滑块盒子的样式
const swiperBoxStyle = computed(() => {
    return {
        width: `${state.swiperWidth}px`,
        clipPath: `path("M 0 0 C 0,0 600,50 ${state.swiperWidth},0 V ${
            state.slideWidth * 2
        } H 1 ")`,
    };
});

// 滑块 silde的宽度计算
const screenWidth = computed(() => `${state.slideWidth - 10}px`);

const prevStyle = computed(() => {
    if (state.slideNum - state.currentIndex <= state.slide) {
        return { filter: "grayscale(80%)" };
    }
    return {};
});
const nextStyle = computed(() => {
    if (state.currentIndex == 0) {
        return { filter: "grayscale(80%)" };
    }
    return {};
});
const onPrev = () => {
    if (state.slideNum - state.currentIndex <= state.slide) {
        return;
    }
    state.currentIndex = state.currentIndex + 1;
    state.centerIndex = state.centerIndex + 1;
};

const onNext = () => {
    if (state.currentIndex == 0) {
        return;
    }
    state.currentIndex = state.currentIndex - 1;
    state.centerIndex = state.centerIndex - 1;
};
</script>

<style lang="scss" scoped>
.main-box {
    width: 100%;
    display: flex;
    justify-content: space-around;
    align-items: center;
}

.btn-img {
    width: 50px;
    height: 80px;
    cursor: pointer;
    // filter: grayscale(80%);
}
.swiper-box {
    // background-color: #ffffff;
    display: flex;
    align-items: center;
    // clip-path: path("M 0 0 C 0,0 600,50 1270,0 V 800 H 1 ");
}
.wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.3s ease-out;

    :deep(.screen) {
        width: v-bind(screenWidth);
        height: v-bind(screenWidth);
        margin: -5px 5px 0 5px;
        overflow: hidden;
        cursor: pointer;
    }
}

.reflect-below-mask {
    -webkit-box-reflect: below 20px
        linear-gradient(transparent, rgba(0, 0, 0, 0.24));
}
</style>
