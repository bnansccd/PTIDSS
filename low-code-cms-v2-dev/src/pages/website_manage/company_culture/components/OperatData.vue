<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";
import { baseStaticUrl } from "@/env/index";
import type {
    UploadProps,
    UploadUserFile,
    UploadFile,
    UploadFiles,
} from "element-plus";
interface AjaxParams {
    urls: Array<string>; //岗位编码
    status: string; //岗位名称
    title: string; //备注
}
interface UploadResponse {
    response: uploadData;
    name: string;
    url: string;
    filePath: string;
}

interface uploadData {
    data: UploadResponse;
}
const props = defineProps<{
    id: string;
}>();

const fileList = ref<UploadUserFile[]>([]);
const ajaxParams = reactive<AjaxParams>({
    urls: [] as string[], // 图片列表
    status: "", // 是否启用 0未 1启
    title: "", // 文章标题
});

onBeforeMount(async () => {
    if (props.id !== "0") {
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
                        filePath: item,
                    },
                },
                name: "图片",
                url: baseStaticUrl + item,
                filePath: item,
            };
        });
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    const urlList = fileList.value.map(
        (item: any) => item.response.data.filePath
    );
    ajaxParams.urls = urlList as string[];
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            const bool =
                props.id === "0"
                    ? await apiPostAdd(ajaxParams)
                    : await apiPutEdit(ajaxParams, props.id);
            bool && emit("close");
            bool && emit("query");
        }
    });
}
</script>

<template>
    <GlobalElDialog width="50vw" :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem label="文章标题" prop="title">
                <el-input
                    v-model="ajaxParams.title"
                    placeholder="请输入文章标题"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="图片列表" prop="urls">
                <GlobalUpload v-model:file-list="fileList" :limit="20" />
            </GlobalElFormItem>

            <GlobalElFormItem label="是否启用 " prop="status">
                <GlobalDictSelect
                    parentId="ENABLE"
                    v-model="ajaxParams.status"
                    placeholder="请选择是否启用"
                />
            </GlobalElFormItem>
        </GlobalElForm>

        <template #footer>
            <el-button type="primary" @click="submit(refElForm)"
                >保存</el-button
            >

            <el-button @click="$emit('close')">取消</el-button>
        </template>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "数据操作",
    name: "OperatData",
});
</script>
