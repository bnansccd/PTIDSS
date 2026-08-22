<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    accessTime: "", //访问时间
    loginIp: "", //登录IP地址
    msg: "", //提示信息
    status: "", //登录状态（0成功1失败）
    statusName: "", //登录状态（0成功1失败）
    username: "", //用户账号
});

onBeforeMount(async () => {
    let data = await apiGetDetails(props.id);
    Object.keys(ajaxParams).forEach((item: string) => {
        ajaxParams[item] = data[item];
    });
});
</script>

<template>
    <GlobalElDialog title="查看" :="$attrs" @close="$emit('close')">
        <GlobalElForm ref="refElForm" label-width="5vw">
            <GlobalElFormItem label="用户账号" prop="">
                {{ ajaxParams.username }}
            </GlobalElFormItem>

            <GlobalElFormItem label="访问时间" prop="">
                {{ ajaxParams.accessTime }}
            </GlobalElFormItem>

            <GlobalElFormItem label="登录IP地址" prop="">
                {{ ajaxParams.loginIp }}
            </GlobalElFormItem>

            <GlobalElFormItem label="提示信息" prop="">
                {{ ajaxParams.msg }}
            </GlobalElFormItem>

            <GlobalElFormItem label="登录状态" prop="">
                {{ ajaxParams.statusName }}
            </GlobalElFormItem>
        </GlobalElForm>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "查看详情",
    name: "LookData",
});
</script>
