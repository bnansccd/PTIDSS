<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit, apiGetSort } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    code: "",
    name: "",
    status: "",
    startTime: "",
    endTime: "",
    id: props.id,
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
    } else {
        // 查询排序序号
        // const sort = await apiGetSort();
        // ajaxParams.sort = sort;
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
    <GlobalElDialog :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <!-- <GlobalElFormItem label="岗位编码" prop="postCode">
                <el-input
                    v-model="ajaxParams.postCode"
                    placeholder="请输入岗位编码"
                />
            </GlobalElFormItem> -->
            <GlobalElFormItem label="租户编码" prop="code">
                <el-input
                    v-model="ajaxParams.code"
                    placeholder="请输入租户编码"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="租户名称" prop="name">
                <el-input
                    v-model="ajaxParams.name"
                    placeholder="请输入租户名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="有效时间" prop="startTime">
                <GlobalDatePicker
                    v-model:startTime="ajaxParams.startTime"
                    v-model:endTime="ajaxParams.endTime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                />
            </GlobalElFormItem>

            <!-- <GlobalElFormItem label="状态" prop="status">
                <GlobalElSwitch
                    v-model="ajaxParams.status"
                    active-value="0"
                    inactive-value="1"
                />
            </GlobalElFormItem> -->

            <!-- <GlobalElFormItem label="排序" prop="sort">
                <el-input
                    type="number"
                    v-model.number="ajaxParams.sort"
                    placeholder="请输入排序值"
                />
            </GlobalElFormItem> -->
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
