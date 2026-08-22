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
    overflow: hidden;
    padding: 20px;
    display: flex;
    flex-direction: column;
    background: var(--sys-theme-warp-background);
    border-radius: 12px 12px 12px 12px;
    margin: 0px 20px 0 20px;
}

.Gw-WrapTableLayout-head {
    display: flex;
    align-items: center;
}
.Gw-WrapTableLayout ::-webkit-scrollbar {
    width: 0;
    /* 整个滚动条 */
}

::-webkit-scrollbar {
    width: 0;
    /* 整个滚动条 */
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "容器布局",
    name: "WrapLayout",
});
</script>
