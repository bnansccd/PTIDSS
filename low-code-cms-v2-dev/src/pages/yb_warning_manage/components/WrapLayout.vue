<template>
    <div class="Gw-WrapTableLayout">
        <div class="Gw-WrapTableLayout-head">
            <slot name="head" />
        </div>

        <slot />
    </div>
</template>

<style lang="scss" scoped>
.Gw-WrapTableLayout {
    height: calc(100% - 60px);
    overflow: auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    background: var(--sys-theme-warp-background);
    border-radius: 12px 12px 12px 12px;
    margin: 0px 20px 0 20px;
    overflow-y: hidden;
}

.Gw-WrapTableLayout-head {
    display: flex;
    align-items: center;
}
.Gw-WrapTableLayout ::-webkit-scrollbar {
    width: 10;
    /* 整个滚动条 */
}

::-webkit-scrollbar {
    width: 10;
    /* 整个滚动条 */
}

// .Gw-WrapTableLayout {
//     width: 100%;

//     //  background: #ffffff;
//     overflow-y: auto;
//     overflow-x: hidden;
// }

.Gw-WrapTableLayout::-webkit-scrollbar {
    width: 6px;
    height: 8px;
    background-color: transparent;
    /* 整个滚动条 */
}

.Gw-WrapTableLayout::-webkit-scrollbar-button {
    height: 0;
    /* 滚动条上的按钮（上下箭头）*/
}

.Gw-WrapTableLayout::-webkit-scrollbar-thumb {
    border-radius: 10px;
    width: 6px;
    height: 112px;
    background: #afe8ca;
    border-radius: 5px;
    /* 滚动条上的滚动滑块 */
}

.Gw-WrapTableLayout::-webkit-scrollbar-track {
    border-radius: 0;
    /* 滚动条轨道 */
}

.Gw-WrapTableLayout::-webkit-scrollbar-track-piece {
    border-radius: 0;
    /* 滚动条没有滑块的轨道部分 */
}

.Gw-WrapTableLayout::-webkit-scrollbar-corner {
    border-radius: 0;
    //  background-color: #00f;
    /* 当同时有垂直滚动条和水平滚动条时交汇的部分。通常是浏览器窗口的右下角 */
}

.Gw-WrapTableLayout::-webkit-resizer {
    border-radius: 0;
    background-color: #f00;
    /* 出现在某些元素底角的可拖动调整大小的滑块(比如:textarea的可拖动按钮) */
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "容器布局",
    name: "WrapLayout",
});
</script>
