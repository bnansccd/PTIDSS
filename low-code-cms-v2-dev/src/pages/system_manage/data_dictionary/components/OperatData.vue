<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit, apiGetSort } from "./api";
import { rules } from "./rules";

import ParentDictSelect from "./ParentDictSelect.vue";

const props = defineProps<{
    id: string;
    parentId: string;
}>();

const ajaxParams = reactive({
    dictName: "",
    dictType: "",
    parentId: props.parentId,
    parentType: "",
    remarks: "",
    sort: "",
    parent: {
        id: "",
        dictType: "",
        dictName: "",
    },
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
        ajaxParams.sort = ajaxParams.sort + "";
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

            <GlobalElFormItem label="字典名称" prop="dictName">
                <el-input
                    v-model="ajaxParams.dictName"
                    placeholder="请输入字典名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="字典类型" prop="dictType">
                <el-input
                    v-if="$props.id === '0'"
                    :readonly="$props.id !== '0'"
                    v-model="ajaxParams.dictType"
                    placeholder="请输入字典类型"
                />
                <div v-else>
                    {{ ajaxParams.dictType }}
                </div>
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
                    v-model="ajaxParams.sort"
                    placeholder="请输入排序"
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
