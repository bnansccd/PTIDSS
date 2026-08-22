<script setup lang="ts">
import { reactive, onBeforeMount, ref } from "vue";
import { baseStaticUrl } from "@/env/index";
import { apiGetDetails } from "./api";
import type {
    UploadProps,
    UploadUserFile,
    UploadFile,
    UploadFiles,
} from "element-plus";
const props = defineProps<{
    id: string;
}>();

const fileList = ref<UploadUserFile[]>([]);
const ajaxParams = reactive({
    urls: [] as string[], // 图片列表
    status: "", // 是否启用 0未 1启
    title: "", // 文章标题
});
onBeforeMount(async () => {
    const data = await apiGetDetails(props.id);
    Object.keys(ajaxParams).forEach((item: string) => {
        ajaxParams[item] = data[item];
    });
    fileList.value = ajaxParams.urls.map((item) => {
        return {
            response: {
                data: {
                    name: "图片",
                    url: baseStaticUrl + item,
                },
            },
            name: "图片",
            url: baseStaticUrl + item,
        };
    });
});
</script>

<template>
    <GlobalElDialog
        title="查看"
        width="50vw"
        :="$attrs"
        @close="$emit('close')"
    >
        <GlobalElForm
            ref="refElForm"
            size="large"
            label-position="right"
            label-width="5vw"
        >
            <GlobalElFormItem label="文章标题" prop="title">
                {{ ajaxParams.title }}
            </GlobalElFormItem>

            <GlobalElFormItem label="图片列表" prop="postName">
                <GlobalUploadPreview :fileList="fileList" />
            </GlobalElFormItem>

            <GlobalElFormItem label="是否启用" prop="status">
                <GlobalDictSelect
                    parentId="ENABLE"
                    v-model="ajaxParams.status"
                    placeholder="请选择是否启用"
                    type="text"
                    :id="ajaxParams.status"
                />
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
