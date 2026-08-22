<template>
    <div class="yb-realtime-monitor">
        <WrapLayout class="left-box">
            <TopMenu :activeIndex="activeIndex" @chose="onChose"></TopMenu>
            <LeftTree v-if="activeIndex != 0" @chose="onChoseNode"></LeftTree>
        </WrapLayout>
        <WrapLayout class="right-box">视频回放</WrapLayout>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import TopMenu from "./components/TopMenu.vue";
import LeftTree from "./components/LeftTree.vue";

const activeIndex = ref(1);
const emit = defineEmits(["onChose", "onChoseNode"]);

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
</style>
