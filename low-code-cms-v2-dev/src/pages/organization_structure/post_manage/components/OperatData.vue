<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit, apiGetSort } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    // postCode: "",
    postName: "",
    remarks: "",
    sort: ""
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        let data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
    } else {
        // 查询排序序号
        const sort = await apiGetSort();
        ajaxParams.sort = sort;
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

            <GlobalElFormItem label="岗位名称" prop="postName">
                <el-input
                    v-model="ajaxParams.postName"
                    placeholder="请输入岗位名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="备注" prop="remarks">
                <el-input
                    v-model="ajaxParams.remarks"
                    placeholder="请输入备注"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="排序" prop="sort">
                <el-input
                    type="number"
                    v-model.number="ajaxParams.sort"
                    placeholder="请输入排序值"
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
