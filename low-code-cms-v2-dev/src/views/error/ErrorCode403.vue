<template>
    <el-result
        icon="warning"
        title="没有权限访问"
        sub-title="请联系管理员开通权限"
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
