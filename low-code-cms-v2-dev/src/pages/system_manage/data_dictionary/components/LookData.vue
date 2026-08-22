<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    dictName: "",
    dictType: "",
    parentId: null,
    parentType: "",
    remarks: "",
    sort: null,
    parent: {
        dictName: "",
    },
});

onBeforeMount(async () => {
    let data = await apiGetDetails(props.id);
    Object.keys(ajaxParams).forEach((item: string) => {
        ajaxParams[item] = data[item];
    });
});
</script>

<template>
    <GlobalElDialog :="$attrs" @close="$emit('close')">
        <GlobalElForm ref="refElForm" label-width="5vw">
            <GlobalElFormItem
                v-if="ajaxParams.parent"
                v-show="ajaxParams.parent"
                label="父级字典"
                prop="parentId"
            >
                {{ ajaxParams.parent && ajaxParams.parent.dictName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="字典名称" prop="dictName">
                {{ ajaxParams.dictName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="字典类型" prop="dictType">
                {{ ajaxParams.dictType }}
            </GlobalElFormItem>

            <GlobalElFormItem label="备注" prop="remarks">
                {{ ajaxParams.remarks }}
            </GlobalElFormItem>

            <GlobalElFormItem label="排序" prop="">
                {{ ajaxParams.sort }}
            </GlobalElFormItem>
        </GlobalElForm>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "查看详情",
    name: "LookData",
});
</script>
