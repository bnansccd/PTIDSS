<script setup lang="ts">
import { reactive, ref, onBeforeMount, toRaw } from "vue";
import type {
    FormInstance,
    UploadFile,
    UploadFiles,
    UploadUserFile,
} from "element-plus";
import type { Response } from "@/types/index";
import type { ConfigParams } from "@/types/index";
import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";
import { baseStaticUrl } from "@/env/index";
const props = defineProps<{
    id: string;
    row: ConfigParams;
}>();
const fileList = ref<UploadUserFile[]>([]);
const fileList2 = ref<UploadUserFile[]>([]);
const ajaxParams = ref<Array<ConfigParams>>([]);

const onChange = (
    response: Response,
    uploadFile: UploadFile,
    uploadFiles: UploadFiles
) => {
    if (response.code == 200) {
        ajaxParams.value[0].configValue = response.data.filePath;
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
        ajaxParams.value[1].configValue = response.data.filePath;
    } else {
        fileList2.value = [];
    }
};
onBeforeMount(async () => {
    if (props.id !== "0") {
        // const data = await apiGetDetails(props.id);
        ajaxParams.value = [props.row];
        // Object.keys(ajaxParams).forEach((item: string) => {
        //     ajaxParams[item] = data[item];
        // });
        if (ajaxParams.value[0].configValue) {
            fileList.value = [
                {
                    response: {
                        data: {
                            name: "图片",
                            url:
                                baseStaticUrl + ajaxParams.value[0].configValue,
                        },
                    },
                    name: "图片",
                    url: baseStaticUrl + ajaxParams.value[0].configValue,
                },
            ];
        }
        if (ajaxParams.value[1].configValue) {
            fileList2.value = [
                {
                    response: {
                        data: {
                            name: "图片",
                            url:
                                baseStaticUrl + ajaxParams.value[1].configValue,
                        },
                    },
                    name: "图片",
                    url: baseStaticUrl + ajaxParams.value[1].configValue,
                },
            ];
        }
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
const submit = (refElForm: FormInstance | null) => {
    refElForm?.validate(async (valid, fields) => {
        if (valid) {
            const bool = await apiPutEdit(ajaxParams.value[0]);
            bool && emit("close");
            bool && emit("query");
        }
    });
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
            <div v-for="(item, index) in ajaxParams" :key="index">
                <GlobalElFormItem
                    :label="item.configName"
                    :prop="item.configKey"
                    v-if="index == 0"
                >
                    <GlobalUpload
                        v-model:file-list="fileList"
                        :on-success="onChange"
                        :limit="1"
                    />
                </GlobalElFormItem>
                <GlobalElFormItem
                    :label="item.configName"
                    :prop="item.configKey"
                    v-if="index == 1"
                >
                    <GlobalUpload
                        v-model:file-list="fileList2"
                        :on-success="onChange2"
                        :limit="1"
                    />
                </GlobalElFormItem>
            </div>
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
    title: "编辑系统Logo",
    name: "OperatSysLogo",
});
</script>
