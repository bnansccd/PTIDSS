<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams:any = reactive({
    cn: "",
    sn: "",
    userId: "",
    userVO: {}
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
        <GlobalElForm
            ref="refElForm"
            size="large"
            label-position="right"
            label-width="7vw"
        >
            <!-- <GlobalElFormItem label="岗位编码" prop="postCode">
                {{ ajaxParams.postCode }}
            </GlobalElFormItem> -->

            <GlobalElFormItem label="证书持有人(CN)" prop="cn">
                {{ ajaxParams.cn }}
            </GlobalElFormItem>

            <GlobalElFormItem label="证书号码(SN)" prop="sn">
                {{ ajaxParams.sn }}
            </GlobalElFormItem>

            <GlobalElFormItem label="证书使用用户" prop="sort">
                {{ ajaxParams.userVO?.username }}
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
