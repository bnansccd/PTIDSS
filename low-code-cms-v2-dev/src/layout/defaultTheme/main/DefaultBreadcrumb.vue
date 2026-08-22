<template>
    <el-breadcrumb separator="/">
        <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
    </el-breadcrumb>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "默认面包屑",
    name: "DefaultBreadcrumb",
    data() {
        return {};
    },
    created() {
        console.log(this.$route.meta.title);
    },
});
</script>
