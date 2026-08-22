<script setup lang="ts">
const props = defineProps<{
    closeColon?: boolean; //不显示冒号
    label: string;
}>();
</script>

<template>
    <span class="WrapToolbarSearchLayout">
        <span
            >{{ $props.label }}
            <span v-if="!closeColon">:</span>
        </span>
        <span>
            <slot></slot>
        </span>
    </span>
</template>

<style scoped lang="scss">
.WrapToolbarSearchLayout {
    display: flex;
    align-items: center;
    // background-color: rgba(209, 209, 209, 0.5);
    border-radius: 4px;
    > span:nth-of-type(1) {
        display: inline-block;
        margin: 0 5px;
        color: var(--sys-theme-form-label-color);
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "表格toolbar搜索包裹",
    name: "WrapToolbarSearchLayout",
});
</script>
