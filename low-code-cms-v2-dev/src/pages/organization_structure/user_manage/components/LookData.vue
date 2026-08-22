<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    // departId: null,
    sysDepartVO: null,
    email: "",
    headUrl: "",
    phone: "",
    // postIds: [],
    sysPostVOS: null,
    realName: "",
    sex: null,
    username: "",
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
            <GlobalElFormItem label="组织" prop="departId">
                {{ ajaxParams.sysDepartVO }}
            </GlobalElFormItem>

            <GlobalElFormItem label="岗位" prop="postIds">
                {{ ajaxParams.sysPostVOS }}
            </GlobalElFormItem>

            <GlobalElFormItem label="邮箱" prop="email">
                {{ ajaxParams.email }}
            </GlobalElFormItem>

            <GlobalElFormItem label="头像地址" prop="headUrl">
                {{ ajaxParams.headUrl }}
            </GlobalElFormItem>

            <GlobalElFormItem label="手机号" prop="phone">
                {{ ajaxParams.phone }}
            </GlobalElFormItem>

            <GlobalElFormItem label="真实姓名" prop="realName">
                {{ ajaxParams.realName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="性别" prop="sex">
                {{ ajaxParams.sex }}
            </GlobalElFormItem>

            <GlobalElFormItem label="用户名" prop="username">
                {{ ajaxParams.username }}
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
