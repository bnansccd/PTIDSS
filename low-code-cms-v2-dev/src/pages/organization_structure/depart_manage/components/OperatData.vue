<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit, apiGetSort } from "./api";
import { rules } from "./rules";

import ParentDepartSelect from "../global/ParentDepartSelect.vue";

const props = defineProps<{
    id: string;
    parentId: string;
}>();

const ajaxParams = reactive({
    departName: "",
    code: "",
    parentId: props.parentId,
    sort: null,
    userId: null,
    userName: ""
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
    } else {
        // 查询排序序号
        const sort = await apiGetSort(props.parentId);
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
            <!-- <GlobalElFormItem label="上级组织" prop="parentId">
                <ParentDepartSelect
                    :flag="true"
                    v-model="ajaxParams.parentId"
                    placeholder="请选择上级组织"
                />
            </GlobalElFormItem> -->

            <GlobalElFormItem label="组织名称" prop="departName">
                <el-input
                    v-model="ajaxParams.departName"
                    placeholder="请输入组织名称"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="组织编码" prop="code">
                <el-input
                    v-model="ajaxParams.code"
                    placeholder="请输入组织编码"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="组织负责人" prop="userId">
                <GlobalUserSelect
                    v-model="ajaxParams.userId"
                    :userName="ajaxParams.userName"
                    placeholder="请输入组织负责人"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="排序" prop="sort">
                <el-input
                    v-model="ajaxParams.sort"
                    type="number"
                    placeholder="请输入排序序号"
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
