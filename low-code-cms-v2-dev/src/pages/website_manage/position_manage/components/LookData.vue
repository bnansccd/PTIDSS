<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    postName: "", // 岗位名称
    status: "", // 是否启用 0未 1启
    departmentId: "", // 组织ID
    departName: "",
    jobDetail: "", // 岗位内容
    num: "", // 招聘人数
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
            <GlobalElFormItem label="岗位名称" prop="postName">
                {{ ajaxParams.postName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="组织" prop="departName">
                {{ ajaxParams.departName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="招聘人数" prop="num">
                {{ ajaxParams.num }}
            </GlobalElFormItem>

            <GlobalElFormItem label="岗位内容" prop="jobDetail">
                <div v-html="ajaxParams.jobDetail" class="editor-content"></div>
            </GlobalElFormItem>
            <GlobalElFormItem label="是否启用 " prop="status">
                <GlobalDictSelect
                    parentId="ENABLE"
                    v-model="ajaxParams.status"
                    placeholder="请选择是否启用"
                />
            </GlobalElFormItem>
        </GlobalElForm>
    </GlobalElDialog>
</template>

<style lang="scss" scoped>
.editor-content {
    width: 100%;
    :deep(img) {
        max-width: 100% !important;
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "查看详情",
    name: "LookData",
});
</script>
