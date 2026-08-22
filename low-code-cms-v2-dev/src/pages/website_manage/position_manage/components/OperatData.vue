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
    postName: "", // 岗位名称
    status: "", // 是否启用 0未 1启
    departmentId: "", // 组织ID
    jobDetail: "", // 岗位内容
    num: "", // 招聘人数
});

const onChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
    console.log(uploadFile);
};

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
            let bool =
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
            <GlobalElFormItem label="岗位名称" prop="postName">
                <el-input
                    v-model="ajaxParams.postName"
                    placeholder="请输入岗位名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="组织" prop="departmentId">
                <GlobalDepartSelect
                    v-model="ajaxParams.departmentId"
                    placeholder="请选择组织"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="招聘人数" prop="num">
                <el-input
                    v-model="ajaxParams.num"
                    placeholder="请输入招聘人数"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="岗位内容" prop="jobDetail">
                <GlobalEditor v-model="ajaxParams.jobDetail" />
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
