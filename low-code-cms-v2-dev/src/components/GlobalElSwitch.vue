<script setup lang="ts"></script>

<template>
    <el-switch
        :="$attrs"
        style="--el-switch-on-color: #13ce66; --el-switch-off-color: #e6a23c"
    />
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "自定义开关",
    name: "GlobalElSwitch",
});
</script>
