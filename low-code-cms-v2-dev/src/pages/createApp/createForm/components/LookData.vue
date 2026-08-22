<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    postCode: "",
    postName: "",
    remarks: "",
    sort: null,
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
            label-width="5vw"
        >
            <!-- <GlobalElFormItem label="岗位编码" prop="postCode">
                {{ ajaxParams.postCode }}
            </GlobalElFormItem> -->

            <GlobalElFormItem label="岗位名称" prop="postName">
                {{ ajaxParams.postName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="备注" prop="remarks">
                {{ ajaxParams.remarks }}
            </GlobalElFormItem>

            <GlobalElFormItem label="排序" prop="sort">
                {{ ajaxParams.sort }}
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
