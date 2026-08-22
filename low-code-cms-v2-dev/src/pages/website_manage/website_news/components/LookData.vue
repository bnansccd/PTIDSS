<script setup lang="ts">
import { baseStaticUrl } from "@/env/index";
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";
import type { UploadUserFile } from "element-plus";
const props = defineProps<{
    id: string;
}>();

const fileList = ref<UploadUserFile[]>([]);
const ajaxParams = reactive({
    photoUrl: "", // 图片
    status: "", // 是否启用 0未 1启
    summary: "", // 新闻摘要
    text: "", // 正文
    title: "", // 文章标题
    type: "", // 类型 根据字典来
});

onBeforeMount(async () => {
    const data = await apiGetDetails(props.id);

    console.log(data, "data11");
    Object.keys(ajaxParams).forEach((item: string) => {
        ajaxParams[item] = data[item];
    });
    fileList.value = [
        {
            response: {
                data: {
                    name: "图片",
                    url: baseStaticUrl + ajaxParams.photoUrl,
                },
            },
            name: "图片",
            url: baseStaticUrl + ajaxParams.photoUrl,
        },
    ];
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

            <GlobalElFormItem label="新闻摘要" prop="summary">
                {{ ajaxParams.summary }}
            </GlobalElFormItem>

            <GlobalElFormItem label="新闻封面" prop="photoUrl">
                <!-- <GlobalUpload
                    v-model:file-list="fileList"
                    :on-success="onChange"
                    :limit="1"
                /> -->
                <GlobalUploadPreview :fileList="fileList" />
            </GlobalElFormItem>

            <GlobalElFormItem label="新闻内容" prop="text">
                <!-- <GlobalEditor v-model="ajaxParams.text" /> -->
                <div v-html="ajaxParams.text" class="editor-content"></div>
            </GlobalElFormItem>

            <GlobalElFormItem label="是否启用 " prop="status">
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

<style lang="scss" scoped>
.editor-content {
    width: 100%;
    :deep(img) {
        max-width: 100% !important;
    }
}
</style>

<script lang="ts">
import { defineComponent, ref } from "vue";
export default defineComponent({
    title: "查看详情",
    name: "LookData",
});
</script>
