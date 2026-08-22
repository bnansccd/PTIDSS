<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";
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
    url: "", // 地址
    status: "", // 是否启用 0未 1启
    keyword: "", // 文章标题
    name: "", // 产品名
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
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
            <GlobalElFormItem label="产品名" prop="name">
                <el-input
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                    v-model="ajaxParams.name"
                    placeholder="请输入产品名"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="标签" prop="keyword">
                <el-input
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 20 }"
                    v-model="ajaxParams.keyword"
                    placeholder="请输入标签"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="前端地址" prop="url">
                <el-input
                    v-model="ajaxParams.url"
                    placeholder="请输入前端地址"
                />
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
