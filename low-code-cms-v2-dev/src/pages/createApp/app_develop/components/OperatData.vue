<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit, apiGetSort } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    roleCode: "",
    roleName: "", // 角色名称
    status: "", // 状态 启用停用(0启用1停用)
    remark: "", // 备注
    sort: null, // 排序
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
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
    <GlobalElDialog :="$attrs">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem label="角色编码" prop="roleCode">
                <el-input
                    v-model="ajaxParams.roleCode"
                    placeholder="请输入角色编码"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="角色名称" prop="roleName">
                <el-input
                    v-model="ajaxParams.roleName"
                    placeholder="请输入角色名称"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="备注" prop="remark">
                <el-input
                    v-model="ajaxParams.remark"
                    placeholder="请输入排序序号"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="排序" prop="sort">
                <el-input
                    type="number"
                    v-model="ajaxParams.sort"
                    placeholder="请输入排序序号"
                />
            </GlobalElFormItem>
            <!-- <GlobalElFormItem label="状态" prop="status">
                <GlobalElSwitch
                    v-model="ajaxParams.status"
                    active-value="0"
                    inactive-value="1"
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
