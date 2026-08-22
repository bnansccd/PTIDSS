<template>
    <div class="yb-realtime-monitor">
        <WrapLayout class="left-box">
            <TopMenu :activeIndex="activeIndex" @chose="onChose"></TopMenu>
            <LeftTree v-if="activeIndex != 0" @chose="onChoseNode"></LeftTree>
        </WrapLayout>
        <WrapLayout class="right-box">
            <iframe
                class="my-video"
                importance="high"
                id="demo_window_simple_preview"
                name="demo_window_simple_preview"
                frameborder="0"
                :src="`${BASE_URL}/hk_demo/demo_window_simple_preview.html`"
            >
            </iframe>
        </WrapLayout>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import TopMenu from "./components/TopMenu.vue";
import LeftTree from "./components/LeftTree.vue";

const activeIndex = ref(1);
const emit = defineEmits(["onChose", "onChoseNode"]);
const BASE_URL = computed(() => {
    const BASE_URL = import.meta.env.BASE_URL;
    return window.location.origin + (BASE_URL === "/" ? "" : BASE_URL);
});
const onChose = (index: number) => {
    activeIndex.value = index;
};

// 树节点被点击
const onChoseNode = (node: any) => {
    emit("onChoseNode", node);
};
</script>

<style lang="scss" scoped>
.yb-realtime-monitor {
    width: 100%;
    height: 100%;
    display: flex;
    //   background: #ffffff;

    .left-box {
        width: 300px;
    }

    .right-box {
        flex: 1;
        margin-left: 0 !important;
    }
}

.my-video {
    width: 100%;
    height: 100%;
}
</style>
