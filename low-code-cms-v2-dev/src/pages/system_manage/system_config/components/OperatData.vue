<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    configKey: "",
    configName: "",
    configType: "",
    configValue: "",
    code: "",
    remarks: "",
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
const submit = (refElForm: FormInstance | null) => {
    refElForm?.validate(async (valid, fields) => {
        if (valid) {
            const bool =
                props.id === "0"
                    ? await apiPostAdd(ajaxParams)
                    : await apiPutEdit(ajaxParams, props.id);
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
            <GlobalElFormItem label="参数键名" prop="configKey">
                <el-input
                    v-model="ajaxParams.configKey"
                    placeholder="请输入参数键名"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="参数名称" prop="configName">
                <el-input
                    v-model="ajaxParams.configName"
                    placeholder="请输入参数名称"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="code" prop="code">
                <el-input
                    v-model="ajaxParams.code"
                    placeholder="请输入参数名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="系统内置" prop="configType">
                <el-input
                    v-model="ajaxParams.configType"
                    placeholder="请输入系统内置"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="参数键值" prop="configValue">
                <el-input
                    v-model="ajaxParams.configValue"
                    placeholder="请输入参数键值"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="备注" prop="remarks">
                <el-input
                    v-model="ajaxParams.remarks"
                    placeholder="请输入备注"
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
