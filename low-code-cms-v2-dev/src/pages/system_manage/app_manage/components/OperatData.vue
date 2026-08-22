<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";
import { baseStaticUrl } from "@/env";
import {
    apiPostAdd,
    apiGetDetails,
    apiPutEdit,
    apiGetSort,
    type AppParams,
} from "./api";
import { rules } from "./rules";
import type { UploadFile, UploadFiles, UploadUserFile } from "element-plus";
const props = defineProps<{
    id: string;
    parentId: string;
}>();

const ajaxParams = reactive<AppParams>({
    background: "", // 背景图
    icon: "", // 图标
    name: "", // 名称
    code: "",
    status: "", // 启用状态 0未启用 1启用
    url: "", // 地址
    sort: 0,
    type: "",
});
const fileList = ref<UploadUserFile[]>([]);
const fileList2 = ref<UploadUserFile[]>([]);
onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
        ajaxParams.sort = ajaxParams.sort + "";
        if (ajaxParams.icon) {
            fileList.value = [
                {
                    response: {
                        data: {
                            name: "图片",
                            url: baseStaticUrl + ajaxParams.icon,
                        },
                    },
                    name: "图片",
                    url: baseStaticUrl + ajaxParams.icon,
                },
            ];
        }
        if (ajaxParams.background) {
            fileList2.value = [
                {
                    response: {
                        data: {
                            name: "图片",
                            url: baseStaticUrl + ajaxParams.background,
                        },
                    },
                    name: "图片",
                    url: baseStaticUrl + ajaxParams.background,
                },
            ];
        }
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            const params = JSON.parse(JSON.stringify(ajaxParams));

            const bool =
                props.id === "0"
                    ? await apiPostAdd(params)
                    : await apiPutEdit(params, props.id);
            bool && emit("close");
            bool && emit("query");
        }
    });
}

const onChange = (
    response: Response,
    uploadFile: UploadFile,
    uploadFiles: UploadFiles
) => {
    if (response.code == 200) {
        ajaxParams.icon = response.data.filePath;
    } else {
        fileList.value = [];
    }
};

const onChange2 = (
    response: Response,
    uploadFile: UploadFile,
    uploadFiles: UploadFiles
) => {
    if (response.code == 200) {
        ajaxParams.background = response.data.filePath;
    } else {
        fileList2.value = [];
    }
};
</script>

<template>
    <GlobalElDialog :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <!-- <GlobalElFormItem label="父级字典" prop="parentId">
                <ParentDictSelect
                    clearable
                    v-model="ajaxParams.parent"
                    placeholder="请选择或搜索后选择父级字典"
                />
            </GlobalElFormItem> -->

            <GlobalElFormItem label="应用编码" prop="code">
                <el-input
                    v-model="ajaxParams.code"
                    placeholder="请输入应用编码"
                    :disabled="props.id != 0"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="应用名称" prop="name">
                <el-input
                    v-model="ajaxParams.name"
                    placeholder="请输入应用名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="应用地址" prop="url">
                <el-input
                    v-model="ajaxParams.url"
                    placeholder="请输入应用地址"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="应用图标" prop="icon">
                <GlobalUpload
                    v-model:file-list="fileList"
                    :on-success="onChange"
                    :limit="1"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="应用背景" prop="background">
                <GlobalUpload
                    v-model:file-list="fileList2"
                    :on-success="onChange2"
                    :limit="1"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="应用类型" prop="type">
                <GlobalDictSelect
                    parentId="APP_TYPE"
                    placeholder="请选择菜单类型"
                    v-model="ajaxParams.type"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="排序" prop="sort">
                <el-input
                    type="number"
                    v-model.number="ajaxParams.sort"
                    placeholder="请输入排序序号"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="状态" prop="status">
                <GlobalElSwitch
                    v-model="ajaxParams.status"
                    active-value="1"
                    inactive-value="0"
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
