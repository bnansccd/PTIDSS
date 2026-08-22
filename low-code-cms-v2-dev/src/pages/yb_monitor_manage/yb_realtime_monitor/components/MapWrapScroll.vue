<script setup lang="ts"></script>

<template>
    <div class="WrapScroll">
        <slot></slot>
    </div>
</template>

<style lang="scss" scoped>
.WrapScroll {
    width: 100%;

    //  background: #ffffff;
    overflow-y: auto;
    overflow-x: hidden;
}

.WrapScroll::-webkit-scrollbar {
    width: 6px;
    background-color: transparent;
    /* 整个滚动条 */
}

.WrapScroll::-webkit-scrollbar-button {
    height: 0;
    /* 滚动条上的按钮（上下箭头）*/
}

.WrapScroll::-webkit-scrollbar-thumb {
    border-radius: 10px;
    width: 6px;
    height: 112px;
    background: #afe8ca;
    border-radius: 5px;
    /* 滚动条上的滚动滑块 */
}

.WrapScroll::-webkit-scrollbar-track {
    border-radius: 0;
    /* 滚动条轨道 */
}

.WrapScroll::-webkit-scrollbar-track-piece {
    border-radius: 0;
    /* 滚动条没有滑块的轨道部分 */
}

.WrapScroll::-webkit-scrollbar-corner {
    border-radius: 0;
    background-color: #00f;
    /* 当同时有垂直滚动条和水平滚动条时交汇的部分。通常是浏览器窗口的右下角 */
}

.WrapScroll::-webkit-resizer {
    border-radius: 0;
    background-color: #f00;
    /* 出现在某些元素底角的可拖动调整大小的滑块(比如:textarea的可拖动按钮) */
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "卡片滚动组件",
    name: "MapWrapScroll",
});
</script>
