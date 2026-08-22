<script setup lang="ts">
const props = defineProps<{
    closeColon?: boolean; //不显示冒号
    label: string;
}>();
</script>

<template>
    <el-form-item :="$attrs" :label="`${label}${!closeColon ? ':' : ''}`">
        <slot></slot>
    </el-form-item>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "全局表单子项",
    name: "GlobalElFormItem",
});
</script>
