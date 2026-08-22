<template>
    <el-result
        icon="warning"
        title="找不到菜单资源"
        sub-title="你访问的菜单不存在"
    >
        <template #extra>
            <el-button @click="toLogin" type="primary">返回登录</el-button>
        </template>
    </el-result>
</template>

<script lang="ts" setup>
import { useRouter } from "vue-router";

const router = useRouter();

const toLogin = () => {
    router.push({ path: "/login" });
};
</script>

<style scoped lang="scss"></style>
