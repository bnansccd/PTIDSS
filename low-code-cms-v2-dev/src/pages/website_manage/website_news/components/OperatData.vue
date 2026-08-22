<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";
import { useDataDict } from "@/stores/modules/dataDict";
import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";
import type {
    UploadProps,
    UploadUserFile,
    UploadFile,
    UploadFiles,
} from "element-plus";
import type { Response } from "@/types/index";
import { baseStaticUrl } from "@/env/index";
const props = defineProps<{
    id: string;
}>();

const useData = useDataDict();

const fileList = ref<UploadUserFile[]>([]);
const ajaxParams = reactive({
    photoUrl: "", // 图片
    status: "", // 是否启用 0未 1启
    summary: "", // 新闻摘要
    text: "", // 正文
    title: "", // 文章标题
});

const onChange = (
    response: Response,
    uploadFile: UploadFile,
    uploadFiles: UploadFiles
) => {
    if (response.code == 200) {
        ajaxParams.photoUrl = response.data.filePath;
    } else {
        fileList.value = [];
    }
};

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
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
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
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

            <GlobalElFormItem label="新闻摘要" prop="summary">
                <el-input
                    v-model="ajaxParams.summary"
                    placeholder="请输入新闻摘要"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="新闻封面" prop="photoUrl">
                <GlobalUpload
                    v-model:file-list="fileList"
                    :on-success="onChange"
                    :limit="1"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="新闻内容" prop="text">
                <GlobalEditor v-model="ajaxParams.text" />
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
