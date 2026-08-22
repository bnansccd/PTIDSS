<script setup lang="ts"></script>

<template>
    <el-dialog
        :close-on-click-modal="false"
        draggable
        :="$attrs"
        width="30%"
        center
        :append-to-body="true"
    >
        <!-- :destroy-on-close="true" -->
        <template #header>
            <slot name="header"></slot>
        </template>

        <slot />

        <template #footer>
            <slot name="footer"></slot>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "全局弹窗",
    name: "GlobalElDialog",
});
</script>
